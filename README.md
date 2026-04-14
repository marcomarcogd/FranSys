# FranSys

FranSys 是一个基于 Spring Boot + Vue 3 + Element Plus 的一体化单体系统，当前聚焦内部 CRM 与供给协同，覆盖客户管理、跟进记录、产品推荐、企业库、产品库、套餐方案、账号权限和仪表盘。

## 技术栈

- 后端：Spring Boot 3.5、Spring Security、JPA、Flyway、JWT
- 前端：Vue 3、Vite、Element Plus、Pinia、Vue Router
- 数据库：MySQL 8
- 构建：Maven 单体打包，前端内嵌在 Spring Boot 包内

## 默认账号

- 管理员：`admin / 123456`
- 销售主管：`sales_leader / Leader@123`
- 销售：`sales / Sales@123`
- 运营：`ops / Ops@123`

## 启动方式

1. 准备 MySQL 8 数据库，例如 `fransys`
2. 配置环境变量，未配置时默认如下：
   - `DB_HOST=localhost`
   - `DB_PORT=3306`
   - `DB_NAME=fransys`
   - `DB_USERNAME=root`
   - `DB_PASSWORD=123456`
   - `JWT_SECRET=FranSysJwtSecretFranSysJwtSecret2026`
3. 启动应用：

```powershell
mvn spring-boot:run
```

4. 访问地址：
   - 管理端登录页：`http://localhost:8080/login`

## 打包

```powershell
mvn package
```

打包产物：

- `target/fransys-0.0.1-SNAPSHOT.jar`

## 校验

```powershell
npm run build
mvn -DskipTests package
```
