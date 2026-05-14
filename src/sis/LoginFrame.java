package sis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;
    private JLabel lblMessage;

    public LoginFrame() {
        setTitle("Login");
        setSize(350, 220);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblTitle = new JLabel("University Information System");
        lblTitle.setBounds(50, 10, 280, 25);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(30, 50, 90, 25);

        tfUsername = new JTextField();
        tfUsername.setBounds(130, 50, 170, 25);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(30, 90, 90, 25);

        pfPassword = new JPasswordField();
        pfPassword.setBounds(130, 90, 170, 25);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(130, 130, 90, 28);

        lblMessage = new JLabel("");
        lblMessage.setBounds(30, 165, 290, 20);
        lblMessage.setForeground(Color.RED);
        lblMessage.setFont(new Font("Arial", Font.PLAIN, 11));

        add(lblTitle);
        add(lblUser);
        add(tfUsername);
        add(lblPass);
        add(pfPassword);
        add(btnLogin);
        add(lblMessage);

        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });

        pfPassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });
    }

    private void doLogin() {
        String username = tfUsername.getText().trim();
        String password = new String(pfPassword.getPassword());

        if (username.equals("") || password.equals("")) {
            lblMessage.setText("Please enter username and password.");
            return;
        }

        User user = DataStore.getInstance().authenticate(username, password);

        if (user == null) {
            lblMessage.setText("Wrong username or password!");
            pfPassword.setText("");
            return;
        }

        dispose();

        if (user.getRole().equals("ADMIN")) {
            AdminFrame af = new AdminFrame(user);
            af.setVisible(true);
        } else if (user.getRole().equals("INSTRUCTOR")) {
            InstructorFrame inf = new InstructorFrame(user);
            inf.setVisible(true);
        } else if (user.getRole().equals("STUDENT")) {
            StudentFrame sf = new StudentFrame(user);
            sf.setVisible(true);
        }
    }
}
