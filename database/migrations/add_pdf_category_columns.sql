-- Add new columns to courses table
ALTER TABLE courses 
ADD COLUMN category VARCHAR(100) DEFAULT NULL,
ADD COLUMN pdf_file_name VARCHAR(255) DEFAULT NULL,
ADD COLUMN pdf_file_url VARCHAR(500) DEFAULT NULL;

-- Update existing courses with categories
UPDATE courses SET category = 'programming' WHERE id = 1;
UPDATE courses SET category = 'web' WHERE id = 2;
UPDATE courses SET category = 'database' WHERE id = 3;
