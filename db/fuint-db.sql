/*
SQLyog Trial v11.01 (32 bit)
MySQL - 5.7.14 : Database - fuint-db
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`fuint-db` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `fuint-db`;

/*Table structure for table `mt_banner` */

DROP TABLE IF EXISTS `mt_banner`;

CREATE TABLE `mt_banner` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `TITLE` varchar(100) DEFAULT '' COMMENT '标题',
  `URL` varchar(100) DEFAULT '' COMMENT '链接地址',
  `IMAGE` varchar(200) DEFAULT '' COMMENT '图片地址',
  `DESCRIPTION` text COMMENT '描述',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT NULL COMMENT '最后操作人',
  `STATUS` char(1) DEFAULT 'A' COMMENT 'A：正常；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Table structure for table `mt_confirm_log` */

DROP TABLE IF EXISTS `mt_confirm_log`;

CREATE TABLE `mt_confirm_log` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `CODE` varchar(32) NOT NULL DEFAULT '' COMMENT '编码',
  `USER_COUPON_ID` int(10) NOT NULL DEFAULT '0' COMMENT '用户券ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `USER_ID` int(10) NOT NULL DEFAULT '0' COMMENT '用户优惠券所属用户id',
  `OPERATOR_USER_ID` int(11) DEFAULT NULL COMMENT '核销者用户ID',
  `STORE_ID` int(10) NOT NULL DEFAULT '0' COMMENT '核销店铺ID',
  `STATUS` varchar(1) NOT NULL COMMENT '状态，A正常核销；D：撤销使用',
  `CANCEL_TIME` datetime DEFAULT NULL COMMENT '撤销时间',
  `OPERATOR` varchar(128) DEFAULT NULL COMMENT '最后操作人',
  `OPERATOR_FROM` varchar(30) DEFAULT 'mt_user' COMMENT '操作来源user_id对应表t_account 还是 mt_user',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='核销记录表';

/*Table structure for table `mt_confirmer` */

DROP TABLE IF EXISTS `mt_confirmer`;

