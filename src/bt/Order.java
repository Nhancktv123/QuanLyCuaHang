package bt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Order extends JPanel {

    private static final long serialVersionUID = 1L;
    private DefaultTableModel model;
    private JTextField txtOrderID;
    private JTextField txtOrderDate;
    private JTextField txtProductID;
    private JTextField txtProductName;
    private JTextField txtQuantity;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;
    private JTable table;

    /**
     * Create the panel.
     */
    public Order() {
        setLayout(null); // Sử dụng Absolute Layout

//        JLabel label = new JLabel("Mã Đơn Hàng:");
//        label.setBounds(10, 11, 91, 20);
//        add(label);

        txtOrderID = new JTextField();
        txtOrderID.setEditable(false);
        txtOrderID.setBounds(1399, 11, 17, 20);
        txtOrderID.setVisible(false);
        add(txtOrderID);
        
        JLabel label_1 = new JLabel("Ngày Đặt:");
        label_1.setBounds(10, 11, 79, 20);
        add(label_1);

        txtOrderDate = new JTextField();
        txtOrderDate.setBounds(120, 11, 180, 20);
        add(txtOrderDate);

        JLabel label_2 = new JLabel("Mã Hàng Hóa:");
        label_2.setBounds(10, 42, 100, 20);
        add(label_2);

        txtProductID = new JTextField();
        txtProductID.setBounds(120, 42, 180, 20);
        add(txtProductID);

        JLabel label_3 = new JLabel("Tên Hàng Hóa:");
        label_3.setBounds(10, 73, 100, 20);
        add(label_3);

        txtProductName = new JTextField();
        txtProductName.setBounds(120, 73, 180, 20);
        add(txtProductName);

        JLabel label_4 = new JLabel("Số Lượng Đặt:");
        label_4.setBounds(10, 104, 100, 20);
        add(label_4);

        txtQuantity = new JTextField();
        txtQuantity.setBounds(120, 104, 180, 20);
        add(txtQuantity);

        // Tạo bảng và đặt vào JScrollPane
        model = new DefaultTableModel(new String[]{"Mã Đơn Hàng", "Ngày Đặt", "Mã Hàng Hóa", "Tên Hàng Hóa", "Số Lượng"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 142, 700, 189); // Đặt vị trí và kích thước cho JScrollPane
        add(scrollPane);

        // Các nút thêm, sửa, xóa, làm mới
        btnAdd = new JButton("Thêm");
        btnAdd.setBounds(226, 342, 80, 30);
        add(btnAdd);

        btnEdit = new JButton("Sửa");
        btnEdit.setBounds(316, 342, 80, 30);
        add(btnEdit);

        btnDelete = new JButton("Xóa");
        btnDelete.setBounds(406, 342, 80, 30);
        add(btnDelete);

//        btnRefresh = new JButton("Làm Mới");
//        btnRefresh.setBounds(450, 300, 100, 30);
//        add(btnRefresh);

        // Action Listeners
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addOrder();
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editOrder();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteOrder();
            }
        });

//        btnRefresh.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                loadOrders();
//            }
//        });

        // Khi chọn một dòng trong bảng, hiển thị thông tin lên các trường nhập
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if(selectedRow >= 0){
                    txtOrderID.setText(model.getValueAt(selectedRow, 0).toString());
                    txtOrderDate.setText(model.getValueAt(selectedRow, 1).toString());
                    txtProductID.setText(model.getValueAt(selectedRow, 2).toString());
                    txtProductName.setText(model.getValueAt(selectedRow, 3).toString());
                    txtQuantity.setText(model.getValueAt(selectedRow, 4).toString());
                }
            }
        });

        loadOrders(); // Tải dữ liệu khi khởi tạo
    }

    private void loadOrders() {
        model.setRowCount(0); // Xóa dữ liệu cũ
        String sql = "SELECT * FROM orders";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("order_id"),
                    rs.getDate("order_date"),
                    rs.getString("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("quantity")
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu!");
        }
    }

    private void addOrder() {
        String orderDate = txtOrderDate.getText();
        int productId;
        String productName = txtProductName.getText();
        int quantity;

        // Kiểm tra và chuyển đổi dữ liệu nhập vào
        try {
            productId = Integer.parseInt(txtProductID.getText());
            quantity = Integer.parseInt(txtQuantity.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho Mã Hàng Hóa và Số Lượng!");
            return;
        }

        // Kiểm tra xem productId có tồn tại trong bảng products không
        if (!isProductIdValid(productId)) {
            JOptionPane.showMessageDialog(this, "Mã Hàng Hóa không hợp lệ!");
            return;
        }

        String sql = "INSERT INTO orders (order_date, product_id, product_name, quantity) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setDate(1, Date.valueOf(orderDate));
            pst.setInt(2, productId);
            pst.setString(3, productName);
            pst.setInt(4, quantity);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Thêm đơn đặt hàng thành công!");
                loadOrders();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi thêm đơn đặt hàng!");
        }
    }

    private boolean isProductIdValid(int productId) {
        String sql = "SELECT COUNT(*) FROM products WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, productId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Trả về true nếu sản phẩm tồn tại
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false; // Trả về false nếu xảy ra lỗi hoặc sản phẩm không tồn tại
    }


    private void editOrder() {
        int orderId;
        String orderDate = txtOrderDate.getText();
        int productId;
        String productName = txtProductName.getText();
        int quantity;

        // Kiểm tra và chuyển đổi dữ liệu nhập vào
        try {
            orderId = Integer.parseInt(txtOrderID.getText());
            productId = Integer.parseInt(txtProductID.getText());
            quantity = Integer.parseInt(txtQuantity.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho Mã Đơn Hàng, Mã Hàng Hóa và Số Lượng!");
            return;
        }

        String sql = "UPDATE orders SET order_date=?, product_id=?, product_name=?, quantity=? WHERE order_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setDate(1, Date.valueOf(orderDate));
            pst.setInt(2, productId);
            pst.setString(3, productName);
            pst.setInt(4, quantity);
            pst.setInt(5, orderId);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Sửa đơn đặt hàng thành công!");
                loadOrders();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi sửa đơn đặt hàng!");
        }
    }

    private void deleteOrder() {
        int orderId;

        // Kiểm tra và chuyển đổi dữ liệu nhập vào
        try {
            orderId = Integer.parseInt(txtOrderID.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho Mã Đơn Hàng!");
            return;
        }

        String sql = "DELETE FROM orders WHERE order_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, orderId);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Xóa đơn đặt hàng thành công!");
                loadOrders();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi xóa đơn đặt hàng!");
        }
    }
}
