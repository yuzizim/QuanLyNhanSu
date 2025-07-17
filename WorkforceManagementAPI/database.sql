-- Tạo bảng users
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(250) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('admin', 'employee', 'hr', 'dep_manager') NOT NULL DEFAULT 'employee',
    is_active BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    otp_code VARCHAR(10),
    otp_expiry DATETIME
);

-- Bảng departments (Phòng ban)
CREATE TABLE departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT NULL,
    manager_id INT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_department_code (code)
);

-- Bảng employees (Nhân viên)
CREATE TABLE employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE NULL, -- Allow NULL to permit deletion of users
    employee_code VARCHAR(50) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(155) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NULL,
    department_id INT NULL,
    position VARCHAR(100) NOT NULL,
    hire_date DATE NOT NULL,
    status ENUM('active', 'on_leave', 'terminated') NOT NULL DEFAULT 'active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL, -- Changed to SET NULL
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    INDEX idx_employee_email (email),
    INDEX idx_employee_code (employee_code),
    INDEX idx_employee_status (status)
);

-- Cập nhật khóa ngoại cho departments
ALTER TABLE departments
ADD CONSTRAINT fk_manager
FOREIGN KEY (manager_id) REFERENCES employees(id) ON DELETE SET NULL;

-- Bảng tasks (Công việc)
CREATE TABLE tasks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    start_date DATETIME NULL,
    deadline DATETIME NULL,
    assignee_id INT NULL,
    creator_id INT NULL, -- Allow NULL to permit deletion of employees
    priority ENUM('low', 'medium', 'high', 'urgent') NOT NULL DEFAULT 'medium',
    status ENUM('pending', 'in_progress', 'review', 'completed', 'cancelled') NOT NULL DEFAULT 'pending',
    progress INT NOT NULL DEFAULT 0 CHECK (progress >= 0 AND progress <= 100),
    estimated_hours DECIMAL(6,2) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (assignee_id) REFERENCES employees(id) ON DELETE SET NULL,
    FOREIGN KEY (creator_id) REFERENCES employees(id) ON DELETE SET NULL, -- Changed to SET NULL
    INDEX idx_task_assignee (assignee_id),
    INDEX idx_task_status (status),
    INDEX idx_task_deadline (deadline),
    INDEX idx_task_creator (creator_id)
);

-- Bảng attendance (Chấm công)
CREATE TABLE attendance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NULL, -- Allow NULL to permit deletion of employees
    date DATE NOT NULL,
    check_in_time DATETIME NOT NULL,
    check_out_time DATETIME NULL,
    work_hours DECIMAL(5,2) AS (
        CASE 
            WHEN check_out_time IS NOT NULL 
            THEN TIMESTAMPDIFF(SECOND, check_in_time, check_out_time) / 3600 
            ELSE NULL 
        END
    ) STORED,
    status ENUM('present', 'absent', 'late', 'early_leave') NOT NULL DEFAULT 'present',
    notes TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE SET NULL, -- Changed to SET NULL
    UNIQUE KEY uk_employee_date (employee_id, date),
    INDEX idx_attendance_date (date),
    INDEX idx_attendance_status (status)
);

-- Bảng schedules (Lịch làm việc)
CREATE TABLE schedules (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NULL, -- Allow NULL to permit deletion of employees
    date DATE NOT NULL,
    shift_start TIME NOT NULL,
    shift_end TIME NOT NULL,
    break_duration INT NOT NULL DEFAULT 60 COMMENT 'Break duration in minutes',
    status ENUM('draft', 'confirmed', 'completed') NOT NULL DEFAULT 'draft',
    created_by INT NULL, -- Allow NULL to permit deletion of employees
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE SET NULL, -- Changed to SET NULL
    FOREIGN KEY (created_by) REFERENCES employees(id) ON DELETE SET NULL, -- Changed to SET NULL
    UNIQUE KEY uk_employee_date_schedule (employee_id, date),
    INDEX idx_schedule_date (date),
    INDEX idx_schedule_status (status)
);

-- Bảng evaluations (Đánh giá nhân viên)
CREATE TABLE evaluations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NULL, -- Allow NULL to permit deletion of employees
    evaluator_id INT NULL, -- Allow NULL to permit deletion of employees
    evaluation_date DATE NOT NULL,
    performance_score INT NOT NULL CHECK (performance_score >= 0 AND performance_score <= 100),
    kpi_score INT NOT NULL CHECK (kpi_score >= 0 AND kpi_score <= 100),
    overall_score INT NOT NULL CHECK (overall_score >= 0 AND overall_score <= 100),
    strengths TEXT NULL,
    improvement_areas TEXT NULL,
    comments TEXT NULL,
    status ENUM('draft', 'submitted', 'approved', 'finalized') NOT NULL DEFAULT 'draft',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE SET NULL, -- Changed to SET NULL
    FOREIGN KEY (evaluator_id) REFERENCES employees(id) ON DELETE SET NULL, -- Changed to SET NULL
    INDEX idx_evaluation_date (evaluation_date),
    INDEX idx_evaluation_status (status)
);

