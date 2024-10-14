package bt;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
    private JButton btnLogout;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
        setTitle("Ứng Dụng Quản Lý Cửa Hàng");
        setSize(760, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);  // Set layout to null for absolute positioning

        // Panel trên cùng cho nút Đăng Xuất
        JPanel topPanel = new JPanel(null);
        topPanel.setBounds(0, 0, 746, 50);  // Set bounds for the panel
        btnLogout = new JButton("Đăng Xuất");
        btnLogout.setBounds(10, 11, 100, 30);  // Set bounds for the button
        topPanel.add(btnLogout);
        getContentPane().add(topPanel);

        // TabbedPane
        tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(10, 52, 723, 500);  // Set bounds for the tabbed pane

        // Thêm các tab
        tabbedPane.addTab("Danh Sách Hàng Hóa", new ProductInfo());
        tabbedPane.addTab("Quản Lý Đặt Hàng", new Order());
        tabbedPane.addTab("Quản Lý Nhập Hàng", new Imports());
        tabbedPane.addTab("Quản Lý Bán Hàng", new Sales());

        getContentPane().add(tabbedPane);

        // Action Logout
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(MainFrame.this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    new LoginFrame();
                    dispose();
                }
            }
        });

        setVisible(true);
    }

}
