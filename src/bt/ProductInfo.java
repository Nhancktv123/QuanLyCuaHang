package bt;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ProductInfo extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private JButton btnRefresh;

    /**
     * Create the panel.
     */
    public ProductInfo() {
        setLayout(null);

        // Tạo bảng và đặt vào JScrollPane
        model = new DefaultTableModel(new String[]{"Mã Hàng Hóa", "Tên Hàng Hóa", "Số lượng", "Giá Bán"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 11, 700, 200); // Đặt vị trí và kích thước cho JScrollPane
        add(scrollPane);
        
     // Thêm nút "Refresh"
        btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(318, 222, 80, 30);
        add(btnRefresh);

        // Action Listener cho nút Refresh
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	loadProducts(); // Gọi phương thức tải dữ liệu khi nhấn nút
            }
        });

        loadProducts(); // Tải dữ liệu khi khởi tạo
    }

    // Hàm tải dữ liệu từ CSDL vào JTable
    private void loadProducts() {
        model.setRowCount(0); // Xóa dữ liệu cũ
        String sql = "SELECT * FROM products";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("quantity"),
                    rs.getDouble("sale_price")
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu!");
        }
    }
}

