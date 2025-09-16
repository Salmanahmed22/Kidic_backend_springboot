package com.example.kidic.config;

import com.example.kidic.entity.*;
import com.example.kidic.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ParentRepository parentRepository;
    
    @Autowired
    private FamilyRepository familyRepository;
    
    @Autowired
    private ChildRepository childRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private EducationalContentRepository educationalContentRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private AIInfoRepository aiInfoRepository;
    
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    
    @Autowired
    private DiseaseAndAllergyRepository diseaseAndAllergyRepository;
    
    @Autowired
    private GrowthRecordRepository growthRecordRepository;
    
    @Autowired
    private MealRepository mealRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 DataInitializer starting...");
        System.out.println("📊 Current parent count: " + parentRepository.count());
        
        // Only initialize if no data exists
        if (parentRepository.count() == 0) {
            System.out.println("📝 No data found, initializing sample data...");
            initializeData();
            System.out.println("✅ Data initialization completed!");
        } else {
            System.out.println("ℹ️ Data already exists, skipping initialization.");
        }
    }

    private void initializeData() {
        try {

            // Create Families
            Family family1 = new Family();
            family1.setId(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"));

            Family family2 = new Family();
            family2.setId(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"));

            familyRepository.saveAll(Arrays.asList(family1, family2));

            System.out.println("👨‍👩‍👧‍👦 Creating parents...");
            // Create Parents
            Parent parent1 = new Parent("John Smith", "1234567890", "john.smith@email.com", true,
                    "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi");
            parent1.setProfilePicture(Parent.ProfilePictureType.DEFAULT);
            parent1.setFamily(family1);

            Parent parent2 = new Parent("Jane Smith", "1234567891", "jane.smith@email.com", false,
                    "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi");
            parent2.setProfilePicture(Parent.ProfilePictureType.AVATAR_1);
            parent2.setFamily(family1);

            Parent parent3 = new Parent("Mike Johnson", "1234567892", "mike.johnson@email.com", true,
                    "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi");
            parent3.setProfilePicture(Parent.ProfilePictureType.CUSTOM);
            parent3.setFamily(family2);
            System.out.println("✅ Parents created successfully");

        // Add parents to families
        family1.getParents().addAll(Arrays.asList(parent1, parent2));
        family2.getParents().add(parent3);
        familyRepository.saveAll(Arrays.asList(family1, family2));

        // Create Children
        Child child1 = new Child("Emma Smith", false, LocalDate.of(2020, 5, 15), null, family1);
        child1.setMedicalNotes("No known allergies");

        Child child2 = new Child("Liam Smith", true, LocalDate.of(2019, 8, 22), null, family1);
        child2.setMedicalNotes("Allergic to peanuts");

        Child child3 = new Child("Sophia Johnson", false, LocalDate.of(2021, 3, 10), null, family2);
        child3.setMedicalNotes("Asthma condition");

        childRepository.saveAll(Arrays.asList(child1, child2, child3));

        // Create Notifications
        Notification notification1 = new Notification(Notification.NotificationType.MEDICAL, 
                "Emma has a vaccination appointment tomorrow", family1);
        
        Notification notification2 = new Notification(Notification.NotificationType.GROWTH, 
                "Liam achieved new physical milestone", family1);
        notification2.setIsRead(true);
        
        Notification notification3 = new Notification(Notification.NotificationType.MEAL, 
                "New healthy meal options available", family2);
        
        notificationRepository.saveAll(Arrays.asList(notification1, notification2, notification3));

        // Create Educational Content
        EducationalContent eduContent1 = new EducationalContent("ABC Learning", 
                "https://example.com/abc", "30 minutes", "Basic alphabet learning for toddlers");
        
        EducationalContent eduContent2 = new EducationalContent("Numbers 1-10", 
                "https://example.com/numbers", "20 minutes", "Counting and number recognition");
        
        EducationalContent eduContent3 = new EducationalContent("Colors and Shapes", 
                "https://example.com/colors", "25 minutes", "Learning basic colors and shapes");
        
        educationalContentRepository.saveAll(Arrays.asList(eduContent1, eduContent2, eduContent3));

        // Create Products
        Product product1 = new Product("Educational Blocks", "https://example.com/blocks", 
                "Colorful building blocks for creativity", Product.ImageType.IMAGE_1, Product.CategoryType.TOYS);
        
        Product product2 = new Product("Children Book Set", "https://example.com/books", 
                "Age-appropriate story books", Product.ImageType.IMAGE_2, Product.CategoryType.BOOKS);
        
        Product product3 = new Product("Healthy Snacks", "https://example.com/snacks", 
                "Nutritious snacks for children", Product.ImageType.IMAGE_3, Product.CategoryType.FOOD);
        
        productRepository.saveAll(Arrays.asList(product1, product2, product3));

        // Create AI Info
        AIInfo aiInfo1 = new AIInfo(AIInfo.PlaceOfUsageType.HOME, true, 3, product1);
        AIInfo aiInfo2 = new AIInfo(AIInfo.PlaceOfUsageType.SCHOOL, false, 4, product2);
        AIInfo aiInfo3 = new AIInfo(AIInfo.PlaceOfUsageType.INDOOR, true, 2, product3);
        
        aiInfoRepository.saveAll(Arrays.asList(aiInfo1, aiInfo2, aiInfo3));

        // Create Medical Records
        MedicalRecord medicalRecord1 = new MedicalRecord(MedicalRecord.MedicalRecordType.VACCINATION, 
                LocalDate.of(2024, 1, 15), "MMR vaccination completed", 
                MedicalRecord.FileType.PDF, MedicalRecord.StatusType.COMPLETED, child1);
        
        MedicalRecord medicalRecord2 = new MedicalRecord(MedicalRecord.MedicalRecordType.CHECKUP, 
                LocalDate.of(2024, 2, 10), "Regular health checkup", 
                MedicalRecord.FileType.PDF, MedicalRecord.StatusType.COMPLETED, child2);
        
        MedicalRecord medicalRecord3 = new MedicalRecord(MedicalRecord.MedicalRecordType.ALLERGY, 
                LocalDate.of(2024, 3, 5), "Peanut allergy diagnosed", 
                MedicalRecord.FileType.DOCUMENT, MedicalRecord.StatusType.ACTIVE, child2);
        
        medicalRecordRepository.saveAll(Arrays.asList(medicalRecord1, medicalRecord2, medicalRecord3));

        // Create Disease and Allergies
        DiseaseAndAllergy disease1 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.ALLERGY, 
                "Peanut allergy - severe reaction", "Avoid all peanut products and carry epinephrine", child2);
        
        DiseaseAndAllergy disease2 = new DiseaseAndAllergy(DiseaseAndAllergy.DiseaseAllergyType.CONDITION, 
                "Mild asthma", "Use inhaler as prescribed and monitor symptoms", child3);
        
        diseaseAndAllergyRepository.saveAll(Arrays.asList(disease1, disease2));

        // Create Growth Records
        GrowthRecord growthRecord1 = new GrowthRecord("Good progress", LocalDate.of(2024, 1, 20), 
                95.5, 14.2, GrowthRecord.GrowthType.PHYSICAL, GrowthRecord.StatusType.ACHIEVED, child1);
        
        GrowthRecord growthRecord2 = new GrowthRecord("Excellent development", LocalDate.of(2024, 2, 15), 
                98.0, 15.1, GrowthRecord.GrowthType.PHYSICAL, GrowthRecord.StatusType.ACHIEVED, child2);
        
        GrowthRecord growthRecord3 = new GrowthRecord("Needs improvement", LocalDate.of(2024, 3, 1), 
                92.0, 13.8, GrowthRecord.GrowthType.PHYSICAL, GrowthRecord.StatusType.NOT_ACHIEVED, child3);
        
        growthRecordRepository.saveAll(Arrays.asList(growthRecord1, growthRecord2, growthRecord3));

        // Create Meals
        Meal meal1 = new Meal("Healthy Pancakes", Arrays.asList("Flour", "Eggs", "Milk"), 
                "Mix flour, eggs, milk. Cook on griddle until golden.");
        meal1.setChild(child1);
        
        Meal meal2 = new Meal("Vegetable Soup", Arrays.asList("Carrots", "Broccoli", "Chicken broth"), 
                "Boil vegetables in broth. Season to taste.");
        meal2.setChild(child2);
        
        Meal meal3 = new Meal("Fruit Salad", Arrays.asList("Apples", "Bananas", "Oranges"), 
                "Cut mixed fruits. Mix gently. Serve fresh.");
        meal3.setChild(child1);
        
        mealRepository.saveAll(Arrays.asList(meal1, meal2, meal3));

            System.out.println("✅ Sample data initialized successfully!");
        } catch (Exception e) {
            System.err.println("❌ Error initializing data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
