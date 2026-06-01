-- 创建订单表
CREATE TABLE orders (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID，自增主键',
    user_id INT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    order_status TINYINT DEFAULT 0 COMMENT '订单状态：0未支付 1已支付/未发货 2已发货 3已收货',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    pay_time DATETIME COMMENT '支付时间',
    ship_time DATETIME COMMENT '发货时间',
    receive_time DATETIME COMMENT '收货时间',
    INDEX idx_user_id (user_id),
    INDEX idx_order_status (order_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 创建订单项表
CREATE TABLE order_item (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '订单项ID，自增主键',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    book_id INT NOT NULL COMMENT '图书ID',
    book_name VARCHAR(100) NOT NULL COMMENT '图书名称',
    author VARCHAR(50) COMMENT '作者',
    price DECIMAL(10, 2) NOT NULL COMMENT '单价',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';
