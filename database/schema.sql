-- Create database
CREATE DATABASE learning;
USE learning;

-- Create users table
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
);

-- Create courses table with category and PDF fields
CREATE TABLE courses (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  category VARCHAR(100) DEFAULT NULL,
  pdf_file_name VARCHAR(255) DEFAULT NULL,
  pdf_file_url VARCHAR(500) DEFAULT NULL,
  author_id BIGINT NOT NULL,
  FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create course_units table
CREATE TABLE course_units (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  order_index INT NOT NULL,
  course_id BIGINT NOT NULL,
  FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Create enrollments table
CREATE TABLE enrollments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  enrollment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_completed_unit INT NOT NULL DEFAULT 0,
  completed BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
  UNIQUE KEY unique_enrollment (user_id, course_id)
);

-- Create certificates table
CREATE TABLE certificates (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  enrollment_id BIGINT NOT NULL,
  issue_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  certificate_number VARCHAR(255) NOT NULL UNIQUE,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
  FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE,
  UNIQUE KEY unique_cert (user_id, course_id)
);

-- Create indexes for improved query performance
CREATE INDEX idx_courses_author ON courses(author_id);
CREATE INDEX idx_course_units_course ON course_units(course_id);
CREATE INDEX idx_enrollments_user ON enrollments(user_id);
CREATE INDEX idx_enrollments_course ON enrollments(course_id);
CREATE INDEX idx_certificates_user ON certificates(user_id);
CREATE INDEX idx_certificates_course ON certificates(course_id);

-- Sample data insertions

-- Insert sample users
INSERT INTO users (name, email, password) VALUES 
('Admin User', 'admin@example.com', 'password123'),
('John Doe', 'john@example.com', 'password123'),
('Jane Smith', 'jane@example.com', 'password123');

-- Insert sample courses with categories
INSERT INTO courses (title, description, category, author_id) VALUES 
('Introduction to Java Programming', 'Learn the basics of Java programming language including syntax, variables, and control structures.', 'programming', 1),
('Web Development with React', 'Master the React library and build modern, responsive web applications.', 'web', 1),
('Database Design and SQL', 'Learn how to design efficient databases and write powerful SQL queries.', 'database', 2);

-- Insert sample course units
INSERT INTO course_units (title, content, order_index, course_id) VALUES 
-- Java course units
('Getting Started with Java', 'Java is a high-level, class-based, object-oriented programming language. In this unit, we will set up your Java development environment.', 0, 1),
('Variables and Data Types', 'Java has several data types including primitives like int, float, double, boolean, and reference types like String, Array, etc.', 1, 1),
('Control Flow Structures', 'Control flow structures in Java include if-else statements, switch statements, loops (for, while, do-while), and more.', 2, 1),

-- React course units
('React Fundamentals', 'React is a JavaScript library for building user interfaces. Learn about components, props, and state.', 0, 2),
('React Hooks', 'Hooks let you use state and other React features without writing a class.', 1, 2),
('React Router', 'React Router is a collection of navigational components that compose declaratively with your application.', 2, 2),

-- SQL course units
('Database Basics', 'Understand what databases are and how they store information.', 0, 3),
('SQL Queries', 'Learn how to write SELECT, INSERT, UPDATE, and DELETE queries.', 1, 3),
('Database Normalization', 'Database normalization is the process of structuring a database to reduce redundancy and improve data integrity.', 2, 3);

-- Insert sample enrollments
INSERT INTO enrollments (user_id, course_id, enrollment_date, last_completed_unit, completed) VALUES 
(2, 1, '2023-01-15 10:00:00', 2, true),
(2, 2, '2023-02-10 14:30:00', 1, false),
(3, 1, '2023-01-20 09:15:00', 1, false),
(3, 3, '2023-03-05 13:45:00', 3, true);

-- Insert sample certificates
INSERT INTO certificates (user_id, course_id, enrollment_id, issue_date, certificate_number) VALUES 
(2, 1, 1, '2023-02-01 15:30:00', 'CERT-JAVA-001-2023'),
(3, 3, 4, '2023-04-10 11:20:00', 'CERT-SQL-002-2023');
