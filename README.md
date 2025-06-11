# 标题：《基于Java的学生管理系统》

# 配置要求

1. Java版本：OpenJDK version 21.0.1及以上
2. mysql连接Java的必要jar包（需要到mysql官方下载/系统自带的也有）

# 注意事项

1. 需要根据系统更改端口建议通过搜索找到该处，位置：`String jdbcUrl = "jdbc:mysql://localhost:3306/" + dbName;`
2. 需要自行配置数据库和表（与作者提供的应相同），如果数据库不同，会造成系统异常显示表数据，需要修改java中的代码。
