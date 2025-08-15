CREATE DATABASE nigerian_village_family_tree;
USE nigerian_village_family_tree;

-- Nigerian Village Family Tree Database Schema

-- Family Members Table
CREATE TABLE family_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    birth_date DATE,
    death_date DATE,
    village_role VARCHAR(255),
    notes TEXT,
    photo_url VARCHAR(255),
    
    -- Audit columns
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes
    INDEX idx_name (name),
    INDEX idx_gender (gender),
    INDEX idx_birth_date (birth_date),
    INDEX idx_village_role (village_role)
);

-- Family Relationships Table (Parent-Child)
CREATE TABLE family_relationships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT NOT NULL,
    child_id BIGINT NOT NULL,
    relationship_type VARCHAR(50) DEFAULT 'biological', -- biological, adopted, step, etc.
    
    -- Audit columns
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    FOREIGN KEY (parent_id) REFERENCES family_members(id) ON DELETE CASCADE,
    FOREIGN KEY (child_id) REFERENCES family_members(id) ON DELETE CASCADE,
    
    -- Ensure unique relationships
    UNIQUE KEY unique_relationship (parent_id, child_id),
    
    -- Ensure we don't have circular relationships
    CONSTRAINT check_not_self CHECK (parent_id != child_id)
);

-- Marriages Table
CREATE TABLE marriages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    spouse_id BIGINT NOT NULL,
    marriage_date DATE,
    divorce_date DATE,
    
    -- Audit columns
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Constraints
    FOREIGN KEY (member_id) REFERENCES family_members(id) ON DELETE CASCADE,
    FOREIGN KEY (spouse_id) REFERENCES family_members(id) ON DELETE CASCADE,
    
    -- Ensure unique marriages
    UNIQUE KEY unique_marriage (member_id, spouse_id),
    
    -- Ensure we don't have self-marriages
    CONSTRAINT check_not_self_marriage CHECK (member_id != spouse_id)
);

-- Events Table (for important family events beyond births, marriages, deaths)
CREATE TABLE family_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_name VARCHAR(255) NOT NULL,
    event_date DATE,
    event_location VARCHAR(255),
    description TEXT,
    
    -- Audit columns
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes
    INDEX idx_event_name (event_name),
    INDEX idx_event_date (event_date)
);

-- Event Participants (connecting family members to events)
CREATE TABLE event_participants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    role VARCHAR(255), -- Attendee, Host, Honoree, etc.
    
    -- Constraints
    FOREIGN KEY (event_id) REFERENCES family_events(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES family_members(id) ON DELETE CASCADE,
    
    -- Ensure unique participation
    UNIQUE KEY unique_participation (event_id, member_id)
);

-- Additional Information Table (for custom attributes)
CREATE TABLE member_attributes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    attribute_name VARCHAR(255) NOT NULL,
    attribute_value TEXT,
    
    -- Constraints
    FOREIGN KEY (member_id) REFERENCES family_members(id) ON DELETE CASCADE,
    
    -- Ensure unique attributes per member
    UNIQUE KEY unique_attribute (member_id, attribute_name)
);

-- Photo Gallery Table
CREATE TABLE photo_gallery (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    photo_url VARCHAR(255) NOT NULL,
    description TEXT,
    date_taken DATE,
    
    -- Audit columns
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Photo Tags (connecting family members to photos)
CREATE TABLE photo_tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    photo_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    
    -- Constraints
    FOREIGN KEY (photo_id) REFERENCES photo_gallery(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES family_members(id) ON DELETE CASCADE,
    
    -- Ensure unique tags
    UNIQUE KEY unique_tag (photo_id, member_id)
);

-- Family Stories Table
CREATE TABLE family_stories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT,
    publication_date DATE,
    
    -- Audit columns
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Constraints
    FOREIGN KEY (author_id) REFERENCES family_members(id) ON DELETE SET NULL
);

-- Story Subjects (connecting family members to stories)
CREATE TABLE story_subjects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    story_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    
    -- Constraints
    FOREIGN KEY (story_id) REFERENCES family_stories(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES family_members(id) ON DELETE CASCADE,
    
    -- Ensure unique subjects
    UNIQUE KEY unique_subject (story_id, member_id)
);

-- Sample Data Insertion
INSERT INTO family_members (name, gender, birth_date, death_date, village_role, notes)
VALUES 
('Adebayo Olatunji', 'male', '1900-01-15', '1980-07-22', 'Village Elder', 'Founder of the eastern settlement'),
('Folami Olatunji', 'female', '1905-03-10', '1985-11-05', 'Midwife', 'Known for her herbal remedies');

-- Insert parent-child relationship
INSERT INTO family_relationships (parent_id, child_id)
SELECT 
    (SELECT id FROM family_members WHERE name = 'Adebayo Olatunji'),
    (SELECT id FROM family_members WHERE name = 'Oluwasegun Olatunji')
WHERE EXISTS (SELECT 1 FROM family_members WHERE name = 'Oluwasegun Olatunji');

-- Insert marriage
INSERT INTO marriages (member_id, spouse_id)
SELECT 
    (SELECT id FROM family_members WHERE name = 'Adebayo Olatunji'),
    (SELECT id FROM family_members WHERE name = 'Folami Olatunji');