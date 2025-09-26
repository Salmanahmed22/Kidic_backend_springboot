package com.example.kidic.controller;

import com.example.kidic.entity.Family;
import com.example.kidic.entity.Notification;
import com.example.kidic.entity.Parent;
import com.example.kidic.repository.FamilyRepository;
import com.example.kidic.repository.NotificationRepository;
import com.example.kidic.repository.ParentRepository;
import com.example.kidic.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class NotificationControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private NotificationService notificationService;

    private String token;
    private UUID familyId;
    private Long parentId;
    private Long invalidParentId = 999L;
    private Long invalidNotificationId = 999L;

    @BeforeEach
    void setUp() {
        // Clear existing data
        notificationRepository.deleteAll();
        parentRepository.deleteAll();
        familyRepository.deleteAll();

        // Create test data
        Family family = new Family();
        family = familyRepository.save(family);
        familyId = family.getId();

        Parent parent = new Parent("John Smith", "1234567890", "john.smith@email.com", true, "password");
        parent.setProfilePictureType(Parent.ProfilePictureType.DEFAULT);
        parent.setFamily(family);
        parent = parentRepository.save(parent);
        parentId = parent.getId();

        // Ensure family-parents relationship is updated
        family.getParents().add(parent);
        familyRepository.save(family);

        // Authenticate parent to generate token
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(parent, null, parent.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        token = "Bearer " + new com.example.kidic.config.JwtService().generateToken(parent);
    }

    @Test
    void testSendNotification_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/notifications/send?message=Test+notification&type=INFO",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("notification sent", response.getBody());

        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(1, notifications.size());
        assertEquals("Test notification", notifications.get(0).getContent());
        assertEquals(Notification.NotificationType.INFO, notifications.get(0).getType());
        assertEquals(parentId, notifications.get(0).getParentId());
        assertFalse(notifications.get(0).getIsRead());
        assertNotNull(notifications.get(0).getCreatedAt());
        assertEquals(LocalDateTime.now().toLocalDate(), notifications.get(0).getCreatedAt().toLocalDate(), "CreatedAt should reflect current date");
    }

    @Test
    void testSendNotification_MissingAuthorization() {
        HttpEntity<String> entity = new HttpEntity<>("");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/notifications/send?message=Test+notification&type=INFO",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode()); // Expect 401 due to JwtAuthFilter
    }

    @Test
    void testBroadcastNotification_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token); // Authorization not required, but included for consistency

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/notifications/broadcast?message=Broadcast+test&type=URGENT",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("notifications broadcasted", response.getBody());

        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(1, notifications.size());
        assertEquals("Broadcast test", notifications.get(0).getContent());
        assertEquals(Notification.NotificationType.URGENT, notifications.get(0).getType());
        assertEquals(parentId, notifications.get(0).getParentId());
        assertFalse(notifications.get(0).getIsRead());
        assertNotNull(notifications.get(0).getCreatedAt());
        assertEquals(LocalDateTime.now().toLocalDate(), notifications.get(0).getCreatedAt().toLocalDate(), "CreatedAt should reflect current date");
    }

    @Test
    void testGetNotifications_Success() {
        Notification notification = new Notification();
        notification.setParentId(parentId);
        notification.setContent("Test notification");
        notification.setType(Notification.NotificationType.INFO);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Notification[]> response = restTemplate.exchange(
                "/api/notifications/{parentId}", HttpMethod.GET, entity, Notification[].class, parentId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals("Test notification", response.getBody()[0].getContent());
    }

    @Test
    void testGetNotifications_MissingAuthorization() {
        ResponseEntity<Notification[]> response = restTemplate.getForEntity(
                "/api/notifications/{parentId}", Notification[].class, parentId
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode()); // Expect 401 due to JwtAuthFilter
    }

    @Test
    void testGetNotifications_InvalidParentId() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Notification[]> response = restTemplate.exchange(
                "/api/notifications/{parentId}", HttpMethod.GET, entity, Notification[].class, invalidParentId
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()); // Assumes RuntimeException as 500
    }

    @Test
    void testGetUnreadNotifications_Success() {
        Notification notification = new Notification();
        notification.setParentId(parentId);
        notification.setContent("Unread notification");
        notification.setType(Notification.NotificationType.GENERAL);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Notification[]> response = restTemplate.exchange(
                "/api/notifications/unread/{parentId}", HttpMethod.GET, entity, Notification[].class, parentId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals("Unread notification", response.getBody()[0].getContent());
        assertFalse(response.getBody()[0].getIsRead());
    }

    @Test
    void testGetUnreadNotifications_MissingAuthorization() {
        ResponseEntity<Notification[]> response = restTemplate.getForEntity(
                "/api/notifications/unread/{parentId}", Notification[].class, parentId
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode()); // Expect 401 due to JwtAuthFilter
    }

    @Test
    void testGetUnreadNotifications_InvalidParentId() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Notification[]> response = restTemplate.exchange(
                "/api/notifications/unread/{parentId}", HttpMethod.GET, entity, Notification[].class, invalidParentId
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()); // Assumes RuntimeException as 500
    }

    @Test
    void testMarkAsRead_Success() {
        Notification notification = new Notification();
        notification.setParentId(parentId);
        notification.setContent("Test notification");
        notification.setType(Notification.NotificationType.INFO);
        notification.setCreatedAt(LocalDateTime.now());
        notification = notificationRepository.save(notification);
        Long notificationId = notification.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/notifications/{id}/read", HttpMethod.PUT, entity, Void.class, notificationId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Notification updatedNotification = notificationRepository.findById(notificationId).get();
        assertTrue(updatedNotification.getIsRead(), "Notification should be marked as read");
    }

    @Test
    void testMarkAsRead_InvalidNotificationId() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/notifications/{id}/read", HttpMethod.PUT, entity, Void.class, invalidNotificationId
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()); // Assumes RuntimeException as 500
    }

    @Test
    void testGetNotifications_MultipleNotifications() {
        Notification notification1 = new Notification();
        notification1.setParentId(parentId);
        notification1.setContent("Test notification 1");
        notification1.setType(Notification.NotificationType.INFO);
        notification1.setCreatedAt(LocalDateTime.now().minusHours(1));

        Notification notification2 = new Notification();
        notification2.setParentId(parentId);
        notification2.setContent("Test notification 2");
        notification2.setType(Notification.NotificationType.URGENT);
        notification2.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification1);
        notificationRepository.save(notification2);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Notification[]> response = restTemplate.exchange(
                "/api/notifications/{parentId}", HttpMethod.GET, entity, Notification[].class, parentId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
        assertTrue(response.getBody()[0].getContent().equals("Test notification 2") || response.getBody()[1].getContent().equals("Test notification 2"), "Latest notification should be included");
    }
}