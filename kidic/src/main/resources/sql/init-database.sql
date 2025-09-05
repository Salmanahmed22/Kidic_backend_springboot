-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS kidic_fresh;
USE kidic_fresh;

-- Create parents table
CREATE TABLE IF NOT EXISTS parents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    gender BOOLEAN NOT NULL,
    password VARCHAR(255) NOT NULL,
    profile_picture ENUM('DEFAULT', 'CUSTOM', 'AVATAR_1', 'AVATAR_2', 'AVATAR_3')
);

-- Create families table
CREATE TABLE IF NOT EXISTS families (
    id BINARY(16) PRIMARY KEY,
    password VARCHAR(255) NOT NULL
);

-- Create children table
CREATE TABLE IF NOT EXISTS children (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    gender BOOLEAN NOT NULL,
    date_of_birth DATE NOT NULL,
    parent_id BIGINT,
    family_id BINARY(16),
    medical_notes TEXT,
    FOREIGN KEY (parent_id) REFERENCES parents(id) ON DELETE SET NULL,
    FOREIGN KEY (family_id) REFERENCES families(id) ON DELETE SET NULL
);

-- Create notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type ENUM('MEDICAL', 'EDUCATIONAL', 'MEAL', 'GROWTH', 'GENERAL', 'URGENT') NOT NULL,
    content VARCHAR(1000) NOT NULL,
    family_id BINARY(16) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (family_id) REFERENCES families(id) ON DELETE CASCADE
);

-- Create educational_contents table
CREATE TABLE IF NOT EXISTS educational_contents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    link VARCHAR(500) NOT NULL,
    estimated_time VARCHAR(50) NOT NULL,
    description VARCHAR(1000)
);

-- Create products table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    link VARCHAR(500) NOT NULL,
    description VARCHAR(1000),
    image ENUM('IMAGE_1', 'IMAGE_2', 'IMAGE_3', 'IMAGE_4', 'IMAGE_5', 'CUSTOM'),
    category ENUM('TOYS', 'BOOKS', 'CLOTHING', 'FOOD', 'MEDICAL', 'EDUCATIONAL', 'ENTERTAINMENT', 'OTHER')
);

-- Create ai_infos table
CREATE TABLE IF NOT EXISTS ai_infos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    place_of_usage ENUM('HOME', 'SCHOOL', 'HOSPITAL', 'OUTDOOR', 'INDOOR', 'PLAYGROUND', 'OTHER') NOT NULL,
    gender BOOLEAN,
    age INT,
    product_id BIGINT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Create medical_records table
CREATE TABLE IF NOT EXISTS medical_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type ENUM('VACCINATION', 'CHECKUP', 'ILLNESS', 'INJURY', 'ALLERGY', 'MEDICATION', 'OTHER') NOT NULL,
    date_of_record DATE NOT NULL,
    description VARCHAR(1000),
    file ENUM('PDF', 'IMAGE', 'DOCUMENT', 'VIDEO', 'AUDIO', 'OTHER'),
    status ENUM('ACTIVE', 'ARCHIVED', 'PENDING', 'COMPLETED', 'CANCELLED'),
    child_id BIGINT,
    FOREIGN KEY (child_id) REFERENCES children(id) ON DELETE CASCADE
);

-- Create diseases_and_allergies table
CREATE TABLE IF NOT EXISTS diseases_and_allergies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type ENUM('ALLERGY', 'DISEASE', 'CONDITION', 'INTOLERANCE', 'SENSITIVITY', 'OTHER') NOT NULL,
    description VARCHAR(1000),
    ai_response VARCHAR(2000),
    child_id BIGINT,
    FOREIGN KEY (child_id) REFERENCES children(id) ON DELETE CASCADE
);

-- Create growth_records table
CREATE TABLE IF NOT EXISTS growth_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    additional_info VARCHAR(1000),
    date_of_record DATE NOT NULL,
    height DOUBLE,
    weight DOUBLE,
    type ENUM('EMOTIONAL', 'PHYSICAL', 'COGNITION') NOT NULL,
    status ENUM('ACHIEVED', 'NOT_ACHIEVED') NOT NULL,
    child_id BIGINT NOT NULL,
    FOREIGN KEY (child_id) REFERENCES children(id) ON DELETE CASCADE
);

-- Create meals table
CREATE TABLE IF NOT EXISTS meals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    recipe VARCHAR(2000),
    child_id BIGINT,
    FOREIGN KEY (child_id) REFERENCES children(id) ON DELETE SET NULL
);

-- Create meal_ingredients table (for ingredients list)
CREATE TABLE IF NOT EXISTS meal_ingredients (
    meal_id BIGINT NOT NULL,
    ingredient VARCHAR(200) NOT NULL,
    FOREIGN KEY (meal_id) REFERENCES meals(id) ON DELETE CASCADE
);