CREATE TABLE `mt_confirmer` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `MOBILE` varchar(16) NOT NULL DEFAULT '' COMMENT '手机号码',
  `REAL_NAME` varchar(30) DEFAULT '' COMMENT '真实姓名',
  `WECHAT` varchar(64) DEFAULT NULL COMMENT '微信号',
  `STORE_ID` int(11) DEFAULT NULL COMMENT '对应的核销店铺id',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `AUDITED_STATUS` char(1) DEFAULT 'U' COMMENT '审核状态，A：审核通过；U：未审核；D：无效; ',
  `AUDITED_TIME` datetime DEFAULT NULL COMMENT '审核时间',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '备注',
  `USER_ID` int(11) DEFAULT NULL COMMENT '对应前端用户id,审核通过时，记录到mt_user表',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `uix_mobile` (`MOBILE`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='酒店核销人员表';

/*Table structure for table `mt_coupon` */

DROP TABLE IF EXISTS `mt_coupon`;

CREATE TABLE `mt_coupon` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `GROUP_ID` int(10) NOT NULL DEFAULT '0' COMMENT '券组ID',
  `TYPE` char(1) DEFAULT 'C' COMMENT '券类型，C优惠券；P预存卡；T集次卡',
  `NAME` varchar(100) NOT NULL DEFAULT '' COMMENT '券名称',
  `BEGIN_TIME` datetime DEFAULT NULL COMMENT '开始有效期',
  `END_TIME` datetime DEFAULT NULL COMMENT '结束有效期',
  `AMOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '面额',
  `SEND_WAY` varchar(20) DEFAULT 'backend' COMMENT '发放方式',
  `SEND_NUM` int(10) DEFAULT '1' COMMENT '每次发放数量',
  `LIMIT_NUM` int(10) DEFAULT '1' COMMENT '拥有数量限制',
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='卡券信息表';

/*Table structure for table `mt_coupon_group` */

DROP TABLE IF EXISTS `mt_coupon_group`;

CREATE TABLE `mt_coupon_group` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `NAME` varchar(100) NOT NULL DEFAULT '' COMMENT '券组名称',
  `MONEY` decimal(18,2) DEFAULT '0.00' COMMENT '价值金额',
  `NUM` int(10) DEFAULT '0' COMMENT '券种类数量',
  `TOTAL` int(10) NOT NULL DEFAULT '0' COMMENT '发行数量',
  `DESCRIPTION` varchar(2000) DEFAULT '' COMMENT '备注',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建日期',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新日期',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) NOT NULL DEFAULT 'A' COMMENT 'A：正常；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='优惠券组';

/*Table structure for table `mt_give` */

DROP TABLE IF EXISTS `mt_give`;

CREATE TABLE `mt_give` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增',
  `USER_ID` int(10) NOT NULL DEFAULT '0' COMMENT '获赠者用户ID',
  `GIVE_USER_ID` int(10) NOT NULL DEFAULT '0' COMMENT '赠送者用户ID',
  `MOBILE` varchar(20) NOT NULL DEFAULT '' COMMENT '赠予对象手机号',
  `HNA_ACCOUNT` varchar(50) DEFAULT NULL COMMENT '赠予对象海航账号',
  `USER_MOBILE` varchar(20) NOT NULL DEFAULT '' COMMENT '用户手机',
  `USER_HNA_ACCOUNT` varchar(50) DEFAULT NULL COMMENT '用户海航账号',
  `GROUP_IDS` varchar(200) NOT NULL DEFAULT '' COMMENT '券组ID，逗号隔开',
  `GROUP_NAMES` varchar(500) NOT NULL DEFAULT '' COMMENT '券组名称，逗号隔开',
  `COUPON_IDS` varchar(200) NOT NULL DEFAULT '' COMMENT '券ID，逗号隔开',
  `COUPON_NAMES` varchar(500) NOT NULL DEFAULT '' COMMENT '券名称，逗号隔开',
  `NUM` int(10) NOT NULL DEFAULT '0' COMMENT '数量',
  `MONEY` decimal(10,2) NOT NULL COMMENT '总金额',
  `NOTE` varchar(200) DEFAULT '' COMMENT '备注',
  `MESSAGE` varchar(500) DEFAULT '' COMMENT '留言',
  `CREATE_TIME` datetime NOT NULL COMMENT '赠送时间',
  `UPDATE_TIME` datetime NOT NULL COMMENT '更新时间',
  `STATUS` char(1) NOT NULL DEFAULT 'A' COMMENT '状态，A正常；C取消',
  PRIMARY KEY (`ID`),
  KEY `index_user_id` (`USER_ID`) USING BTREE,
  KEY `index_give_user_id` (`GIVE_USER_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='转赠记录表';

/*Table structure for table `mt_give_item` */

DROP TABLE IF EXISTS `mt_give_item`;

CREATE TABLE `mt_give_item` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `GIVE_ID` int(10) NOT NULL COMMENT '转赠ID',
  `USER_COUPON_ID` int(10) NOT NULL COMMENT '用户电子券ID',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPDATE_TIEM` datetime NOT NULL COMMENT '更新时间',
  `STATUS` char(1) NOT NULL COMMENT '状态，A正常；D删除',
  PRIMARY KEY (`ID`),
  KEY `index_give_id` (`GIVE_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='转赠明细表';

/*Table structure for table `mt_order` */

DROP TABLE IF EXISTS `mt_order`;

CREATE TABLE `mt_order` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `TYPE` varchar(30) DEFAULT NULL COMMENT '订单类型',
  `ORDER_SN` varchar(32) NOT NULL DEFAULT '' COMMENT '订单号',
  `COUPON_ID` int(10) DEFAULT '0' COMMENT '卡券ID',
  `USER_ID` int(10) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `AMOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '订单金额',
  `PARAM` varchar(500) DEFAULT '' COMMENT '订单参数',
  `REMARK` varchar(500) DEFAULT '' COMMENT '用户备注',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Table structure for table `mt_point` */

DROP TABLE IF EXISTS `mt_point`;

CREATE TABLE `mt_point` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `USER_ID` int(10) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `AMOUNT` int(10) NOT NULL DEFAULT '0' COMMENT '积分数量',
  `IS_DEAL` tinyint(1) DEFAULT '1' COMMENT '是否处理',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `DESCRIPTION` varchar(200) DEFAULT '' COMMENT '备注说明',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='积分表';

/*Table structure for table `mt_send_log` */

DROP TABLE IF EXISTS `mt_send_log`;

CREATE TABLE `mt_send_log` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `TYPE` tinyint(1) NOT NULL COMMENT '1：单用户发券；2：批量发券',
  `USER_ID` int(10) DEFAULT NULL COMMENT '用户ID',
  `FILE_NAME` varchar(100) DEFAULT '' COMMENT '导入excel文件名',
  `FILE_PATH` varchar(200) DEFAULT '' COMMENT '导入excel文件路径',
  `MOBILE` varchar(20) NOT NULL COMMENT '用户手机',
  `GROUP_ID` int(10) NOT NULL COMMENT '券组ID',
  `GROUP_NAME` varchar(100) DEFAULT '' COMMENT '券组名称',
  `SEND_NUM` int(10) DEFAULT NULL COMMENT '发放套数',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '操作时间',
  `OPERATOR` varchar(30) DEFAULT NULL COMMENT '操作人',
  `UUID` varchar(50) DEFAULT '' COMMENT '导入UUID',
  `REMOVE_SUCCESS_NUM` int(10) DEFAULT '0' COMMENT '作废成功张数',
  `REMOVE_FAIL_NUM` int(10) DEFAULT '0' COMMENT '作废失败张数',
  `STATUS` char(1) DEFAULT NULL COMMENT '状态，A正常；B：部分作废；D全部作废',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='卡券发放记录表';

/*Table structure for table `mt_sms_sended_log` */

DROP TABLE IF EXISTS `mt_sms_sended_log`;

CREATE TABLE `mt_sms_sended_log` (
  `LOG_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
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
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '名称',
  `UNAME` varchar(50) NOT NULL DEFAULT '' COMMENT '英文名称',
  `CODE` varchar(30) NOT NULL DEFAULT '' COMMENT '编码',
  `CONTENT` varchar(255) NOT NULL DEFAULT '' COMMENT '内容',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) NOT NULL DEFAULT 'A' COMMENT '状态：A激活；N禁用',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='短信模板';

