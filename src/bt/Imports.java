package bt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Imports extends JPanel {

    private static final long serialVersionUID = 1L;
    private DefaultTableModel model;
    private JTextField txtStockInID;
    private JTextField txtStockInDate;
    private JTextField txtProductID;
    private JTextField txtProductName;
    private JTextField txtQuantity;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;
    private JTable table;
    private JTextField txtPurchasePrice;

    public Imports() {
        setLayout(null); // Sử dụng Absolute Layout

        txtStockInID = new JTextField();
        txtStockInID.setEditable(false);
        txtStockInID.setBounds(280, 33, 0, 0);
        txtStockInID.setVisible(false);
        add(txtStockInID);
        
        JLabel label_1 = new JLabel("Ngày Nhập:");
        label_1.setBounds(10, 13, 79, 20);
        add(label_1);

        txtStockInDate = new JTextField();
        txtStockInDate.setBounds(120, 13, 180, 20);
        add(txtStockInDate);

        JLabel label_2 = new JLabel("Mã Hàng Hóa:");
        label_2.setBounds(10, 44, 100, 20);
        add(label_2);

        txtProductID = new JTextField();
        txtProductID.setBounds(120, 44, 180, 20);
        add(txtProductID);

        JLabel label_3 = new JLabel("Tên Hàng Hóa:");
        label_3.setBounds(10, 73, 100, 20);
        add(label_3);

        txtProductName = new JTextField();
        txtProductName.setEditable(false);
        txtProductName.setBounds(120, 73, 180, 20);
        add(txtProductName);

        JLabel label_4 = new JLabel("Số Lượng Nhập:");
        label_4.setBounds(10, 104, 100, 20);
        add(label_4);

        txtQuantity = new JTextField();
        txtQuantity.setBounds(120, 104, 180, 20);
        add(txtQuantity);
        
        JLabel label_5 = new JLabel("Giá mua");
        label_5.setBounds(10, 135, 100, 20);
        add(label_5);
        
        txtPurchasePrice = new JTextField();
        txtPurchasePrice.setBounds(120, 135, 180, 20);
        add(txtPurchasePrice);

        // Tạo bảng và đặt vào JScrollPane
        model = new DefaultTableModel(new String[]{"Mã Nhập", "Ngày Nhập", "Mã Hàng Hóa", "Tên Hàng Hóa", "Số Lượng", "Giá Mua"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 173, 700, 200); // Đặt vị trí và kích thước cho JScrollPane
        add(scrollPane);

        // Các nút thêm, sửa, xóa
        btnAdd = new JButton("Thêm");
        btnAdd.setBounds(229, 384, 80, 30);
        add(btnAdd);

        btnEdit = new JButton("Sửa");
        btnEdit.setBounds(319, 384, 80, 30);
        add(btnEdit);

        btnDelete = new JButton("Xóa");
        btnDelete.setBounds(409, 384, 80, 30);
        add(btnDelete);

//        btnRefresh = new JButton("Làm Mới");
//        btnRefresh.setBounds(420, 320, 100, 30);
//        add(btnRefresh);

        // Action Listeners
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addStockIn();
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editStockIn();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteStockIn();
            }
        });

