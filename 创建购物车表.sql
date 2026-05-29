-- 创建购物车表
CREATE TABLE cart_item (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '购物车项ID，自增主键',
    user_id INT NOT NULL COMMENT '用户ID',
    book_id INT NOT NULL COMMENT '图书ID',
    book_name VARCHAR(100) NOT NULL COMMENT '图书名称',
    author VARCHAR(50) COMMENT '作者',
    price DECIMAL(10, 2) NOT NULL COMMENT '单价',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';
