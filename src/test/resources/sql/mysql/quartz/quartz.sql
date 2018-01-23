DROP TABLE IF EXISTS yourdb.QRTZ_JOB_LISTENERS;  
DROP TABLE IF EXISTS yourdb.QRTZ_TRIGGER_LISTENERS;  
DROP TABLE IF EXISTS yourdb.QRTZ_FIRED_TRIGGERS;  
DROP TABLE IF EXISTS yourdb.QRTZ_PAUSED_TRIGGER_GRPS;  
DROP TABLE IF EXISTS yourdb.QRTZ_SCHEDULER_STATE;  
DROP TABLE IF EXISTS yourdb.QRTZ_LOCKS;  
DROP TABLE IF EXISTS yourdb.QRTZ_SIMPLE_TRIGGERS;  
DROP TABLE IF EXISTS yourdb.QRTZ_CRON_TRIGGERS;  
DROP TABLE IF EXISTS yourdb.QRTZ_BLOB_TRIGGERS;  
DROP TABLE IF EXISTS yourdb.QRTZ_TRIGGERS;  
DROP TABLE IF EXISTS yourdb.QRTZ_JOB_DETAILS;  
DROP TABLE IF EXISTS yourdb.QRTZ_CALENDARS;  
  
  
CREATE TABLE yourdb.QRTZ_JOB_DETAILS  
(  
JOB_NAME VARCHAR(200) NOT NULL,  
JOB_GROUP VARCHAR(200) NOT NULL,  
DESCRIPTION VARCHAR(250) NULL,  
JOB_CLASS_NAME VARCHAR(250) NOT NULL,  
IS_DURABLE VARCHAR(1) NOT NULL,  
IS_VOLATILE VARCHAR(1) NOT NULL,  
IS_STATEFUL VARCHAR(1) NOT NULL,  
REQUESTS_RECOVERY VARCHAR(1) NOT NULL,  
JOB_DATA BLOB NULL,  
PRIMARY KEY (JOB_NAME,JOB_GROUP)  
)type=INNODB;  
  
CREATE TABLE yourdb.QRTZ_JOB_LISTENERS  
(  
JOB_NAME VARCHAR(200) NOT NULL,  
JOB_GROUP VARCHAR(200) NOT NULL,  
JOB_LISTENER VARCHAR(200) NOT NULL,  
PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER),  
FOREIGN KEY (JOB_NAME,JOB_GROUP)  
REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP)  
)type=INNODB;  
  
CREATE TABLE yourdb.QRTZ_TRIGGERS  
(  
TRIGGER_NAME VARCHAR(200) NOT NULL,  
TRIGGER_GROUP VARCHAR(200) NOT NULL,  
JOB_NAME VARCHAR(200) NOT NULL,  
JOB_GROUP VARCHAR(200) NOT NULL,  
IS_VOLATILE VARCHAR(1) NOT NULL,  
DESCRIPTION VARCHAR(250) NULL,  
NEXT_FIRE_TIME BIGINT(13) NULL,  
PREV_FIRE_TIME BIGINT(13) NULL,  
PRIORITY INTEGER NULL,  
TRIGGER_STATE VARCHAR(16) NOT NULL,  
TRIGGER_TYPE VARCHAR(8) NOT NULL,  
START_TIME BIGINT(13) NOT NULL,  
END_TIME BIGINT(13) NULL,  
CALENDAR_NAME VARCHAR(200) NULL,  
MISFIRE_INSTR SMALLINT(2) NULL,  
JOB_DATA BLOB NULL,  
PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),  
FOREIGN KEY (JOB_NAME,JOB_GROUP)  
REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP)  
)type=INNODB;  
  
CREATE TABLE yourdb.QRTZ_SIMPLE_TRIGGERS  
(  
TRIGGER_NAME VARCHAR(200) NOT NULL,  
TRIGGER_GROUP VARCHAR(200) NOT NULL,  
REPEAT_COUNT BIGINT(7) NOT NULL,  
REPEAT_INTERVAL BIGINT(12) NOT NULL,  
TIMES_TRIGGERED BIGINT(7) NOT NULL,  
PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),  
FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)  
REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)  
)type=INNODB;  
  
