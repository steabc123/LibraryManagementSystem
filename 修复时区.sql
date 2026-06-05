-- 检查当前MySQL时区设置
SELECT @@global.time_zone, @@session.time_zone;

-- 如果显示的是 SYSTEM，需要设置为东八区
-- 方法1：设置全局时区为东八区（需要重启MySQL或重新连接才生效）
SET GLOBAL time_zone = '+8:00';

-- 方法2：设置当前会话时区为东八区（立即生效，但只对当前连接有效）
SET time_zone = '+8:00';

-- 验证时区是否设置成功
SELECT NOW(), CURTIME(), UTC_TIMESTAMP();

-- 查看当前时间是否正确（应该显示北京时间）
SELECT 
    NOW() as '当前时间',
    UTC_TIMESTAMP() as 'UTC时间',
    TIMEDIFF(NOW(), UTC_TIMESTAMP()) as '时差';

-- ========================================
-- 永久修复MySQL时区（重启后仍然有效）
-- ========================================
-- 方法1：修改 MySQL 配置文件（my.ini 或 my.cnf）
-- 在 [mysqld] 部分添加：
-- default-time-zone = '+8:00'
-- 然后重启 MySQL 服务

-- 方法2：使用 Docker 运行 MySQL 时，添加环境变量：
-- docker run -e TZ=Asia/Shanghai ...
-- 或者在 docker-compose.yml 中：
-- environment:
--   - TZ=Asia/Shanghai

-- 验证永久配置是否生效（重启MySQL后执行）
SELECT @@global.time_zone, @@session.time_zone, NOW(), UTC_TIMESTAMP();
