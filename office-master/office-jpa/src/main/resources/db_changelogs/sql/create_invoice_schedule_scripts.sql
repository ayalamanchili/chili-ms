
--
-- System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
--

CREATE TABLE `invoiceschedule` (
  `id` bigint(20) NOT NULL,
  `sourceEntityId` bigint(20) DEFAULT NULL,
  `sourceEntityName` varchar(255) DEFAULT NULL,
  `targetEntityId` bigint(20) NOT NULL,
  `targetEntityName` varchar(255) NOT NULL,
  `version` int(11) DEFAULT NULL,
  `endDate` date NOT NULL,
  `notes` longtext,
  `reminderDays` int(11) NOT NULL,
  `startDate` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `InvoiceScheduleTGT_ENTY_ID` (`targetEntityId`),
  KEY `InvoiceScheduleSRC_ENTY_NM` (`sourceEntityName`),
  KEY `InvoiceScheduleSRC_ENTY_ID` (`sourceEntityId`),
  KEY `InvoiceScheduleTGT_ENTY_NM` (`targetEntityName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `invoiceschedule_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `sourceEntityId` bigint(20) DEFAULT NULL,
  `sourceEntityName` varchar(255) DEFAULT NULL,
  `targetEntityId` bigint(20) DEFAULT NULL,
  `targetEntityName` varchar(255) DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  `notes` longtext,
  `reminderDays` int(11) DEFAULT NULL,
  `startDate` date DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FKFD09F4DE5B7A65D0b55aa095` (`REV`),
  CONSTRAINT `FKFD09F4DE5B7A65D0b55aa095` FOREIGN KEY (`REV`) REFERENCES `auditrevisionentity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