-- Create family_parents junction table
CREATE TABLE IF NOT EXISTS family_parents (
    family_id BINARY(16) NOT NULL,
    parent_id BIGINT NOT NULL,
    PRIMARY KEY (family_id, parent_id),
    FOREIGN KEY (family_id) REFERENCES families(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES parents(id) ON DELETE CASCADE
);

-- Insert sample data
INSERT INTO parents (name, phone, email, gender, password, profile_picture) VALUES
('John Smith', '1234567890', 'john.smith@email.com', TRUE, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'DEFAULT'),
('Jane Smith', '1234567891', 'jane.smith@email.com', FALSE, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'AVATAR_1'),
('Mike Johnson', '1234567892', 'mike.johnson@email.com', TRUE, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'CUSTOM');

INSERT INTO families (id, password) VALUES
(UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440001'), 'family123'),
(UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440002'), 'family456');

INSERT INTO children (name, gender, date_of_birth, parent_id, family_id, medical_notes) VALUES
('Emma Smith', FALSE, '2020-05-15', 1, UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440001'), 'No known allergies'),
('Liam Smith', TRUE, '2019-08-22', 1, UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440001'), 'Allergic to peanuts'),
('Sophia Johnson', FALSE, '2021-03-10', 3, UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440002'), 'Asthma condition');

INSERT INTO notifications (type, content, family_id, is_read) VALUES
('MEDICAL', 'Emma has a vaccination appointment tomorrow', UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440001'), FALSE),
('GROWTH', 'Liam achieved new physical milestone', UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440001'), TRUE),
('MEAL', 'New healthy meal options available', UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440002'), FALSE);

INSERT INTO educational_contents (title, link, estimated_time, description) VALUES
('ABC Learning', 'https://example.com/abc', '30 minutes', 'Basic alphabet learning for toddlers'),
('Numbers 1-10', 'https://example.com/numbers', '20 minutes', 'Counting and number recognition'),
('Colors and Shapes', 'https://example.com/colors', '25 minutes', 'Learning basic colors and shapes');

INSERT INTO products (name, link, description, image, category) VALUES
('Educational Blocks', 'https://example.com/blocks', 'Colorful building blocks for creativity', 'IMAGE_1', 'TOYS'),
('Children Book Set', 'https://example.com/books', 'Age-appropriate story books', 'IMAGE_2', 'BOOKS'),
('Healthy Snacks', 'https://example.com/snacks', 'Nutritious snacks for children', 'IMAGE_3', 'FOOD');

INSERT INTO ai_infos (place_of_usage, gender, age, product_id) VALUES
('HOME', TRUE, 3, 1),
('SCHOOL', FALSE, 4, 2),
('INDOOR', TRUE, 2, 3);

INSERT INTO medical_records (type, date_of_record, description, file, status, child_id) VALUES
('VACCINATION', '2024-01-15', 'MMR vaccination completed', 'PDF', 'COMPLETED', 1),
('CHECKUP', '2024-02-10', 'Regular health checkup', 'PDF', 'COMPLETED', 2),
('ALLERGY', '2024-03-05', 'Peanut allergy diagnosed', 'DOCUMENT', 'ACTIVE', 2);

INSERT INTO diseases_and_allergies (type, description, ai_response, child_id) VALUES
('ALLERGY', 'Peanut allergy - severe reaction', 'Avoid all peanut products and carry epinephrine', 2),
('CONDITION', 'Mild asthma', 'Use inhaler as prescribed and monitor symptoms', 3);

INSERT INTO growth_records (additional_info, date_of_record, height, weight, type, status, child_id) VALUES
('Good progress', '2024-01-20', 95.5, 14.2, 'PHYSICAL', 'ACHIEVED', 1),
('Excellent development', '2024-02-15', 98.0, 15.1, 'PHYSICAL', 'ACHIEVED', 2),
('Needs improvement', '2024-03-01', 92.0, 13.8, 'PHYSICAL', 'NOT_ACHIEVED', 3);

INSERT INTO meals (title, recipe, child_id) VALUES
('Healthy Pancakes', 'Mix flour, eggs, milk. Cook on griddle until golden.', 1),
('Vegetable Soup', 'Boil vegetables in broth. Season to taste.', 2),
('Fruit Salad', 'Cut mixed fruits. Mix gently. Serve fresh.', 1);

INSERT INTO meal_ingredients (meal_id, ingredient) VALUES
(1, 'Flour'),
(1, 'Eggs'),
(1, 'Milk'),
(2, 'Carrots'),
(2, 'Broccoli'),
(2, 'Chicken broth'),
(3, 'Apples'),
(3, 'Bananas'),
(3, 'Oranges');

-- Create indexes for better performance
CREATE INDEX idx_parents_email ON parents(email);
CREATE INDEX idx_children_parent_id ON children(parent_id);
CREATE INDEX idx_children_family_id ON children(family_id);
CREATE INDEX idx_notifications_family_id ON notifications(family_id);
CREATE INDEX idx_ai_infos_product_id ON ai_infos(product_id);
CREATE INDEX idx_medical_records_child_id ON medical_records(child_id);
CREATE INDEX idx_diseases_allergies_child_id ON diseases_and_allergies(child_id);
CREATE INDEX idx_growth_records_child_id ON growth_records(child_id);
