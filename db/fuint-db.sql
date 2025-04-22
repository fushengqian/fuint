/*Table structure for table `mt_address` */

DROP TABLE IF EXISTS `mt_address`;

CREATE TABLE `mt_address` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `USER_ID` int NOT NULL DEFAULT '0' COMMENT '用户ID',
  `NAME` varchar(30) NOT NULL DEFAULT '' COMMENT '收货人姓名',
  `MOBILE` varchar(20) DEFAULT '' COMMENT '收货手机号',
  `PROVINCE_ID` int unsigned DEFAULT '0' COMMENT '省份ID',
  `CITY_ID` int unsigned DEFAULT '0' COMMENT '城市ID',
  `REGION_ID` int DEFAULT '0' COMMENT '区/县ID',
  `DETAIL` varchar(255) DEFAULT '' COMMENT '详细地址',
  `IS_DEFAULT` char(1) DEFAULT 'N' COMMENT '是否默认',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员地址表';

/*Table structure for table `mt_article` */

DROP TABLE IF EXISTS `mt_article`;

CREATE TABLE `mt_article` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `STORE_ID` int NOT NULL DEFAULT '0' COMMENT '目录ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `TITLE` varchar(100) DEFAULT '' COMMENT '标题',
  `BRIEF` varchar(500) DEFAULT '' COMMENT '简介',
  `URL` varchar(100) DEFAULT '' COMMENT '链接地址',
  `IMAGE` varchar(200) DEFAULT '' COMMENT '图片地址',
  `DESCRIPTION` text COMMENT '描述',
  `CLICK` int DEFAULT '0' COMMENT '点击次数',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT NULL COMMENT '最后操作人',
  `SORT` int DEFAULT '0' COMMENT '排序',
  `STATUS` char(1) DEFAULT 'A' COMMENT 'A：正常；N：禁用；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='文章表';

/*Table structure for table `mt_balance` */

DROP TABLE IF EXISTS `mt_balance`;

CREATE TABLE `mt_balance` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '所属商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '所属店铺ID',
  `MOBILE` varchar(11) DEFAULT '' COMMENT '手机号',
  `USER_ID` int NOT NULL DEFAULT '0' COMMENT '用户ID',
  `ORDER_SN` varchar(32) DEFAULT '' COMMENT '订单号',
  `AMOUNT` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '余额变化数量',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `DESCRIPTION` varchar(200) DEFAULT '' COMMENT '备注说明',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A正常；D作废',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='余额变化表';

/*Table structure for table `mt_banner` */

DROP TABLE IF EXISTS `mt_banner`;

CREATE TABLE `mt_banner` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `TITLE` varchar(100) DEFAULT '' COMMENT '标题',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '所属商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '所属店铺ID',
  `URL` varchar(100) DEFAULT '' COMMENT '链接地址',
  `IMAGE` varchar(200) DEFAULT '' COMMENT '图片地址',
  `DESCRIPTION` text COMMENT '描述',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT NULL COMMENT '最后操作人',
  `SORT` int DEFAULT '0' COMMENT '排序',
  `STATUS` char(1) DEFAULT 'A' COMMENT 'A：正常；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=216 DEFAULT CHARSET=utf8 COMMENT='会员端焦点图表';

/*Table structure for table `mt_book` */

DROP TABLE IF EXISTS `mt_book`;

CREATE TABLE `mt_book` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `NAME` varchar(255) DEFAULT NULL COMMENT '预约名称',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '所属商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
  `TYPE` varchar(30) DEFAULT 'time' COMMENT '预约方式，time：按时间预约；staff:按员工预约',
  `LOGO` varchar(255) DEFAULT '' COMMENT 'LOGO图片',
  `GOODS_ID` int DEFAULT '0' COMMENT '预约服务ID',
  `CATE_ID` int DEFAULT '0' COMMENT '预约分类',
  `SERVICE_DATES` varchar(1000) DEFAULT '' COMMENT '可预约日期',
  `SERVICE_TIMES` varchar(1000) DEFAULT '' COMMENT '可预约时间段',
  `SERVICE_STAFF_IDS` varchar(1000) DEFAULT '' COMMENT '可预约员工ID',
  `DESCRIPTION` varchar(1000) DEFAULT '' COMMENT '预约说明',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `SORT` int DEFAULT '0' COMMENT '排序',
  `STATUS` char(1) DEFAULT 'A' COMMENT '订单状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='预约表';

/*Table structure for table `mt_book_cate` */

DROP TABLE IF EXISTS `mt_book_cate`;

CREATE TABLE `mt_book_cate` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int NOT NULL COMMENT '所属商户',
  `STORE_ID` int DEFAULT NULL COMMENT '所属店铺',
  `NAME` varchar(50) NOT NULL COMMENT '分类名称',
  `LOGO` varchar(255) DEFAULT NULL COMMENT '封面图片',
  `DESCRIPTION` varchar(500) DEFAULT NULL COMMENT '说明',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `SORT` int DEFAULT NULL COMMENT '排序',
  `OPERATOR` varchar(30) DEFAULT NULL COMMENT '最后操作人',
  `STATUS` char(1) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='预约分类';

/*Table structure for table `mt_book_item` */

DROP TABLE IF EXISTS `mt_book_item`;