/*Table structure for table `mt_store` */

DROP TABLE IF EXISTS `mt_store`;

CREATE TABLE `mt_store` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '酒店名称',
  `CONTACT` varchar(30) DEFAULT '' COMMENT '联系人姓名',
  `PHONE` varchar(20) DEFAULT '' COMMENT '联系电话',
  `DESCRIPTION` varchar(2000) DEFAULT '' COMMENT '备注信息',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A：有效/启用；D：无效',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='酒店表';

/*Table structure for table `mt_user` */

DROP TABLE IF EXISTS `mt_user`;

CREATE TABLE `mt_user` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '会员ID',
  `REAL_NAME` varchar(30) DEFAULT '' COMMENT '姓名',
  `MOBILE` varchar(20) DEFAULT '' COMMENT '手机号码',
  `IDCARD` varchar(20) DEFAULT '' COMMENT '证件号码',
  `GROUP_ID` varchar(10) DEFAULT '' COMMENT '分组',
  `SEX` int(1) DEFAULT '0' COMMENT '性别 0男；1女',
  `BIRTHDAY` varchar(20) DEFAULT '' COMMENT '出生日期',
  `CAR_NO` varchar(10) DEFAULT '' COMMENT '车牌号',
  `PASSWORD` varchar(32) DEFAULT '' COMMENT '密码',
  `SALT` varchar(4) DEFAULT '' COMMENT 'salt',
  `ADDRESS` varchar(100) DEFAULT '' COMMENT '地址',
  `POINT` int(10) DEFAULT '0' COMMENT '积分',
  `CREATE_TIME` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A：激活；N：禁用；D：删除',
  `IS_DEAL` tinyint(1) DEFAULT '0' COMMENT '是否处理',
  `DESCRIPTION` varchar(255) DEFAULT '' COMMENT '备注信息',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`ID`),
  KEY `index_phone` (`MOBILE`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='会员个人信息';

/*Table structure for table `mt_user_coupon` */

DROP TABLE IF EXISTS `mt_user_coupon`;

CREATE TABLE `mt_user_coupon` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `CODE` varchar(32) NOT NULL DEFAULT '' COMMENT '编码',
  `TYPE` char(1) NOT NULL DEFAULT 'C' COMMENT '券类型，C优惠券；P预存卡；T集次卡',
  `IMAGE` varchar(100) DEFAULT '' COMMENT '效果图',
  `GROUP_ID` int(10) NOT NULL DEFAULT '0' COMMENT '券组ID',
  `COUPON_ID` int(10) NOT NULL DEFAULT '0' COMMENT '券ID',
  `MOBILE` varchar(20) DEFAULT '' COMMENT '用户手机号码',
  `USER_ID` int(10) DEFAULT '0' COMMENT '用户ID',
  `STORE_ID` int(11) DEFAULT NULL COMMENT '使用店铺ID',
  `AMOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '面额',
  `BALANCE` decimal(10,2) DEFAULT '0.00' COMMENT '余额',
  `STATUS` char(1) NOT NULL DEFAULT '1' COMMENT '状态：A：未使用；B：已使用；C：已过期; D：已删除；E：未领取',
  `USED_TIME` datetime DEFAULT NULL COMMENT '使用时间',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `UUID` varchar(50) DEFAULT '' COMMENT '导入UUID',
  `ORDER_ID` int(10) DEFAULT '0' COMMENT '订单ID',
  PRIMARY KEY (`ID`),
  KEY `index_user_id` (`USER_ID`),
  KEY `index_coupon_id` (`COUPON_ID`),
  KEY `index_group_id` (`GROUP_ID`) USING BTREE,
  KEY `index_code` (`CODE`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='会员优惠券表';

/*Table structure for table `mt_user_group` */

DROP TABLE IF EXISTS `mt_user_group`;

CREATE TABLE `mt_user_group` (
  `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `NAME` varchar(30) DEFAULT '' COMMENT '分组名称',
  `MIN_POINT` int(10) DEFAULT '0' COMMENT '最少积分',
  `MAX_POINT` int(10) DEFAULT '0' COMMENT '最多积分',
  `DESCRIPTION` text COMMENT '分组描述',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

/*Table structure for table `mt_verify_code` */

DROP TABLE IF EXISTS `mt_verify_code`;

CREATE TABLE `mt_verify_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mobile` varchar(16) DEFAULT NULL COMMENT '手机号',
  `verifyCode` char(6) DEFAULT NULL COMMENT '验证码',
  `addTime` datetime DEFAULT NULL COMMENT '创建时间',
  `expireTime` datetime DEFAULT NULL COMMENT '过期时间',
  `usedTime` datetime DEFAULT NULL COMMENT '使用时间',
  `validFlag` char(1) DEFAULT NULL COMMENT '可用状态 0未用 1已用 2置为失效',
  PRIMARY KEY (`id`),
  KEY `ix_mobile_verifyCode` (`mobile`,`verifyCode`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='短信验证码表';

/*Table structure for table `t_account` */

DROP TABLE IF EXISTS `t_account`;

CREATE TABLE `t_account` (
  `acct_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `account_key` varchar(23) NOT NULL DEFAULT '' COMMENT '账户编码',
  `account_name` varchar(20) NOT NULL DEFAULT '' COMMENT '账户名称',
  `password` varchar(100) NOT NULL DEFAULT '' COMMENT '密码',
  `account_status` int(11) NOT NULL DEFAULT '1' COMMENT '0 无效 1 有效',
  `is_active` int(11) NOT NULL DEFAULT '0' COMMENT '0 未激活 1已激活',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `modify_date` datetime NOT NULL COMMENT '修改时间',
  `salt` varchar(64) NOT NULL DEFAULT '' COMMENT '随机码',
  `role_ids` varchar(100) DEFAULT NULL,
  `locked` int(1) NOT NULL DEFAULT '0',
  `owner_id` int(20) DEFAULT NULL COMMENT '所属平台',
  `real_name` varchar(255) DEFAULT NULL,
  `store_id` int(11) DEFAULT NULL COMMENT '管辖店铺id  : -1 代表全部',
  `store_name` varchar(255) DEFAULT NULL COMMENT '管辖店铺名称',
  PRIMARY KEY (`acct_id`),
  KEY `FKmlsqc08c6khxhoed7abkl2s9l` (`owner_id`),
  CONSTRAINT `FKmlsqc08c6khxhoed7abkl2s9l` FOREIGN KEY (`owner_id`) REFERENCES `t_platform` (`owner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `t_account_duty` */

DROP TABLE IF EXISTS `t_account_duty`;

CREATE TABLE `t_account_duty` (
  `acc_duty_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '账户角色ID',
  `acct_id` int(11) NOT NULL COMMENT '账户ID',
  `duty_id` int(11) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`acc_duty_id`),
  KEY `FKcym10gcigo2c175iqqjj7xu5h` (`acct_id`),
  KEY `FKpfts0wq2y4xhq9vv2g7uo1kr0` (`duty_id`),
  CONSTRAINT `FKcym10gcigo2c175iqqjj7xu5h` FOREIGN KEY (`acct_id`) REFERENCES `t_account` (`acct_id`),
  CONSTRAINT `FKpfts0wq2y4xhq9vv2g7uo1kr0` FOREIGN KEY (`duty_id`) REFERENCES `t_duty` (`duty_id`)
) ENGINE=InnoDB AUTO_INCREMENT=241 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `t_action_log` */

DROP TABLE IF EXISTS `t_action_log`;

CREATE TABLE `t_action_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `action_time` datetime NOT NULL COMMENT '操作时间',
  `time_consuming` decimal(11,0) DEFAULT NULL COMMENT '耗时',
  `client_ip` varchar(50) DEFAULT NULL COMMENT '客户端IP',
  `module` varchar(255) DEFAULT NULL COMMENT '操作模块',
  `url` varchar(255) DEFAULT NULL COMMENT '请求URL',
  `acct_name` varchar(255) NOT NULL COMMENT '操作用户账户',
  `user_agent` varchar(255) DEFAULT NULL COMMENT '用户系统以及浏览器信息',
  `client_port` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=224 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `t_duty` */

DROP TABLE IF EXISTS `t_duty`;

CREATE TABLE `t_duty` (
  `duty_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `duty_name` varchar(240) DEFAULT NULL COMMENT '角色名称',
  `status` varchar(6) NOT NULL COMMENT '状态(A: 可用  D: 禁用)',
  `description` varchar(400) DEFAULT NULL COMMENT '描述',
  `duty_type` varchar(50) NOT NULL COMMENT '角色类型',
  PRIMARY KEY (`duty_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='角色表';

/*Table structure for table `t_duty_source` */

DROP TABLE IF EXISTS `t_duty_source`;

CREATE TABLE `t_duty_source` (
  `duty_source_id` int(11) NOT NULL AUTO_INCREMENT,
  `duty_id` int(11) DEFAULT NULL,
  `source_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`duty_source_id`),
  KEY `FKlciudb88j4tptc36d43ghl5dg` (`duty_id`),
  KEY `FKp1c59mwxgjue4qdl86sd6dogf` (`source_id`),
  CONSTRAINT `FKlciudb88j4tptc36d43ghl5dg` FOREIGN KEY (`duty_id`) REFERENCES `t_duty` (`duty_id`),
  CONSTRAINT `FKp1c59mwxgjue4qdl86sd6dogf` FOREIGN KEY (`source_id`) REFERENCES `t_source` (`source_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5453 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `t_platform` */

DROP TABLE IF EXISTS `t_platform`;

CREATE TABLE `t_platform` (
  `owner_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(20) NOT NULL COMMENT '平台名称',
  `status` int(11) NOT NULL COMMENT '状态 0 无效 1 有效',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `platform_type` int(11) NOT NULL COMMENT '平台类型 1：免税易购 2：其他体验店',
  PRIMARY KEY (`owner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `t_source` */

DROP TABLE IF EXISTS `t_source`;

CREATE TABLE `t_source` (
  `source_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '菜单Id',
  `source_name` varchar(240) NOT NULL COMMENT '菜单名称',
  `source_code` varchar(200) NOT NULL COMMENT '菜单对应url',
  `status` varchar(6) NOT NULL COMMENT '状态(A:可用 D:禁用)',
  `source_level` int(11) NOT NULL COMMENT '菜单级别',
  `source_style` varchar(40) NOT NULL COMMENT '样式',
  `is_menu` int(20) NOT NULL COMMENT '是否显示',
  `description` varchar(400) DEFAULT NULL COMMENT '描述',
  `parent_id` int(11) DEFAULT NULL COMMENT '上级菜单ID',
  `is_log` int(11) DEFAULT NULL,
  `icon` varchar(20) DEFAULT NULL COMMENT '菜单图标',
  PRIMARY KEY (`source_id`),
  UNIQUE KEY `SOURCE_NAME` (`source_name`,`parent_id`),
  KEY `FKfcvh926f0p0tey75b7spk8sd3` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='菜单表';

/*Table structure for table `uv_confirm_log` */

DROP TABLE IF EXISTS `uv_confirm_log`;

/*!50001 DROP VIEW IF EXISTS `uv_confirm_log` */;
/*!50001 DROP TABLE IF EXISTS `uv_confirm_log` */;

/*!50001 CREATE TABLE  `uv_confirm_log`(
 `ID` int(10) ,
 `CODE` varchar(32) ,
 `ConfirmStatus` varchar(1) ,
 `UserCouponId` int(10) ,
 `ConfirmTime` datetime ,
 `COUPON_ID` int(10) ,
 `USER_ID` int(10) ,
 `COUPON_INFO_STATUS` varchar(1) ,
 `COUPON_INFO_STATUS_DESC` char(0) ,
 `STORE_ID` int(11) ,
 `USED_TIME` datetime ,
 `CREATE_TIME` datetime ,
 `UPDATE_TIME` datetime ,
 `UUID` varchar(50) ,
 `MOBILE` varchar(20) ,
 `REAL_NAME` varchar(30) ,
 `SUIT_STORE_IDS` varchar(102) ,
 `coupon_name` varchar(100) ,
 `MONEY` decimal(10,2) ,
 `GROUP_ID` int(10) ,
 `coupon_status` char(1) ,
 `BEGIN_TIME` datetime ,
 `END_TIME` datetime ,
 `coupon_group_name` varchar(100) ,
 `store_Name` varchar(50) ,
 `CancelTime` datetime ,
 `EndCancelTime` datetime 
)*/;

/*Table structure for table `uv_coupon_info` */

DROP TABLE IF EXISTS `uv_coupon_info`;

/*!50001 DROP VIEW IF EXISTS `uv_coupon_info` */;
/*!50001 DROP TABLE IF EXISTS `uv_coupon_info` */;

/*!50001 CREATE TABLE  `uv_coupon_info`(
 `ID` int(10) ,
 `CODE` varchar(32) ,
 `COUPON_ID` int(10) ,
 `USER_ID` int(10) ,
 `COUPON_INFO_STATUS` varchar(1) ,
 `COUPON_INFO_STATUS_DESC` char(0) ,
 `STORE_ID` int(11) ,
 `USED_TIME` datetime ,
 `CREATE_TIME` datetime ,
 `UPDATE_TIME` datetime ,
 `UUID` varchar(50) ,
 `MOBILE` varchar(20) ,
 `REAL_NAME` varchar(30) ,
 `SUIT_STORE_IDS` varchar(102) ,
 `coupon_name` varchar(100) ,
 `coupon_image` varchar(100) ,
 `MONEY` decimal(10,2) ,
 `GROUP_ID` int(10) ,
 `coupon_status` char(1) ,
 `BEGIN_TIME` datetime ,
 `END_TIME` datetime ,
 `coupon_group_name` varchar(100) ,
 `store_Name` varchar(50) 
)*/;

/*View structure for view uv_confirm_log */

/*!50001 DROP TABLE IF EXISTS `uv_confirm_log` */;
/*!50001 DROP VIEW IF EXISTS `uv_confirm_log` */;

/*!50001 CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `uv_confirm_log` AS select `mt_confirm_log`.`ID` AS `ID`,`mt_confirm_log`.`CODE` AS `CODE`,`mt_confirm_log`.`STATUS` AS `ConfirmStatus`,`mt_confirm_log`.`USER_COUPON_ID` AS `UserCouponId`,`mt_confirm_log`.`CREATE_TIME` AS `ConfirmTime`,`mt_user_coupon`.`COUPON_ID` AS `COUPON_ID`,`mt_user_coupon`.`USER_ID` AS `USER_ID`,if((now() between `mt_coupon`.`BEGIN_TIME` and `mt_coupon`.`END_TIME`),`mt_user_coupon`.`STATUS`,if((`mt_user_coupon`.`STATUS` = 'B'),'B','C')) AS `COUPON_INFO_STATUS`,'' AS `COUPON_INFO_STATUS_DESC`,`mt_user_coupon`.`STORE_ID` AS `STORE_ID`,`mt_user_coupon`.`USED_TIME` AS `USED_TIME`,`mt_user_coupon`.`CREATE_TIME` AS `CREATE_TIME`,`mt_user_coupon`.`UPDATE_TIME` AS `UPDATE_TIME`,`mt_user_coupon`.`UUID` AS `UUID`,`mt_user`.`MOBILE` AS `MOBILE`,`mt_user`.`REAL_NAME` AS `REAL_NAME`,concat(',',`mt_coupon`.`STORE_IDS`,',') AS `SUIT_STORE_IDS`,`mt_coupon`.`NAME` AS `coupon_name`,`mt_coupon`.`AMOUNT` AS `MONEY`,`mt_coupon`.`GROUP_ID` AS `GROUP_ID`,`mt_coupon`.`STATUS` AS `coupon_status`,`mt_coupon`.`BEGIN_TIME` AS `BEGIN_TIME`,`mt_coupon`.`END_TIME` AS `END_TIME`,`mt_coupon_group`.`NAME` AS `coupon_group_name`,`mt_store`.`NAME` AS `store_Name`,`mt_confirm_log`.`CANCEL_TIME` AS `CancelTime`,(`mt_confirm_log`.`CREATE_TIME` + interval 48 hour) AS `EndCancelTime` from (((((`mt_confirm_log` join `mt_user_coupon` on((`mt_confirm_log`.`USER_COUPON_ID` = `mt_user_coupon`.`ID`))) join `mt_coupon` on((`mt_user_coupon`.`COUPON_ID` = `mt_coupon`.`ID`))) join `mt_coupon_group` on((`mt_coupon`.`GROUP_ID` = `mt_coupon_group`.`ID`))) join `mt_user` on((`mt_user_coupon`.`USER_ID` = `mt_user`.`ID`))) join `mt_store` on((`mt_confirm_log`.`STORE_ID` = `mt_store`.`ID`))) */;

/*View structure for view uv_coupon_info */

/*!50001 DROP TABLE IF EXISTS `uv_coupon_info` */;
/*!50001 DROP VIEW IF EXISTS `uv_coupon_info` */;

/*!50001 CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `uv_coupon_info` AS select `mt_user_coupon`.`ID` AS `ID`,`mt_user_coupon`.`CODE` AS `CODE`,`mt_user_coupon`.`COUPON_ID` AS `COUPON_ID`,`mt_user_coupon`.`USER_ID` AS `USER_ID`,if(((now() between `mt_coupon`.`BEGIN_TIME` and `mt_coupon`.`END_TIME`) or (now() < `mt_coupon`.`BEGIN_TIME`)),`mt_user_coupon`.`STATUS`,if((`mt_user_coupon`.`STATUS` = 'B'),'B','C')) AS `COUPON_INFO_STATUS`,'' AS `COUPON_INFO_STATUS_DESC`,`mt_user_coupon`.`STORE_ID` AS `STORE_ID`,`mt_user_coupon`.`USED_TIME` AS `USED_TIME`,`mt_user_coupon`.`CREATE_TIME` AS `CREATE_TIME`,`mt_user_coupon`.`UPDATE_TIME` AS `UPDATE_TIME`,`mt_user_coupon`.`UUID` AS `UUID`,`mt_user`.`MOBILE` AS `MOBILE`,`mt_user`.`REAL_NAME` AS `REAL_NAME`,concat(',',`mt_coupon`.`STORE_IDS`,',') AS `SUIT_STORE_IDS`,`mt_coupon`.`NAME` AS `coupon_name`,`mt_coupon`.`IMAGE` AS `coupon_image`,`mt_coupon`.`AMOUNT` AS `MONEY`,`mt_coupon`.`GROUP_ID` AS `GROUP_ID`,`mt_coupon`.`STATUS` AS `coupon_status`,`mt_coupon`.`BEGIN_TIME` AS `BEGIN_TIME`,`mt_coupon`.`END_TIME` AS `END_TIME`,`mt_coupon_group`.`NAME` AS `coupon_group_name`,`mt_store`.`NAME` AS `store_Name` from ((((`mt_user_coupon` join `mt_coupon` on((`mt_user_coupon`.`COUPON_ID` = `mt_coupon`.`ID`))) join `mt_coupon_group` on((`mt_coupon`.`GROUP_ID` = `mt_coupon_group`.`ID`))) join `mt_user` on((`mt_user_coupon`.`USER_ID` = `mt_user`.`ID`))) left join `mt_store` on((`mt_user_coupon`.`STORE_ID` = `mt_store`.`ID`))) */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
