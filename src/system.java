import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
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
    private static String savedDriver = "com.mysql.cj.jdbc.Driver";

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
        // 添加事件监听器
        displayButton.addActionListener(e -> showTableData());
        functionPanel.add(displayButton);

        // 修改数据按钮
        JButton editButton = createFunctionButton("修改数据", new Color(155, 89, 182));
        functionPanel.add(editButton);

        // 查询数据按钮
        JButton searchButton = createFunctionButton("查询数据", new Color(241, 196, 15));
        functionPanel.add(searchButton);

        // 统计学分按钮
        JButton statsButton = createFunctionButton("统计学分", new Color(230, 126, 34));
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
            if (!text.equals("显示数据")) {
                JOptionPane.showMessageDialog(null,
                        "【" + text + "】功能正在开发中...",
                        "功能提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return button;
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
}