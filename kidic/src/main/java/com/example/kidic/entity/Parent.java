package com.example.kidic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "parents")
public class Parent implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    private String name;
    
    @NotBlank
    @Size(max = 20)
    private String phone;
    
    @Email
    @NotBlank
    @Size(max = 100)
    private String email;
    
    private Boolean gender; // true for male, false for female

    @NotBlank
    @Size(max = 255)
    private String password; // Hashed String
    
    @Enumerated(EnumType.STRING)
    @Column(name = "profile_picture_type")
    private ProfilePictureType profilePictureType;
    
    @Size(max = 500)
    @Column(name = "profile_picture_name")
    private String profilePictureName;
    
    @Column(name = "profile_picture_content", columnDefinition = "LONGBLOB")
    private byte[] profilePictureContent;
    
    @Column(name = "profile_picture_size")
    private Long profilePictureSize;
    
    @Size(max = 100)
    @Column(name = "profile_picture_content_type")
    private String profilePictureContentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = true)
    private Family family;


    // Constructorsrfg
    public Parent() {}
    
    public Parent(String name, String phone, String email, Boolean gender, String password) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.password = password;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Boolean getGender() {
        return gender;
    }
    
    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public ProfilePictureType getProfilePictureType() {
        return profilePictureType;
    }
    
    public void setProfilePictureType(ProfilePictureType profilePictureType) {
        this.profilePictureType = profilePictureType;
    }
    
    public String getProfilePictureName() {
        return profilePictureName;
    }
    
    public void setProfilePictureName(String profilePictureName) {
        this.profilePictureName = profilePictureName;
    }
    
    public byte[] getProfilePictureContent() {
        return profilePictureContent;
    }
    
    public void setProfilePictureContent(byte[] profilePictureContent) {
        this.profilePictureContent = profilePictureContent;
    }
    
    public Long getProfilePictureSize() {
        return profilePictureSize;
    }
    
    public void setProfilePictureSize(Long profilePictureSize) {
        this.profilePictureSize = profilePictureSize;
    }
    
    public String getProfilePictureContentType() {
        return profilePictureContentType;
    }
    
    public void setProfilePictureContentType(String profilePictureContentType) {
        this.profilePictureContentType = profilePictureContentType;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public enum ProfilePictureType {
        DEFAULT, CUSTOM, AVATAR_1, AVATAR_2, AVATAR_3
    }

    //Auth methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
