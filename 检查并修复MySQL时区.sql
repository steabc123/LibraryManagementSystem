-- ============================================
-- MySQL 时区检查和修复脚本
-- ============================================

-- 1. 查看当前 MySQL 时区设置
SELECT 
    @@global.time_zone AS '全局时区',
    @@session.time_zone AS '会话时区',
    @@system_time_zone AS '系统时区';

-- 2. 查看当前时间
SELECT 
    NOW() AS 'MySQL当前时间',
    UTC_TIMESTAMP() AS 'UTC时间',
    CURTIME() AS '当前时间',
    TIMEDIFF(NOW(), UTC_TIMESTAMP()) AS '与UTC的时差';

-- 3. 如果时差不是 +08:00，执行以下命令设置时区
-- 注意：这需要 MySQL 管理员权限
SET GLOBAL time_zone = '+8:00';
SET time_zone = '+8:00';

-- 4. 刷新权限使设置生效
FLUSH PRIVILEGES;

-- 5. 验证设置是否成功
SELECT 
    @@global.time_zone AS '新的全局时区',
    @@session.time_zone AS '新的会话时区',
    NOW() AS '设置后的MySQL时间';

-- 6. 如果上述方法无效，可以尝试在 MySQL 配置文件 (my.ini 或 my.cnf) 中添加：
-- [mysqld]
-- default-time-zone = '+8:00'
-- 然后重启 MySQL 服务

-- 7. 检查表中的实际数据
SELECT 
    record_id,
    borrow_time,
    return_time,
    NOW() AS '当前数据库时间'
FROM borrow_record 
ORDER BY borrow_time DESC 
LIMIT 5;
