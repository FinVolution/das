/*
The MySQL scripts creates 2 databases and 10 tables as follows:

database: dal_shard_0
     tables: person,
             person_0,
             person_1,
             person_2,
             person_3,

database: dal_shard_1
     tables: person,
             person_0,
             person_1,
             person_2,
             person_3,
*/
drop database if exists dal_shard_0;
create database dal_shard_0;

use dal_shard_0;
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for person
-- ----------------------------
DROP TABLE IF EXISTS `person`;
CREATE TABLE `person` (
  `PeopleID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) DEFAULT NULL,
  `CityID` int(11) DEFAULT NULL,
  `ProvinceID` int(11) DEFAULT NULL,
  `CountryID` int(11) DEFAULT NULL,
  `DataChange_LastTime` datetime DEFAULT NULL,
  PRIMARY KEY (`PeopleID`)
) ENGINE=InnoDB AUTO_INCREMENT=8784 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for person_0
-- ----------------------------
DROP TABLE IF EXISTS `person_0`;
CREATE TABLE `person_0` (
  `PeopleID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) DEFAULT NULL,
  `CityID` int(11) DEFAULT NULL,
  `ProvinceID` int(11) DEFAULT NULL,
  `CountryID` int(11) DEFAULT NULL,
  `DataChange_LastTime` datetime DEFAULT NULL,
  PRIMARY KEY (`PeopleID`)
) ENGINE=InnoDB AUTO_INCREMENT=10861 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for person_1
-- ----------------------------
DROP TABLE IF EXISTS `person_1`;
CREATE TABLE `person_1` (
  `PeopleID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) DEFAULT NULL,
  `CityID` int(11) DEFAULT NULL,
  `ProvinceID` int(11) DEFAULT NULL,
  `CountryID` int(11) DEFAULT NULL,
  `DataChange_LastTime` datetime DEFAULT NULL,
  PRIMARY KEY (`PeopleID`)
) ENGINE=InnoDB AUTO_INCREMENT=9685 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for person_2
-- ----------------------------
DROP TABLE IF EXISTS `person_2`;
CREATE TABLE `person_2` (
  `PeopleID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) DEFAULT NULL,
  `CityID` int(11) DEFAULT NULL,
  `ProvinceID` int(11) DEFAULT NULL,
  `CountryID` int(11) DEFAULT NULL,
  `DataChange_LastTime` datetime DEFAULT NULL,
  PRIMARY KEY (`PeopleID`)
) ENGINE=InnoDB AUTO_INCREMENT=9748 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for person_3
-- ----------------------------
DROP TABLE IF EXISTS `person_3`;
CREATE TABLE `person_3` (
  `PeopleID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) DEFAULT NULL,
  `CityID` int(11) DEFAULT NULL,
  `ProvinceID` int(11) DEFAULT NULL,
  `CountryID` int(11) DEFAULT NULL,
  `DataChange_LastTime` datetime DEFAULT NULL,
  PRIMARY KEY (`PeopleID`)
) ENGINE=InnoDB AUTO_INCREMENT=9811 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Procedure structure for SP_WITHOUT_OUT_PARAM
-- ----------------------------
DROP PROCEDURE IF EXISTS `SP_WITHOUT_OUT_PARAM`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_WITHOUT_OUT_PARAM`(v_id int,v_cityID int,v_countryID int,v_name VARCHAR(64))
BEGIN INSERT INTO person(peopleid, cityID, countryID, name) VALUES(v_id, v_cityID, v_countryID, v_name);END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for SP_WITH_INTERMEDIATE_RESULT
-- ----------------------------
DROP PROCEDURE IF EXISTS `SP_WITH_INTERMEDIATE_RESULT`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_WITH_INTERMEDIATE_RESULT`(v_id int,v_quantity int,v_type smallint,INOUT v_address VARCHAR(64))
BEGIN UPDATE dal_client_test SET quantity = v_quantity, type=v_type, address=v_address WHERE id=v_id;SELECT ROW_COUNT() AS result;SELECT 1 AS result2;UPDATE dal_client_test SET `quantity` = quantity + 1, `type`=type + 1, `address`='aaa';SELECT 'abc' AS result3, 456 AS count2;SELECT * from dal_client_test;SELECT 'output' INTO v_address;END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for SP_WITH_IN_OUT_PARAM
-- ----------------------------
DROP PROCEDURE IF EXISTS `SP_WITH_IN_OUT_PARAM`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_WITH_IN_OUT_PARAM`(v_id int,v_cityID int,v_countryID int,INOUT v_name VARCHAR(64))
BEGIN UPDATE person SET cityID = v_cityID, countryID=v_countryID, name=v_name WHERE peopleid=v_id;SELECT 'output' INTO v_name;END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for SP_WITH_OUT_PARAM
-- ----------------------------
DROP PROCEDURE IF EXISTS `SP_WITH_OUT_PARAM`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_WITH_OUT_PARAM`(v_id int,out count int)
BEGIN DELETE FROM person WHERE peopleid=v_id;SELECT COUNT(*) INTO count from person;END
;;
DELIMITER ;

drop database if exists dal_shard_1;
create database dal_shard_1;

use dal_shard_1;
-- ----------------------------
-- Table structure for person
-- ----------------------------
DROP TABLE IF EXISTS `person`;
CREATE TABLE `person` (
  `PeopleID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) DEFAULT NULL,
  `CityID` int(11) DEFAULT NULL,
  `ProvinceID` int(11) DEFAULT NULL,
  `CountryID` int(11) DEFAULT NULL,
  `DataChange_LastTime` datetime DEFAULT NULL,
  PRIMARY KEY (`PeopleID`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for person_0
-- ----------------------------
DROP TABLE IF EXISTS `person_0`;
CREATE TABLE `person_0` (
  `PeopleID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) DEFAULT NULL,
  `CityID` int(11) DEFAULT NULL,
  `ProvinceID` int(11) DEFAULT NULL,
  `CountryID` int(11) DEFAULT NULL,
  `DataChange_LastTime` datetime DEFAULT NULL,
  PRIMARY KEY (`PeopleID`)
) ENGINE=InnoDB AUTO_INCREMENT=288 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for person_1
-- ----------------------------
DROP TABLE IF EXISTS `person_1`;
CREATE TABLE `person_1` (
  `PeopleID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) DEFAULT NULL,
  `CityID` int(11) DEFAULT NULL,
  `ProvinceID` int(11) DEFAULT NULL,
  `CountryID` int(11) DEFAULT NULL,
  `DataChange_LastTime` datetime DEFAULT NULL,
  PRIMARY KEY (`PeopleID`)
) ENGINE=InnoDB AUTO_INCREMENT=291 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for person_2
-- ----------------------------
DROP TABLE IF EXISTS `person_2`;
CREATE TABLE `person_2` (
  `PeopleID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) DEFAULT NULL,
  `CityID` int(11) DEFAULT NULL,
  `ProvinceID` int(11) DEFAULT NULL,
  `CountryID` int(11) DEFAULT NULL,
  `DataChange_LastTime` datetime DEFAULT NULL,
  PRIMARY KEY (`PeopleID`)
) ENGINE=InnoDB AUTO_INCREMENT=294 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for person_3
-- ----------------------------
DROP TABLE IF EXISTS `person_3`;
CREATE TABLE `person_3` (
  `PeopleID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) DEFAULT NULL,
  `CityID` int(11) DEFAULT NULL,
  `ProvinceID` int(11) DEFAULT NULL,
  `CountryID` int(11) DEFAULT NULL,
  `DataChange_LastTime` datetime DEFAULT NULL,
  PRIMARY KEY (`PeopleID`)
) ENGINE=InnoDB AUTO_INCREMENT=295 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Procedure structure for SP_WITHOUT_OUT_PARAM
-- ----------------------------
DROP PROCEDURE IF EXISTS `SP_WITHOUT_OUT_PARAM`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_WITHOUT_OUT_PARAM`(v_id int,v_cityID int,v_countryID int,v_name VARCHAR(64))
BEGIN INSERT INTO person(peopleid, cityID, countryID, name) VALUES(v_id, v_cityID, v_countryID, v_name);END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for SP_WITH_INTERMEDIATE_RESULT
-- ----------------------------
DROP PROCEDURE IF EXISTS `SP_WITH_INTERMEDIATE_RESULT`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_WITH_INTERMEDIATE_RESULT`(v_id int,v_quantity int,v_type smallint,INOUT v_address VARCHAR(64))
BEGIN UPDATE dal_client_test SET quantity = v_quantity, type=v_type, address=v_address WHERE id=v_id;SELECT ROW_COUNT() AS result;SELECT 1 AS result2;UPDATE dal_client_test SET `quantity` = quantity + 1, `type`=type + 1, `address`='aaa';SELECT 'abc' AS result3, 456 AS count2;SELECT * from dal_client_test;SELECT 'output' INTO v_address;END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for SP_WITH_IN_OUT_PARAM
-- ----------------------------
DROP PROCEDURE IF EXISTS `SP_WITH_IN_OUT_PARAM`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_WITH_IN_OUT_PARAM`(v_id int,v_cityID int,v_countryID int,INOUT v_name VARCHAR(64))
BEGIN UPDATE person SET cityID = v_cityID, countryID=v_countryID, name=v_name WHERE peopleid=v_id;SELECT 'output' INTO v_name;END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for SP_WITH_OUT_PARAM
-- ----------------------------
DROP PROCEDURE IF EXISTS `SP_WITH_OUT_PARAM`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `SP_WITH_OUT_PARAM`(v_id int,out count int)
BEGIN DELETE FROM person WHERE peopleid=v_id;SELECT COUNT(*) INTO count from person;END
;;
DELIMITER ;
