package bt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Sales extends JPanel {

    private static final long serialVersionUID = 1L;
    private DefaultTableModel model;
    private JTextField txtSaleID;
    private JTextField txtSaleDate;
    private JTextField txtProductID;
    private JTextField txtProductName;
    private JTextField txtQuantity;
    private JTextField txtSalePrice;
    private JTextField txtTotalAmount;
    private JTextField txtReceiveMoney;
    private JTextField txtChangeMoney;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;
    private JTable table;

    public Sales() {
        setLayout(null); // Sử dụng Absolute Layout

        // Ẩn trường Sale ID
        txtSaleID = new JTextField();
        txtSaleID.setVisible(false);
        txtSaleID.setEditable(false);
        txtSaleID.setBounds(915, 10, 10, 20);
        add(txtSaleID);
        
        // Các nhãn và trường nhập liệu
        JLabel lblSaleDate = new JLabel("Ngày Bán:");
        lblSaleDate.setBounds(10, 10, 80, 20);
        add(lblSaleDate);

        txtSaleDate = new JTextField();
        txtSaleDate.setBounds(120, 10, 180, 20);
        add(txtSaleDate);

        JLabel lblProductID = new JLabel("Mã Hàng Hóa:");
        lblProductID.setBounds(10, 41, 100, 20);
        add(lblProductID);

        txtProductID = new JTextField();
        txtProductID.setBounds(120, 41, 180, 20);
        add(txtProductID);

        JLabel lblProductName = new JLabel("Tên Hàng Hóa:");
        lblProductName.setBounds(10, 103, 100, 20);
        add(lblProductName);

        txtProductName = new JTextField();
        txtProductName.setEditable(false);
        txtProductName.setBounds(120, 103, 180, 20);
        add(txtProductName);

        JLabel lblQuantity = new JLabel("Số Lượng:");
        lblQuantity.setBounds(10, 72, 100, 20);
        add(lblQuantity);

        txtQuantity = new JTextField();
        txtQuantity.setBounds(120, 72, 180, 20);
        add(txtQuantity);
        
        JLabel lblSalePrice = new JLabel("Giá bán:");
        lblSalePrice.setBounds(10, 134, 100, 20);
        add(lblSalePrice);
        
        txtSalePrice = new JTextField();
        txtSalePrice.setEditable(false);
        txtSalePrice.setBounds(120, 134, 180, 20);
        add(txtSalePrice);

        JLabel lblTotalAmount = new JLabel("Tổng Tiền:");
        lblTotalAmount.setBounds(368, 41, 100, 20);
        add(lblTotalAmount);

        txtTotalAmount = new JTextField();
        txtTotalAmount.setEditable(false);
        txtTotalAmount.setBounds(498, 41, 180, 20);
        add(txtTotalAmount);

        JLabel lblReceiveMoney = new JLabel("Tiền Khách Đưa:");
        lblReceiveMoney.setBounds(368, 10, 130, 20);
        add(lblReceiveMoney);

        txtReceiveMoney = new JTextField();
        txtReceiveMoney.setBounds(498, 10, 180, 20);
        add(txtReceiveMoney);

        JLabel lblChangeMoney = new JLabel("Tiền Thối:");
        lblChangeMoney.setBounds(368, 72, 100, 20);
        add(lblChangeMoney);

        txtChangeMoney = new JTextField();
        txtChangeMoney.setBounds(498, 72, 180, 20);
        txtChangeMoney.setEditable(false); // Không cho phép sửa
        add(txtChangeMoney);

        // Tạo bảng và đặt vào JScrollPane
        model = new DefaultTableModel(new String[]{
            "Mã Hóa Đơn", "Ngày Xuất", "Mã Hàng Hóa", "Tên Hàng Hóa", "Số Lượng", "Giá Bán", "Tổng Tiền", "Tiền Khách Đưa", "Tiền Thối"
        }, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(0, 176, 720, 200); // Đặt vị trí và kích thước cho JScrollPane
        add(scrollPane);
        
        txtProductID.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // Optional: You can add actions when the field gains focus (e.g., clearing fields)
            }

            @Override
            public void focusLost(FocusEvent e) {
                // When the field loses focus, we trigger the product details update
                String productIdText = txtProductID.getText().trim();
                if (!productIdText.isEmpty()) {
                    try {
                        int productId = Integer.parseInt(productIdText);
                        loadProductDetails(productId);
                    } catch (NumberFormatException ex) {
                        // Handle invalid input (non-integer input)
                        txtProductName.setText("");
                        txtSalePrice.setText("");
                    }
                } else {
                    txtProductName.setText("");
                    txtSalePrice.setText("");
                }
            }
        });
        
        // Thêm DocumentListener cho các trường liên quan
        txtQuantity.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                calculateTotalAndChange();
            }
        });

        txtSalePrice.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                calculateTotalAndChange();
            }
        });

        txtReceiveMoney.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                calculateTotalAndChange();
            }
        });

        // Các nút thêm, sửa, xóa, làm mới
        btnAdd = new JButton("Thêm");
        btnAdd.setBounds(228, 387, 80, 30);
        add(btnAdd);

        btnEdit = new JButton("Sửa");
        btnEdit.setBounds(318, 387, 80, 30);
        add(btnEdit);

        btnDelete = new JButton("Xóa");
        btnDelete.setBounds(408, 387, 80, 30);
        add(btnDelete);

        btnRefresh = new JButton("Làm Mới");
        btnRefresh.setBounds(578, 135, 100, 30);
        add(btnRefresh);

        // Action Listeners
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addSale();
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSale();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSale();
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadSales();
            }
        });

        // Khi chọn một dòng trong bảng, hiển thị thông tin lên các trường nhập
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if(selectedRow >= 0){
                    txtSaleID.setText(model.getValueAt(selectedRow, 0).toString());
                    txtSaleDate.setText(model.getValueAt(selectedRow, 1).toString());
                    txtProductID.setText(model.getValueAt(selectedRow, 2).toString());
                    txtProductName.setText(model.getValueAt(selectedRow, 3).toString());
                    txtQuantity.setText(model.getValueAt(selectedRow, 4).toString());
                    txtSalePrice.setText(model.getValueAt(selectedRow, 5).toString());
                    txtTotalAmount.setText(model.getValueAt(selectedRow, 6).toString());
                    txtReceiveMoney.setText(model.getValueAt(selectedRow, 7).toString());
                    txtChangeMoney.setText(model.getValueAt(selectedRow, 8).toString());
                }
            }
        });

        loadSales(); // Tải dữ liệu khi khởi tạo
    }
    
    private void loadProductDetails(int productId) {
        String sql = "SELECT product_name, sale_price FROM products WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, productId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String productName = rs.getString("product_name");
                double salePrice = rs.getDouble("sale_price");

                // Populate the fields
                txtProductName.setText(productName);
                txtSalePrice.setText(String.valueOf(salePrice));
            } else {
                // If no product is found with the given product_id
                txtProductName.setText("");
                txtSalePrice.setText("");
                JOptionPane.showMessageDialog(this, "No product found for this ID!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving product details!");
        }
    }
    
    private void calculateTotalAndChange() {
        try {
            // Lấy dữ liệu từ các trường nhập liệu
            int quantity = Integer.parseInt(txtQuantity.getText());
            double salePrice = Double.parseDouble(txtSalePrice.getText());
            double totalAmount = quantity * salePrice;
            txtTotalAmount.setText(String.valueOf(totalAmount));

            double receiveMoney = Double.parseDouble(txtReceiveMoney.getText());
            double changeMoney = receiveMoney - totalAmount;
            txtChangeMoney.setText(String.valueOf(changeMoney));

        } catch (NumberFormatException e) {
            // Nếu dữ liệu không hợp lệ, không tính toán
            txtTotalAmount.setText("");
            txtChangeMoney.setText("");
        }
    }

    private void loadSales() {
        model.setRowCount(0); // Xóa dữ liệu cũ
        String sql = "SELECT * FROM sales";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("sale_id"),
                    rs.getDate("sale_date"),
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("quantity"),
                    rs.getDouble("sale_price"),
                    rs.getDouble("total_amount"),
                    rs.getDouble("receive_money"),
                    rs.getDouble("change_money")
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu!");
        }
    }

    private void addSale() {
    	String saleDate = txtSaleDate.getText();
        int productId;
        String productName = txtProductName.getText();
        int quantity;
        double salePrice;
        double totalAmount;
        double receiveMoney;
        double changeMoney;

        // Kiểm tra và chuyển đổi dữ liệu nhập vào
        try {
            productId = Integer.parseInt(txtProductID.getText());
            quantity = Integer.parseInt(txtQuantity.getText());
            salePrice = Double.parseDouble(txtSalePrice.getText());
            totalAmount = Double.parseDouble(txtTotalAmount.getText()); 
            receiveMoney = Double.parseDouble(txtReceiveMoney.getText());
            changeMoney = Double.parseDouble(txtChangeMoney.getText()); 
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!");
            return;
        }

        // Kiểm tra xem productId có tồn tại trong bảng products không
        if (!isProductIdValid(productId)) {
            JOptionPane.showMessageDialog(this, "Mã Hàng Hóa không hợp lệ!");
            return;
        }

        // Kiểm tra xem số lượng tồn kho có đủ để bán không
        if (!isQuantitySufficient(productId, quantity)) {
            JOptionPane.showMessageDialog(this, "Số lượng sản phẩm tồn kho không đủ!");
            return;
        }

        String sql = "INSERT INTO sales (sale_date, product_id, product_name, quantity, sale_price, total_amount, receive_money, change_money) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String updateQuantitySql = "UPDATE products SET quantity = quantity - ? WHERE product_id = ?"; // Câu lệnh cập nhật số lượng

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             PreparedStatement updatePst = conn.prepareStatement(updateQuantitySql)) {

            // Thêm hóa đơn bán hàng
        	pst.setDate(1, Date.valueOf(saleDate));
        	pst.setInt(2, productId);
        	pst.setString(3, productName);
        	pst.setInt(4, quantity);
        	pst.setDouble(5, salePrice);
        	pst.setDouble(6, totalAmount);
        	pst.setDouble(7, receiveMoney);
        	pst.setDouble(8, changeMoney);


            int rows = pst.executeUpdate();
            if (rows > 0) {
                // Cập nhật số lượng trong bảng products
                updatePst.setInt(1, quantity);
                updatePst.setInt(2, productId);
                updatePst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Thêm hóa đơn bán hàng thành công!");
                loadSales();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi thêm hóa đơn bán hàng!");
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

    private boolean isQuantitySufficient(int productId, int quantity) {
        String sql = "SELECT quantity FROM products WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, productId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int currentQuantity = rs.getInt("quantity");
                return currentQuantity >= quantity;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false; // Trả về false nếu xảy ra lỗi hoặc sản phẩm không tồn tại
    }

    private void editSale() {
        int saleId;
        String saleDate = txtSaleDate.getText();
        int productId;
        String productName = txtProductName.getText();
        int quantity;
        double totalAmount;
        double receiveMoney;
        double changeMoney;

        // Kiểm tra và chuyển đổi dữ liệu nhập vào
        try {
            saleId = Integer.parseInt(txtSaleID.getText());
            productId = Integer.parseInt(txtProductID.getText());
            quantity = Integer.parseInt(txtQuantity.getText());
            totalAmount = Double.parseDouble(txtTotalAmount.getText());
            receiveMoney = Double.parseDouble(txtReceiveMoney.getText());
            changeMoney = receiveMoney - totalAmount; // Tính tiền thối
            txtChangeMoney.setText(String.valueOf(changeMoney)); // Hiển thị tiền thối
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho Mã Bán, Mã Hàng Hóa, Số Lượng, Tổng Tiền và Số Tiền Khách Đưa!");
            return;
        }

        // Kiểm tra xem productId có tồn tại trong bảng products không
        if (!isProductIdValid(productId)) {
            JOptionPane.showMessageDialog(this, "Mã Hàng Hóa không hợp lệ!");
            return;
        }

        // Kiểm tra xem số lượng tồn kho có đủ để bán không
        if (!isQuantitySufficient(productId, quantity)) {
            JOptionPane.showMessageDialog(this, "Số lượng sản phẩm tồn kho không đủ!");
            return;
        }

        String sql = "UPDATE sales SET sale_date=?, product_id=?, product_name=?, quantity=?, total_amount=?, receive_money=?, change_money=? WHERE sale_id=?";
        String updateQuantitySql = "UPDATE products SET quantity = quantity - ? WHERE product_id = ?"; // Câu lệnh cập nhật số lượng

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             PreparedStatement updatePst = conn.prepareStatement(updateQuantitySql)) {

            // Sửa hóa đơn bán hàng
            pst.setDate(1, Date.valueOf(saleDate));
            pst.setInt(2, productId);
            pst.setString(3, productName);
            pst.setInt(4, quantity);
            pst.setDouble(5, totalAmount);
            pst.setDouble(6, receiveMoney);
            pst.setDouble(7, changeMoney);
            pst.setInt(8, saleId);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                // Cập nhật số lượng trong bảng products
                updatePst.setInt(1, quantity);
                updatePst.setInt(2, productId);
                updatePst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Sửa hóa đơn bán hàng thành công!");
                loadSales();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi sửa hóa đơn bán hàng!");
        }
    }

    private void deleteSale() {
        int saleId;

        // Kiểm tra và chuyển đổi dữ liệu nhập vào
        try {
            saleId = Integer.parseInt(txtSaleID.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho Mã Bán!");
            return;
        }

        // Lấy thông tin số lượng sản phẩm từ hóa đơn trước khi xóa để cập nhật lại tồn kho
        String getQuantitySql = "SELECT product_id, quantity FROM sales WHERE sale_id = ?";
        String updateQuantitySql = "UPDATE products SET quantity = quantity + ? WHERE product_id = ?";
        String deleteSaleSql = "DELETE FROM sales WHERE sale_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstGet = conn.prepareStatement(getQuantitySql);
             PreparedStatement pstUpdate = conn.prepareStatement(updateQuantitySql);
             PreparedStatement pstDelete = conn.prepareStatement(deleteSaleSql)) {

            // Lấy thông tin sản phẩm và số lượng từ hóa đơn
            pstGet.setInt(1, saleId);
            ResultSet rs = pstGet.executeQuery();
            if (rs.next()) {
                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");

                // Cập nhật số lượng trong bảng products (trả lại số lượng đã bán)
                pstUpdate.setInt(1, quantity);
                pstUpdate.setInt(2, productId);
                pstUpdate.executeUpdate();

                // Xóa hóa đơn bán hàng
                pstDelete.setInt(1, saleId);
                int rows = pstDelete.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Xóa hóa đơn bán hàng thành công!");
                    loadSales();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn bán hàng!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi xóa hóa đơn bán hàng!");
        }
    }
    abstract class SimpleDocumentListener implements DocumentListener {
        public abstract void update();

        @Override
        public void insertUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            update();
        }
    }
}
