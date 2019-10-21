DROP TABLE IF EXISTS `alldbs`;
DROP TABLE IF EXISTS `app_group`;
DROP TABLE IF EXISTS `dal_group`;
DROP TABLE IF EXISTS `data_search_log`;
DROP TABLE IF EXISTS `databaseset`;
DROP TABLE IF EXISTS `databasesetentry`;
DROP TABLE IF EXISTS `group_relation`;
DROP TABLE IF EXISTS `login_users`;
DROP TABLE IF EXISTS `project`;
DROP TABLE IF EXISTS `project_dbset_relation`;
DROP TABLE IF EXISTS `public_strategy`;
DROP TABLE IF EXISTS `server`;
DROP TABLE IF EXISTS `server_config`;
DROP TABLE IF EXISTS `server_group`;
DROP TABLE IF EXISTS `task_auto`;
DROP TABLE IF EXISTS `task_sql`;
DROP TABLE IF EXISTS `task_table`;
DROP TABLE IF EXISTS `user_group`;
DROP TABLE IF EXISTS `user_project`;

CREATE TABLE `alldbs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `db_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `dal_group_id` int(11) DEFAULT NULL,
  `db_address` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `db_port` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `db_user` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `db_password` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `db_catalog` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `db_type` tinyint(2) NOT NULL COMMENT '数据库类型：1、mysql 2、SqlServer',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后操作人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_key` (`db_name`),
  KEY `FK_Reference_3` (`dal_group_id`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
);

CREATE TABLE `app_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `server_group_id` int(11) DEFAULT '0',
  `comment` text COLLATE utf8mb4_unicode_ci,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后操作人',
  `server_enabled` tinyint(2) DEFAULT '0' COMMENT '是否是远程连接Das Server的方式 0:否 1:是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_name` (`name`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
);

CREATE TABLE `dal_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `group_comment` text COLLATE utf8mb4_unicode_ci,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_group_name` (`group_name`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
);

CREATE TABLE `data_search_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `ip` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `request_type` tinyint(2) NOT NULL COMMENT '类型：1、查询 0、下载',
  `request` longtext COLLATE utf8mb4_unicode_ci COMMENT '请求参数',
  `success` tinyint(1) NOT NULL COMMENT '请求：1、成功 0、失败',
  `result` text COLLATE utf8mb4_unicode_ci COMMENT '异常信息等记录',
  `user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人工号',
  `inserttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
  `updatetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isactive` tinyint(1) NOT NULL DEFAULT '1' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_inserttime` (`inserttime`),
  KEY `idx_updatetime` (`updatetime`),
  KEY `idx_user_no` (`user_no`)
);

CREATE TABLE `databaseset` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `db_type` tinyint(2) NOT NULL COMMENT '数据库类型：1、mysql 2、SqlServer',
  `strategy_type` tinyint(2) NOT NULL DEFAULT '1' COMMENT '类型：0.无策略 1、私有策略 2、公共策略',
  `class_name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `strategy_source` text COLLATE utf8mb4_unicode_ci,
  `group_id` bigint(20) DEFAULT NULL,
  `dynamic_strategy_id` int(11) DEFAULT NULL COMMENT '这字段指向公有策略，对应public_sharding_strategy.id',
  `update_user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `AK_unique_key` (`name`),
  KEY `FK_Reference_4` (`group_id`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
);

CREATE TABLE `databasesetentry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `database_type` tinyint(2) NOT NULL COMMENT '1.Master 2.Slave',
  `sharding` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `db_Id` int(11) NOT NULL COMMENT '物理数据ID',
  `dbset_id` int(11) DEFAULT NULL COMMENT '逻辑数据库ID',
  `update_user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `AK_unique_key` (`name`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
);

