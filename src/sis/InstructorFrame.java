package sis;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class InstructorFrame extends JFrame {

    private User currentUser;
    private DataStore ds;

    // my courses tab
    private JTable coursesTable;
    private DefaultTableModel coursesTableModel;

    // grades tab
    private JComboBox<String> cbCourseSelect;
    private JTable gradeTable;
    private DefaultTableModel gradeTableModel;
    private JTextField tfMidterm, tfFinal;
    private JButton btnSaveGrade;

    public InstructorFrame(User user) {
        this.currentUser = user;
        this.ds = DataStore.getInstance();

        setTitle("Instructor Panel - " + user.getFullName());
        setSize(750, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // top
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel lblWelcome = new JLabel("  Welcome, " + user.getFullName() + "  [INSTRUCTOR]");
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
        tabs.addTab("My Courses", buildMyCoursesPanel());
        tabs.addTab("Enter Grades", buildGradesPanel());

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Course Code", "Course Name", "Credits", "Quota", "Enrolled"};
        coursesTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        coursesTable = new JTable(coursesTableModel);
        loadCoursesTable();

        panel.add(new JScrollPane(coursesTable), BorderLayout.CENTER);
        panel.add(new JLabel("  Your courses are listed above."), BorderLayout.SOUTH);
        return panel;
    }

    private void loadCoursesTable() {
        coursesTableModel.setRowCount(0);
        ArrayList<Course> courses = ds.getCoursesByInstructor(currentUser.getUsername());
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            int enrolled = ds.getStudentsOfCourse(c.getCourseCode()).size();
            coursesTableModel.addRow(new Object[]{c.getCourseCode(), c.getCourseName(), c.getCredit(), c.getQuota(), enrolled});
        }
    }

    private JPanel buildGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // top: course selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Course:"));
        cbCourseSelect = new JComboBox<String>();
        ArrayList<Course> courses = ds.getCoursesByInstructor(currentUser.getUsername());
        for (int i = 0; i < courses.size(); i++) {
            cbCourseSelect.addItem(courses.get(i).getCourseCode() + " - " + courses.get(i).getCourseName());
        }
        topPanel.add(cbCourseSelect);

        JButton btnLoad = new JButton("Load Students");
        btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadGradeTable();
            }
        });
        topPanel.add(btnLoad);

        // grade table
        String[] cols = {"Username", "Full Name", "Midterm", "Final", "Average", "Letter Grade"};
        gradeTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        gradeTable = new JTable(gradeTableModel);
        gradeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // when a row is selected, fill the fields
        gradeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int row = gradeTable.getSelectedRow();
                    if (row >= 0) {
                        Object mid = gradeTableModel.getValueAt(row, 2);
                        Object fin = gradeTableModel.getValueAt(row, 3);
                        tfMidterm.setText(mid.equals("-") ? "" : mid.toString());
                        tfFinal.setText(fin.equals("-") ? "" : fin.toString());
                    }
                }
            }
        });

        // bottom: grade entry
        JPanel gradeEntryPanel = new JPanel();
        gradeEntryPanel.setBorder(BorderFactory.createTitledBorder("Enter / Update Grade"));
        gradeEntryPanel.add(new JLabel("Midterm (0-100):"));
        tfMidterm = new JTextField(6);
        gradeEntryPanel.add(tfMidterm);
        gradeEntryPanel.add(new JLabel("Final (0-100):"));
        tfFinal = new JTextField(6);
        gradeEntryPanel.add(tfFinal);
        btnSaveGrade = new JButton("Save Grade");
        btnSaveGrade.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveGradeAction();
            }
        });
        gradeEntryPanel.add(btnSaveGrade);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(gradeTable), BorderLayout.CENTER);
        panel.add(gradeEntryPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadGradeTable() {
        gradeTableModel.setRowCount(0);
        String selected = (String) cbCourseSelect.getSelectedItem();
        if (selected == null) return;
        String code = selected.split(" - ")[0].trim();

        ArrayList<String> students = ds.getStudentsOfCourse(code);
        for (int i = 0; i < students.size(); i++) {
            String uname = students.get(i);
            StudentProfile sp = ds.findStudentByUsername(uname);
            String fullname = sp != null ? sp.getFullName() : uname;
            GradeRecord gr = ds.findGrade(uname, code);
            if (gr != null) {
                gradeTableModel.addRow(new Object[]{
                    uname, fullname,
                    String.format("%.1f", gr.getMidterm()),
                    String.format("%.1f", gr.getFinalExam()),
                    String.format("%.2f", gr.getAverage()),
                    gr.getLetterGrade()
                });
            } else {
                gradeTableModel.addRow(new Object[]{uname, fullname, "-", "-", "-", "-"});
            }
        }
    }

    private void saveGradeAction() {
        int row = gradeTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selected = (String) cbCourseSelect.getSelectedItem();
        if (selected == null) return;
        String code = selected.split(" - ")[0].trim();
        String studentUsername = (String) gradeTableModel.getValueAt(row, 0);

        double mid, fin;
        try {
            mid = Double.parseDouble(tfMidterm.getText().trim());
            fin = Double.parseDouble(tfFinal.getText().trim());
            if (mid < 0 || mid > 100 || fin < 0 || fin > 100) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Grades must be between 0 and 100.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        GradeRecord gr = new GradeRecord(studentUsername, code, mid, fin);
        ds.saveOrUpdateGrade(gr);
        loadGradeTable();
        JOptionPane.showMessageDialog(this, "Grade saved.", "OK", JOptionPane.INFORMATION_MESSAGE);
    }
}
