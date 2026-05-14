package sis;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class AdminFrame extends JFrame {

    private User currentUser;
    private DataStore ds;

    // user tab
    private JTable userTable;
    private DefaultTableModel userTableModel;

    private JTextField tfNewUsername;
    private JTextField tfNewFullname;
    private JTextField tfNewRefid;

    private JTextField tfStuDeptUser;
    private JTextField tfStuYearUser;
    
    private JLabel lblDept;
    private JLabel lblYear;

    private JPasswordField pfNewPassword;
    private JComboBox<String> cbRole;

    // student tab
    private JTable stuTable;
    private DefaultTableModel stuTableModel;

    // course tab
    private JTable crsTable;
    private DefaultTableModel crsTableModel;
    private JTextField tfCrsCode, tfCrsName, tfCrsCredit, tfCrsQuota, tfCrsInstr;

    // report tab
    private JTextArea reportArea;

    public AdminFrame(User user) {

        this.currentUser = user;
        this.ds = DataStore.getInstance();

        setTitle("Admin Panel - " + user.getFullName());
        setSize(900, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel lblWelcome =
                new JLabel("  Welcome, " + user.getFullName() + " [ADMIN]");

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

        tabs.addTab("Users", buildUsersPanel());
        tabs.addTab("Students", buildStudentsPanel());
        tabs.addTab("Courses", buildCoursesPanel());
        tabs.addTab("Reports", buildReportsPanel());

        setLayout(new BorderLayout());

        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    // =========================================================
    // USERS PANEL
    // =========================================================

    private JPanel buildUsersPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Username","Full Name","Role","Ref ID"};

        userTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        userTable = new JTable(userTableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadUserTable();
        JScrollPane scrollPane = new JScrollPane(userTable);

        JPanel formPanel = new JPanel();

        formPanel.setBorder(
                BorderFactory.createTitledBorder("Create New User")
        );

        formPanel.setLayout(new GridLayout(5, 4, 5, 5));

        formPanel.add(new JLabel("Username:"));
        tfNewUsername = new JTextField();
        formPanel.add(tfNewUsername);

        formPanel.add(new JLabel("Password:"));
        pfNewPassword = new JPasswordField();
        formPanel.add(pfNewPassword);

        formPanel.add(new JLabel("Full Name:"));
        tfNewFullname = new JTextField();
        formPanel.add(tfNewFullname);

        formPanel.add(new JLabel("Role:"));
        cbRole = new JComboBox<String>(
                new String[]{"ADMIN", "INSTRUCTOR", "STUDENT"}
        );

        formPanel.add(cbRole);
        
        cbRole.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean isStudent = cbRole.getSelectedItem().equals("STUDENT");
                lblDept.setVisible(isStudent);
                tfStuDeptUser.setVisible(isStudent);
                lblYear.setVisible(isStudent);
                tfStuYearUser.setVisible(isStudent);
            }
        });

        formPanel.add(new JLabel("Reference ID:"));
        tfNewRefid = new JTextField();
        formPanel.add(tfNewRefid);

        lblDept = new JLabel("Department:");
        formPanel.add(lblDept);
        tfStuDeptUser = new JTextField();
        formPanel.add(tfStuDeptUser);

        lblYear = new JLabel("Year:");
        formPanel.add(lblYear);
        tfStuYearUser = new JTextField();
        formPanel.add(tfStuYearUser);

        JButton btnAdd = new JButton("Add User");

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addUserAction();
            }
        });

        JButton btnDelete = new JButton("Delete Selected");

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteUserAction();
            }
        });

        formPanel.add(btnAdd);
        formPanel.add(btnDelete);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        boolean startAsStudent = cbRole.getSelectedItem().equals("STUDENT");
        lblDept.setVisible(startAsStudent);
        tfStuDeptUser.setVisible(startAsStudent);
        lblYear.setVisible(startAsStudent);
        tfStuYearUser.setVisible(startAsStudent);
        return panel;
    }

    private void loadUserTable() {

        userTableModel.setRowCount(0);

        ArrayList<User> users = ds.getUsers();

        users.sort(new java.util.Comparator<User>() {
            public int compare(User u1, User u2) {

                if (u1.getRole().equals("ADMIN")) return -1;
                if (u2.getRole().equals("ADMIN")) return 1;

                String r1 = u1.getReferenceId();
                String r2 = u2.getReferenceId();

                try {
                    int n1 = Integer.parseInt(r1);
                    int n2 = Integer.parseInt(r2);
                    return Integer.compare(n1, n2);
                } catch (Exception e) {
                    return r1.compareTo(r2);
                }
            }
        });

        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            userTableModel.addRow(new Object[]{
                    u.getUsername(),
                    u.getFullName(),
                    u.getRole(),
                    u.getReferenceId()
            });
        }
    }
    private void addUserAction() {

        String username = tfNewUsername.getText().trim();
        String password = new String(pfNewPassword.getPassword());
        String fullname = tfNewFullname.getText().trim();
        String role = (String) cbRole.getSelectedItem();
        String refid = tfNewRefid.getText().trim();

        if (username.equals("") || password.equals("") ||  fullname.equals("")) {
            JOptionPane.showMessageDialog(this,"Username, password and full name cannot be empty.",
                    "Error",JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(
                    this, "Password must be at least 4 characters.",
                    "Error",JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        String dept = "";
        int year = 1;
        if (role.equals("STUDENT")) {
            dept = tfStuDeptUser.getText().trim();
            String yearText = tfStuYearUser.getText().trim();

         if (refid.equals("") ||  dept.equals("") ||   yearText.equals("")) {
            JOptionPane.showMessageDialog(
                        this,
                        "Student ID, Department and Year are required for students.",
                        "Error",JOptionPane.ERROR_MESSAGE
                );
            return;
            }
         
            try {
                year = Integer.parseInt(yearText);
                if (year < 1 || year > 6) {
                    throw new NumberFormatException();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Year must be between 1 and 6.",
                        "Error", JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }
        
        User newUser =
                new User(username, password, role, fullname, refid);
        boolean added = ds.addUser(newUser);
        if (!added) {
            JOptionPane.showMessageDialog(
                    this, "Username already exists!",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (role.equals("STUDENT")) {
            StudentProfile sp =
                    new StudentProfile (refid, fullname, dept, year, username);
            boolean studentAdded = ds.addStudent(sp);

            if (!studentAdded) {
               ds.deleteUser(username);
                JOptionPane.showMessageDialog(
                        this,
                        "Student ID already exists!",
                        "Error",  JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }

        loadUserTable();
        loadStuTable();

        tfNewUsername.setText("");
        pfNewPassword.setText("");
        tfNewFullname.setText("");
        tfNewRefid.setText("");
        tfStuDeptUser.setText("");
        tfStuYearUser.setText("");

        JOptionPane.showMessageDialog(
                this,
                "User added successfully.",
                "OK",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void deleteUserAction() {

        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a user.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String username =
                (String) userTableModel.getValueAt(row, 0);
        if (username.equals(currentUser.getUsername())) {
            JOptionPane.showMessageDialog(
                    this,
                    "You cannot delete yourself.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete user: " + username + "?",
                "Confirm", JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            ds.deleteUser(username);
            loadUserTable();
        }
    }

    // STUDENTS PANEL

    private JPanel buildStudentsPanel() {
    	
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Student ID","Full Name", "Department", "Year", "Username" };

        stuTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        stuTable = new JTable(stuTableModel);
        stuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadStuTable();
        JScrollPane scrollPane = new JScrollPane(stuTable);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDelete = new JButton("Delete Selected Student");

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteStudentAction();
            }
        });

        bottomPanel.add(btnDelete);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadStuTable() {

        stuTableModel.setRowCount(0);

        ArrayList<StudentProfile> students = ds.getStudents();
        
        students.sort(new java.util.Comparator<StudentProfile>() {
            public int compare(StudentProfile s1, StudentProfile s2) {

                try {
                    int n1 = Integer.parseInt(s1.getStudentId());
                    int n2 = Integer.parseInt(s2.getStudentId());

                    return Integer.compare(n1, n2);

                } catch (Exception e) {

                    return s1.getStudentId().compareTo(s2.getStudentId());
                }
            }
        });

        for (int i = 0; i < students.size(); i++) {

            StudentProfile s = students.get(i);

            stuTableModel.addRow(new Object[]{s.getStudentId(), s.getFullName(), s.getDepartment(), s.getYear(), s.getUsername() });
        }
    }
    
    private void deleteStudentAction() {

        int row = stuTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a student.",
                    "Error",JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String sid =  (String) stuTableModel.getValueAt(row, 0);
        String username = (String) stuTableModel.getValueAt(row, 4);

        if (username.equals(currentUser.getUsername())) {
            JOptionPane.showMessageDialog(
                    this,
                    "You cannot delete yourself.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete student " + sid + " ?",
                "Confirm", JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            ds.deleteUser(username);

            loadStuTable();
            loadUserTable();
            loadCrsTable();
            JOptionPane.showMessageDialog(
                    this,
                    "Student deleted successfully.",
                    "OK",  JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // COURSES PANEL

    private JPanel buildCoursesPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = { "Code", "Name", "Credits", "Quota", "Enrolled", "Instructor" };
        crsTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        crsTable = new JTable(crsTableModel);
        crsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadCrsTable();

        JScrollPane scrollPane = new JScrollPane(crsTable);

        JPanel formPanel = new JPanel();

        formPanel.setBorder( BorderFactory.createTitledBorder("Add New Course") );
        formPanel.setLayout(new GridLayout(3, 4, 5, 5));
        
        formPanel.add(new JLabel("Course Code:"));
        tfCrsCode = new JTextField();
        formPanel.add(tfCrsCode);
        
        formPanel.add(new JLabel("Course Name:"));
        tfCrsName = new JTextField();
        formPanel.add(tfCrsName);
        
        formPanel.add(new JLabel("Credits:"));
        tfCrsCredit = new JTextField();
        formPanel.add(tfCrsCredit);
        
        formPanel.add(new JLabel("Quota:"));
        tfCrsQuota = new JTextField();
        formPanel.add(tfCrsQuota);

        formPanel.add(new JLabel("Instructor Username:"));
        tfCrsInstr = new JTextField();
        formPanel.add(tfCrsInstr);

        JButton btnAdd = new JButton("Add Course");

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addCourseAction();
            }
        });

        JButton btnDelete = new JButton("Delete Selected");

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteCourseAction();
            }
        });

        formPanel.add(btnAdd);
        formPanel.add(btnDelete);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadCrsTable() {

        crsTableModel.setRowCount(0);
        ArrayList<Course> courses = ds.getCourses();

        courses.sort(new java.util.Comparator<Course>() {
            public int compare(Course c1, Course c2) {
                return c1.getCourseCode().compareTo(c2.getCourseCode());
            }
        });
        
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            int enrolledCount =
                    ds.getStudentsOfCourse(c.getCourseCode()).size();

            crsTableModel.addRow(new Object[]{ c.getCourseCode(), c.getCourseName(), c.getCredit(), c.getQuota(), enrolledCount, c.getInstructorUsername() });
        }
    }

    private void addCourseAction() {

        String code = tfCrsCode.getText().trim().toUpperCase();
        String name = tfCrsName.getText().trim();
        String cred = tfCrsCredit.getText().trim();
        String quota = tfCrsQuota.getText().trim();
        String instr = tfCrsInstr.getText().trim();

        if (code.equals("") || name.equals("") || cred.equals("") || quota.equals("") || instr.equals("")) {
              JOptionPane.showMessageDialog(
                    this,
                    "All fields are required.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int cr, qt;
        try {
            cr = Integer.parseInt(cred);
            qt = Integer.parseInt(quota);
            if (cr < 1 || qt < 1) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Credits and quota must be positive integers.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        User instrUser = ds.findUser(instr);

        if (instrUser == null || !instrUser.getRole().equals("INSTRUCTOR")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Instructor username not found or not an INSTRUCTOR.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Course c =
                new Course(code, name, cr, qt, instr);
        boolean added = ds.addCourse(c);
        if (!added) {
            JOptionPane.showMessageDialog(
                    this,
                    "Course code already exists!",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        loadCrsTable();

        tfCrsCode.setText("");
        tfCrsName.setText("");
        tfCrsCredit.setText("");
        tfCrsQuota.setText("");
        tfCrsInstr.setText("");
        JOptionPane.showMessageDialog(
                this,
                "Course added.",
                "OK", JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void deleteCourseAction() {

        int row = crsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a course.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String code =
                (String) crsTableModel.getValueAt(row, 0);
         int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete course " + code + "?",
                "Confirm", JOptionPane.YES_NO_OPTION
        );
         
        if (confirm == JOptionPane.YES_OPTION) {
            ds.deleteCourse(code);
            loadCrsTable();
        }
    }

    // REPORTS PANEL

    private JPanel buildReportsPanel() {

        JPanel panel = new JPanel(new BorderLayout());
        
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(
                new Font("Monospaced", Font.PLAIN, 12)
        );

        JScrollPane scrollPane = new JScrollPane(reportArea);
        JButton btnRefresh = new JButton("Refresh");

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showReport();
            }
        });

        showReport();

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnRefresh, BorderLayout.SOUTH);
        return panel;
    }

    private void showReport() {

        StringBuilder sb = new StringBuilder();
        
        sb.append("=== SYSTEM REPORT ===\n\n");       
        sb.append("Total Users     : " +  ds.getUsers().size() + "\n");
        sb.append("  Admins        : " +  ds.getUsersByRole("ADMIN").size() + "\n");
        sb.append("  Instructors   : " +  ds.getUsersByRole("INSTRUCTOR").size() + "\n");
        sb.append("  Students      : " +  ds.getUsersByRole("STUDENT").size() + "\n\n");
        sb.append("Total Students  : " +  ds.getStudents().size() + "\n");
        sb.append("Total Courses   : " +  ds.getCourses().size() + "\n\n");
        sb.append("--- COURSE ENROLLMENT ---\n");

        ArrayList<Course> courses = ds.getCourses();
        for (int i = 0; i < courses.size(); i++) {

            Course c = courses.get(i);
            int count =
                    ds.getStudentsOfCourse( c.getCourseCode()).size();
            
            sb.append( c.getCourseCode()+ " - " + c.getCourseName() + " : " + count + "/" + c.getQuota() + "\n"
            );
        }

        sb.append("\nTotal Grade Entries: " +
                ds.getEnrollments().size() + "\n");
        reportArea.setText(sb.toString());
    }
}