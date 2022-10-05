# Transactions Routine

### Application design information
- A [Transaction](https://github.com/Udit-Jindal/TransactionRoutine/blob/main/TransactionRoutineWebService/src/main/java/com/pismo/txnroutine/model/Transaction.java) is considered as an entity and all the type of transactions (Like, [Credit](https://github.com/Udit-Jindal/TransactionRoutine/blob/main/TransactionRoutineWebService/src/main/java/com/pismo/txnroutine/model/CreditVoucher.java), [Ins-Purchase](https://github.com/Udit-Jindal/TransactionRoutine/blob/main/TransactionRoutineWebService/src/main/java/com/pismo/txnroutine/model/InstallmentPurchase.java), [Withdrawl](https://github.com/Udit-Jindal/TransactionRoutine/blob/main/TransactionRoutineWebService/src/main/java/com/pismo/txnroutine/model/Withdrawal.java)) are implementing it. This is done to handle transaction specific logic independently.
- There is no transaction type distinction in database. All the transaction specific logic is in application layer only. 
- Auto converting the amount based on the operation type, inside the model. [Sample code](https://github.com/Udit-Jindal/TransactionRoutine/blob/main/TransactionRoutineWebService/src/main/java/com/pismo/txnroutine/model/Purchase.java#L13)
- The responsibility of generating unique ids like, accountId and TransactionId is deligated to DB. This is done to avoid possible cases of collision.
- Every request that is interacting with DB, is making a new connection. This is done for smooth commit and rollback implementation.
- Extra functionality of `delete` has been implemented in DAO for both account and transaction. This is done for testing the complete flow of entity creation.

### Code structure information
- Application logic is grouped under packages.
- Base package name is same for all i.e, `com.pismo.txnroutine`
    - Below is the brief information about all the packages
    
| Plugin | README |
| ------ | ------ |
| webservice | The http servlet code |
| model | Models of the asserted entities |
| exception | Custom exception classes |
| dao | Data access objects of all the entities  |
| dao.impl.sql | SQL implementation of all the data access objects |
| utils | Utility functions |
| test | All test cases |
| utils.test | Utility functions for the test classes|
    
### Database schema
```
DROP TABLE IF EXISTS `Account`;
CREATE TABLE `Account` (
  `account_id` int NOT NULL AUTO_INCREMENT,
  `document_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



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
```

### Usage guide
- Maven build will generate a war. It can be deployed in tomcat 9.
- Need to set following environment vriables

| Variable Name | Description |
| ------ | ------ |
| MYSQL_HOSTNAME | Address of the mysql server |
| MYSQL_PORT | Open port |
| MYSQL_DB_NAME | Database name |
| MYSQL_USER | User name  |
| MYSQL_PASS | Password |


### Possible improvements
- Docker can be used for easy deployement.
- For the sake of simplicity, no framework has been used. In case more APIs are needed, it would make sense to use a framework like SpringBoot.
- A deployement script for setup.
- A separate in-memory DB can be used for tests.
- Embedded web server can be used, like jetty.
- DB connection pooling should be implemented.
