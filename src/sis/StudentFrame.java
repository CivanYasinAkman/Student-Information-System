package sis;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class StudentFrame extends JFrame {

    private User currentUser;
    private DataStore ds;
    private StudentProfile profile;

    // available courses tab
    private JTable availTable;
    private DefaultTableModel availTableModel;

    // my courses tab
    private JTable myTable;
    private DefaultTableModel myTableModel;

    // transcript tab
    private JTextArea transcriptArea;

    public StudentFrame(User user) {
        this.currentUser = user;
        this.ds = DataStore.getInstance();
        this.profile = ds.findStudentByUsername(user.getUsername());

        setTitle("Student Panel - " + user.getFullName());
        setSize(780, 560);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        String info = "  Welcome, " + user.getFullName();
        if (profile != null) {
            info += "   |   " + profile.getDepartment() + " - Year " + profile.getYear();
        }
        JLabel lblWelcome = new JLabel(info);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 13));
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        topPanel.add(lblWelcome, BorderLayout.WEST);
        topPanel.add(btnLogout, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Available Courses", buildAvailablePanel());
        tabs.addTab("My Courses", buildMyCoursesPanel());
        tabs.addTab("Transcript", buildTranscriptPanel());

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    // ===== AVAILABLE COURSES =====
    private JPanel buildAvailablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Code", "Course Name", "Credits", "Quota", "Enrolled", "Instructor", "Status"};
        availTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        availTable = new JTable(availTableModel);
        availTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loadAvailTable();

        JButton btnEnroll = new JButton("Enroll in Selected Course");
        btnEnroll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enrollAction();
            }
        });

        panel.add(new JScrollPane(availTable), BorderLayout.CENTER);
        panel.add(btnEnroll, BorderLayout.SOUTH);
        return panel;
    }

    private void loadAvailTable() {
        availTableModel.setRowCount(0);
        ArrayList<Course> courses = ds.getCourses();
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            int enrolled = ds.getStudentsOfCourse(c.getCourseCode()).size();
            boolean alreadyEnrolled = ds.isEnrolled(currentUser.getUsername(), c.getCourseCode());
            String status;
            if (alreadyEnrolled) {
                status = "Enrolled";
            } else if (enrolled >= c.getQuota()) {
                status = "Full";
            } else {
                status = "Open";
            }
            User instr = ds.findUser(c.getInstructorUsername());
            String instrName = instr != null ? instr.getFullName() : c.getInstructorUsername();
            availTableModel.addRow(new Object[]{c.getCourseCode(), c.getCourseName(), c.getCredit(), c.getQuota(), enrolled, instrName, status});
        }
    }

    private void enrollAction() {
        int row = availTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String code   = (String) availTableModel.getValueAt(row, 0);
        String status = (String) availTableModel.getValueAt(row, 6);

        if (status.equals("Enrolled")) {
            JOptionPane.showMessageDialog(this, "You are already enrolled in this course.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (status.equals("Full")) {
            JOptionPane.showMessageDialog(this, "This course is full.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = ds.enroll(currentUser.getUsername(), code);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Enrolled in " + code + " successfully!", "OK", JOptionPane.INFORMATION_MESSAGE);
            loadAvailTable();
            loadMyTable();
        } else {
            JOptionPane.showMessageDialog(this, "Enrollment failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== MY COURSES =====
    private JPanel buildMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Code", "Course Name", "Credits", "Instructor", "Midterm", "Final", "Average", "Grade"};
        myTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        myTable = new JTable(myTableModel);
        myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loadMyTable();

        JButton btnDrop = new JButton("Drop Selected Course");
        btnDrop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dropAction();
            }
        });

        panel.add(new JScrollPane(myTable), BorderLayout.CENTER);
        panel.add(btnDrop, BorderLayout.SOUTH);
        return panel;
    }

    private void loadMyTable() {
        myTableModel.setRowCount(0);
        ArrayList<String> codes = ds.getCoursesOfStudent(currentUser.getUsername());
        for (int i = 0; i < codes.size(); i++) {
            String code = codes.get(i);
            Course c = ds.findCourse(code);
            if (c == null) continue;
            User instr = ds.findUser(c.getInstructorUsername());
            String instrName = instr != null ? instr.getFullName() : c.getInstructorUsername();
            GradeRecord gr = ds.findGrade(currentUser.getUsername(), code);
            String mid = "-", fin = "-", avg = "-", letter = "-";
            if (gr != null) {
                mid    = String.format("%.1f", gr.getMidterm());
                fin    = String.format("%.1f", gr.getFinalExam());
                avg    = String.format("%.2f", gr.getAverage());
                letter = gr.getLetterGrade();
            }
            myTableModel.addRow(new Object[]{code, c.getCourseName(), c.getCredit(), instrName, mid, fin, avg, letter});
        }
    }

    private void dropAction() {
        int row = myTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to drop.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String code = (String) myTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Drop course " + code + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            ds.dropEnrollment(currentUser.getUsername(), code);
            loadMyTable();
            loadAvailTable();
        }
    }

    // ===== TRANSCRIPT =====
    private JPanel buildTranscriptPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        transcriptArea = new JTextArea();
        transcriptArea.setEditable(false);
        transcriptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showTranscript();
            }
        });

        showTranscript();

        panel.add(new JScrollPane(transcriptArea), BorderLayout.CENTER);
        panel.add(btnRefresh, BorderLayout.SOUTH);
        return panel;
    }

    private void showTranscript() {
        StringBuilder sb = new StringBuilder();
        sb.append("                                        TRANSCRIPT\n");

        if (profile != null) {
            sb.append("Name       : " + profile.getFullName() + "\n");
            sb.append("Student ID : " + profile.getStudentId() + "\n");
            sb.append("Department : " + profile.getDepartment() + "\n");
            sb.append("Year       : " + profile.getYear() + "\n\n");
        } else {
            sb.append("Name : " + currentUser.getFullName() + "\n\n");
        }

        sb.append(String.format("%-10s %-25s %6s %6s %6s %5s\n", "Code", "Course", "Mid", "Final", "Avg", "Grade"));
        sb.append("---------------------------------------------------------------\n");

        ArrayList<String> codes = ds.getCoursesOfStudent(currentUser.getUsername());
        double totalPoints = 0;
        int totalCredits = 0;

        for (int i = 0; i < codes.size(); i++) {
            String code = codes.get(i);
            Course c = ds.findCourse(code);
            if (c == null) continue;
            GradeRecord gr = ds.findGrade(currentUser.getUsername(), code);
            if (gr != null) {
                sb.append(String.format("%-10s %-25s %6.1f %6.1f %6.2f %5s\n",
                        code, c.getCourseName(), gr.getMidterm(), gr.getFinalExam(), gr.getAverage(), gr.getLetterGrade()));
                totalPoints  += gr.getGpaPoints() * c.getCredit();
                totalCredits += c.getCredit();
            } else {
                sb.append(String.format("%-10s %-25s %6s %6s %6s %5s\n",
                        code, c.getCourseName(), "-", "-", "-", "N/A"));
            }
        }

        sb.append("---------------------------------------------------------------\n");
        sb.append("Total Enrolled Courses : " + codes.size() + "\n");
        if (totalCredits > 0) {
            double gpa = totalPoints / totalCredits;
            sb.append("GPA  : " + String.format("%.2f", gpa) + " / 4.00\n");
        } else {
            sb.append("GPA  : N/A\n");
        }

        transcriptArea.setText(sb.toString());
    }
}
