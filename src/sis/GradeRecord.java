package sis;

public class GradeRecord {

    private String studentUsername;
    private String courseCode;
    private double midterm;
    private double finalExam;

    public GradeRecord(String studentUsername, String courseCode, double midterm, double finalExam) {
        this.studentUsername = studentUsername;
        this.courseCode = courseCode;
        this.midterm = midterm;
        this.finalExam = finalExam;
    }

    public String getStudentUsername() { return studentUsername; }
    public String getCourseCode()      { return courseCode; }
    public double getMidterm()         { return midterm; }
    public double getFinalExam()       { return finalExam; }

    public void setMidterm(double midterm)     { this.midterm = midterm; }
    public void setFinalExam(double finalExam) { this.finalExam = finalExam; }

    // 40% midterm + 60% final
    public double getAverage() {
        return (midterm * 0.4) + (finalExam * 0.6);
    }

    public String getLetterGrade() {
        double avg = getAverage();
        if (avg >= 90) return "AA";
        else if (avg >= 85) return "BA";
        else if (avg >= 80) return "BB";
        else if (avg >= 75) return "CB";
        else if (avg >= 70) return "CC";
        else if (avg >= 65) return "DC";
        else if (avg >= 60) return "DD";
        else if (avg >= 50) return "FD";
        else return "FF";
    }

    public double getGpaPoints() {
        double avg = getAverage();
        if (avg >= 90) return 4.0;
        else if (avg >= 85) return 3.5;
        else if (avg >= 80) return 3.0;
        else if (avg >= 75) return 2.5;
        else if (avg >= 70) return 2.0;
        else if (avg >= 65) return 1.5;
        else if (avg >= 60) return 1.0;
        else if (avg >= 50) return 0.5;
        else return 0.0;
    }

    public String toFileLine() {
        return studentUsername + "|" + courseCode + "|" + midterm + "|" + finalExam;
    }

    public static GradeRecord fromFileLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 4) return null;
        try {
            double mid = Double.parseDouble(parts[2]);
            double fin = Double.parseDouble(parts[3]);
            return new GradeRecord(parts[0], parts[1], mid, fin);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