CREATE TABLE yourdb.QRTZ_CRON_TRIGGERS  
(  
TRIGGER_NAME VARCHAR(200) NOT NULL,  
TRIGGER_GROUP VARCHAR(200) NOT NULL,  
CRON_EXPRESSION VARCHAR(200) NOT NULL,  
TIME_ZONE_ID VARCHAR(80),  
PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),  
FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)  
REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)  
)type=INNODB;  
  
CREATE TABLE yourdb.QRTZ_BLOB_TRIGGERS  
(  
TRIGGER_NAME VARCHAR(200) NOT NULL,  
TRIGGER_GROUP VARCHAR(200) NOT NULL,  
BLOB_DATA BLOB NULL,  
PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),  
FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)  
REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)  
)type=INNODB;  
  
CREATE TABLE yourdb.QRTZ_TRIGGER_LISTENERS  
(  
TRIGGER_NAME VARCHAR(200) NOT NULL,  
TRIGGER_GROUP VARCHAR(200) NOT NULL,  
TRIGGER_LISTENER VARCHAR(200) NOT NULL,  
PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER),  
FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)  
REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)  
)type=INNODB;  
  
  
CREATE TABLE yourdb.QRTZ_CALENDARS  
(  
CALENDAR_NAME VARCHAR(200) NOT NULL,  
CALENDAR BLOB NOT NULL,  
PRIMARY KEY (CALENDAR_NAME)  
)type=INNODB;  
  
  
  
CREATE TABLE yourdb.QRTZ_PAUSED_TRIGGER_GRPS  
(  
TRIGGER_GROUP VARCHAR(200) NOT NULL,  
PRIMARY KEY (TRIGGER_GROUP)  
)type=INNODB;  
  
CREATE TABLE yourdb.QRTZ_FIRED_TRIGGERS  
(  
ENTRY_ID VARCHAR(95) NOT NULL,  
TRIGGER_NAME VARCHAR(200) NOT NULL,  
TRIGGER_GROUP VARCHAR(200) NOT NULL,  
IS_VOLATILE VARCHAR(1) NOT NULL,  
INSTANCE_NAME VARCHAR(200) NOT NULL,  
FIRED_TIME BIGINT(13) NOT NULL,  
PRIORITY INTEGER NOT NULL,  
STATE VARCHAR(16) NOT NULL,  
JOB_NAME VARCHAR(200) NULL,  
JOB_GROUP VARCHAR(200) NULL,  
IS_STATEFUL VARCHAR(1) NULL,  
REQUESTS_RECOVERY VARCHAR(1) NULL,  
PRIMARY KEY (ENTRY_ID)  
)type=INNODB;  
  
CREATE TABLE yourdb.QRTZ_SCHEDULER_STATE  
(  
INSTANCE_NAME VARCHAR(200) NOT NULL,  
LAST_CHECKIN_TIME BIGINT(13) NOT NULL,  
CHECKIN_INTERVAL BIGINT(13) NOT NULL,  
PRIMARY KEY (INSTANCE_NAME)  
)type=INNODB;  
  
CREATE TABLE yourdb.QRTZ_LOCKS  
(  
LOCK_NAME VARCHAR(40) NOT NULL,  
PRIMARY KEY (LOCK_NAME)  
)type=INNODB;  
  
  
INSERT INTO yourdb.QRTZ_LOCKS VALUES('TRIGGER_ACCESS');  
INSERT INTO yourdb.QRTZ_LOCKS VALUES('JOB_ACCESS');  
INSERT INTO yourdb.QRTZ_LOCKS VALUES('CALENDAR_ACCESS');  
INSERT INTO yourdb.QRTZ_LOCKS VALUES('STATE_ACCESS');  
INSERT INTO yourdb.QRTZ_LOCKS VALUES('MISFIRE_ACCESS');  
  
  
COMMIT; 