CREATE TABLE `mt_book_item` (
    `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `MERCHANT_ID` int DEFAULT '0' COMMENT '所属商户ID',
    `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
    `CATE_ID` int DEFAULT '0' COMMENT '预约分类ID',
    `BOOK_ID` int DEFAULT '0' COMMENT '预约ID',
    `USER_ID` int DEFAULT '0' COMMENT '预约用户ID',
    `GOODS_ID` int DEFAULT '0' COMMENT '预约服务ID',
    `VERIFY_CODE` varchar(10) DEFAULT '' COMMENT '核销码',
    `CONTACT` varchar(30) DEFAULT NULL COMMENT '预约联系人',
    `MOBILE` varchar(30) DEFAULT NULL COMMENT '预约手机号',
    `SERVICE_DATE` varchar(100) DEFAULT NULL COMMENT '预约日期',
    `SERVICE_TIME` varchar(100) DEFAULT NULL COMMENT '预约时间段',
    `SERVICE_STAFF_ID` int DEFAULT NULL COMMENT '预约员工ID',
    `REMARK` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '预约说明',
    `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
    `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
    `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
    `STATUS` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT 'A' COMMENT '状态',
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='预约详情表';


/*Table structure for table `mt_cart` */

DROP TABLE IF EXISTS `mt_cart`;

CREATE TABLE `mt_cart` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `USER_ID` int NOT NULL DEFAULT '0' COMMENT '会员ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
  `IS_VISITOR` char(1) DEFAULT 'N' COMMENT '是否游客',
  `HANG_NO` varchar(10) DEFAULT '' COMMENT '挂单号',
  `SKU_ID` int DEFAULT '0' COMMENT 'skuID',
  `GOODS_ID` int DEFAULT '0' COMMENT '商品ID',
  `NUM` int DEFAULT '1' COMMENT '数量',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='购物车';

/*Table structure for table `mt_commission_cash` */

DROP TABLE IF EXISTS `mt_commission_cash`;

CREATE TABLE `mt_commission_cash` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `SETTLE_NO` varchar(32) DEFAULT NULL COMMENT '结算单号',
  `UUID` varchar(32) DEFAULT NULL COMMENT '结算UUID',
  `MERCHANT_ID` int NOT NULL COMMENT '商户ID',
  `STORE_ID` int DEFAULT NULL COMMENT '店铺ID',
  `USER_ID` int DEFAULT NULL COMMENT '会员ID',
  `STAFF_ID` int DEFAULT NULL COMMENT '员工ID',
  `AMOUNT` decimal(10,2) DEFAULT NULL COMMENT '金额',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `DESCRIPTION` varchar(500) DEFAULT NULL COMMENT '备注信息',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A:待确认,B:已确认,C:已支付,D:已作废',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分佣提现记录表';

/*Table structure for table `mt_commission_log` */

DROP TABLE IF EXISTS `mt_commission_log`;

CREATE TABLE `mt_commission_log` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int NOT NULL COMMENT '商户ID',
  `TARGET` varchar(30) DEFAULT '' COMMENT '对象,member:会员分销；staff：员工提成',
  `TYPE` varchar(30) NOT NULL COMMENT '分佣类型',
  `LEVEL` int DEFAULT '1' COMMENT '分销等级',
  `USER_ID` int DEFAULT NULL COMMENT '会员ID',
  `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
  `STAFF_ID` int DEFAULT '0' COMMENT '员工ID',
  `ORDER_ID` int DEFAULT '0' COMMENT '订单ID',
  `AMOUNT` decimal(10,2) DEFAULT NULL COMMENT '分佣金额',
  `RULE_ID` int DEFAULT NULL COMMENT '分佣规则ID',
  `RULE_ITEM_ID` int DEFAULT NULL COMMENT '分佣规则项ID',
  `DESCRIPTION` varchar(500) DEFAULT NULL COMMENT '备注信息',
  `SETTLE_UUID` varchar(32) DEFAULT NULL COMMENT '结算uuid',
  `CASH_ID` int DEFAULT NULL COMMENT '提现记录ID',
  `IS_CASH` char(1) DEFAULT 'N' COMMENT '是否提现，Y：是；N：否',
  `CASH_TIME` datetime DEFAULT NULL COMMENT '提现时间',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A：待结算；B：已结算；C：已作废',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='佣金记录表';

/*Table structure for table `mt_commission_relation` */

DROP TABLE IF EXISTS `mt_commission_relation`;

CREATE TABLE `mt_commission_relation` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int NOT NULL COMMENT '商户ID',
  `USER_ID` int DEFAULT NULL COMMENT '邀请会员ID',
  `LEVEL` int DEFAULT '1' COMMENT '等级',
  `INVITE_CODE` varchar(32) DEFAULT '' COMMENT '邀请码',
  `SUB_USER_ID` int DEFAULT NULL COMMENT '被邀请会员ID',
  `DESCRIPTION` varchar(500) DEFAULT NULL COMMENT '说明',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A：激活；D：删除',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员分销关系表';

/*Table structure for table `mt_commission_rule` */

DROP TABLE IF EXISTS `mt_commission_rule`;

CREATE TABLE `mt_commission_rule` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `NAME` varchar(100) DEFAULT '' COMMENT '规则名称',
  `TYPE` varchar(30) DEFAULT NULL COMMENT '方案类型',
  `TARGET` varchar(30) DEFAULT '' COMMENT '方案对象,member:会员分销；staff：员工提成',
  `MERCHANT_ID` int NOT NULL COMMENT '商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
  `STORE_IDS` varchar(500) DEFAULT '' COMMENT '适用店铺',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `DESCRIPTION` varchar(1000) DEFAULT NULL COMMENT '备注信息',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A：激活；N：禁用；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='方案规则表';

/*Table structure for table `mt_commission_rule_item` */

DROP TABLE IF EXISTS `mt_commission_rule_item`;

CREATE TABLE `mt_commission_rule_item` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `TYPE` varchar(30) DEFAULT NULL COMMENT '分佣类型',
  `RULE_ID` int NOT NULL DEFAULT '0' COMMENT '规则ID',
  `MERCHANT_ID` int NOT NULL COMMENT '商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '适用店铺',
  `TARGET` varchar(30) DEFAULT NULL COMMENT '分佣对象',
  `TARGET_ID` int NOT NULL DEFAULT '0' COMMENT '对象ID',
  `METHOD` varchar(30) DEFAULT NULL COMMENT '提成方式（按比例/固定金额）',
  `STORE_IDS` varchar(500) DEFAULT '' COMMENT '适用店铺',
  `GUEST` decimal(10,2) DEFAULT NULL COMMENT '散客佣金',
  `SUB_GUEST` decimal(10,2) DEFAULT NULL COMMENT '二级散客佣金',
  `MEMBER` decimal(10,2) DEFAULT NULL COMMENT '会员佣金',
  `SUB_MEMBER` decimal(10,2) DEFAULT NULL COMMENT '二级会员佣金',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A：激活；N：禁用；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分佣提成规则项目表';

/*Table structure for table `mt_confirm_log` */

DROP TABLE IF EXISTS `mt_confirm_log`;

CREATE TABLE `mt_confirm_log` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `CODE` varchar(32) NOT NULL DEFAULT '' COMMENT '编码',
  `AMOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '核销金额',
  `COUPON_ID` int DEFAULT '0' COMMENT '卡券ID',
  `USER_COUPON_ID` int NOT NULL DEFAULT '0' COMMENT '用户券ID',
  `ORDER_ID` int DEFAULT '0' COMMENT '订单ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `USER_ID` int NOT NULL DEFAULT '0' COMMENT '卡券所属用户ID',
  `OPERATOR_USER_ID` int DEFAULT NULL COMMENT '核销者用户ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `STORE_ID` int NOT NULL DEFAULT '0' COMMENT '核销店铺ID',
  `STATUS` varchar(1) NOT NULL COMMENT '状态，A正常核销；D：撤销使用',
  `CANCEL_TIME` datetime DEFAULT NULL COMMENT '撤销时间',
  `OPERATOR` varchar(30) DEFAULT NULL COMMENT '最后操作人',
  `OPERATOR_FROM` varchar(30) DEFAULT 'mt_user' COMMENT '操作来源user_id对应表t_account 还是 mt_user',
  `REMARK` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='核销记录表';

/*Table structure for table `mt_coupon` */

DROP TABLE IF EXISTS `mt_coupon`;

CREATE TABLE `mt_coupon` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
  `GROUP_ID` int NOT NULL DEFAULT '0' COMMENT '券组ID',
  `TYPE` char(1) DEFAULT 'C' COMMENT '券类型，C优惠券；P预存卡；T集次卡',
  `NAME` varchar(100) NOT NULL DEFAULT '' COMMENT '券名称',
  `IS_GIVE` tinyint(1) DEFAULT '0' COMMENT '是否允许转赠',
  `GRADE_IDS` varchar(100) DEFAULT '' COMMENT '适用会员等级',
  `POINT` int DEFAULT '0' COMMENT '获得卡券所消耗积分',
  `APPLY_GOODS` varchar(20) DEFAULT '' COMMENT '适用商品：allGoods、parkGoods',
  `RECEIVE_CODE` varchar(32) DEFAULT '' COMMENT '领取码',
  `USE_FOR` varchar(30) DEFAULT '' COMMENT '使用专项',
  `EXPIRE_TYPE` varchar(30) DEFAULT '' COMMENT '过期类型',
  `EXPIRE_TIME` int DEFAULT '0' COMMENT '有效期，单位：天',
  `BEGIN_TIME` datetime DEFAULT NULL COMMENT '开始有效期',
  `END_TIME` datetime DEFAULT NULL COMMENT '结束有效期',
  `AMOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '面额',
  `SEND_WAY` varchar(20) DEFAULT 'backend' COMMENT '发放方式',
  `SEND_NUM` int unsigned DEFAULT '1' COMMENT '每次发放数量',
  `TOTAL` int DEFAULT '0' COMMENT '发行数量',
  `LIMIT_NUM` int DEFAULT '1' COMMENT '每人拥有数量限制',
  `EXCEPT_TIME` varchar(500) DEFAULT '' COMMENT '不可用日期，逗号隔开。周末：weekend；其他：2019-01-02_2019-02-09',
  `STORE_IDS` varchar(100) DEFAULT '' COMMENT '所属店铺ID,逗号隔开',
  `DESCRIPTION` varchar(2000) DEFAULT '' COMMENT '描述信息',
  `IMAGE` varchar(100) DEFAULT '' COMMENT '效果图片',
  `REMARKS` varchar(1000) DEFAULT '' COMMENT '后台备注',
  `IN_RULE` varchar(1000) DEFAULT '' COMMENT '获取券的规则',
  `OUT_RULE` varchar(1000) DEFAULT '' COMMENT '核销券的规则',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) DEFAULT 'A' COMMENT 'A：正常；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='卡券信息表';

