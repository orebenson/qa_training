-- create database classdb;

use classdb;

-- table declarations

drop table if exists marks;
drop table if exists course;
drop table if exists students;

CREATE TABLE course (
course_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
course_name VARCHAR(255) NOT NULL,
teacher VARCHAR(255) NOT NULL
);

CREATE TABLE students (
student_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
student_name VARCHAR(255) NOT NULL,
date_of_birth DATE
);

CREATE TABLE marks (
mark_id INT  NOT NULL AUTO_INCREMENT PRIMARY KEY,
student_id INT NOT NULL,
course_id INT NOT NULL,
marks INT,
FOREIGN KEY (student_id) REFERENCES students(student_id),
FOREIGN KEY (course_id) REFERENCES course(course_id)
);

-- insert statements

insert into course (course_name, teacher) 
values 
('MYSQL-101', 'Courage'),
('Java-101', 'Jordan')
;

insert into students (student_name, date_of_birth) 
values 
('Ore Benson', '2001-01-23')
;

insert into marks (student_id, course_id, marks)
values
(1,1,100),
(1,2,100)
;

-- select statements

SELECT 
    t.student_id, 
    t.student_name, 
    t.date_of_birth,
    c.course_id,
    c.course_name, 
    c.teacher
FROM course c 
JOIN (
    SELECT 
        s.student_id, 
        m.course_id, 
        s.student_name, 
        s.date_of_birth
    FROM students s 
    JOIN marks m ON s.student_id = m.student_id
) t ON c.course_id = t.course_id;

