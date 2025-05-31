import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class main {
    public static void main(String[] args) throws Exception {

        //注册驱动
        Class.forName("com.mysql.jdbc.Driver");

        //获取连接
        String url = "jdbc:mysql://127.0.0.1:3306/数据库实训-学生信息管理系统";
        String username = "";
        String password = "";

        //输入上面数据
        System.out.print("输入用户名：");
        Scanner sc = new Scanner(System.in);
        username = sc.next();
        System.out.print("输入密码：");
        password = sc.next();

        Connection connection = DriverManager.getConnection(url, username, password);

        //定义sql
        String sql = "update 健康信息 set 体重 = 65 where 学号 = 202301001";

        Statement statement = connection.createStatement();

        //返回受影响的行数
        int count = statement.executeUpdate(sql);

        //打印count
        System.out.println(count);

        //关闭连接
        statement.close();
        connection.close();
    }
}