/*Table structure for table `mt_coupon_goods` */

DROP TABLE IF EXISTS `mt_coupon_goods`;

CREATE TABLE `mt_coupon_goods` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `COUPON_ID` int NOT NULL COMMENT '卡券ID',
  `GOODS_ID` int NOT NULL COMMENT '商品ID',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime NOT NULL COMMENT '更新时间',
  `STATUS` char(1) NOT NULL DEFAULT 'A' COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='卡券商品表';

/*Table structure for table `mt_coupon_group` */

DROP TABLE IF EXISTS `mt_coupon_group`;

CREATE TABLE `mt_coupon_group` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
  `NAME` varchar(100) NOT NULL DEFAULT '' COMMENT '券组名称',
  `MONEY` decimal(18,2) DEFAULT '0.00' COMMENT '价值金额',
  `NUM` int DEFAULT '0' COMMENT '券种类数量',
  `TOTAL` int DEFAULT '0' COMMENT '发行数量',
  `DESCRIPTION` varchar(2000) DEFAULT '' COMMENT '备注',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建日期',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新日期',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) NOT NULL DEFAULT 'A' COMMENT 'A：正常；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='优惠券组';

/*Table structure for table `mt_freight` */

DROP TABLE IF EXISTS `mt_freight`;

CREATE TABLE `mt_freight` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
  `NAME` varchar(100) NOT NULL COMMENT '名称',
  `TYPE` int NOT NULL COMMENT '计费类型，1：按件数；2：按重量',
  `AMOUNT` decimal(10,2) NOT NULL COMMENT '费用',
  `INCRE_AMOUNT` decimal(10,2) NOT NULL COMMENT '续费',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime NOT NULL COMMENT '更新时间',
  `STATUS` char(1) NOT NULL COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='运费模板';

/*Table structure for table `mt_freight_region` */

DROP TABLE IF EXISTS `mt_freight_region`;

CREATE TABLE `mt_freight_region` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `FREIGHT_ID` int NOT NULL COMMENT '运费模板ID',
  `PROVINCE_ID` int NOT NULL COMMENT '省份ID',
  `CITY_ID` int NOT NULL COMMENT '城市ID',
  `AREA_ID` int NOT NULL COMMENT '区域ID',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime NOT NULL COMMENT '更新时间',
  `STATUS` char(1) NOT NULL COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='运费模板地区';

/*Table structure for table `mt_give` */

DROP TABLE IF EXISTS `mt_give`;

