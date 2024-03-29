CREATE TABLE OH_PERMISSIONS (
  P_ID_A int NOT NULL auto_increment,
  P_NAME varchar(50) NOT NULL default '',
  P_DESCRIPTION varchar(255) NOT NULL default '',
  P_ACTIVE char(1) NOT NULL default '',
  P_CREATED_BY varchar(50) default NULL,
  P_CREATED_DATE datetime default NULL,
  P_LAST_MODIFIED_BY varchar(50) default NULL,
  P_LAST_MODIFIED_DATE datetime default NULL,
  PRIMARY KEY ( P_ID_A )
) ENGINE INNODB DEFAULT CHARACTER SET utf8;



INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('opd.read','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('opd.create','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('opd.update','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('opd.delete','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('summary.read','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('summary.create','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('summary.update','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('summary.delete','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('examination.read','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('examination.create','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('examination.update','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('examination.delete','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('admission.read','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('admission.create','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('admission.update','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('admission.delete','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('therapy.read','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('therapy.create','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('therapy.update','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('therapy.delete','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('vaccine.read','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('vaccine.create','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('vaccine.update','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('vaccine.delete','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('exam.read','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('exam.create','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('exam.update','',1,NULL,NULL,NULL,NULL);
INSERT INTO OH_PERMISSIONS (P_NAME, P_DESCRIPTION, P_ACTIVE, P_CREATED_BY, P_CREATED_DATE, P_LAST_MODIFIED_BY, P_LAST_MODIFIED_DATE) VALUES ('exam.delete','',1,NULL,NULL,NULL,NULL);

CREATE TABLE OH_GROUPPERMISSION (
  GP_ID int NOT NULL auto_increment,
  GP_UG_ID_A varchar(50) NOT NULL default '',
  GP_P_ID_A int NOT NULL,
  GP_ACTIVE char(1) NOT NULL default '',
  GP_CREATED_BY varchar(50) default NULL,
  GP_CREATED_DATE datetime default NULL,
  GP_LAST_MODIFIED_BY varchar(50) default NULL,
  GP_LAST_MODIFIED_DATE datetime default NULL,
  PRIMARY KEY (GP_ID)
) ENGINE INNODB DEFAULT CHARACTER SET utf8;

INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',1,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',2,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',3,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',4,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',5,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',6,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',7,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',8,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',9,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',10,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',11,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',12,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',13,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',14,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',15,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',16,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',17,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',18,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',19,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',20,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',21,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',22,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',23,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',24,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',25,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',26,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',27,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('admin',28,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('guest',1,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('guest',5,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('guest',9,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('guest',13,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('guest',17,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('guest',21,1,NULL,NULL,NULL,NULL);
INSERT INTO OH_GROUPPERMISSION (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE, GP_CREATED_BY,  GP_CREATED_DATE, GP_LAST_MODIFIED_BY, GP_LAST_MODIFIED_DATE) VALUES ('guest',25,1,NULL,NULL,NULL,NULL);
