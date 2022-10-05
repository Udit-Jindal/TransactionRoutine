--
-- Table structure for table `Account`
--

DROP TABLE IF EXISTS `Account`;
CREATE TABLE `Account` (
  `account_id` int NOT NULL AUTO_INCREMENT,
  `document_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `Transaction`
--

DROP TABLE IF EXISTS `Transaction`;
CREATE TABLE `Transaction` (
  `txn_id` bigint NOT NULL AUTO_INCREMENT,
  `account_id` int NOT NULL,
  `operation_type` int NOT NULL,
  `amount` double NOT NULL,
  `event_date` timestamp NOT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`txn_id`),
  KEY `Transaction_FK` (`account_id`),
  CONSTRAINT `Transaction_FK` FOREIGN KEY (`account_id`) REFERENCES `Account` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;