CREATE TABLE `mt_give` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增',
  `USER_ID` int NOT NULL DEFAULT '0' COMMENT '获赠者用户ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
  `GIVE_USER_ID` int NOT NULL DEFAULT '0' COMMENT '赠送者用户ID',
  `MOBILE` varchar(20) NOT NULL DEFAULT '' COMMENT '赠予对象手机号',
  `USER_MOBILE` varchar(20) NOT NULL DEFAULT '' COMMENT '用户手机',
  `GROUP_IDS` varchar(200) NOT NULL DEFAULT '' COMMENT '券组ID，逗号隔开',
  `GROUP_NAMES` varchar(500) NOT NULL DEFAULT '' COMMENT '券组名称，逗号隔开',
  `COUPON_IDS` varchar(200) NOT NULL DEFAULT '' COMMENT '券ID，逗号隔开',
  `COUPON_NAMES` varchar(500) NOT NULL DEFAULT '' COMMENT '券名称，逗号隔开',
  `NUM` int NOT NULL DEFAULT '0' COMMENT '数量',
  `MONEY` decimal(10,2) NOT NULL COMMENT '总金额',
  `NOTE` varchar(200) DEFAULT '' COMMENT '备注',
  `MESSAGE` varchar(500) DEFAULT '' COMMENT '留言',
  `CREATE_TIME` datetime NOT NULL COMMENT '赠送时间',
  `UPDATE_TIME` datetime NOT NULL COMMENT '更新时间',
  `STATUS` char(1) NOT NULL DEFAULT 'A' COMMENT '状态，A正常；C取消',
  PRIMARY KEY (`ID`),
  KEY `index_user_id` (`USER_ID`) USING BTREE,
  KEY `index_give_user_id` (`GIVE_USER_ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='转赠记录表';

/*Table structure for table `mt_give_item` */

DROP TABLE IF EXISTS `mt_give_item`;

CREATE TABLE `mt_give_item` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `GIVE_ID` int NOT NULL COMMENT '转赠ID',
  `USER_COUPON_ID` int NOT NULL COMMENT '用户电子券ID',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime NOT NULL COMMENT '更新时间',
  `STATUS` char(1) NOT NULL COMMENT '状态，A正常；D删除',
  PRIMARY KEY (`ID`),
  KEY `index_give_id` (`GIVE_ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='转赠明细表';

/*Table structure for table `mt_goods` */

DROP TABLE IF EXISTS `mt_goods`;

CREATE TABLE `mt_goods` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `TYPE` varchar(30) DEFAULT 'goods' COMMENT '商品类别',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '所属商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '所属店铺ID',
  `NAME` varchar(100) DEFAULT '' COMMENT '商品名称',
  `CATE_ID` int DEFAULT '0' COMMENT '分类ID',
  `GOODS_NO` varchar(100) DEFAULT '' COMMENT '商品编码',
  `IS_SINGLE_SPEC` char(1) NOT NULL DEFAULT 'Y' COMMENT '是否单规格',
  `LOGO` varchar(200) DEFAULT '' COMMENT '主图地址',
  `IMAGES` varchar(1000) DEFAULT '' COMMENT '图片地址',
  `PRICE` decimal(10,2) unsigned DEFAULT '0.00' COMMENT '价格',
  `LINE_PRICE` decimal(10,2) unsigned DEFAULT '0.00' COMMENT '划线价格',
  `STOCK` int unsigned DEFAULT '0' COMMENT '库存',
  `WEIGHT` decimal(10,2) DEFAULT '0.00' COMMENT '重量',
  `COUPON_IDS` varchar(500) DEFAULT '' COMMENT '关联卡券ID',
  `SERVICE_TIME` int DEFAULT '0' COMMENT '服务时长，单位：分钟',
  `INIT_SALE` int DEFAULT '0' COMMENT '初始销量',
  `SALE_POINT` varchar(100) DEFAULT '' COMMENT '商品卖点',
  `CAN_USE_POINT` char(1) DEFAULT 'N' COMMENT '可否使用积分抵扣',
  `IS_MEMBER_DISCOUNT` char(1) DEFAULT 'Y' COMMENT '会员是否有折扣',
  `SORT` int DEFAULT '0' COMMENT '排序',
  `DESCRIPTION` text COMMENT '商品描述',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT NULL COMMENT '最后操作人',
  `STATUS` char(1) DEFAULT 'A' COMMENT 'A：正常；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=388 DEFAULT CHARSET=utf8 COMMENT='商品表';

/*Table structure for table `mt_goods_cate` */

DROP TABLE IF EXISTS `mt_goods_cate`;

CREATE TABLE `mt_goods_cate` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '所属商户',
  `STORE_ID` int DEFAULT '0' COMMENT '所属店铺',
  `NAME` varchar(100) DEFAULT '' COMMENT '分类名称',
  `LOGO` varchar(200) DEFAULT '' COMMENT 'LOGO地址',
  `DESCRIPTION` text COMMENT '分类描述',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT NULL COMMENT '最后操作人',
  `SORT` int DEFAULT '0' COMMENT '排序',
  `STATUS` char(1) DEFAULT 'A' COMMENT 'A：正常；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=193 DEFAULT CHARSET=utf8 COMMENT='商品分类表';

/*Table structure for table `mt_goods_sku` */

DROP TABLE IF EXISTS `mt_goods_sku`;

CREATE TABLE `mt_goods_sku` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `SKU_NO` varchar(50) DEFAULT '' COMMENT 'sku编码',
  `LOGO` varchar(255) DEFAULT '' COMMENT '图片',
  `GOODS_ID` int NOT NULL DEFAULT '0' COMMENT '商品ID',
  `SPEC_IDS` varchar(100) NOT NULL DEFAULT '' COMMENT '规格ID',
  `STOCK` int NOT NULL DEFAULT '0' COMMENT '库存',
  `PRICE` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '价格',
  `LINE_PRICE` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '划线价格',
  `WEIGHT` decimal(10,2) DEFAULT '0.00' COMMENT '重量',
  `STATUS` char(1) NOT NULL DEFAULT 'A' COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=745 DEFAULT CHARSET=utf8 COMMENT='商品SKU表';

/*Table structure for table `mt_goods_spec` */

DROP TABLE IF EXISTS `mt_goods_spec`;

CREATE TABLE `mt_goods_spec` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `GOODS_ID` int NOT NULL DEFAULT '0' COMMENT '商品ID',
  `NAME` varchar(100) NOT NULL DEFAULT '' COMMENT '规格名称',
  `VALUE` varchar(100) NOT NULL DEFAULT '' COMMENT '规格值',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=433 DEFAULT CHARSET=utf8 COMMENT='规格表';

/*Table structure for table `mt_merchant` */

DROP TABLE IF EXISTS `mt_merchant`;

CREATE TABLE `mt_merchant` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `TYPE` varchar(30) DEFAULT '' COMMENT '类型，restaurant：餐饮；retail：零售；service：服务；other：其他',
  `LOGO` varchar(255) DEFAULT '' COMMENT 'logo',
  `NO` varchar(20) NOT NULL DEFAULT '' COMMENT '商户号',
  `NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '商户名称',
  `CONTACT` varchar(30) DEFAULT '' COMMENT '联系人姓名',
  `PHONE` varchar(20) DEFAULT '' COMMENT '联系电话',
  `ADDRESS` varchar(100) DEFAULT '' COMMENT '联系地址',
  `WX_APP_ID` varchar(50) DEFAULT '' COMMENT '微信小程序appId',
  `WX_APP_SECRET` varchar(50) DEFAULT '' COMMENT '微信小程序秘钥',
  `WX_OFFICIAL_APP_ID` varchar(50) DEFAULT '' COMMENT '微信公众号appId',
  `WX_OFFICIAL_APP_SECRET` varchar(50) DEFAULT '' COMMENT '微信公众号秘钥',
  `DESCRIPTION` varchar(2000) DEFAULT '' COMMENT '备注信息',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A：有效/启用；D：无效',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='商户表';

/*Table structure for table `mt_message` */

DROP TABLE IF EXISTS `mt_message`;

CREATE TABLE `mt_message` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `USER_ID` int NOT NULL COMMENT '用户ID',
  `TYPE` varchar(30) NOT NULL DEFAULT '' COMMENT '消息类型',
  `TITLE` varchar(200) DEFAULT '友情提示' COMMENT '消息标题',
  `CONTENT` varchar(500) NOT NULL DEFAULT '' COMMENT '消息内容',
  `IS_READ` char(1) DEFAULT 'N' COMMENT '是否已读',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `PARAMS` varchar(1000) DEFAULT '' COMMENT '参数信息',
  `IS_SEND` char(1) DEFAULT 'N' COMMENT '是否已发送',
  `SEND_TIME` datetime DEFAULT NULL COMMENT '发送时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态',
  PRIMARY KEY (`ID`),
  KEY `index_user_id` (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统消息表';

/*Table structure for table `mt_open_gift` */

DROP TABLE IF EXISTS `mt_open_gift`;

CREATE TABLE `mt_open_gift` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `STORE_ID` int NOT NULL DEFAULT '0' COMMENT '门店ID',
  `GRADE_ID` int NOT NULL DEFAULT '0' COMMENT '会员等级ID',
  `POINT` int NOT NULL DEFAULT '0' COMMENT '赠送积分',
  `COUPON_ID` int NOT NULL DEFAULT '0' COMMENT '卡券ID',
  `COUPON_NUM` int NOT NULL DEFAULT '1' COMMENT '卡券数量',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime NOT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8 COMMENT='会员开卡赠礼';

/*Table structure for table `mt_open_gift_item` */

DROP TABLE IF EXISTS `mt_open_gift_item`;

CREATE TABLE `mt_open_gift_item` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `USER_ID` int NOT NULL COMMENT '会用ID',
  `OPEN_GIFT_ID` int NOT NULL COMMENT '赠礼ID',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `STATUS` char(1) NOT NULL COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf32 COMMENT='开卡赠礼明细表';

/*Table structure for table `mt_order` */

DROP TABLE IF EXISTS `mt_order`;

CREATE TABLE `mt_order` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `TYPE` varchar(30) DEFAULT NULL COMMENT '订单类型',
  `PAY_TYPE` varchar(30) DEFAULT 'JSAPI' COMMENT '支付类型',
  `ORDER_MODE` varchar(30) DEFAULT 'express' COMMENT '订单模式',
  `PLATFORM` varchar(30) DEFAULT '' COMMENT '平台',
  `ORDER_SN` varchar(32) NOT NULL DEFAULT '' COMMENT '订单号',
  `COUPON_ID` int DEFAULT '0' COMMENT '卡券ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '所属商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '所属店铺ID',
  `USER_ID` int NOT NULL DEFAULT '0' COMMENT '用户ID',
  `VERIFY_CODE` varchar(10) DEFAULT '' COMMENT '核销验证码',
  `IS_VISITOR` char(1) DEFAULT 'N' COMMENT '是否游客',
  `AMOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '订单金额',
  `PAY_AMOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '支付金额',
  `USE_POINT` int DEFAULT '0' COMMENT '使用积分数量',
  `POINT_AMOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '积分金额',
  `DISCOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '折扣金额',
  `DELIVERY_FEE` decimal(10,2) DEFAULT '0.00' COMMENT '配送费用',
  `PARAM` varchar(500) DEFAULT '' COMMENT '订单参数',
  `EXPRESS_INFO` varchar(500) DEFAULT '' COMMENT '物流信息',
  `REMARK` varchar(500) DEFAULT '' COMMENT '用户备注',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '订单状态',
  `PAY_TIME` datetime DEFAULT NULL COMMENT '支付时间',
  `PAY_STATUS` char(1) DEFAULT '' COMMENT '支付状态',
  `SETTLE_STATUS` char(1) DEFAULT 'A' COMMENT '结算状态',
  `STAFF_ID` int DEFAULT '0' COMMENT '操作员工',
  `CONFIRM_STATUS` char(1) DEFAULT 'N' COMMENT '核销状态',
  `CONFIRM_TIME` datetime DEFAULT NULL COMMENT '核销时间',
  `CONFIRM_REMARK` varchar(500) DEFAULT NULL COMMENT '核销备注',
  `COMMISSION_USER_ID` int DEFAULT '0' COMMENT '分佣用户ID',
  `COMMISSION_STATUS` char(1) DEFAULT 'A' COMMENT '分佣提成计算状态',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单表';

/*Table structure for table `mt_order_address` */

DROP TABLE IF EXISTS `mt_order_address`;

CREATE TABLE `mt_order_address` (
  `ID` int unsigned NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `NAME` varchar(30) NOT NULL DEFAULT '' COMMENT '收货人姓名',
  `MOBILE` varchar(20) NOT NULL DEFAULT '' COMMENT '联系电话',
  `PROVINCE_ID` int unsigned NOT NULL DEFAULT '0' COMMENT '省份ID',
  `CITY_ID` int unsigned NOT NULL DEFAULT '0' COMMENT '城市ID',
  `REGION_ID` int unsigned NOT NULL DEFAULT '0' COMMENT '区/县ID',
  `DETAIL` varchar(255) NOT NULL DEFAULT '' COMMENT '详细地址',
  `ORDER_ID` int unsigned NOT NULL DEFAULT '0' COMMENT '订单ID',
  `USER_ID` int unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `ORDER_ID` (`ORDER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单收货地址记录表';

/*Table structure for table `mt_order_goods` */

DROP TABLE IF EXISTS `mt_order_goods`;

CREATE TABLE `mt_order_goods` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `ORDER_ID` int NOT NULL DEFAULT '0' COMMENT '订单ID',
  `GOODS_ID` int NOT NULL DEFAULT '0' COMMENT '商品ID',
  `SKU_ID` int DEFAULT '0' COMMENT 'skuID',
  `PRICE` decimal(10,2) DEFAULT '0.00' COMMENT '价格',
  `DISCOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '优惠价',
  `NUM` int NOT NULL DEFAULT '0' COMMENT '商品数量',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT 'A：正常；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单商品表';

/*Table structure for table `mt_point` */

DROP TABLE IF EXISTS `mt_point`;

CREATE TABLE `mt_point` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '所属商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '所属店铺ID',
  `USER_ID` int NOT NULL DEFAULT '0' COMMENT '用户ID',
  `ORDER_SN` varchar(32) DEFAULT '' COMMENT '订单号',
  `AMOUNT` int NOT NULL DEFAULT '0' COMMENT '积分变化数量',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `DESCRIPTION` varchar(200) DEFAULT '' COMMENT '备注说明',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A正常；D作废',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='积分变化表';

/*Table structure for table `mt_printer` */

DROP TABLE IF EXISTS `mt_printer`;

CREATE TABLE `mt_printer` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '所属商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '所属店铺ID',
  `SN` varchar(64) DEFAULT NULL COMMENT '打印机编号',
  `NAME` varchar(64) DEFAULT NULL COMMENT '打印机名称',
  `AUTO_PRINT` char(1) DEFAULT 'N' COMMENT '是否自动打印',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `DESCRIPTION` varchar(255) DEFAULT '' COMMENT '备注说明',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A正常；D作废',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='打印机表';

/*Table structure for table `mt_refund` */

DROP TABLE IF EXISTS `mt_refund`;

CREATE TABLE `mt_refund` (
    `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `ORDER_ID` int NOT NULL COMMENT '订单ID',
    `MERCHANT_ID` int DEFAULT '0' COMMENT '所属商户ID',
    `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
    `USER_ID` int NOT NULL COMMENT '会员ID',
    `AMOUNT` decimal(10,2) DEFAULT NULL COMMENT '退款金额',
    `TYPE` varchar(20) DEFAULT '' COMMENT '售后类型',
    `REMARK` varchar(500) DEFAULT '' COMMENT '退款备注',
    `EXPRESS_NAME` varchar(30) DEFAULT '' COMMENT '物流公司',
    `EXPRESS_NO` varchar(30) DEFAULT '' COMMENT '物流单号',
    `REJECT_REASON` varchar(1000) DEFAULT NULL COMMENT '拒绝原因',
    `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
    `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
    `STATUS` char(1) DEFAULT 'A' COMMENT '状态',
    `IMAGES` varchar(1000) DEFAULT NULL COMMENT '图片',
    `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
    PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='售后表';


/*Table structure for table `mt_region` */

DROP TABLE IF EXISTS `mt_region`;

CREATE TABLE `mt_region` (
  `ID` int unsigned NOT NULL AUTO_INCREMENT COMMENT '区划信息ID',
  `NAME` varchar(255) NOT NULL DEFAULT '' COMMENT '区划名称',
  `PID` int unsigned NOT NULL DEFAULT '0' COMMENT '父级ID',
  `CODE` varchar(255) NOT NULL DEFAULT '' COMMENT '区划编码',
  `LEVEL` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '层级(1省级 2市级 3区/县级)',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3705 DEFAULT CHARSET=utf8 COMMENT='省市区数据表';

/*Table structure for table `mt_send_log` */

DROP TABLE IF EXISTS `mt_send_log`;

CREATE TABLE `mt_send_log` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
  `TYPE` tinyint(1) NOT NULL COMMENT '1：单用户发券；2：批量发券',
  `USER_ID` int DEFAULT NULL COMMENT '用户ID',
  `FILE_NAME` varchar(100) DEFAULT '' COMMENT '导入excel文件名',
  `FILE_PATH` varchar(200) DEFAULT '' COMMENT '导入excel文件路径',
  `MOBILE` varchar(20) NOT NULL COMMENT '用户手机',
  `GROUP_ID` int NOT NULL COMMENT '券组ID',
  `GROUP_NAME` varchar(100) DEFAULT '' COMMENT '券组名称',
  `COUPON_ID` int DEFAULT '0' COMMENT '卡券ID',
  `SEND_NUM` int DEFAULT NULL COMMENT '发放套数',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '操作时间',
  `OPERATOR` varchar(30) DEFAULT NULL COMMENT '操作人',
  `UUID` varchar(50) DEFAULT '' COMMENT '导入UUID',
  `REMOVE_SUCCESS_NUM` int DEFAULT '0' COMMENT '作废成功张数',
  `REMOVE_FAIL_NUM` int DEFAULT '0' COMMENT '作废失败张数',
  `STATUS` char(1) DEFAULT NULL COMMENT '状态，A正常；B：部分作废；D全部作废',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='卡券发放记录表';

/*Table structure for table `mt_setting` */

DROP TABLE IF EXISTS `mt_setting`;

CREATE TABLE `mt_setting` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
  `TYPE` varchar(30) NOT NULL DEFAULT '' COMMENT '类型',
  `NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '配置项',
  `VALUE` varchar(1000) NOT NULL DEFAULT '' COMMENT '配置值',
  `DESCRIPTION` varchar(200) DEFAULT '' COMMENT '配置说明',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态 A启用；D禁用',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf8 COMMENT='全局设置表';

/*Table structure for table `mt_settlement` */

DROP TABLE IF EXISTS `mt_settlement`;

CREATE TABLE `mt_settlement` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `SETTLEMENT_NO` varchar(32) DEFAULT NULL COMMENT '结算单号',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
  `TOTAL_ORDER_AMOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '订单总金额',
  `AMOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '结算金额',
  `DESCRIPTION` varchar(1000) DEFAULT '' COMMENT '备注说明',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `PAY_STATUS` char(1) DEFAULT '' COMMENT '支付状态',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='结算表';

/*Table structure for table `mt_settlement_order` */

DROP TABLE IF EXISTS `mt_settlement_order`;

CREATE TABLE `mt_settlement_order` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `SETTLEMENT_ID` int NOT NULL DEFAULT '0' COMMENT '结算ID',
  `ORDER_ID` int DEFAULT '0' COMMENT '订单ID',
  `DESCRIPTION` varchar(1000) DEFAULT '' COMMENT '备注说明',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='结算订单表';

/*Table structure for table `mt_sms_sended_log` */

DROP TABLE IF EXISTS `mt_sms_sended_log`;

CREATE TABLE `mt_sms_sended_log` (
  `LOG_ID` int NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
  `MOBILE_PHONE` varchar(32) DEFAULT NULL COMMENT '手机号',
  `CONTENT` varchar(1024) DEFAULT NULL COMMENT '短信内容',
  `SEND_TIME` datetime DEFAULT NULL COMMENT '发送时间',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`LOG_ID`),
  KEY `FK_REFERENCE_1` (`MOBILE_PHONE`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='短信发送记录表';

/*Table structure for table `mt_sms_template` */

DROP TABLE IF EXISTS `mt_sms_template`;

CREATE TABLE `mt_sms_template` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
  `NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '名称',
  `UNAME` varchar(50) NOT NULL DEFAULT '' COMMENT '英文名称',
  `CODE` varchar(30) NOT NULL DEFAULT '' COMMENT '编码',
  `CONTENT` varchar(255) NOT NULL DEFAULT '' COMMENT '内容',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) NOT NULL DEFAULT 'A' COMMENT '状态：A激活；N禁用',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='短信模板';

/*Table structure for table `mt_staff` */

DROP TABLE IF EXISTS `mt_staff`;

CREATE TABLE `mt_staff` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
  `USER_ID` int DEFAULT '0' COMMENT '用户ID',
  `CATEGORY` int DEFAULT '0' COMMENT '员工类别,1:店长;2:收银员;3:销售人员;3:服务人员;',
  `MOBILE` varchar(16) NOT NULL DEFAULT '' COMMENT '手机号码',
  `REAL_NAME` varchar(30) DEFAULT '' COMMENT '真实姓名',
  `WECHAT` varchar(64) DEFAULT NULL COMMENT '微信号',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `AUDITED_STATUS` char(1) DEFAULT 'U' COMMENT '审核状态，A：审核通过；U：未审核；D：无效; ',
  `AUDITED_TIME` datetime DEFAULT NULL COMMENT '审核时间',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='店铺员工表';

/*Table structure for table `mt_stock` */

DROP TABLE IF EXISTS `mt_stock`;

CREATE TABLE `mt_stock` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `STORE_ID` int NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `TYPE` varchar(20) NOT NULL DEFAULT 'increase' COMMENT '类型，increase:入库，reduce:出库',
  `DESCRIPTION` varchar(1000) DEFAULT '' COMMENT '备注说明',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) NOT NULL DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) NOT NULL DEFAULT 'A' COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='库存管理记录表';