CREATE TABLE `group_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `current_group_id` int(11) DEFAULT NULL,
  `child_group_id` int(11) DEFAULT NULL,
  `child_group_role` int(11) DEFAULT NULL,
  `adduser` int(11) DEFAULT NULL,
  `update_user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_Index_1` (`current_group_id`,`child_group_id`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
);

CREATE TABLE `login_users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_no` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `user_name` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `user_real_name` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_email` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后操作人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_no_UNIQUE` (`user_no`),
  UNIQUE KEY `user_name` (`user_name`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
);

CREATE TABLE `project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `namespace` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dal_group_id` int(11) DEFAULT NULL,
  `dal_config_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `app_id` varchar(50) CHARACTER SET utf8mb4 NOT NULL DEFAULT '0',
  `app_group_id` int(11) DEFAULT '0',
  `pre_release_time` timestamp NULL DEFAULT NULL COMMENT '预计上线时间',
  `app_scene` text COLLATE utf8mb4_unicode_ci COMMENT '应用场景',
  `comment` text COLLATE utf8mb4_unicode_ci,
  `first_release_time` timestamp NULL DEFAULT NULL COMMENT '首次上线时间',
  `token` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'das token',
  PRIMARY KEY (`id`),
  UNIQUE KEY `app_id` (`app_id`),
  UNIQUE KEY `app_id_2` (`app_id`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
);

CREATE TABLE `project_dbset_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dbset_id` int(11) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  `update_user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_index_1` (`dbset_id`,`project_id`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
);

CREATE TABLE `public_strategy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `strategy_loading_type` tinyint(2) NOT NULL COMMENT '策略类型：1、静态加载的策略 2、动态加载策略',
  `class_name` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `strategy_source` text COLLATE utf8mb4_unicode_ci,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `comment` text COLLATE utf8mb4_unicode_ci,
  `update_user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `strategy_params` text COLLATE utf8mb4_unicode_ci COMMENT '策略参数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_name` (`name`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
);

CREATE TABLE `server` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `server_group_id` int(11) NOT NULL COMMENT 'server_group.id',
  `ip` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后操作人',
  PRIMARY KEY (`id`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
);

CREATE TABLE `server_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `server_id` int(11) NOT NULL COMMENT 'server.id',
  `keya` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `value` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后操作人',
  PRIMARY KEY (`id`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
);

CREATE TABLE `server_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后操作人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
);

CREATE TABLE `task_auto` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) DEFAULT NULL,
  `db_name` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `table_name` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `class_name` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `method_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sql_style` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `crud_type` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fields` text COLLATE utf8mb4_unicode_ci,
  `where_condition` text COLLATE utf8mb4_unicode_ci,
  `sql_content` text COLLATE utf8mb4_unicode_ci,
  `generated` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `update_user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `scalarType` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pagination` tinyint(1) DEFAULT NULL,
  `orderby` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `approved` int(11) DEFAULT NULL,
  `approveMsg` longtext COLLATE utf8mb4_unicode_ci,
  `hints` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
);

CREATE TABLE `task_sql` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `class_name` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pojo_name` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `method_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `crud_type` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sql_content` text COLLATE utf8mb4_unicode_ci,
  `project_id` int(11) DEFAULT NULL,
  `parameters` text COLLATE utf8mb4_unicode_ci,
  `generated` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `update_user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `scalarType` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pojoType` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pagination` tinyint(1) DEFAULT NULL,
  `sql_style` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `approved` int(11) DEFAULT NULL,
  `approveMsg` longtext COLLATE utf8mb4_unicode_ci,
  `hints` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dbset_id` int(11) NOT NULL COMMENT 'databaseset id',
  `field_type` int(2) DEFAULT '11' COMMENT '字段类型',
  PRIMARY KEY (`id`),
  KEY `idx_updatetime` (`update_time`)
);

CREATE TABLE `task_table` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) DEFAULT NULL,
  `table_names` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `view_names` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `custom_table_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `prefix` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `suffix` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cud_by_sp` tinyint(1) DEFAULT NULL,
  `pagination` tinyint(1) DEFAULT NULL,
  `generated` tinyint(1) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `update_user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `sql_style` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `api_list` text COLLATE utf8mb4_unicode_ci,
  `approved` int(11) DEFAULT NULL,
  `approveMsg` longtext COLLATE utf8mb4_unicode_ci,
  `dbset_id` int(11) NOT NULL COMMENT 'task_table id',
  `field_type` int(2) DEFAULT '11' COMMENT '字段类型',
  PRIMARY KEY (`id`),
  KEY `idx_updatetime` (`update_time`)
);

CREATE TABLE `user_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `role` tinyint(2) DEFAULT '1',
  `opt_user` tinyint(2) DEFAULT '1',
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_user_no` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后操作人',
  PRIMARY KEY (`id`),
  KEY `idx_inserttime` (`insert_time`),
  KEY `idx_updatetime` (`update_time`)
);

CREATE TABLE `user_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
);




