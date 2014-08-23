DROP TABLE IF EXISTS `CorrectionRequest`;
DROP TABLE IF EXISTS `SigninData`;
DROP TABLE IF EXISTS `Employee`;

DROP TABLE IF EXISTS `EmployeeType`;
DROP TABLE IF EXISTS `CorrectionRequestStatus`;

DROP TABLE IF EXISTS `PeriodEnd`;

-- End of drop tables, begin of create tables

CREATE TABLE `PeriodEnd` (
	PeriodEndId INT NOT NULL PRIMARY KEY,
	PeriodEnd   DATE NOT NULL,

	INDEX (PeriodEnd)

) CHARSET = UTF8, ENGINE = InnoDB;

CREATE TABLE `CorrectionRequestStatus` (
	CorrectionRequestStatusId   INT NOT NULL PRIMARY KEY,
	CorrectionRequestStatusName VARCHAR(128) NOT NULL
) CHARSET = UTF8, ENGINE = InnoDB;

CREATE TABLE `EmployeeType` (
	EmployeeTypeId   INT NOT NULL PRIMARY KEY,
	EmployeeTypeName VARCHAR(128) NOT NULL
) CHARSET = UTF8, ENGINE = InnoDB;


CREATE TABLE `Employee` (
	EmployeeId        INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	EmployeeFirstName VARCHAR(128) NOT NULL,
	EmployeeLastName  VARCHAR(128) NOT NULL,
	EmployeeRiceId    VARCHAR(32) NOT NULL,
	EmployeeNetId     VARCHAR(32) NOT NULL,
	EmployeeTypeId    INT NOT NULL,
	EmployeeIsActive  BOOLEAN NOT NULL,

	FOREIGN KEY (EmployeeTypeId) REFERENCES `EmployeeType` (`EmployeeTypeId`)

) CHARSET = UTF8, ENGINE = InnoDB, AUTO_INCREMENT = 1;

CREATE TABLE `SigninData` (
	SigninDataId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	EmployeeId   INT NOT NULL,
	SigninTime   TIMESTAMP NOT NULL,
	SignoutTime  TIMESTAMP,
	CreateDate   TIMESTAMP NOT NULL,

	INDEX (SigninTime),
	Index (SignoutTime),

	FOREIGN KEY (EmployeeId) REFERENCES `Employee` (`EmployeeId`)

) CHARSET = UTF8, ENGINE = InnoDB, AUTO_INCREMENT = 1;

CREATE TABLE `CorrectionRequest` (
	CorrectionRequestId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	CorrectionRequestStatusId INT NOT NULL,
	SigninDataId        INT NOT NULL,
	SubmitterEmployeeId INT NOT NULL,
	NewSigninTime       TIMESTAMP NOT NULL,
	NewSignoutTime      TIMESTAMP NOT NULL,
	OriginalSigninTime  TIMESTAMP NOT NULL,
	OriginalSignoutTime TIMESTAMP NOT NULL,

	FOREIGN KEY (SubmitterEmployeeId) REFERENCES `Employee` (`EmployeeId`),
	FOREIGN KEY (CorrectionRequestStatusId) REFERENCES `CorrectionRequestStatus` (`CorrectionRequestStatusId`)

) CHARSET = UTF8, ENGINE = InnoDB, AUTO_INCREMENT = 1;


INSERT INTO `EmployeeType` (`EmployeeTypeId`, `EmployeeTypeName`) VALUES 
	(0, 'NONE'),
	(1, 'SCC'),
	(2, 'SCC_LEAD'),
	(3, 'SUPERVISOR'),
	(4, 'SYSADMIN');

INSERT INTO `CorrectionRequestStatus` (`CorrectionRequestStatusId`, `CorrectionRequestStatusName`) VALUES
	(0, 'PENDING'),
	(1, 'APPROVED'),
	(2, 'DENIED');