/*Table structure for table `mt_stock_item` */

DROP TABLE IF EXISTS `mt_stock_item`;

CREATE TABLE `mt_stock_item` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `STOCK_ID` int NOT NULL DEFAULT '0' COMMENT '库存管理ID',
  `GOODS_ID` int NOT NULL DEFAULT '0' COMMENT '商品ID',
  `SKU_ID` int NOT NULL DEFAULT '0' COMMENT 'SKUID',
  `NUM` int NOT NULL DEFAULT '0' COMMENT '数量',
  `DESCRIPTION` varchar(1000) DEFAULT '' COMMENT '说明备注',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime NOT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) NOT NULL DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) NOT NULL DEFAULT 'A' COMMENT '订单状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='库存管理明细表';

/*Table structure for table `mt_store` */

DROP TABLE IF EXISTS `mt_store`;

CREATE TABLE `mt_store` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int unsigned DEFAULT '0' COMMENT '所属商户',
  `NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '店铺名称',
  `QR_CODE` varchar(255) DEFAULT '' COMMENT '店铺二维码',
  `LOGO` varchar(100) DEFAULT '' COMMENT '店铺LOGO',
  `IS_DEFAULT` char(1) NOT NULL DEFAULT 'N' COMMENT '是否默认',
  `CONTACT` varchar(30) DEFAULT '' COMMENT '联系人姓名',
  `WX_MCH_ID` varchar(30) DEFAULT '' COMMENT '微信支付商户号',
  `WX_API_V2` varchar(32) DEFAULT '' COMMENT '微信支付APIv2密钥',
  `WX_CERT_PATH` varchar(255) DEFAULT '' COMMENT '微信支付证书',
  `ALIPAY_APP_ID` varchar(100) DEFAULT '' COMMENT '支付宝appId',
  `ALIPAY_PRIVATE_KEY` varchar(5000) DEFAULT '' COMMENT '支付宝应用私钥',
  `ALIPAY_PUBLIC_KEY` varchar(5000) DEFAULT '' COMMENT '支付宝应用公钥',
  `PHONE` varchar(20) DEFAULT '' COMMENT '联系电话',
  `ADDRESS` varchar(100) DEFAULT '' COMMENT '地址',
  `LATITUDE` varchar(30) DEFAULT '' COMMENT '经度',
  `LONGITUDE` varchar(30) DEFAULT '' COMMENT '维度',
  `DISTANCE` decimal(10,2) DEFAULT '0.00' COMMENT '距离',
  `HOURS` varchar(255) DEFAULT '' COMMENT '营业时间',
  `LICENSE` varchar(255) DEFAULT '' COMMENT '营业执照',
  `CREDIT_CODE` varchar(50) DEFAULT '' COMMENT '统一社会信用码',
  `BANK_NAME` varchar(100) DEFAULT '' COMMENT '银行名称',
  `BANK_CARD_NAME` varchar(100) DEFAULT '' COMMENT '银行卡账户名',
  `BANK_CARD_NO` varchar(100) DEFAULT '' COMMENT '银行卡卡号',
  `DESCRIPTION` varchar(2000) DEFAULT '' COMMENT '备注信息',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A：有效/启用；D：无效',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED COMMENT='店铺表';

/*Table structure for table `mt_store_goods` */

DROP TABLE IF EXISTS `mt_store_goods`;
CREATE TABLE `mt_store_goods` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int unsigned NOT NULL DEFAULT '0' COMMENT '所属商户',
  `STORE_ID` int NOT NULL DEFAULT '0' COMMENT '所属店铺',
  `GOODS_ID` int NOT NULL DEFAULT '0' COMMENT '商品ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A：有效/启用；D：无效',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`ID`),
  KEY `INDEX_STORE_ID` (`STORE_ID`),
  KEY `INDEX_GOODS_ID` (`GOODS_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED COMMENT='店铺商品表';

