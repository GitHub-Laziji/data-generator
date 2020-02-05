# Data Generator
![](https://img.shields.io/github/languages/top/github-laziji/data-generator.svg?style=flat)
![](https://img.shields.io/github/stars/gitHub-laziji/data-generator.svg?style=social)

数据表随机数据生成器

## 下载
[https://github.com/GitHub-Laziji/data-generator/releases](https://github.com/GitHub-Laziji/data-generator/releases)

## 示例

### 使用
```
java -jar ./data-generator-1.0-full.jar ./config.json
```

### config.json
```json
{
  "database": {
    "driverPath": "./mysql-connector-java-8.0.13.jar",
    "driverClass": "com.mysql.cj.jdbc.Driver",
    "url": "jdbc:mysql://localhost:3306/test?serverTimezone=Asia/Shanghai",
    "username": "root",
    "password": ""
  },
  "tables": [
    {
      "name": "user_profile",
      "amount": 100,
      "rules": {
        "name": "[A-Z][a-z]{2,4}",
        "age": "[1-9]\\d?",
        "email": "\\w{6,12}@[a-z0-9]{3,4}\\.(com|cn)",
        "mobile": "1(3|5|7|8)\\d{9}",
        "blog_url": "https?://[\\w-]+(\\.[\\w-]+){1,2}(/[\\w-]{3,6}){0,2}(\\?[\\w_]{4,6}=[\\w_]{4,6}(&[\\w_]{4,6}=[\\w_]{4,6}){0,2})?"
      }
    }
  ]
}
```


## 开发
### 安装依赖
本项目使用了依赖[https://github.com/GitHub-Laziji/reverse-regexp](https://github.com/GitHub-Laziji/reverse-regexp)
```
git clone https://github.com/GitHub-Laziji/reverse-regexp.git
cd reverse-regexp
mvn install
```

### 构建项目
```
mvn assembly:assembly
```