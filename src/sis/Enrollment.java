package sis;

public class Enrollment {
    private String studentUsername;
    private String courseCode;

    public Enrollment(String studentUsername, String courseCode) {
        this.studentUsername = studentUsername;
        this.courseCode      = courseCode;
    }

    public String getStudentUsername() { return studentUsername; }
    public String getCourseCode()      { return courseCode; }

    public String toFileLine() {
        return studentUsername + "|" + courseCode;
    }

    public static Enrollment fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 2) return null;
        return new Enrollment(p[0], p[1]);
    }

    @Override
    public String toString() { return studentUsername + " -> " + courseCode; }
}