-- Bảng notifications (Thông báo)
CREATE TABLE notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NULL, -- Allow NULL to permit deletion of users
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type ENUM('task', 'attendance', 'evaluation', 'announcement', 'system') NOT NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL, -- Changed to SET NULL
    INDEX idx_notification_user (user_id),
    INDEX idx_notification_read (is_read),
    INDEX idx_notification_type (type)
);

-- Thêm các chỉ mục tối ưu truy vấn bổ sung
CREATE INDEX idx_tasks_created_at ON tasks(created_at);
CREATE INDEX idx_employee_department ON employees(department_id);
CREATE INDEX idx_evaluations_employee ON evaluations(employee_id);


--Register users
INSERT INTO users (username, email, password_hash, role, is_active) VALUES
('phamchinh', 'phamchinh.11a1@gmail.com', 'Chinh123@', 'admin', TRUE),
('john_doe', 'john.doe@example.com', 'John123@', 'hr', TRUE),
('jane_smith', 'jane.smith@example.com', 'Jane123@', 'employee', TRUE),
('bob_wilson', 'bob.wilson@example.com', 'Bob123@', 'dep_manager', TRUE);

INSERT INTO departments (name, code, description, manager_id) VALUES
('Human Resources', 'HR01', 'Handles all HR matters', NULL),
('IT Department', 'IT01', 'Technology and Development', NULL),
('Marketing', 'MK01', 'Marketing and PR', NULL);


INSERT INTO employees (user_id, employee_code, first_name, last_name, email, phone, department_id, position, hire_date, status) VALUES
(2, 'EMP002', 'John', 'Doe', 'john.doe@example.com', '0987654321', 1, 'HR Specialist', '2024-02-15', 'active'),
(3, 'EMP003', 'Jane', 'Smith', 'jane.smith@example.com', '0369876543', 2, 'Frontend Developer', '2024-03-01', 'active'),
(4, 'EMP004', 'Bob', 'Wilson', 'bob.wilson@example.com', '0345678912', 3, 'Marketing Manager', '2024-04-20', 'active');

INSERT INTO tasks (title, description, start_date, deadline, assignee_id, creator_id, priority, status, progress, estimated_hours) VALUES
('Develop Login Module', 'Implement user authentication system', '2025-05-20 09:00:00', '2025-06-01 17:00:00', 1, NULL, 'high', 'in_progress', 50, 40.00),
('Plan Marketing Campaign', 'Create strategy for Q3 product launch', '2025-05-22 08:00:00', '2025-06-15 17:00:00', 3, NULL, 'medium', 'pending', 10, 60.00),
('Employee Onboarding', 'Prepare onboarding materials for new hires', '2025-05-25 09:00:00', '2025-06-05 17:00:00', 2, NULL, 'urgent', 'pending', 0, 20.00);

INSERT INTO attendance (employee_id, date, check_in_time, check_out_time, status, notes) VALUES
(2, '2025-05-23', '2025-05-23 08:00:00', '2025-05-23 17:00:00', 'present', 'Full day work'),
(3, '2025-05-23', '2025-05-23 08:30:00', NULL, 'present', 'Still working'),
(1, '2025-05-23', '2025-05-23 09:15:00', '2025-05-23 16:00:00', 'late', 'Arrived late due to traffic');

INSERT INTO schedules (employee_id, date, shift_start, shift_end, break_duration, status, created_by) VALUES
(1, '2025-05-24', '08:00:00', '17:00:00', 60, 'confirmed', 2),
(3, '2025-05-24', '09:00:00', '18:00:00', 60, 'draft', 2),
(2, '2025-05-24', '08:30:00', '16:30:00', 30, 'confirmed', 2);


INSERT INTO evaluations (employee_id, evaluator_id, evaluation_date, performance_score, kpi_score, overall_score, strengths, improvement_areas, comments, status) VALUES
(2, 3, '2025-05-20', 85, 90, 88, 'Strong coding skills', 'Improve time management', 'Great team player', 'submitted'),
(2, 3, '2025-05-21', 78, 80, 79, 'Good communication', 'Enhance technical depth', 'Shows potential', 'approved'),
(3, 1, '2025-05-22', 92, 95, 94, 'Excellent leadership', 'Delegate more', 'Inspires team', 'finalized');




