CREATE TABLE litemall_goods_product (
  id int NOT NULL AUTO_INCREMENT,
  goods_id int NOT NULL DEFAULT 0,
  specifications varchar(1023) NOT NULL,
  price decimal(10,2) NOT NULL DEFAULT 0.00,
  number int NOT NULL DEFAULT 0,
  url varchar(125) DEFAULT NULL,
  add_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  deleted tinyint(1) DEFAULT 0,
  PRIMARY KEY (id)
);

CREATE TABLE litemall_order (
  id int NOT NULL AUTO_INCREMENT,
  user_id int NOT NULL,
  order_sn varchar(63) NOT NULL,
  order_status smallint NOT NULL,
  aftersale_status smallint DEFAULT 0,
  consignee varchar(63) NOT NULL,
  mobile varchar(63) NOT NULL,
  address varchar(127) NOT NULL,
  message varchar(512) NOT NULL DEFAULT '',
  goods_price decimal(10,2) NOT NULL,
  freight_price decimal(10,2) NOT NULL,
  coupon_price decimal(10,2) NOT NULL,
  integral_price decimal(10,2) NOT NULL,
  groupon_price decimal(10,2) NOT NULL,
  order_price decimal(10,2) NOT NULL,
  actual_price decimal(10,2) NOT NULL,
  pay_id varchar(63) DEFAULT NULL,
  pay_time datetime DEFAULT NULL,
  ship_sn varchar(63) DEFAULT NULL,
  ship_channel varchar(63) DEFAULT NULL,
  ship_time datetime DEFAULT NULL,
  refund_amount decimal(10,2) DEFAULT NULL,
  refund_type varchar(63) DEFAULT NULL,
  refund_content varchar(127) DEFAULT NULL,
  refund_time datetime DEFAULT NULL,
  confirm_time datetime DEFAULT NULL,
  comments smallint DEFAULT 0,
  end_time datetime DEFAULT NULL,
  add_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  deleted tinyint(1) DEFAULT 0,
  PRIMARY KEY (id),
  KEY order_sn (order_sn)
);

CREATE TABLE litemall_order_goods (
  id int NOT NULL AUTO_INCREMENT,
  order_id int NOT NULL DEFAULT 0,
  goods_id int NOT NULL DEFAULT 0,
  goods_name varchar(127) NOT NULL DEFAULT '',
  goods_sn varchar(63) NOT NULL DEFAULT '',
  product_id int NOT NULL DEFAULT 0,
  number smallint NOT NULL DEFAULT 0,
  price decimal(10,2) NOT NULL DEFAULT 0.00,
  specifications varchar(1023) NOT NULL,
  pic_url varchar(255) NOT NULL DEFAULT '',
  comment int DEFAULT 0,
  add_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  deleted tinyint(1) DEFAULT 0,
  PRIMARY KEY (id),
  KEY order_id (order_id),
  KEY goods_id (goods_id)
);

CREATE TABLE litemall_coupon_user (
  id int NOT NULL AUTO_INCREMENT,
  user_id int DEFAULT NULL,
  coupon_id int DEFAULT NULL,
  status smallint DEFAULT NULL,
  used_time datetime DEFAULT NULL,
  start_time datetime DEFAULT NULL,
  end_time datetime DEFAULT NULL,
  order_id int DEFAULT NULL,
  add_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  deleted tinyint(1) DEFAULT 0,
  PRIMARY KEY (id),
  KEY order_id (order_id)
);

CREATE TABLE litemall_groupon (
  id int NOT NULL AUTO_INCREMENT,
  order_id int DEFAULT NULL,
  groupon_id int DEFAULT NULL,
  rules_id int DEFAULT NULL,
  user_id int DEFAULT NULL,
  share_url varchar(255) DEFAULT NULL,
  creator_user_id int DEFAULT NULL,
  creator_user_time datetime DEFAULT NULL,
  status smallint DEFAULT NULL,
  add_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  deleted tinyint(1) DEFAULT 0,
  PRIMARY KEY (id),
  KEY order_id (order_id),
  KEY groupon_id (groupon_id)
);

INSERT INTO litemall_goods_product
  (id, goods_id, specifications, price, number, url, add_time, update_time, deleted)
VALUES
  (1, 100, '["standard"]', 100.00, 5, '', now(), now(), 0);

INSERT INTO litemall_order
  (id, user_id, order_sn, order_status, consignee, mobile, address, message,
   goods_price, freight_price, coupon_price, integral_price, groupon_price,
   order_price, actual_price, comments, add_time, update_time, deleted)
VALUES
  (1, 1, '20260711000001', 101, '测试用户', '13800000000', '测试地址', '',
   100.00, 0.00, 0.00, 0.00, 0.00, 100.00, 100.00, 0, now(), now(), 0);

INSERT INTO litemall_coupon_user
  (id, user_id, coupon_id, status, used_time, start_time, end_time, order_id, add_time, update_time, deleted)
VALUES
  (1, 1, 2001, 1, now(), '2026-07-01 00:00:00', '2026-07-31 23:59:59', 1, now(), now(), 0);

INSERT INTO litemall_groupon
  (id, order_id, groupon_id, rules_id, user_id, share_url, creator_user_id, creator_user_time, status, add_time, update_time, deleted)
VALUES
  (10, 900, 0, 7, 1, '/storage/source.png', 1, now(), 1, now(), now(), 0),
  (11, 2, 10, 7, 2, NULL, 1, now(), 1, now(), now(), 0),
  (12, 3, 10, 7, 3, NULL, 1, now(), 0, now(), now(), 0),
  (13, 4, 10, 7, 4, NULL, 1, now(), 1, now(), now(), 1);
