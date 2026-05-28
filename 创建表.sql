CREATE TABLE user_info (
                           id INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID，自增主键',
                           username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名，唯一',
                           password VARCHAR(100) NOT NULL COMMENT '登录密码',
                           role VARCHAR(20) DEFAULT 'user' COMMENT '角色：admin或user',
                           status TINYINT DEFAULT 1 COMMENT '状态：1正常 0禁用',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

CREATE TABLE book_info (
                           id INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '图书ID，自增主键',
                           book_name VARCHAR(100) NOT NULL COMMENT '图书名称',
                           author VARCHAR(50) COMMENT '作者',
                           isbn VARCHAR(20) COMMENT 'ISBN编号',
                           publisher VARCHAR(50) COMMENT '出版社',
                           price DECIMAL(10, 2) COMMENT '图书定价',
                           stock INT DEFAULT 0 COMMENT '库存数量',
                           create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书信息表';

CREATE TABLE borrow_record (
                               record_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '借阅记录ID，自增主键',
                               user_id INT NOT NULL COMMENT '借阅用户ID',
                               book_id INT NOT NULL COMMENT '借阅图书ID',
                               borrow_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间',
                               return_time DATETIME COMMENT '实际归还时间',
                               borrow_status TINYINT DEFAULT 0 COMMENT '状态：0借阅中 1已归还'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

CREATE TABLE book_category (
                               category_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID，自增主键',
                               category_name VARCHAR(20) NOT NULL COMMENT '分类名称',
                               category_desc VARCHAR(100) COMMENT '分类描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

INSERT INTO book_info VALUES(1,'C++数据结构与算法','耿祥义、张跃平','9787302687443','清华大学出版社',59.8,10,'2026-5-28');
INSERT INTO book_info VALUES(2,'Spring Boot从入门到精通','明日科技','9787302648604','明日科技出版社',99.8,2,'2026-5-29');
INSERT INTO book_info VALUES(3,'操作系统','汤小丹','9787115686572','人民邮电出版社',69.8,10,'2026-6-1');
INSERT INTO book_info VALUES(4,'计算机组成原理','王万生','978730265476','清华大学出版社',58.0,5,'2026-5-31');
INSERT INTO book_info VALUES(5,'计算机网络','谢希仁','9787121302954','电子工业出版社',59.8,10,'2026-5-28');
INSERT INTO book_info VALUES(6,'数据结构题集(C语言版)','严蔚敏','9787302033141','清华大学出版社',16,30,'2026-5-28');
