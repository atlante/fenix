alter table STUDENT_TEST_QUESTION add column `ITEM_ID` VARCHAR(50) default NULL;

alter table STUDENT_TEST_QUESTION drop KEY `U1`;

alter table STUDENT_TEST_QUESTION add UNIQUE KEY `U1` (`KEY_STUDENT`,`KEY_DISTRIBUTED_TEST`,`KEY_QUESTION`, `ITEM_ID`);
