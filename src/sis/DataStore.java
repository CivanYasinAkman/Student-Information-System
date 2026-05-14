package sis;

import java.io.*;
import java.util.ArrayList;

public class DataStore {

    private static final String USERS_FILE      = "data/users.txt";
    private static final String STUDENTS_FILE   = "data/students.txt";
    private static final String COURSES_FILE    = "data/courses.txt";
    private static final String ENROLLMENTS_FILE= "data/enrollments.txt";
    private static final String GRADES_FILE     = "data/grades.txt";

    private ArrayList<User>          userList       = new ArrayList<User>();
    private ArrayList<StudentProfile>studentList    = new ArrayList<StudentProfile>();
    private ArrayList<Course>        courseList     = new ArrayList<Course>();
    private ArrayList<Enrollment>    enrollmentList = new ArrayList<Enrollment>();
    private ArrayList<GradeRecord>   gradeList      = new ArrayList<GradeRecord>();

    private static DataStore instance = null;

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    private DataStore() {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdir(); }
        loadAll();
        
        if (userList.isEmpty()) {
            userList.add(new User("admin", "admin123", "ADMIN", "System Admin", ""));
            saveUsers();
        }
    }

    private void loadAll() {
        loadUsers();
        loadStudents();
        loadCourses();
        loadEnrollments();
        loadGrades();
    }
    
    // ---- LOAD METHODS ----

    private void loadUsers() {
        userList.clear();
        try {
            BufferedReader br = new BufferedReader(new FileReader(USERS_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("")) {
                    User u = User.fromFileLine(line);
                    if (u != null) userList.add(u);
                }
            }
            br.close();
        } catch (IOException e) {
        }
    }
    private void loadStudents() {
        studentList.clear();
        try {
            BufferedReader br = new BufferedReader(new FileReader(STUDENTS_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("")) {
                    StudentProfile s = StudentProfile.fromFileLine(line);
                    if (s != null) studentList.add(s);
                }
            }
            br.close();
        } catch (IOException e) {
        }
    }

    private void loadCourses() {
        courseList.clear();
        try {
            BufferedReader br = new BufferedReader(new FileReader(COURSES_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("")) {
                    Course c = Course.fromFileLine(line);
                    if (c != null) courseList.add(c);
                }
            }
            br.close();
        } catch (IOException e) {
        }
    }
    private void loadEnrollments() {
        enrollmentList.clear();
        try {
            BufferedReader br = new BufferedReader(new FileReader(ENROLLMENTS_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("")) {
                    Enrollment en = Enrollment.fromFileLine(line);
                    if (en != null) enrollmentList.add(en);
                }
            }
            br.close();
        } catch (IOException e) {
        }
    }
    private void loadGrades() {
        gradeList.clear();
        try {
            BufferedReader br = new BufferedReader(new FileReader(GRADES_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("")) {
                    GradeRecord g = GradeRecord.fromFileLine(line);
                    if (g != null) gradeList.add(g);
                }
            }
            br.close();
        } catch (IOException e) {
        }
    }
    
    // ---- SAVE METHODS ----

    public void saveUsers() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE));
            for (int i = 0; i < userList.size(); i++) {
                pw.println(userList.get(i).toFileLine());
            }
            pw.close();
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }
    public void saveStudents() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(STUDENTS_FILE));
            for (int i = 0; i < studentList.size(); i++) {
                pw.println(studentList.get(i).toFileLine());
            }
            pw.close();
        } catch (IOException e) {
            System.out.println("Error saving students: " + e.getMessage());
        }
    }
    public void saveCourses() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(COURSES_FILE));
            for (int i = 0; i < courseList.size(); i++) {
                pw.println(courseList.get(i).toFileLine());
            }
            pw.close();
        } catch (IOException e) {
            System.out.println("Error saving courses: " + e.getMessage());
        }
    }
    public void saveEnrollments() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(ENROLLMENTS_FILE));
            for (int i = 0; i < enrollmentList.size(); i++) {
                pw.println(enrollmentList.get(i).toFileLine());
            }
            pw.close();
        } catch (IOException e) {
            System.out.println("Error saving enrollments: " + e.getMessage());
        }
    }
    public void saveGrades() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(GRADES_FILE));
            for (int i = 0; i < gradeList.size(); i++) {
                pw.println(gradeList.get(i).toFileLine());
            }
            pw.close();
        } catch (IOException e) {
            System.out.println("Error saving grades: " + e.getMessage());
        }
    }
    
    // ---- USER OPERATIONS ----

    public ArrayList<User> getUsers() {
        return userList;
    }
    
    public User findUser(String username) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUsername().equals(username)) {
                return userList.get(i);
            }
        }
        return null;
    }

    public User authenticate(String username, String password) {
        User u = findUser(username);
        if (u != null && u.getPassword().equals(password)) {
            return u;
        }
        return null;
    }

    public boolean addUser(User u) {
        if (findUser(u.getUsername()) != null) {
            return false; 
        }
        userList.add(u);
        saveUsers();
        return true;
    }

    public void deleteUser(String username) {
        User deletedUser = findUser(username);
      if (deletedUser == null) {
            return;
        }
        String role = deletedUser.getRole();
        
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUsername().equals(username)) {
                userList.remove(i);
                break;
            }
        }

        // IF USER IS INSTRUCTOR
        
        if (role.equals("INSTRUCTOR")) {
            for (int i = courseList.size() - 1; i >= 0; i--) {
                Course c = courseList.get(i);
                if (c.getInstructorUsername().equals(username)) {
                	String courseCode = c.getCourseCode();

                    for (int j = enrollmentList.size() - 1; j >= 0; j--) {
                        if (enrollmentList.get(j).getCourseCode().equals(courseCode)) {
                            enrollmentList.remove(j);
                        }
                    }

                    for (int j = gradeList.size() - 1; j >= 0; j--) {
                        if (gradeList.get(j).getCourseCode().equals(courseCode)) {
                            gradeList.remove(j);
                        }
                    }
                    courseList.remove(i);
                }
            }
        }

        // IF USER IS STUDENT

        else if (role.equals("STUDENT")) {
            for (int i = studentList.size() - 1; i >= 0; i--) {
                if (studentList.get(i).getUsername().equals(username)) {
                    studentList.remove(i);
                }
            }

            for (int i = enrollmentList.size() - 1; i >= 0; i--) {
                if (enrollmentList.get(i).getStudentUsername().equals(username)) {
                    enrollmentList.remove(i);
                }
            }

            for (int i = gradeList.size() - 1; i >= 0; i--) {
                if (gradeList.get(i).getStudentUsername().equals(username)) {
                    gradeList.remove(i);
                }
            }
        }
        saveUsers();
        saveStudents();
        saveCourses();
        saveEnrollments();
        saveGrades();
    }

    public ArrayList<User> getUsersByRole(String role) {
        ArrayList<User> result = new ArrayList<User>();
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getRole().equals(role)) {
                result.add(userList.get(i));
            }
        }
        return result;
    }

    // ---- STUDENT OPERATIONS ----

    public ArrayList<StudentProfile> getStudents() {
        return studentList;
    }

    public StudentProfile findStudentByUsername(String username) {
        for (int i = 0; i < studentList.size(); i++) {
            if (studentList.get(i).getUsername().equals(username)) {
                return studentList.get(i);
            }
        }
        return null;
    }

    public StudentProfile findStudentById(String id) {
        for (int i = 0; i < studentList.size(); i++) {
            if (studentList.get(i).getStudentId().equals(id)) {
                return studentList.get(i);
            }
        }
        return null;
    }

    public boolean addStudent(StudentProfile sp) {
        if (findStudentById(sp.getStudentId()) != null) {
            return false;
        }
        studentList.add(sp);
        saveStudents();
        return true;
    }

    public void deleteStudent(String studentId) {
        for (int i = 0; i < studentList.size(); i++) {
            if (studentList.get(i).getStudentId().equals(studentId)) {
                studentList.remove(i);
                break;
            }
        }
        saveStudents();
    }

    // ---- COURSE OPERATIONS ----

    public ArrayList<Course> getCourses() {
        return courseList;
    }

    public Course findCourse(String code) {
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).getCourseCode().equals(code)) {
                return courseList.get(i);
            }
        }
        return null;
    }

    public boolean addCourse(Course c) {
        if (findCourse(c.getCourseCode()) != null) {
            return false;
        }
        courseList.add(c);
        saveCourses();
        return true;
    }

    public void deleteCourse(String code) {

        for (int i = 0; i < courseList.size(); i++) {

            if (courseList.get(i).getCourseCode().equals(code)) {

                courseList.remove(i);
                break;
            }
        }

        for (int i = enrollmentList.size() - 1; i >= 0; i--) {

            if (enrollmentList.get(i).getCourseCode().equals(code)) {

                enrollmentList.remove(i);
            }
        }

        for (int i = gradeList.size() - 1; i >= 0; i--) {

            if (gradeList.get(i).getCourseCode().equals(code)) {

                gradeList.remove(i);
            }
        }
        saveCourses();
        saveEnrollments();
        saveGrades();
    }

    public ArrayList<Course> getCoursesByInstructor(String instrUsername) {
        ArrayList<Course> result = new ArrayList<Course>();
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).getInstructorUsername().equals(instrUsername)) {
                result.add(courseList.get(i));
            }
        }
        return result;
    }

    // ---- ENROLLMENT OPERATIONS ----

    public ArrayList<Enrollment> getEnrollments() {
        return enrollmentList;
    }

    public boolean isEnrolled(String studentUsername, String courseCode) {
        for (int i = 0; i < enrollmentList.size(); i++) {
            Enrollment e = enrollmentList.get(i);
            if (e.getStudentUsername().equals(studentUsername) && e.getCourseCode().equals(courseCode)) {
                return true;
            }
        }
        return false;
    }

    public boolean enroll(String studentUsername, String courseCode) {
        if (isEnrolled(studentUsername, courseCode)) {
            return false;
        }
        Course c = findCourse(courseCode);
        if (c != null) {
            int count = 0;
            for (int i = 0; i < enrollmentList.size(); i++) {
                if (enrollmentList.get(i).getCourseCode().equals(courseCode)) {
                    count++;
                }
            }
            if (count >= c.getQuota()) {
                return false;
            }
        }
        enrollmentList.add(new Enrollment(studentUsername, courseCode));
        saveEnrollments();
        return true;
    }

    public void dropEnrollment(String studentUsername, String courseCode) {

        for (int i = enrollmentList.size() - 1; i >= 0; i--) {

            Enrollment e = enrollmentList.get(i);

            if (e.getStudentUsername().equals(studentUsername)
                    && e.getCourseCode().equals(courseCode)) {

                enrollmentList.remove(i);
            }
        }

        for (int i = gradeList.size() - 1; i >= 0; i--) {
            GradeRecord g = gradeList.get(i);
            if (g.getStudentUsername().equals(studentUsername)
                    && g.getCourseCode().equals(courseCode)) {
                gradeList.remove(i);
            }
        }
        saveEnrollments();
        saveGrades();
    }

    public ArrayList<String> getCoursesOfStudent(String studentUsername) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < enrollmentList.size(); i++) {
            if (enrollmentList.get(i).getStudentUsername().equals(studentUsername)) {
                result.add(enrollmentList.get(i).getCourseCode());
            }
        }
        return result;
    }

    public ArrayList<String> getStudentsOfCourse(String courseCode) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < enrollmentList.size(); i++) {
            if (enrollmentList.get(i).getCourseCode().equals(courseCode)) {
                result.add(enrollmentList.get(i).getStudentUsername());
            }
        }
        return result;
    }

    // ---- GRADE OPERATIONS ----

    public GradeRecord findGrade(String studentUsername, String courseCode) {
        for (int i = 0; i < gradeList.size(); i++) {
            GradeRecord g = gradeList.get(i);
            if (g.getStudentUsername().equals(studentUsername) && g.getCourseCode().equals(courseCode)) {
                return g;
            }
        }
        return null;
    }

    public void saveOrUpdateGrade(GradeRecord record) {
        GradeRecord existing = findGrade(record.getStudentUsername(), record.getCourseCode());
        if (existing != null) {
            existing.setMidterm(record.getMidterm());
            existing.setFinalExam(record.getFinalExam());
        } else {
            gradeList.add(record);
        }
        saveGrades();
    }

    public ArrayList<GradeRecord> getGradesOfStudent(String studentUsername) {
        ArrayList<GradeRecord> result = new ArrayList<GradeRecord>();
        for (int i = 0; i < gradeList.size(); i++) {
            if (gradeList.get(i).getStudentUsername().equals(studentUsername)) {
                result.add(gradeList.get(i));
            }
        }
        return result;
    }
}
