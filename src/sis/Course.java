package sis;

public class Course {
    private String courseCode;
    private String courseName;
    private int    credit;
    private int    quota;
    private String instructorUsername;

    public Course(String courseCode, String courseName, int credit, int quota, String instructorUsername) {
        this.courseCode         = courseCode;
        this.courseName         = courseName;
        this.credit             = credit;
        this.quota              = quota;
        this.instructorUsername = instructorUsername;
    }

    public String getCourseCode()          { return courseCode; }
    public String getCourseName()          { return courseName; }
    public int    getCredit()              { return credit; }
    public int    getQuota()               { return quota; }
    public String getInstructorUsername()  { return instructorUsername; }

    public void setCourseName(String courseName)               { this.courseName = courseName; }
    public void setCredit(int credit)                          { this.credit = credit; }
    public void setQuota(int quota)                            { this.quota = quota; }
    public void setInstructorUsername(String instructorUsername){ this.instructorUsername = instructorUsername; }

    public String toFileLine() {
        return courseCode + "|" + courseName + "|" + credit + "|" + quota + "|" + instructorUsername;
    }

    public static Course fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 5) return null;
        try {
            return new Course(p[0], p[1], Integer.parseInt(p[2]), Integer.parseInt(p[3]), p[4]);
        } catch (NumberFormatException e) { return null; }
    }

    @Override
    public String toString() { return courseCode + " - " + courseName + " (" + credit + " cr)"; }
}
