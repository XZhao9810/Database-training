import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class system {

    private static JTextField userField;
    private static JPasswordField passField;
    private static JTextField dbField;
    private static JLabel statusLabel;
    private static JFrame loginFrame;

    // 保存数据库连接信息
    private static String savedDbName;
    private static String savedUsername;
    private static String savedPassword;
    private static String savedJdbcUrl;
    private static final String savedDriver = "com.mysql.cj.jdbc.Driver";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        // 创建主窗口框架
        loginFrame = new JFrame("MySQL数据库连接工具");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));

        // 创建约束对象
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ================== 数据库名称输入区域 ==================
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel dbLabel = new JLabel("数据库名称:");
        dbLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        mainPanel.add(dbLabel, gbc);

        gbc.gridx = 1;
        dbField = new JTextField(20);
        dbField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        dbField.setPreferredSize(new Dimension(300, 35));
        dbField.setText("数据库实训-学生信息管理系统");
        mainPanel.add(dbField, gbc);

        // ================== 用户名输入区域 ==================
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel userLabel = new JLabel("用户名:");
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        mainPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        userField = new JTextField(20);
        userField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        userField.setPreferredSize(new Dimension(300, 35));
        mainPanel.add(userField, gbc);

        // ================== 密码输入区域 ==================
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passLabel = new JLabel("密码:");
        passLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        mainPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        passField = new JPasswordField(20);
        passField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        passField.setPreferredSize(new Dimension(300, 35));
        mainPanel.add(passField, gbc);

        // ================== 连接按钮区域 ==================
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton connectButton = new JButton("连接到MySQL数据库");
        connectButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        connectButton.setPreferredSize(new Dimension(250, 40));
        connectButton.setBackground(new Color(70, 130, 180));
        connectButton.setForeground(Color.WHITE);
        connectButton.setFocusPainted(false);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToDatabase();
            }
        });

        mainPanel.add(connectButton, gbc);

        // ================== 状态显示区域 ==================
        gbc.gridy = 4;
        statusLabel = new JLabel("就绪，请输入数据库凭据");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(100, 100, 100));
        mainPanel.add(statusLabel, gbc);

        // 重置布局约束
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // 将主面板添加到窗口
        loginFrame.setContentPane(mainPanel);
        loginFrame.setSize(800, 600);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    /**
     * 尝试连接到MySQL数据库
     */
    private static void connectToDatabase() {
        String dbName = dbField.getText().trim();
        String username = userField.getText().trim();
        char[] password = passField.getPassword();

        // 验证输入
        if (dbName.isEmpty()) {
            statusLabel.setText("错误：数据库名称不能为空！");
            statusLabel.setForeground(Color.RED);
            return;
        }

        if (username.isEmpty()) {
            statusLabel.setText("错误：用户名不能为空！");
            statusLabel.setForeground(Color.RED);
            return;
        }

        if (password.length == 0) {
            statusLabel.setText("错误：密码不能为空！");
            statusLabel.setForeground(Color.RED);
            return;
        }

        String passwordStr = new String(password);
        String jdbcUrl = "jdbc:mysql://localhost:3306/" + dbName;
        String driver = "com.mysql.cj.jdbc.Driver";

        statusLabel.setText("正在尝试连接到数据库 '" + dbName + "'...");
        statusLabel.setForeground(new Color(0, 100, 0));

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Connection conn = null;
                try {
                    Class.forName(driver);
                    conn = DriverManager.getConnection(jdbcUrl, username, passwordStr);
                    return conn != null && !conn.isClosed();
                } catch (ClassNotFoundException ex) {
                    statusLabel.setText("错误：找不到MySQL JDBC驱动！");
                    return false;
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == 1049) {
                        statusLabel.setText("错误：数据库 '" + dbName + "' 不存在！");
                    } else if (ex.getErrorCode() == 1045) {
                        statusLabel.setText("错误：用户名或密码无效！");
                    } else if (ex.getErrorCode() == 0) {
                        statusLabel.setText("错误：无法连接到数据库服务器！");
                    } else {
                        statusLabel.setText("数据库错误: " + ex.getMessage());
                    }
                    return false;
                } finally {
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException ex) {
                        }
                    }
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        // 保存连接信息
                        savedDbName = dbName;
                        savedUsername = username;
                        savedPassword = passwordStr;
                        savedJdbcUrl = jdbcUrl;

                        statusLabel.setText("成功连接到数据库 '" + dbName + "'！");
                        statusLabel.setForeground(new Color(0, 150, 0));

                        showMainApplication();
                        loginFrame.dispose();
                    } else {
                        statusLabel.setForeground(Color.RED);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("连接过程中发生错误: " + ex.getMessage());
                    statusLabel.setForeground(Color.RED);
                }
            }
        };

        worker.execute();
    }

    /**
     * 显示主应用程序界面
     */
    private static void showMainApplication() {
        JFrame mainFrame = new JFrame("学生信息管理系统");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(900, 650);
        mainFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ================== 顶部标题面板 ==================
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("学生信息管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ================== 功能按钮面板 ==================
        JPanel functionPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        functionPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        functionPanel.setBackground(new Color(245, 245, 245));

        // 添加数据按钮
        JButton addButton = createFunctionButton("添加数据", new Color(46, 204, 113));
        functionPanel.add(addButton);

        // 显示数据按钮
        JButton displayButton = createFunctionButton("显示数据", new Color(52, 152, 219));
        functionPanel.add(displayButton);

        // 修改数据按钮
        JButton editButton = createFunctionButton("修改数据", new Color(155, 89, 182));
        functionPanel.add(editButton);

        // 查询数据按钮
        JButton searchButton = createFunctionButton("查询数据", new Color(241, 196, 15));
        functionPanel.add(searchButton);

        // 统计学分按钮
        JButton statsButton = createFunctionButton("查询学分", new Color(230, 126, 34));
        functionPanel.add(statsButton);

        // 预留位置
        functionPanel.add(new JLabel());
        mainPanel.add(functionPanel, BorderLayout.CENTER);

        // ================== 底部状态栏 ==================
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.setBackground(new Color(220, 220, 220));
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));

        JLabel statusLabel = new JLabel("就绪 | 数据库: " + dbField.getText());
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(80, 80, 80));
        footerPanel.add(statusLabel);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        mainFrame.setContentPane(mainPanel);
        mainFrame.setVisible(true);
    }

    /**
     * 创建功能按钮
     */
    private static JButton createFunctionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 20));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 100));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 添加悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        // 添加点击事件
        button.addActionListener(e -> {
            if (text.equals("显示数据")) {
                showTableData();
            } else if (text.equals("添加数据")) {
                addDataToTable();
            }
             else if (text.equals("查询学分")) {
                    queryCredits();
            }
            else if (text.equals("查询数据")) {
                queryStudentData(); // 调用新的查询方法
            }
            else if (text.equals("修改数据")) {
                editTableData(); // 新增的修改数据功能
            }
            else {
                JOptionPane.showMessageDialog(null,
                        "【" + text + "】功能正在开发中...",
                        "功能提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return button;
    }

    /**
     * 新增方法：编辑表数据
     */
    private static void editTableData() {
        // 检查是否已连接数据库
        if (savedDbName == null || savedDbName.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "尚未连接到数据库，请先连接！",
                    "数据库错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 获取数据库表名列表
        Vector<String> tableNames = new Vector<>();
        try (Connection conn = DriverManager.getConnection(savedJdbcUrl, savedUsername, savedPassword)) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet tables = metaData.getTables(savedDbName, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    tableNames.add(tables.getString("TABLE_NAME"));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "获取表列表失败: " + ex.getMessage(),
                    "数据库错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 检查是否有表
        if (tableNames.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "数据库 '" + savedDbName + "' 中没有找到任何表！",
                    "数据库错误",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 创建表选择对话框
        JComboBox<String> tableComboBox = new JComboBox<>(tableNames);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("请选择要编辑的表:"));
        panel.add(tableComboBox);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "选择表", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String selectedTable = (String) tableComboBox.getSelectedItem();
        displayEditableTableData(selectedTable);
    }

    /**
     * 显示可编辑的表数据
     */
    private static void displayEditableTableData(String tableName) {
        // 创建数据编辑窗口
        JFrame editFrame = new JFrame("编辑表数据: " + tableName);
        editFrame.setSize(900, 600);
        editFrame.setLocationRelativeTo(null);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 添加标题
        JLabel titleLabel = new JLabel("表: " + tableName + " (数据库: " + savedDbName + ")");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 创建表格模型
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 所有单元格都可编辑
                return true;
            }
        };
        table.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        table.setRowHeight(25);

        // 添加排序功能
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // 添加滚动面板
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 添加状态标签
        JLabel statusLabel = new JLabel("正在加载数据...");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        editFrame.add(mainPanel);
        editFrame.setVisible(true);

        // 获取主键信息
        Vector<String> primaryKeys = new Vector<>();
        try (Connection conn = DriverManager.getConnection(savedJdbcUrl, savedUsername, savedPassword)) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet pkResult = metaData.getPrimaryKeys(null, null, tableName)) {
                while (pkResult.next()) {
                    primaryKeys.add(pkResult.getString("COLUMN_NAME"));
                }
            }
        } catch (SQLException ex) {
            statusLabel.setText("获取主键失败: " + ex.getMessage());
        }

        // 使用SwingWorker在后台加载数据
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (Connection conn = DriverManager.getConnection(savedJdbcUrl, savedUsername, savedPassword);
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

                    // 获取列信息
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // 添加列名到表格模型
                    Vector<String> columnNames = new Vector<>();
                    for (int i = 1; i <= columnCount; i++) {
                        columnNames.add(metaData.getColumnName(i));
                    }

                    SwingUtilities.invokeLater(() -> {
                        model.setColumnIdentifiers(columnNames);
                        statusLabel.setText("已加载列信息，正在加载行数据...");
                    });

                    // 添加行数据
                    while (rs.next()) {
                        Vector<Object> rowData = new Vector<>();
                        for (int i = 1; i <= columnCount; i++) {
                            rowData.add(rs.getObject(i));
                        }

                        // 在EDT中更新表格
                        final Vector<Object> finalRow = rowData;
                        SwingUtilities.invokeLater(() -> model.addRow(finalRow));
                    }

                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("加载完成! 共 " + model.getRowCount() + " 行记录");

                        // 高亮显示主键列
                        TableColumnModel columnModel = table.getColumnModel();
                        for (int i = 0; i < columnNames.size(); i++) {
                            if (primaryKeys.contains(columnNames.get(i))) {
                                table.getColumnModel().getColumn(i).setCellRenderer(new PrimaryKeyRenderer());
                            }
                        }
                    });

                } catch (SQLException ex) {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("数据加载失败: " + ex.getMessage());
                        JOptionPane.showMessageDialog(editFrame,
                                "查询表数据时出错: " + ex.getMessage(),
                                "数据库错误",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };

        worker.execute();

        // 添加表格模型监听器，处理数据修改
        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() != TableModelEvent.ALL_COLUMNS) {
                    int row = e.getFirstRow();
                    int col = e.getColumn();

                    // 获取模型索引（考虑排序）
                    int modelRow = table.convertRowIndexToModel(row);
                    int modelCol = table.convertColumnIndexToModel(col);

                    Object newValue = model.getValueAt(modelRow, modelCol);

                    // 在后台更新数据库
                    SwingWorker<Boolean, Void> updateWorker = new SwingWorker<Boolean, Void>() {
                        @Override
                        protected Boolean doInBackground() throws Exception {
                            return updateDatabase(tableName, model, modelRow, modelCol, newValue, primaryKeys);
                        }

                        @Override
                        protected void done() {
                            try {
                                if (!get()) {
                                    // 更新失败，恢复原值
                                    JOptionPane.showMessageDialog(editFrame,
                                            "更新数据库失败，已恢复原值",
                                            "更新错误",
                                            JOptionPane.ERROR_MESSAGE);
                                    // 重新加载数据
                                    model.setRowCount(0);
                                    loadTableData(tableName, model, statusLabel);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    };
                    updateWorker.execute();
                }
            }
        });
    }

    /**
     * 高亮显示主键列的渲染器
     */
    static class PrimaryKeyRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
        public PrimaryKeyRenderer() {
            setOpaque(true);
        }

        public PrimaryKeyRenderer getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(new Color(220, 240, 255)); // 浅蓝色背景
            setFont(table.getFont().deriveFont(Font.BOLD));
            return this;
        }
    }

    /**
     * 更新数据库
     */
    private static boolean updateDatabase(String tableName, DefaultTableModel model,
                                          int row, int col, Object newValue, Vector<String> primaryKeys) {
        // 获取列名
        String columnName = model.getColumnName(col);

        // 构建WHERE子句（主键条件）
        StringBuilder whereClause = new StringBuilder();
        Vector<Object> primaryKeyValues = new Vector<>();

        for (String pk : primaryKeys) {
            int pkCol = -1;
            for (int i = 0; i < model.getColumnCount(); i++) {
                if (model.getColumnName(i).equals(pk)) {
                    pkCol = i;
                    break;
                }
            }

            if (pkCol != -1) {
                Object pkValue = model.getValueAt(row, pkCol);
                primaryKeyValues.add(pkValue);

                if (whereClause.length() > 0) {
                    whereClause.append(" AND ");
                }
                whereClause.append(pk).append(" = ?");
            }
        }

        if (whereClause.length() == 0) {
            JOptionPane.showMessageDialog(null,
                    "无法确定主键条件，更新失败",
                    "更新错误",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 构建SQL
        String sql = "UPDATE " + tableName + " SET " + columnName + " = ? WHERE " + whereClause;

        try (Connection conn = DriverManager.getConnection(savedJdbcUrl, savedUsername, savedPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置参数
            pstmt.setObject(1, newValue);

            for (int i = 0; i < primaryKeyValues.size(); i++) {
                pstmt.setObject(i + 2, primaryKeyValues.get(i));
            }

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "更新失败: " + ex.getMessage(),
                    "数据库错误",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * 加载表数据
     */
    private static void loadTableData(String tableName, DefaultTableModel model, JLabel statusLabel) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (Connection conn = DriverManager.getConnection(savedJdbcUrl, savedUsername, savedPassword);
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

                    // 清空现有数据
                    model.setRowCount(0);

                    // 添加行数据
                    while (rs.next()) {
                        Vector<Object> rowData = new Vector<>();
                        for (int i = 1; i <= model.getColumnCount(); i++) {
                            rowData.add(rs.getObject(i));
                        }
                        SwingUtilities.invokeLater(() -> model.addRow(rowData));
                    }

                    SwingUtilities.invokeLater(() ->
                            statusLabel.setText("数据已刷新! 共 " + model.getRowCount() + " 行记录"));

                } catch (SQLException ex) {
                    SwingUtilities.invokeLater(() ->
                            statusLabel.setText("数据加载失败: " + ex.getMessage()));
                }
                return null;
            }
        };
        worker.execute();
    }

    /**
     * 新增方法：查询学生数据
     */
    private static void queryStudentData() {
        // 检查是否已连接数据库
        if (savedDbName == null || savedDbName.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "尚未连接到数据库，请先连接！",
                    "数据库错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 获取用户输入的学号
        String studentId = JOptionPane.showInputDialog(null,
                "请输入学生学号：",
                "学生数据查询",
                JOptionPane.QUESTION_MESSAGE);

        // 验证输入
        if (studentId == null) {
            return; // 用户取消输入
        }
        studentId = studentId.trim();
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "学号不能为空！",
                    "输入错误",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 创建进度对话框
        final JDialog progressDialog = new JDialog((JFrame) null, "正在查询", true);
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(new JLabel("正在查询学号: " + studentId + " 的相关信息...", SwingConstants.CENTER), BorderLayout.CENTER);
        progressPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        progressDialog.add(progressPanel);
        progressDialog.pack();
        progressDialog.setLocationRelativeTo(null);
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // 使用SwingWorker在后台执行查询
        String finalStudentId = studentId;
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<Map<String, Object>, Void>() {
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                Map<String, Object> result = new HashMap<>();
                try (Connection conn = DriverManager.getConnection(savedJdbcUrl, savedUsername, savedPassword)) {
                    // 1. 查询学生基本信息
                    Vector<Vector<Object>> baseInfoData = new Vector<>();
                    String baseInfoQuery = "SELECT * FROM 基本信息 WHERE 学号 = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(baseInfoQuery)) {
                        pstmt.setString(1, finalStudentId);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (!rs.next()) {
                                JOptionPane.showMessageDialog(null,
                                        "找不到学号为 '" + finalStudentId + "' 的学生！",
                                        "查询错误",
                                        JOptionPane.ERROR_MESSAGE);
                                return null;
                            }

                            // 获取列信息
                            ResultSetMetaData metaData = rs.getMetaData();
                            int columnCount = metaData.getColumnCount();

                            // 添加列名
                            Vector<String> columnNames = new Vector<>();
                            for (int i = 1; i <= columnCount; i++) {
                                columnNames.add(metaData.getColumnName(i));
                            }

                            // 添加行数据
                            Vector<Object> row = new Vector<>();
                            for (int i = 1; i <= columnCount; i++) {
                                row.add(rs.getObject(i));
                            }
                            baseInfoData.add(row);

                            result.put("baseInfoData", baseInfoData);
                            result.put("baseInfoColumns", columnNames);

                            // 保存专业编号用于查询学院专业信息
                            String majorCode = rs.getString("专业编号");
                            result.put("majorCode", majorCode);
                        }
                    }

                    // 2. 查询健康信息
                    Vector<Vector<Object>> healthData = new Vector<>();
                    String healthQuery = "SELECT * FROM 健康信息 WHERE 学号 = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(healthQuery)) {
                        pstmt.setString(1, finalStudentId);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            ResultSetMetaData metaData = rs.getMetaData();
                            int columnCount = metaData.getColumnCount();

                            Vector<String> columnNames = new Vector<>();
                            for (int i = 1; i <= columnCount; i++) {
                                columnNames.add(metaData.getColumnName(i));
                            }

                            while (rs.next()) {
                                Vector<Object> row = new Vector<>();
                                for (int i = 1; i <= columnCount; i++) {
                                    row.add(rs.getObject(i));
                                }
                                healthData.add(row);
                            }

                            result.put("healthData", healthData);
                            result.put("healthColumns", columnNames);
                        }
                    }

                    // 3. 查询参与组织信息
                    Vector<Vector<Object>> orgData = new Vector<>();
                    String orgQuery = "SELECT * FROM 参与组织 WHERE 学号 = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(orgQuery)) {
                        pstmt.setString(1, finalStudentId);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            ResultSetMetaData metaData = rs.getMetaData();
                            int columnCount = metaData.getColumnCount();

                            Vector<String> columnNames = new Vector<>();
                            for (int i = 1; i <= columnCount; i++) {
                                columnNames.add(metaData.getColumnName(i));
                            }

                            while (rs.next()) {
                                Vector<Object> row = new Vector<>();
                                for (int i = 1; i <= columnCount; i++) {
                                    row.add(rs.getObject(i));
                                }
                                orgData.add(row);
                            }

                            result.put("orgData", orgData);
                            result.put("orgColumns", columnNames);
                        }
                    }

                    // 4. 查询成绩信息
                    Vector<Vector<Object>> scoreData = new Vector<>();
                    String scoreQuery = "SELECT * FROM 成绩表 WHERE 学号 = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(scoreQuery)) {
                        pstmt.setString(1, finalStudentId);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            ResultSetMetaData metaData = rs.getMetaData();
                            int columnCount = metaData.getColumnCount();

                            Vector<String> columnNames = new Vector<>();
                            for (int i = 1; i <= columnCount; i++) {
                                columnNames.add(metaData.getColumnName(i));
                            }

                            while (rs.next()) {
                                Vector<Object> row = new Vector<>();
                                for (int i = 1; i <= columnCount; i++) {
                                    row.add(rs.getObject(i));
                                }
                                scoreData.add(row);
                            }

                            result.put("scoreData", scoreData);
                            result.put("scoreColumns", columnNames);
                        }
                    }

                    // 5. 查询毕业设计信息
                    Vector<Vector<Object>> gradData = new Vector<>();
                    String gradQuery = "SELECT * FROM 毕业设计 WHERE 学号 = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(gradQuery)) {
                        pstmt.setString(1, finalStudentId);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            ResultSetMetaData metaData = rs.getMetaData();
                            int columnCount = metaData.getColumnCount();

                            Vector<String> columnNames = new Vector<>();
                            for (int i = 1; i <= columnCount; i++) {
                                columnNames.add(metaData.getColumnName(i));
                            }

                            while (rs.next()) {
                                Vector<Object> row = new Vector<>();
                                for (int i = 1; i <= columnCount; i++) {
                                    row.add(rs.getObject(i));
                                }
                                gradData.add(row);
                            }

                            result.put("gradData", gradData);
                            result.put("gradColumns", columnNames);
                        }
                    }

                    // 6. 查询学院专业信息
                    String majorCode = (String) result.get("majorCode");
                    if (majorCode != null && !majorCode.isEmpty()) {
                        String majorQuery = "SELECT * FROM 学院专业表 WHERE 专业编号 = ?";
                        try (PreparedStatement pstmt = conn.prepareStatement(majorQuery)) {
                            pstmt.setString(1, majorCode);
                            try (ResultSet rs = pstmt.executeQuery()) {
                                if (rs.next()) {
                                    result.put("college", rs.getString("学院名称"));
                                    result.put("major", rs.getString("专业名称"));
                                }
                            }
                        }
                    }

                    return result;
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,
                            "数据库查询错误: " + ex.getMessage(),
                            "数据库错误",
                            JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            }

            @Override
            protected void done() {
                progressDialog.dispose(); // 关闭进度对话框
                try {
                    Map<String, Object> result = get();
                    if (result != null) {
                        showStudentDataResult(result);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        worker.execute();
        progressDialog.setVisible(true);
    }

    /**
     * 显示学生数据查询结果
     */
    private static void showStudentDataResult(Map<String, Object> result) {
        // 创建结果窗口
        JFrame resultFrame = new JFrame("学生数据查询结果");
        resultFrame.setSize(900, 600);
        resultFrame.setLocationRelativeTo(null);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建选项卡面板
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("微软雅黑", Font.BOLD, 14));

        // 1. 基本信息表
        Vector<Vector<Object>> baseInfoData = (Vector<Vector<Object>>) result.get("baseInfoData");
        Vector<String> baseInfoColumns = (Vector<String>) result.get("baseInfoColumns");
        if (baseInfoData != null && !baseInfoData.isEmpty()) {
            DefaultTableModel model = new DefaultTableModel(baseInfoData, baseInfoColumns);
            JTable table = new JTable(model);
            table.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            table.setRowHeight(25);

            // 添加滚动面板
            JScrollPane scrollPane = new JScrollPane(table);
            tabbedPane.addTab("基本信息", scrollPane);
        }

        // 2. 健康信息表
        Vector<Vector<Object>> healthData = (Vector<Vector<Object>>) result.get("healthData");
        Vector<String> healthColumns = (Vector<String>) result.get("healthColumns");
        if (healthData != null && !healthData.isEmpty()) {
            DefaultTableModel model = new DefaultTableModel(healthData, healthColumns);
            JTable table = new JTable(model);
            table.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            table.setRowHeight(25);

            JScrollPane scrollPane = new JScrollPane(table);
            tabbedPane.addTab("健康信息", scrollPane);
        }

        // 3. 参与组织表
        Vector<Vector<Object>> orgData = (Vector<Vector<Object>>) result.get("orgData");
        Vector<String> orgColumns = (Vector<String>) result.get("orgColumns");
        if (orgData != null && !orgData.isEmpty()) {
            DefaultTableModel model = new DefaultTableModel(orgData, orgColumns);
            JTable table = new JTable(model);
            table.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            table.setRowHeight(25);

            JScrollPane scrollPane = new JScrollPane(table);
            tabbedPane.addTab("参与组织", scrollPane);
        }

        // 4. 成绩表
        Vector<Vector<Object>> scoreData = (Vector<Vector<Object>>) result.get("scoreData");
        Vector<String> scoreColumns = (Vector<String>) result.get("scoreColumns");
        if (scoreData != null && !scoreData.isEmpty()) {
            DefaultTableModel model = new DefaultTableModel(scoreData, scoreColumns);
            JTable table = new JTable(model);
            table.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            table.setRowHeight(25);

            JScrollPane scrollPane = new JScrollPane(table);
            tabbedPane.addTab("成绩信息", scrollPane);
        }

        // 5. 毕业设计表
        Vector<Vector<Object>> gradData = (Vector<Vector<Object>>) result.get("gradData");
        Vector<String> gradColumns = (Vector<String>) result.get("gradColumns");
        if (gradData != null && !gradData.isEmpty()) {
            DefaultTableModel model = new DefaultTableModel(gradData, gradColumns);
            JTable table = new JTable(model);
            table.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            table.setRowHeight(25);

            JScrollPane scrollPane = new JScrollPane(table);
            tabbedPane.addTab("毕业设计", scrollPane);
        }

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // 添加顶部信息面板 - 显示学院和专业
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        infoPanel.setBackground(new Color(220, 240, 255));

        String college = (String) result.get("college");
        String major = (String) result.get("major");

        if (college != null && major != null) {
            JLabel collegeLabel = new JLabel("所属学院: " + college);
            collegeLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
            collegeLabel.setForeground(new Color(0, 100, 0));

            JLabel majorLabel = new JLabel("所属专业: " + major);
            majorLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
            majorLabel.setForeground(new Color(0, 0, 150));

            infoPanel.add(collegeLabel);
            infoPanel.add(Box.createHorizontalStrut(30));
            infoPanel.add(majorLabel);
        } else {
            JLabel noMajorLabel = new JLabel("尚未分配专业");
            noMajorLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
            noMajorLabel.setForeground(Color.RED);
            infoPanel.add(noMajorLabel);
        }

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        resultFrame.add(mainPanel);
        resultFrame.setVisible(true);
    }

    /**
     * 新增方法：查询学生学分信息
     */
    private static void queryCredits() {
        // 检查是否已连接数据库
        if (savedDbName == null || savedDbName.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "尚未连接到数据库，请先连接！",
                    "数据库错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 获取用户输入的学号
        String studentId = JOptionPane.showInputDialog(null,
                "请输入学生学号：",
                "学分查询",
                JOptionPane.QUESTION_MESSAGE);

        // 验证输入
        if (studentId == null) {
            return; // 用户取消输入
        }
        studentId = studentId.trim();
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "学号不能为空！",
                    "输入错误",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 创建进度对话框
        final JDialog progressDialog = new JDialog((JFrame) null, "正在查询", true);
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(new JLabel("正在查询学号: " + studentId + " 的学分信息...", SwingConstants.CENTER), BorderLayout.CENTER);
        progressPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        progressDialog.add(progressPanel);
        progressDialog.pack();
        progressDialog.setLocationRelativeTo(null);
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // 使用SwingWorker在后台执行查询
        String finalStudentId = studentId;
        SwingWorker<Vector<Vector<Object>>, Void> worker = new SwingWorker<Vector<Vector<Object>>, Void>() {
            @Override
            protected Vector<Vector<Object>> doInBackground() throws Exception {
                Vector<Vector<Object>> data = new Vector<>();
                try (Connection conn = DriverManager.getConnection(savedJdbcUrl, savedUsername, savedPassword)) {
                    // 查询学生基本信息
                    String baseInfoQuery = "SELECT 姓名 FROM 基本信息 WHERE 学号 = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(baseInfoQuery)) {
                        pstmt.setString(1, finalStudentId);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (!rs.next()) {
                                JOptionPane.showMessageDialog(null,
                                        "找不到学号为 '" + finalStudentId + "' 的学生！",
                                        "查询错误",
                                        JOptionPane.ERROR_MESSAGE);
                                return null;
                            }
                        }
                    }

                    // 查询学分详细信息
                    String query = "SELECT " +
                            "s.学号, " +
                            "s.姓名, " +
                            "c.课程名称, " +
                            "sc.成绩, " +
                            "sc.补考成绩, " +
                            "sc.重修成绩, " +
                            "sc.绩点, " +
                            "sc.学分 " +
                            "FROM 成绩表 sc " +
                            "JOIN 基本信息 s ON sc.学号 = s.学号 " +
                            "JOIN 课程表 c ON sc.课程编号 = c.课程编号 " +
                            "WHERE sc.学号 = ?";

                    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setString(1, finalStudentId);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            while (rs.next()) {
                                Vector<Object> row = new Vector<>();
                                row.add(rs.getString("学号"));
                                row.add(rs.getString("姓名"));
                                row.add(rs.getString("课程名称"));
                                row.add(rs.getInt("成绩"));

                                int makeUpScore = rs.getInt("补考成绩");
                                row.add(rs.wasNull() ? "无" : makeUpScore);

                                int retakeScore = rs.getInt("重修成绩");
                                row.add(rs.wasNull() ? "无" : retakeScore);

                                row.add(rs.getFloat("绩点"));
                                row.add(rs.getFloat("学分"));

                                data.add(row);
                            }
                        }
                    }

                    // 如果没有查询到学分信息
                    if (data.isEmpty()) {
                        JOptionPane.showMessageDialog(null,
                                "学号 '" + finalStudentId + "' 没有学分记录！",
                                "查询结果",
                                JOptionPane.INFORMATION_MESSAGE);
                        return null;
                    }

                    return data;
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,
                            "数据库查询错误: " + ex.getMessage(),
                            "数据库错误",
                            JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            }

            @Override
            protected void done() {
                progressDialog.dispose(); // 关闭进度对话框
                try {
                    Vector<Vector<Object>> data = get();
                    if (data != null) {
                        showCreditsResult(data);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        worker.execute();
        progressDialog.setVisible(true);
    }

    /**
     * 显示学分查询结果
     */
    private static void showCreditsResult(Vector<Vector<Object>> data) {
        // 创建结果窗口
        JFrame resultFrame = new JFrame("学分查询结果");
        resultFrame.setSize(900, 600);
        resultFrame.setLocationRelativeTo(null);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建列名
        Vector<String> columnNames = new Vector<>();
        columnNames.add("学号");
        columnNames.add("姓名");
        columnNames.add("课程名称");
        columnNames.add("成绩");
        columnNames.add("补考成绩");
        columnNames.add("重修成绩");
        columnNames.add("绩点");
        columnNames.add("学分");

        // 创建表格
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        table.setRowHeight(25);

        // 添加排序功能
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // 添加滚动面板
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 添加底部信息面板
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 20, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.setBackground(new Color(240, 240, 240));

        // 计算统计信息
        float totalCredits = 0;
        float totalEffectiveScores = 0;  // 改为计算有效成绩总和
        int courseCount = 0;

        for (Vector<Object> row : data) {
            Object scoreObj = row.get(3);     // 成绩
            Object makeUpObj = row.get(4);    // 补考成绩
            Object retakeObj = row.get(5);    // 重修成绩
            Object creditObj = row.get(7);    // 学分

            if (creditObj instanceof Number) {
                float credit = ((Number) creditObj).floatValue();
                totalCredits += credit;

                // 计算有效成绩：取三个成绩中的最大值
                float effectiveScore = 0;
                boolean hasScore = false;

                // 处理原始成绩
                if (scoreObj instanceof Number) {
                    float score = ((Number) scoreObj).floatValue();
                    effectiveScore = Math.max(effectiveScore, score);
                    hasScore = true;
                }

                // 处理补考成绩
                if (makeUpObj instanceof Number) {
                    float makeUp = ((Number) makeUpObj).floatValue();
                    effectiveScore = Math.max(effectiveScore, makeUp);
                    hasScore = true;
                } else if ("无".equals(makeUpObj) && hasScore) {
                    // 保留当前有效成绩
                }

                // 处理重修成绩
                if (retakeObj instanceof Number) {
                    float retake = ((Number) retakeObj).floatValue();
                    effectiveScore = Math.max(effectiveScore, retake);
                    hasScore = true;
                } else if ("无".equals(retakeObj) && hasScore) {
                    // 保留当前有效成绩
                }

                // 如果有有效成绩，则计入统计
                if (hasScore) {
                    totalEffectiveScores += effectiveScore;
                    courseCount++;
                }
            }
        }

        // 计算平均值
        float avgCredits = courseCount > 0 ? totalCredits / courseCount : 0;
        float avgScores = courseCount > 0 ? totalEffectiveScores / courseCount : 0; // 使用有效成绩计算平均值
        // 创建信息标签
        JLabel totalLabel = createInfoLabel("总学分: " + String.format("%.2f", totalCredits),
                new Color(0, 100, 0));
        JLabel avgCreditLabel = createInfoLabel("平均学分: " + String.format("%.2f", avgCredits),
                new Color(52, 152, 219));
        JLabel avgScoreLabel = createInfoLabel("平均成绩: " + String.format("%.2f", avgScores),
                new Color(155, 89, 182));

        infoPanel.add(totalLabel);
        infoPanel.add(avgCreditLabel);
        infoPanel.add(avgScoreLabel);

        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        resultFrame.add(mainPanel);
        resultFrame.setVisible(true);
    }

    /**
     * 创建信息标签
     */
    private static JLabel createInfoLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("微软雅黑", Font.BOLD, 16));
        label.setForeground(color);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        label.setOpaque(true);
        label.setBackground(new Color(255, 255, 255, 200));
        return label;
    }

    /**
     * 显示表格数据功能实现
     */
    private static void showTableData() {
        // 检查是否已连接数据库
        if (savedDbName == null || savedDbName.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "尚未连接到数据库，请先连接！",
                    "数据库错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 获取数据库表名列表
        Vector<String> tableNames = new Vector<>();
        try (Connection conn = DriverManager.getConnection(savedJdbcUrl, savedUsername, savedPassword)) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet tables = metaData.getTables(savedDbName, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    tableNames.add(tables.getString("TABLE_NAME"));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "获取表列表失败: " + ex.getMessage(),
                    "数据库错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 检查是否有表
        if (tableNames.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "数据库 '" + savedDbName + "' 中没有找到任何表！",
                    "数据库错误",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 创建表选择对话框
        JComboBox<String> tableComboBox = new JComboBox<>(tableNames);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("请选择要显示的表:"));
        panel.add(tableComboBox);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "选择表", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String selectedTable = (String) tableComboBox.getSelectedItem();
        displayTableData(selectedTable);
    }

    /**
     * 显示指定表的数据
     */
    private static void displayTableData(String tableName) {
        // 创建数据展示窗口
        JFrame tableFrame = new JFrame("表数据: " + tableName);
        tableFrame.setSize(900, 600);
        tableFrame.setLocationRelativeTo(null);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 添加标题
        JLabel titleLabel = new JLabel("表: " + tableName + " (数据库: " + savedDbName + ")");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 创建表格模型
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        table.setRowHeight(25);

        // 添加排序功能
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // 添加滚动面板
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 添加状态标签
        JLabel statusLabel = new JLabel("正在加载数据...");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        tableFrame.add(mainPanel);
        tableFrame.setVisible(true);

        // 使用SwingWorker在后台加载数据
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (Connection conn = DriverManager.getConnection(savedJdbcUrl, savedUsername, savedPassword);
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

                    // 获取列信息
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // 添加列名到表格模型
                    Vector<String> columnNames = new Vector<>();
                    for (int i = 1; i <= columnCount; i++) {
                        columnNames.add(metaData.getColumnName(i));
                    }

                    SwingUtilities.invokeLater(() -> {
                        model.setColumnIdentifiers(columnNames);
                        statusLabel.setText("已加载列信息，正在加载行数据...");
                    });

                    // 添加行数据
                    while (rs.next()) {
                        Vector<Object> rowData = new Vector<>();
                        for (int i = 1; i <= columnCount; i++) {
                            rowData.add(rs.getObject(i));
                        }

                        // 在EDT中更新表格
                        final Vector<Object> finalRow = rowData;
                        SwingUtilities.invokeLater(() -> model.addRow(finalRow));
                    }

                    SwingUtilities.invokeLater(() ->
                            statusLabel.setText("加载完成! 共 " + model.getRowCount() + " 行记录"));

                } catch (SQLException ex) {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("数据加载失败: " + ex.getMessage());
                        JOptionPane.showMessageDialog(tableFrame,
                                "查询表数据时出错: " + ex.getMessage(),
                                "数据库错误",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };

        worker.execute();
    }

    /**
     * 新增方法：添加数据到表
     */
    private static void addDataToTable() {
        // 检查是否已连接数据库
        if (savedDbName == null || savedDbName.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "尚未连接到数据库，请先连接！",
                    "数据库错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 获取数据库表名列表
        Vector<String> tableNames = new Vector<>();
        try (Connection conn = DriverManager.getConnection(savedJdbcUrl, savedUsername, savedPassword)) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet tables = metaData.getTables(savedDbName, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    tableNames.add(tables.getString("TABLE_NAME"));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "获取表列表失败: " + ex.getMessage(),
                    "数据库错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 检查是否有表
        if (tableNames.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "数据库 '" + savedDbName + "' 中没有找到任何表！",
                    "数据库错误",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 创建表选择对话框
        JComboBox<String> tableComboBox = new JComboBox<>(tableNames);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("请选择要添加数据的表:"));
        panel.add(tableComboBox);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "选择表", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String selectedTable = (String) tableComboBox.getSelectedItem();
        createAddDataForm(selectedTable);
    }

    /**
     * 新增方法：创建添加数据的表单
     */
    private static void createAddDataForm(String tableName) {
        JFrame addFrame = new JFrame("添加数据到表: " + tableName);
        addFrame.setSize(600, 500);
        addFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 添加标题
        JLabel titleLabel = new JLabel("添加新记录到: " + tableName);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 创建表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("请输入数据"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 获取表结构
        Vector<JLabel> labels = new Vector<>();
        Vector<JTextField> fields = new Vector<>();
        Vector<Boolean> isPrimaryKey = new Vector<>();
        Vector<Boolean> isNullable = new Vector<>();

        try (Connection conn = DriverManager.getConnection(savedJdbcUrl, savedUsername, savedPassword)) {
            DatabaseMetaData metaData = conn.getMetaData();

            // 获取主键信息
            Vector<String> primaryKeys = new Vector<>();
            try (ResultSet pkResult = metaData.getPrimaryKeys(null, null, tableName)) {
                while (pkResult.next()) {
                    primaryKeys.add(pkResult.getString("COLUMN_NAME"));
                }
            }

            // 获取列信息
            try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
                int row = 0;
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String typeName = columns.getString("TYPE_NAME");
                    int isNullableInt = columns.getInt("NULLABLE");

                    // 创建标签
                    JLabel label = new JLabel(columnName + " (" + typeName + "):");
                    labels.add(label);

                    // 创建输入框
                    JTextField field = new JTextField(20);
                    fields.add(field);

                    // 检查是否主键
                    boolean isPK = primaryKeys.contains(columnName);
                    isPrimaryKey.add(isPK);

                    // 检查是否可为空
                    boolean nullable = (isNullableInt == DatabaseMetaData.columnNullable);
                    isNullable.add(nullable);

                    // 如果是主键，添加星号标记
                    if (isPK) {
                        label.setText("* " + label.getText() + " (主键)");
                        label.setForeground(Color.BLUE);
                    } else if (!nullable) {
                        label.setText("* " + label.getText() + " (必填)");
                    }

                    // 添加到面板
                    gbc.gridx = 0;
                    gbc.gridy = row;
                    formPanel.add(label, gbc);

                    gbc.gridx = 1;
                    formPanel.add(field, gbc);

                    row++;
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "获取表结构失败: " + ex.getMessage(),
                    "数据库错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 添加表单滚动面板
        JScrollPane formScroll = new JScrollPane(formPanel);
        mainPanel.add(formScroll, BorderLayout.CENTER);

        // 添加按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton submitButton = new JButton("提交");
        submitButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        submitButton.setPreferredSize(new Dimension(120, 40));

        JButton cancelButton = new JButton("取消");
        cancelButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        cancelButton.setPreferredSize(new Dimension(120, 40));

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 提交按钮事件
        submitButton.addActionListener(e -> {
            try {
                if (insertData(tableName, fields, labels, isPrimaryKey, isNullable)) {
                    JOptionPane.showMessageDialog(addFrame,
                            "数据添加成功！",
                            "成功",
                            JOptionPane.INFORMATION_MESSAGE);
                    addFrame.dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(addFrame,
                        "添加失败: " + ex.getMessage(),
                        "数据库错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // 取消按钮事件
        cancelButton.addActionListener(e -> addFrame.dispose());

        addFrame.add(mainPanel);
        addFrame.setVisible(true);
    }

    /**
     * 新增方法：执行数据插入
     */
    private static boolean insertData(String tableName, Vector<JTextField> fields,
                                      Vector<JLabel> labels, Vector<Boolean> isPrimaryKey,
                                      Vector<Boolean> isNullable) throws SQLException {
        // 验证必填字段
        for (int i = 0; i < fields.size(); i++) {
            if (!isNullable.get(i) && fields.get(i).getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "字段 '" + labels.get(i).getText().replaceAll("\\*| \\(.*\\)", "") +
                                "' 是必填字段！",
                        "验证错误",
                        JOptionPane.WARNING_MESSAGE);
                fields.get(i).requestFocus();
                return false;
            }
        }

        try (Connection conn = DriverManager.getConnection(savedJdbcUrl, savedUsername, savedPassword)) {
            // 构建INSERT语句
            StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
            StringBuilder values = new StringBuilder(" VALUES (");

            for (int i = 0; i < labels.size(); i++) {
                String fieldName = labels.get(i).getText()
                        .replaceAll("\\*| \\(.*\\)|:", "").trim();

                sql.append(fieldName);
                values.append("?");

                if (i < labels.size() - 1) {
                    sql.append(", ");
                    values.append(", ");
                }
            }

            sql.append(")").append(values).append(")");

            // 准备语句
            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                // 设置参数
                for (int i = 0; i < fields.size(); i++) {
                    String value = fields.get(i).getText().trim();
                    if (value.isEmpty()) {
                        pstmt.setNull(i + 1, Types.NULL);
                    } else {
                        pstmt.setString(i + 1, value);
                    }
                }

                // 执行插入
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        }
    }
}