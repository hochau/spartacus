/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.7.17-log : Database - blog
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`blog` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `blog`;

/*Table structure for table `persistent_logins` */

DROP TABLE IF EXISTS `persistent_logins`;

CREATE TABLE `persistent_logins` (
  `series` varchar(64) NOT NULL,
  `username` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tb_access` */

DROP TABLE IF EXISTS `tb_access`;

CREATE TABLE `tb_access` (
  `id` bigint(20) NOT NULL,
  `access_time` datetime DEFAULT NULL,
  `cookie_flag` varchar(255) DEFAULT NULL,
  `gps` varchar(255) DEFAULT NULL,
  `gps_address` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `ip_city` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tb_access_forbid` */

DROP TABLE IF EXISTS `tb_access_forbid`;

CREATE TABLE `tb_access_forbid` (
  `id` bigint(20) NOT NULL,
  `day_count` int(11) DEFAULT NULL,
  `forbid_type` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `ip_city` varchar(255) DEFAULT NULL,
  `month_count` int(11) DEFAULT NULL,
  `operate_time` datetime DEFAULT NULL,
  `total_count` int(11) DEFAULT NULL,
  `year_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tb_article` */

DROP TABLE IF EXISTS `tb_article`;

CREATE TABLE `tb_article` (
  `id` bigint(20) NOT NULL,
  `author` varchar(255) DEFAULT NULL,
  `brief` text,
  `cname` varchar(255) DEFAULT NULL,
  `comment_number` int(11) DEFAULT '0',
  `content` text,
  `from_where` varchar(255) DEFAULT NULL,
  `is_top` int(11) DEFAULT '0',
  `labels` varchar(255) DEFAULT NULL,
  `month_day` varchar(255) DEFAULT NULL,
  `pictures` varchar(255) DEFAULT NULL,
  `publish_time` datetime DEFAULT NULL,
  `scan_number` int(11) DEFAULT '0',
  `status` int(11) DEFAULT '0',
  `title` varchar(255) DEFAULT NULL,
  `year` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tb_category` */

DROP TABLE IF EXISTS `tb_category`;

CREATE TABLE `tb_category` (
  `id` bigint(20) NOT NULL,
  `cname` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_mrmnrb2f9leouslsl0pn763ad` (`cname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tb_comment` */

DROP TABLE IF EXISTS `tb_comment`;

CREATE TABLE `tb_comment` (
  `id` bigint(20) NOT NULL,
  `article_id` bigint(20) NOT NULL,
  `article_title` varchar(255) DEFAULT NULL,
  `content` text NOT NULL,
  `front_id` bigint(20) DEFAULT NULL,
  `head_img` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `ip_city` varchar(255) DEFAULT NULL,
  `level` int(11) NOT NULL DEFAULT '1',
  `nickname` varchar(255) DEFAULT NULL,
  `provider_id` varchar(255) DEFAULT NULL,
  `provider_user_id` varchar(255) DEFAULT NULL,
  `publish_time` datetime NOT NULL,
  `rear_id` bigint(20) DEFAULT NULL,
  `ref_id` bigint(20) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tb_comment_forbid` */

DROP TABLE IF EXISTS `tb_comment_forbid`;

CREATE TABLE `tb_comment_forbid` (
  `id` bigint(20) NOT NULL,
  `forbid_type` int(11) DEFAULT NULL,
  `head_img` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `ip_city` varchar(255) DEFAULT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `operate_time` datetime DEFAULT NULL,
  `provider_id` varchar(255) DEFAULT NULL,
  `provider_user_id` varchar(255) DEFAULT NULL,
  `reason` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tb_cos_resource` */

DROP TABLE IF EXISTS `tb_cos_resource`;

CREATE TABLE `tb_cos_resource` (
  `id` bigint(20) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `_key` varchar(255) DEFAULT NULL,
  `acl_flag` int(11) DEFAULT NULL,
  `bucket_name` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `tags` varchar(255) DEFAULT NULL,
  `cos_type` int(11) DEFAULT NULL,
  `content_type` varchar(255) DEFAULT NULL,
  `root_path` varchar(255) DEFAULT NULL,
  `last_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tb_message` */

DROP TABLE IF EXISTS `tb_message`;

CREATE TABLE `tb_message` (
  `id` bigint(20) NOT NULL,
  `head_img` varchar(255) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `openid` varchar(255) DEFAULT NULL,
  `send_time` datetime DEFAULT NULL,
  `unionid` varchar(255) DEFAULT NULL,
  `user_type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tb_mission` */

DROP TABLE IF EXISTS `tb_mission`;

CREATE TABLE `tb_mission` (
  `id` bigint(20) NOT NULL,
  `exe_time` datetime DEFAULT NULL,
  `exe_times` int(11) DEFAULT '1',
  `mission_id` int(11) DEFAULT NULL,
  `mission_name` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tb_net_io` */

DROP TABLE IF EXISTS `tb_net_io`;

CREATE TABLE `tb_net_io` (
  `id` bigint(20) NOT NULL,
  `insert_date` datetime DEFAULT NULL,
  `io_in` float DEFAULT NULL,
  `io_out` float DEFAULT NULL,
  `ip` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tb_socialuserinfo` */

DROP TABLE IF EXISTS `tb_socialuserinfo`;

CREATE TABLE `tb_socialuserinfo` (
  `id` bigint(20) NOT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `head_img` varchar(255) DEFAULT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `provider_id` varchar(255) DEFAULT NULL,
  `provider_user_id` varchar(255) DEFAULT NULL,
  `province` varchar(255) DEFAULT NULL,
  `refresh_time` datetime DEFAULT NULL,
  `sex` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tb_users_entity` */

DROP TABLE IF EXISTS `tb_users_entity`;

CREATE TABLE `tb_users_entity` (
  `id` bigint(20) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `roles` varchar(255) NOT NULL,
  `head_img` varchar(255) NOT NULL,
  `nickname` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
