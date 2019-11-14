/*
Note:
Please create 2 databases in SQLServer: dal_shard_0 and dal_shard_1 before executing the scripts.
And execute the following scripts to create 5 tables for each database.

The MySQL scripts creates 5 tables as follows:
person,
person_0,
person_1,
person_2,
person_3

*/

-- ----------------------------
-- Table structure for person
-- ----------------------------
DROP TABLE [dbo].[person]
GO
CREATE TABLE [dbo].[person] (
[PeopleID] int NOT NULL IDENTITY(1,1) ,
[Name] varchar(50) NULL ,
[CityID] int NULL ,
[ProvinceID] int NULL ,
[CountryID] int NULL ,
[DataChange_LastTime] datetime NULL 
)


GO
DBCC CHECKIDENT(N'[dbo].[person]', RESEED, 312770)
GO

-- ----------------------------
-- Table structure for person_0
-- ----------------------------
DROP TABLE [dbo].[person_0]
GO
CREATE TABLE [dbo].[person_0] (
[PeopleID] int NOT NULL IDENTITY(1,1) ,
[Name] varchar(50) NULL ,
[CityID] int NULL ,
[ProvinceID] int NULL ,
[CountryID] int NULL ,
[DataChange_LastTime] datetime NULL 
)


GO
DBCC CHECKIDENT(N'[dbo].[person_0]', RESEED, 391827)
GO

-- ----------------------------
-- Table structure for person_1
-- ----------------------------
DROP TABLE [dbo].[person_1]
GO
CREATE TABLE [dbo].[person_1] (
[PeopleID] int NOT NULL IDENTITY(1,1) ,
[Name] varchar(50) NULL ,
[CityID] int NULL ,
[ProvinceID] int NULL ,
[CountryID] int NULL ,
[DataChange_LastTime] datetime NULL 
)


GO
DBCC CHECKIDENT(N'[dbo].[person_1]', RESEED, 337326)
GO

-- ----------------------------
-- Table structure for person_2
-- ----------------------------
DROP TABLE [dbo].[person_2]
GO
CREATE TABLE [dbo].[person_2] (
[PeopleID] int NOT NULL IDENTITY(1,1) ,
[Name] varchar(50) NULL ,
[CityID] int NULL ,
[ProvinceID] int NULL ,
[CountryID] int NULL ,
[DataChange_LastTime] datetime NULL 
)


GO
DBCC CHECKIDENT(N'[dbo].[person_2]', RESEED, 338041)
GO

-- ----------------------------
-- Table structure for person_3
-- ----------------------------
DROP TABLE [dbo].[person_3]
GO
CREATE TABLE [dbo].[person_3] (
[PeopleID] int NOT NULL IDENTITY(1,1) ,
[Name] varchar(50) NULL ,
[CityID] int NULL ,
[ProvinceID] int NULL ,
[CountryID] int NULL ,
[DataChange_LastTime] datetime NULL 
)


GO
DBCC CHECKIDENT(N'[dbo].[person_3]', RESEED, 341110)
GO

-- ----------------------------
-- Indexes structure for table person
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table person
-- ----------------------------
ALTER TABLE [dbo].[person] ADD PRIMARY KEY ([PeopleID])
GO

-- ----------------------------
-- Indexes structure for table person_0
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table person_0
-- ----------------------------
ALTER TABLE [dbo].[person_0] ADD PRIMARY KEY ([PeopleID])
GO

-- ----------------------------
-- Indexes structure for table person_1
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table person_1
-- ----------------------------
ALTER TABLE [dbo].[person_1] ADD PRIMARY KEY ([PeopleID])
GO

-- ----------------------------
-- Indexes structure for table person_2
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table person_2
-- ----------------------------
ALTER TABLE [dbo].[person_2] ADD PRIMARY KEY ([PeopleID])
GO

-- ----------------------------
-- Indexes structure for table person_3
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table person_3
-- ----------------------------
ALTER TABLE [dbo].[person_3] ADD PRIMARY KEY ([PeopleID])
GO
