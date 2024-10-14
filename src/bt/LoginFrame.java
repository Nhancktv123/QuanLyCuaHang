package bt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnExit;

    public LoginFrame() {
        setTitle("Đăng Nhập");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Username
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(30, 30, 80, 25);  
        add(lblUsername);

        txtUsername = new JTextField(20);
        txtUsername.setBounds(120, 30, 160, 25);  
        add(txtUsername);

        // Password
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(30, 70, 80, 25);  
        add(lblPassword);

        txtPassword = new JPasswordField(20);
        txtPassword.setBounds(120, 70, 160, 25);  
        add(txtPassword);

        // Buttons
        btnLogin = new JButton("Đăng Nhập");
        btnLogin.setBounds(70, 120, 100, 30);  
        add(btnLogin);

        btnExit = new JButton("Thoát");
        btnExit.setBounds(180, 120, 100, 30);  
        add(btnExit);

        // Thêm Action Listeners
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                authenticate();
            }
        });

        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    private void authenticate() {
        String username = txtUsername.getText();
        String password = String.valueOf(txtPassword.getPassword());

        String sql = "SELECT * FROM users WHERE username=? AND password=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username);
            pst.setString(2, password); 

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                new MainFrame(); // Mở giao diện chính
                dispose(); // Đóng giao diện đăng nhập
            } else {
                JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu!");
        }
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}
