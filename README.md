# University Information System

A desktop-based University Information System developed with Java Swing.

This project was created as a term project for the Visual Based Programming course.

---

# Features

## Authentication System

* Login system with username and password
* Role-based access control
* Different panels for:

  * Admin
  * Instructor
  * Student

---

# Admin Panel

Admin users can:

* Add new users
* Delete users
* Automatically create student profiles
* Add courses
* Delete courses
* View students
* View reports
* Monitor course enrollments

### Admin Functions

* User management
* Student management
* Course management
* System reports
* Enrollment tracking

---

# Instructor Panel

Instructor users can:

* View their courses
* View enrolled students
* Enter grades
* Update grades
* Monitor course information

### Grade System

* Midterm grade
* Final grade
* Average calculation
* Letter grade calculation

---

# Student Panel

Student users can:

* View available courses
* Enroll in courses
* Drop courses
* View transcript information
* View grades
* Calculate GPA information

---

# Technologies Used

* Java
* Java Swing
* Object-Oriented Programming (OOP)
* File-based data storage (.txt)

---

# Data Storage

The system stores data using text files.

Files used:

```text
users.txt
students.txt
courses.txt
enrollments.txt
grades.txt
```

---

# Project Structure

```text
src/
 └── sis/
      ├── AdminFrame.java
      ├── Course.java
      ├── DataStore.java
      ├── Enrollment.java
      ├── GradeRecord.java
      ├── InstructorFrame.java
      ├── LoginFrame.java
      ├── StudentFrame.java
      ├── StudentProfile.java
      ├── UniversityAutomationApp.java
      └── User.java
```

---

# How to Run

1. Open the project in Eclipse or another Java IDE.
2. Run:

```text
UniversityAutomationApp.java
```

3. Login using an existing account.

---

# Default Admin Account

```text
Username: admin
Password: admin123
```

---

# System Design

The application uses:

* Object-Oriented Design
* Singleton pattern for DataStore
* Swing GUI components
* Role-based navigation

---

# Notes

* Data is automatically saved into text files.
* Course quotas are controlled.
* Related enrollments and grades are removed automatically when necessary.
* Tables are sorted for a cleaner interface.

---

# Author

 Civan Yasin Akman
