package sis;

public class StudentProfile {
    private String studentId;
    private String fullName;
    private String department;
    private int year;
    private String username;

    public StudentProfile(String studentId, String fullName, String department, int year, String username) {
        this.studentId  = studentId;
        this.fullName   = fullName;
        this.department = department;
        this.year       = year;
        this.username   = username;
    }

    public String getStudentId()   { return studentId; }
    public String getFullName()    { return fullName; }
    public String getDepartment()  { return department; }
    public int    getYear()        { return year; }
    public String getUsername()    { return username; }

    public void setFullName(String fullName)       { this.fullName = fullName; }
    public void setDepartment(String department)   { this.department = department; }
    public void setYear(int year)                  { this.year = year; }

    public String toFileLine() {
        return studentId + "|" + fullName + "|" + department + "|" + year + "|" + username;
    }

    public static StudentProfile fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 5) return null;
        try {
            return new StudentProfile(p[0], p[1], p[2], Integer.parseInt(p[3]), p[4]);
        } catch (NumberFormatException e) { return null; }
    }

    @Override
    public String toString() { return fullName + " [" + studentId + "]"; }
}
