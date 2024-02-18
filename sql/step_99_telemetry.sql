DROP TABLE IF EXISTS OH_TELEMETRY;
CREATE TABLE OH_TELEMETRY (
  -- identification
  TEL_UUID VARCHAR(36) NOT NULL COMMENT 'Software ID',
  TEL_DBID VARCHAR(36) NOT NULL COMMENT 'Database ID',
  TEL_HWID VARCHAR(36) NOT NULL COMMENT 'Hardware ID',
  TEL_OSID VARCHAR(36) NOT NULL COMMENT 'Operating System ID',
  -- settings
  TEL_ACTIVE TINYINT(1) COMMENT 'true|false|null',
  TEL_CONSENT TEXT COMMENT 'User consent informations',
  -- history
  TEL_INFO TEXT COMMENT 'Last collected data',
  TEL_SENT_TIME DATETIME COMMENT 'Timestamp when message sent',
  TEL_OPTIN_DATE DATETIME COMMENT 'When user enables telemetry',
  TEL_OPTOUT_DATE DATETIME COMMENT 'When user disables telemetry',
  PRIMARY KEY (TEL_UUID,TEL_DBID,TEL_HWID,TEL_OSID)
) ENGINE=MyISAM;

INSERT INTO OH_MENUITEM VALUES ('telemetry', 'angal.menu.btn.telemetry', 'angal.menu.telemetry', 'x', 'M', 'generaldata', 'org.isf.telemetry.gui.TelemetryEdit','N', 9);
INSERT INTO OH_GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE) VALUES ('admin','telemetry',1);
