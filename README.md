# FranSys

FranSys 是一个基于 Spring Boot + Vue 3 + Element Plus 的一体化单体系统，用于把加盟前期的 5 张 SOP 表电子化，并串成一条客户主线流程：

- 客户识别与分级
- 需求评估
- 企业匹配与推荐
- 交付监督与回访
- 售后复购跟进

## 技术栈

- 后端：Spring Boot 3.5、Spring Security、JPA、Flyway、JWT
- 前端：Vue 3、Vite、Element Plus、Pinia、Vue Router
- 数据库：MySQL 8
- 构建：Maven 单体打包，前端内嵌在 Spring Boot 包内

## 默认账号

- 管理员：`admin / Admin@123`
- 销售：`sales / Sales@123`
- 运营：`ops / Ops@123`

## 启动方式

1. 准备 MySQL 8 数据库，例如 `fransys`
2. 配置环境变量，未配置时默认如下：
   - `DB_HOST=localhost`
   - `DB_PORT=3306`
   - `DB_NAME=fransys`
   - `DB_USERNAME=root`
   - `DB_PASSWORD=root`
   - `JWT_SECRET=FranSysJwtSecretFranSysJwtSecret2026`
3. 启动应用：

```powershell
mvn spring-boot:run
```

4. 访问地址：
   - 管理端登录页：`http://localhost:8080/login`
   - 客户公开线索表：`http://localhost:8080/public/lead`

## 打包

```powershell
mvn package
```

打包产物：

- `target/fransys-0.0.1-SNAPSHOT.jar`

## 测试

```powershell
mvn "-Dspring.profiles.active=test" test
```

测试使用 H2 内存数据库，覆盖登录、公开线索提交、交付保存、回访令牌生成和外部回访提交流程。
