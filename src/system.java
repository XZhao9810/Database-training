import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class system {

    private static JTextField userField; // 用户名输入框
    private static JPasswordField passField; // 密码输入框
    private static JTextField dbField; // 新增数据库名称输入框
    private static JLabel statusLabel; // 状态显示标签

    public static void main(String[] args) {
        // 在事件调度线程中创建GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        // 创建主窗口框架
        JFrame frame = new JFrame("MySQL数据库连接工具");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭时退出程序

        // 创建主面板，使用GridBagLayout布局管理器
        JPanel mainPanel = new JPanel(new GridBagLayout()); // 使用灵活布局的面板
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));// 设置边距
        mainPanel.setBackground(new Color(240, 240, 240)); // 背景

        // 创建约束对象，用于控制组件位置
        GridBagConstraints gbc = new GridBagConstraints(); // 布局约束对象
        gbc.insets = new Insets(10, 10, 10, 10);  // 设置组件间距
        gbc.anchor = GridBagConstraints.WEST;      // 组件靠左对齐
        gbc.fill = GridBagConstraints.HORIZONTAL;  // 水平填充

        // ================== 数据库名称输入区域 ==================
        gbc.gridx = 0;  // 第一列
        gbc.gridy = 0;  // 第一行

        // 创建数据库标签
        JLabel dbLabel = new JLabel("数据库名称:");
        dbLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        mainPanel.add(dbLabel, gbc);

        gbc.gridx = 1;  // 第二列
        // 创建数据库名称输入框
        dbField = new JTextField(20);// 文本输入框
        dbField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        dbField.setPreferredSize(new Dimension(300, 35));
        dbField.setText("数据库实训-学生信息管理系统"); // 设置默认数据库名称
        mainPanel.add(dbField, gbc);

        // ================== 用户名输入区域 ==================
        gbc.gridx = 0;  // 第一列
        gbc.gridy = 1;  // 第二行

        // 创建用户名标签
        JLabel userLabel = new JLabel("用户名:");
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        mainPanel.add(userLabel, gbc);

        gbc.gridx = 1;  // 第二列
        // 创建用户名输入框
        userField = new JTextField(20);
        userField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        userField.setPreferredSize(new Dimension(300, 35));
        userField.setText(""); // 设置默认用户名
        mainPanel.add(userField, gbc);

        // ================== 密码输入区域 ==================
        gbc.gridx = 0;  // 第一列
        gbc.gridy = 2;  // 第三行

        // 创建密码标签
        JLabel passLabel = new JLabel("密码:");
        passLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        mainPanel.add(passLabel, gbc);

        gbc.gridx = 1;  // 第二列
        // 创建密码输入框（使用JPasswordField以隐藏输入内容）
        passField = new JPasswordField(20);
        passField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        passField.setPreferredSize(new Dimension(300, 35));
        mainPanel.add(passField, gbc);

        // ================== 连接按钮区域 ==================
        gbc.gridx = 0;  // 第一列
        gbc.gridy = 3;  // 第四行
        gbc.gridwidth = 2; // 跨两列
        gbc.anchor = GridBagConstraints.CENTER; // 居中显示

        // 创建连接按钮
        JButton connectButton = new JButton("连接到MySQL数据库");
        connectButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        connectButton.setPreferredSize(new Dimension(250, 40));
        connectButton.setBackground(new Color(70, 130, 180)); // 钢蓝色
        connectButton.setForeground(Color.WHITE);
        connectButton.setFocusPainted(false);

        // 添加按钮点击事件监听器
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToDatabase(); // 调用数据库连接方法
            }
        });

        mainPanel.add(connectButton, gbc);

        // ================== 状态显示区域 ==================
        gbc.gridy = 4;  // 第五行
        statusLabel = new JLabel("就绪，请输入数据库凭据");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(100, 100, 100)); // 深灰色
        mainPanel.add(statusLabel, gbc);

        // 重置布局约束
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // 将主面板添加到窗口
        frame.setContentPane(mainPanel);

        // 设置窗口大小为800x600
        frame.setSize(800, 600);

        // 窗口居中显示
        frame.setLocationRelativeTo(null);

        // 显示窗口
        frame.setVisible(true);
    }

    /**
     * 尝试连接到MySQL数据库
     */
    private static void connectToDatabase() {
        // 获取输入的数据库名称
        String dbName = dbField.getText().trim();
        // 获取输入的用户名和密码
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

        // 将密码字符数组转换为字符串
        String passwordStr = new String(password);

        // 数据库连接参数（包含数据库名称）
        String jdbcUrl = "jdbc:mysql://localhost:3306/" + dbName;
        String driver = "com.mysql.cj.jdbc.Driver";

        // 显示连接状态
        statusLabel.setText("正在尝试连接到数据库 '" + dbName + "'...");
        statusLabel.setForeground(new Color(0, 100, 0)); // 深绿色

        // 使用SwingWorker在后台线程执行数据库连接
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Connection conn = null;
                try {
                    // 1. 加载JDBC驱动
                    Class.forName(driver);

                    // 2. 建立数据库连接
                    conn = DriverManager.getConnection(jdbcUrl, username, passwordStr);

                    // 3. 检查连接是否成功
                    return conn != null && !conn.isClosed();
                } catch (ClassNotFoundException ex) {
                    statusLabel.setText("错误：找不到MySQL JDBC驱动！");
                    return false;
                } catch (SQLException ex) {
                    // 根据错误代码显示更具体的错误信息
                    if (ex.getErrorCode() == 1049) { // 1049 是未知数据库的错误代码
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
                    // 4. 关闭连接
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException ex) {
                            // 关闭连接时的异常可以忽略
                        }
                    }
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        statusLabel.setText("成功连接到数据库 '" + dbName + "'！");
                        statusLabel.setForeground(new Color(0, 150, 0)); // 绿色
                    } else {
                        statusLabel.setForeground(Color.RED);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("连接过程中发生错误: " + ex.getMessage());
                    statusLabel.setForeground(Color.RED);
                }
            }
        };

        worker.execute(); // 启动后台任务
    }
}