/*Table structure for table `mt_upload_shipping_log` */
DROP TABLE IF EXISTS `mt_upload_shipping_log`;
CREATE TABLE `mt_upload_shipping_log` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '所属商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '所属店铺ID',
  `ORDER_ID` int DEFAULT '0' COMMENT '订单ID',
  `ORDER_SN` varchar(100) DEFAULT '' COMMENT '订单号',
  `MOBILE` varchar(20) DEFAULT '' COMMENT '手机号',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT NULL COMMENT '最后操作人',
  `STATUS` char(1) DEFAULT 'A' COMMENT 'A：完成；B：失败',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=480 DEFAULT CHARSET=utf8 COMMENT='微信小程序上传发货信息记录表';

/*Table structure for table `mt_user` */

DROP TABLE IF EXISTS `mt_user`;

CREATE TABLE `mt_user` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '会员ID',
  `MOBILE` varchar(20) DEFAULT '' COMMENT '手机号码',
  `GROUP_ID` int DEFAULT '0' COMMENT '分组ID',
  `USER_NO` varchar(30) DEFAULT '' COMMENT '会员号',
  `AVATAR` varchar(255) DEFAULT '' COMMENT '头像',
  `NAME` varchar(30) DEFAULT '' COMMENT '称呼',
  `OPEN_ID` varchar(50) DEFAULT '' COMMENT '微信open_id',
  `IDCARD` varchar(20) DEFAULT '' COMMENT '证件号码',
  `GRADE_ID` varchar(10) DEFAULT '1' COMMENT '等级ID',
  `START_TIME` datetime DEFAULT NULL COMMENT '会员开始时间',
  `END_TIME` datetime DEFAULT NULL COMMENT '会员结束时间',
  `BALANCE` float(10,2) DEFAULT '0.00' COMMENT '余额',
  `POINT` int DEFAULT '0' COMMENT '积分',
  `SEX` int DEFAULT '1' COMMENT '性别 1男；0女',
  `BIRTHDAY` varchar(20) DEFAULT '' COMMENT '出生日期',
  `CAR_NO` varchar(10) DEFAULT '' COMMENT '车牌号',
  `SOURCE` varchar(30) DEFAULT '' COMMENT '来源渠道',
  `PASSWORD` varchar(32) DEFAULT '' COMMENT '密码',
  `SALT` varchar(4) DEFAULT '' COMMENT 'salt',
  `ADDRESS` varchar(100) DEFAULT '' COMMENT '地址',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '所属商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '所属店铺ID',
  `IS_STAFF` char(1) DEFAULT 'N' COMMENT '是否员工',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A：激活；N：禁用；D：删除',
  `DESCRIPTION` varchar(255) DEFAULT '' COMMENT '备注信息',
  `IP` varchar(20) DEFAULT '' COMMENT '注册IP',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`ID`),
  KEY `index_phone` (`MOBILE`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='会员个人信息';

/*Table structure for table `mt_user_action` */

DROP TABLE IF EXISTS `mt_user_action`;

CREATE TABLE `mt_user_action` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `USER_ID` int NOT NULL COMMENT '会员ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
  `ACTION` varchar(30) DEFAULT '' COMMENT '行为类别',
  `DESCRIPTION` varchar(255) DEFAULT '' COMMENT '备注信息',
  `PARAM` varchar(255) DEFAULT '' COMMENT '参数',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A：激活；N：禁用；D：删除',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`ID`),
  KEY `index_user_id` (`USER_ID`,`ACTION`,`PARAM`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员行为记录表';

/*Table structure for table `mt_user_coupon` */

DROP TABLE IF EXISTS `mt_user_coupon`;

CREATE TABLE `mt_user_coupon` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `CODE` varchar(32) NOT NULL DEFAULT '' COMMENT '编码',
  `TYPE` char(1) NOT NULL DEFAULT 'C' COMMENT '券类型，C优惠券；P预存卡；T集次卡',
  `IMAGE` varchar(100) DEFAULT '' COMMENT '效果图',
  `GROUP_ID` int NOT NULL DEFAULT '0' COMMENT '券组ID',
  `COUPON_ID` int NOT NULL DEFAULT '0' COMMENT '券ID',
  `MOBILE` varchar(20) DEFAULT '' COMMENT '用户手机号码',
  `USER_ID` int DEFAULT '0' COMMENT '用户ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '使用店铺ID',
  `AMOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '面额',
  `BALANCE` decimal(10,2) DEFAULT '0.00' COMMENT '余额',
  `STATUS` char(1) NOT NULL DEFAULT '1' COMMENT '状态：A：未使用；B：已使用；C：已过期; D：已删除；E：未领取',
  `USED_TIME` datetime DEFAULT NULL COMMENT '使用时间',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `EXPIRE_TIME` datetime DEFAULT NULL COMMENT '过期时间',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `UUID` varchar(50) DEFAULT '' COMMENT '导入UUID',
  `ORDER_ID` int DEFAULT '0' COMMENT '订单ID',
  PRIMARY KEY (`ID`),
  KEY `index_user_id` (`USER_ID`),
  KEY `index_coupon_id` (`COUPON_ID`),
  KEY `index_group_id` (`GROUP_ID`) USING BTREE,
  KEY `index_code` (`CODE`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='会员卡券表';

/*Table structure for table `mt_user_grade` */

DROP TABLE IF EXISTS `mt_user_grade`;

CREATE TABLE `mt_user_grade` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `GRADE` tinyint DEFAULT '1' COMMENT '等级',
  `NAME` varchar(30) DEFAULT '' COMMENT '等级名称',
  `CATCH_CONDITION` varchar(255) DEFAULT '' COMMENT '升级会员等级条件描述',
  `CATCH_TYPE` varchar(30) DEFAULT 'pay' COMMENT '升级会员等级条件，init:默认获取;pay:付费升级；frequency:消费次数；amount:累积消费金额升级',
  `CATCH_VALUE` float(10,2) DEFAULT '0.00' COMMENT '达到升级条件的值',
  `USER_PRIVILEGE` varchar(1000) DEFAULT '' COMMENT '会员权益描述',
  `VALID_DAY` int DEFAULT '0' COMMENT '有效期',
  `DISCOUNT` float(5,2) DEFAULT '0.00' COMMENT '享受折扣',
  `SPEED_POINT` float(5,2) DEFAULT '1.00' COMMENT '积分加速',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='会员等级表';

/*Table structure for table `mt_user_group` */

DROP TABLE IF EXISTS `mt_user_group`;

CREATE TABLE `mt_user_group` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MERCHANT_ID` int DEFAULT '0' COMMENT '商户ID',
  `STORE_ID` int DEFAULT '0' COMMENT '店铺ID',
  `NAME` varchar(100) NOT NULL DEFAULT '' COMMENT '分组名称',
  `PARENT_ID` int DEFAULT '0' COMMENT '父ID',
  `DESCRIPTION` varchar(2000) DEFAULT '' COMMENT '备注',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建日期',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新日期',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) NOT NULL DEFAULT 'A' COMMENT 'A：正常；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='会员分组';

/*Table structure for table `mt_verify_code` */

DROP TABLE IF EXISTS `mt_verify_code`;

CREATE TABLE `mt_verify_code` (
  `ID` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `MOBILE` varchar(16) DEFAULT NULL COMMENT '手机号',
  `VERIFY_CODE` char(6) DEFAULT NULL COMMENT '验证码',
  `ADD_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `EXPIRE_TIME` datetime DEFAULT NULL COMMENT '过期时间',
  `USED_TIME` datetime DEFAULT NULL COMMENT '使用时间',
  `VALID_FLAG` char(1) DEFAULT NULL COMMENT '可用状态 0未用 1已用 2置为失效',
  PRIMARY KEY (`ID`),
  KEY `ix_mobile_verifyCode` (`MOBILE`,`VERIFY_CODE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='短信验证码表';

/*Table structure for table `t_account` */

DROP TABLE IF EXISTS `t_account`;

CREATE TABLE `t_account` (
  `acct_id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `account_key` varchar(23) NOT NULL DEFAULT '' COMMENT '账户编码',
  `account_name` varchar(20) NOT NULL DEFAULT '' COMMENT '账户名称',
  `password` varchar(100) NOT NULL DEFAULT '' COMMENT '密码',
  `account_status` int NOT NULL DEFAULT '1' COMMENT '0 无效 1 有效',
  `is_active` int NOT NULL DEFAULT '0' COMMENT '0 未激活 1已激活',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `modify_date` datetime NOT NULL COMMENT '修改时间',
  `salt` varchar(64) NOT NULL DEFAULT '' COMMENT '随机码',
  `role_ids` varchar(100) DEFAULT NULL COMMENT '角色ID',
  `locked` int NOT NULL DEFAULT '0' COMMENT '是否禁用',
  `owner_id` int DEFAULT NULL COMMENT '所属平台',
  `real_name` varchar(255) DEFAULT NULL COMMENT '姓名',
  `merchant_id` int DEFAULT '0' COMMENT '所属商户ID',
  `store_id` int DEFAULT '0' COMMENT '所属店铺ID',
  `staff_id` int DEFAULT '0' COMMENT '关联员工ID',
  PRIMARY KEY (`acct_id`),
  KEY `FKmlsqc08c6khxhoed7abkl2s9l` (`owner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `t_account_duty` */

DROP TABLE IF EXISTS `t_account_duty`;

CREATE TABLE `t_account_duty` (
  `acc_duty_id` int NOT NULL AUTO_INCREMENT COMMENT '账户角色ID',
  `acct_id` int NOT NULL COMMENT '账户ID',
  `duty_id` int NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`acc_duty_id`),
  KEY `FKcym10gcigo2c175iqqjj7xu5h` (`acct_id`),
  KEY `FKpfts0wq2y4xhq9vv2g7uo1kr0` (`duty_id`)
) ENGINE=InnoDB AUTO_INCREMENT=760 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `t_action_log` */

DROP TABLE IF EXISTS `t_action_log`;

CREATE TABLE `t_action_log` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `merchant_id` int DEFAULT '0' COMMENT '商户ID',
  `store_id` int DEFAULT '0' COMMENT '店铺ID',
  `action_time` datetime DEFAULT NULL COMMENT '操作时间',
  `time_consuming` decimal(11,0) DEFAULT NULL COMMENT '耗时',
  `client_ip` varchar(50) DEFAULT NULL COMMENT '客户端IP',
  `module` varchar(255) DEFAULT NULL COMMENT '操作模块',
  `url` varchar(255) DEFAULT NULL COMMENT '请求URL',
  `acct_name` varchar(255) DEFAULT NULL COMMENT '操作用户账户',
  `user_agent` varchar(255) DEFAULT NULL COMMENT '用户系统以及浏览器信息',
  `client_port` int DEFAULT NULL COMMENT '端口号',
  `param` text COMMENT '参数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `t_duty` */

DROP TABLE IF EXISTS `t_duty`;

CREATE TABLE `t_duty` (
  `merchant_id` int DEFAULT '0' COMMENT '商户ID',
  `duty_id` int NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `duty_name` varchar(240) DEFAULT NULL COMMENT '角色名称',
  `status` varchar(6) NOT NULL COMMENT '状态(A: 可用  D: 禁用)',
  `description` varchar(400) DEFAULT NULL COMMENT '描述',
  `duty_type` varchar(50) NOT NULL COMMENT '角色类型',
  PRIMARY KEY (`duty_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='角色表';

/*Table structure for table `t_duty_source` */

DROP TABLE IF EXISTS `t_duty_source`;

CREATE TABLE `t_duty_source` (
  `duty_source_id` int NOT NULL AUTO_INCREMENT,
  `duty_id` int DEFAULT NULL,
  `source_id` int DEFAULT NULL,
  PRIMARY KEY (`duty_source_id`),
  KEY `FKlciudb88j4tptc36d43ghl5dg` (`duty_id`),
  KEY `FKp1c59mwxgjue4qdl86sd6dogf` (`source_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13326 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `t_gen_code` */

DROP TABLE IF EXISTS `t_gen_code`;

CREATE TABLE `t_gen_code` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `service_name` varchar(64) DEFAULT NULL COMMENT '服务名称',
  `module_name` varchar(64) DEFAULT NULL COMMENT '模块名称',
  `table_name` varchar(64) DEFAULT NULL COMMENT '表名',
  `table_prefix` varchar(64) DEFAULT NULL COMMENT '表前缀',
  `pk_name` varchar(32) DEFAULT NULL COMMENT '主键名',
  `package_name` varchar(500) DEFAULT NULL COMMENT '后端包名',
  `backend_path` varchar(2000) DEFAULT NULL COMMENT '后端路径',
  `front_path` varchar(2000) DEFAULT NULL COMMENT '前端路径',
  `author` varchar(30) DEFAULT NULL COMMENT '作者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `status` char(1) DEFAULT 'A' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='代码生成表';

/*Table structure for table `t_platform` */

DROP TABLE IF EXISTS `t_platform`;

CREATE TABLE `t_platform` (
  `owner_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '平台名称',
  `status` int NOT NULL COMMENT '状态 0 无效 1 有效',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `platform_type` int NOT NULL COMMENT '平台类型',
  PRIMARY KEY (`owner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `t_source` */

DROP TABLE IF EXISTS `t_source`;

CREATE TABLE `t_source` (
  `source_id` int NOT NULL AUTO_INCREMENT COMMENT '菜单Id',
  `merchant_id` int DEFAULT '1' COMMENT '商户ID',
  `source_name` varchar(240) NOT NULL COMMENT '菜单名称',
  `source_code` varchar(200) NOT NULL COMMENT '菜单对应url',
  `path` varchar(255) DEFAULT '' COMMENT '路径',
  `ename` varchar(100) DEFAULT '' COMMENT '字母名称',
  `new_icon` varchar(30) DEFAULT '' COMMENT '新图标',
  `status` varchar(6) NOT NULL COMMENT '状态(A:可用 D:禁用)',
  `source_level` int NOT NULL COMMENT '菜单级别',
  `source_style` varchar(40) NOT NULL COMMENT '样式',
  `is_menu` int NOT NULL COMMENT '是否显示',
  `description` varchar(400) DEFAULT NULL COMMENT '描述',
  `parent_id` int DEFAULT NULL COMMENT '上级菜单ID',
  `is_log` int DEFAULT NULL,
  `icon` varchar(20) DEFAULT NULL COMMENT '菜单图标',
  PRIMARY KEY (`source_id`),
  KEY `index-name` (`source_name`,`parent_id`),
  KEY `index-parent-id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='菜单表';