//        btnRefresh.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                loadImports();
//            }
//        });

        // Khi chọn một dòng trong bảng, hiển thị thông tin lên các trường nhập
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if(selectedRow >= 0){
                    txtStockInID.setText(model.getValueAt(selectedRow, 0).toString());
                    txtStockInDate.setText(model.getValueAt(selectedRow, 1).toString());
                    txtProductID.setText(model.getValueAt(selectedRow, 2).toString());
                    txtProductName.setText(model.getValueAt(selectedRow, 3).toString());
                    txtQuantity.setText(model.getValueAt(selectedRow, 4).toString());
                    txtPurchasePrice.setText(model.getValueAt(selectedRow, 5).toString());
                }
            }
        });
        
        // Add FocusListener to txtProductID to load product details when focus is lost
        txtProductID.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String productIdText = txtProductID.getText().trim();
                if (!productIdText.isEmpty()) {
                    try {
                        int productId = Integer.parseInt(productIdText);
                        loadProductDetails(productId);
                    } catch (NumberFormatException ex) {
                        // Handle invalid input
                        txtProductName.setText("");
                        txtPurchasePrice.setText("");
                    }
                } else {
                    txtProductName.setText("");
                    txtPurchasePrice.setText("");
                }
            }
        });

        loadImports(); // Tải dữ liệu khi khởi tạo
    }

    private void loadImports() {
        model.setRowCount(0); // Xóa dữ liệu cũ
        String sql = "SELECT * FROM imports"; // Thay đổi tên bảng nếu cần

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("import_id"),
                    rs.getDate("import_date"),
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("quantity"),
                    rs.getInt("purchase_price")
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu!");
        }
    }
    
    private void loadProductDetails(int productId) {
        String sql = "SELECT product_name FROM products WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, productId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String productName = rs.getString("product_name");

                // Populate the fields
                txtProductName.setText(productName);
            } else {
                // If no product is found with the given product_id
                txtProductName.setText("");
                JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm với mã này!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin sản phẩm!");
        }
    }

    private void addStockIn() {
        String stockInDate = txtStockInDate.getText();
        int productId;
        String productName = txtProductName.getText();
        int quantity;
        int purchasePrice;  // Thêm biến purchasePrice

        // Kiểm tra và chuyển đổi dữ liệu nhập vào
        try {
            productId = Integer.parseInt(txtProductID.getText());
            quantity = Integer.parseInt(txtQuantity.getText());
            purchasePrice = Integer.parseInt(txtPurchasePrice.getText());  // Lấy giá trị từ txtPurchasePrice
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho Mã Hàng Hóa, Số Lượng và Giá Mua!");
            return;
        }

        // Kiểm tra xem productId có tồn tại trong bảng products không
        if (!isProductIdValid(productId)) {
            JOptionPane.showMessageDialog(this, "Mã Hàng Hóa không hợp lệ!");
            return;
        }

        String sql = "INSERT INTO imports (import_date, product_id, product_name, quantity, purchase_price) VALUES (?, ?, ?, ?, ?)";
        String updateQuantitySql = "UPDATE products SET quantity = quantity + ? WHERE product_id = ?"; // Câu lệnh cập nhật số lượng

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             PreparedStatement updatePst = conn.prepareStatement(updateQuantitySql)) {

            // Thêm hóa đơn nhập hàng
            pst.setDate(1, Date.valueOf(stockInDate));
            pst.setInt(2, productId);
            pst.setString(3, productName);
            pst.setInt(4, quantity);
            pst.setInt(5, purchasePrice);  // Thêm dòng này để thiết lập tham số thứ 5

            int rows = pst.executeUpdate();
            if (rows > 0) {
                // Cập nhật số lượng trong bảng products
                updatePst.setInt(1, quantity);
                updatePst.setInt(2, productId);
                updatePst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Thêm hóa đơn nhập hàng thành công!");
                loadImports();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi thêm hóa đơn nhập hàng!");
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

    private void editStockIn() {
        int stockInId;
        String stockInDate = txtStockInDate.getText();
        int productId;
        String productName = txtProductName.getText();
        int quantity;

        // Kiểm tra và chuyển đổi dữ liệu nhập vào
        try {
            stockInId = Integer.parseInt(txtStockInID.getText());
            productId = Integer.parseInt(txtProductID.getText());
            quantity = Integer.parseInt(txtQuantity.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho Mã Nhập, Mã Hàng Hóa và Số Lượng!");
            return;
        }

        String sql = "UPDATE imports SET import_date=?, product_id=?, product_name=?, quantity=? WHERE import_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setDate(1, Date.valueOf(stockInDate));
            pst.setInt(2, productId);
            pst.setString(3, productName);
            pst.setInt(4, quantity);
            pst.setInt(5, stockInId);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Sửa hóa đơn nhập hàng thành công!");
                loadImports();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi sửa hóa đơn nhập hàng!");
        }
    }

    private void deleteStockIn() {
        int stockInId;
        int productId;
        int quantity;

        // Kiểm tra và chuyển đổi dữ liệu nhập vào
        try {
            stockInId = Integer.parseInt(txtStockInID.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho Mã Nhập!");
            return;
        }

        // Lấy thông tin productId và quantity trước khi xóa
        String selectSql = "SELECT product_id, quantity FROM imports WHERE import_id=?";
        String updateQuantitySql = "UPDATE products SET quantity = quantity - ? WHERE product_id=?";
        String deleteSql = "DELETE FROM imports WHERE import_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement selectPst = conn.prepareStatement(selectSql);
             PreparedStatement updatePst = conn.prepareStatement(updateQuantitySql);
             PreparedStatement deletePst = conn.prepareStatement(deleteSql)) {

            // Lấy thông tin product_id và quantity từ hóa đơn nhập
            selectPst.setInt(1, stockInId);
            ResultSet rs = selectPst.executeQuery();
            
            if (rs.next()) {
                productId = rs.getInt("product_id");
                quantity = rs.getInt("quantity");

                // Cập nhật lại số lượng hàng trong bảng products
                updatePst.setInt(1, quantity);
                updatePst.setInt(2, productId);
                updatePst.executeUpdate();

                // Xóa hóa đơn sau khi đã cập nhật số lượng hàng
                deletePst.setInt(1, stockInId);
                int rows = deletePst.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Xóa hóa đơn nhập hàng thành công!");
                    loadImports();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn để xóa!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi xóa hóa đơn nhập hàng!");
        }
    }

}
