CREATE TABLE SCIM.dbo.USR_FOLDER_MST (
	FLD_SEQ int NULL,
	FLD_NM varchar(100) COLLATE Korean_Wansung_CI_AS NULL,
	FLD_DESC varchar(1000) COLLATE Korean_Wansung_CI_AS NULL,
	FLD_ID varchar(100) COLLATE Korean_Wansung_CI_AS NULL,
	PAR_FLD_SEQ int NULL,
	CLS_SEQ int NULL,
	PROJECT_START_DT varchar(255) COLLATE Korean_Wansung_CI_AS NULL,
	PROJECT_END_DT varchar(255) COLLATE Korean_Wansung_CI_AS NULL,
	PROJECT_CODE varchar(255) COLLATE Korean_Wansung_CI_AS NULL,
	PROJECT_NAME varchar(255) COLLATE Korean_Wansung_CI_AS NULL,
	COMPANY_NAME varchar(255) COLLATE Korean_Wansung_CI_AS NULL,
	INPUT_USER varchar(100) COLLATE Korean_Wansung_CI_AS NULL,
	INPUT_USER_NUMBER varchar(100) COLLATE Korean_Wansung_CI_AS NULL,
	CHECK_YN varchar(50) COLLATE Korean_Wansung_CI_AS NULL,
	CHECK_HISTORY_YN varchar(50) COLLATE Korean_Wansung_CI_AS NULL,
	CHECK_USER_ID varchar(100) COLLATE Korean_Wansung_CI_AS NULL,
	DISP_SEQ int NULL,
	[DEPTH] int NULL,
	UPD_ID varchar(20) COLLATE Korean_Wansung_CI_AS NULL,
	UPD_DT smalldatetime NULL,
	REG_ID varchar(20) COLLATE Korean_Wansung_CI_AS NULL,
	REG_DT smalldatetime NULL,
	FLD_ADMIN_USER_ID varchar(20) COLLATE Korean_Wansung_CI_AS NULL,
	FLD_PATH varchar(500) COLLATE Korean_Wansung_CI_AS NULL,
	CNTRC_NO varchar(100) COLLATE Korean_Wansung_CI_AS NULL,
	CNTRC_DT varchar(100) COLLATE Korean_Wansung_CI_AS NULL
	
) GO




INSERT INTO USR_FOLDER_MST VALUES (137294,'희망아지트(옥인동47-448) 조성공사',null,'201815853A01',82359,1,'20210517','20210810',null,null,null,null,null,'N',null,null,null,null,'sysadm','2021-07-12 15:53','sysadm','2021-07-12 15:53',null,'SH공사>5.주택건설공사>희망아지트(옥인동47-448) 조성공사>',null,null);

INSERT INTO USR_FOLDER_MST VALUES (137295,'2021년 임대아파트 인터폰 교체공사(1권역)',null,'504701633T01',82360,1,'20210713','20220312',null,null,null,null,null,'N',null,null,null,null,'sysadm','2021-07-19 09:21','sysadm','2021-07-19 09:21',null,'SH공사>6.기타사업공사>2021년 임대아파트 인터폰 교체공사(1권역)>',null,null);

INSERT INTO USR_FOLDER_MST VALUES (137296,'2021년 임대아파트 인터폰 교체공사(3권역)',null,'504701673T01',82360,1,'20210713','20220312',null,null,null,null,null,'N',null,null,null,null,'sysadm','2021-07-19 09:21','sysadm','2021-07-19 09:21',null,'SH공사>6.기타사업공사>2021년 임대아파트 인터폰 교체공사(3권역)>',null,null);

-- Drop table

-- DROP TABLE SCIM.dbo.OBJ_OBJECT_MST GO

CREATE TABLE SCIM.dbo.OBJ_OBJECT_MST (
	OBJ_SEQ int NULL,
	OBJ_NUMBER varchar(100) COLLATE Korean_Wansung_CI_AS NULL,
	OBJ_NM varchar(1000) COLLATE Korean_Wansung_CI_AS NULL,
	SECURITY_LVL int NULL,
	OBJ_INIT_STATE_COMM_CD varchar(4) COLLATE Korean_Wansung_CI_AS NULL,
	OBJ_DESC varchar(1000) COLLATE Korean_Wansung_CI_AS NULL,
	OBJ_LINK varchar(1000) COLLATE Korean_Wansung_CI_AS NULL,
	VERSION int NULL,
	REVISION int NULL,
	OWNER_USER_ID varchar(20) COLLATE Korean_Wansung_CI_AS NULL,
	CLS_SEQ int NULL,
	OBJ_STATE_COMM_CD varchar(4) COLLATE Korean_Wansung_CI_AS NULL,
	CLS_TYPE_COMM_CD varchar(4) COLLATE Korean_Wansung_CI_AS NULL,
	DELETE_YN varchar(1) COLLATE Korean_Wansung_CI_AS NULL,
	UPD_ID varchar(20) COLLATE Korean_Wansung_CI_AS NULL,
	UPD_DT datetime NULL,
	REG_ID varchar(20) COLLATE Korean_Wansung_CI_AS NULL,
	REG_DT datetime NULL,
	ORG_SUMMARY_PATH varchar(1000) COLLATE Korean_Wansung_CI_AS NULL,
	ORG_FULL_PATH varchar(1000) COLLATE Korean_Wansung_CI_AS NULL,
	REG_TYPE_COMM_CD varchar(4) COLLATE Korean_Wansung_CI_AS NULL,
	UPD_TYPE_COMM_CD varchar(4) COLLATE Korean_Wansung_CI_AS NULL
) GO

INSERT INTO OBJ_OBJECT_MST VALUES (862793,'2014-0012793','전기,통신,전기소방.pdf',3,'5','','',0,1,'sysadm',1,'5','1','N','sysadm','2014-08-12 19:44','sysadm','2014-08-12 19:44' , '' , '' , '2','2');
INSERT INTO OBJ_OBJECT_MST VALUES (862794,'2014-0012794','기계설비,소방설비.pdf' ,3,'5','','',0,1,'sysadm',1,'5','1','N','sysadm','2014-08-12 19:44','sysadm','2014-08-12 19:44' , '' , '' , '2','2');
INSERT INTO OBJ_OBJECT_MST VALUES (862795,'2014-0012795','구조.pdf'		    ,3,'5','','',0,1,'sysadm',1,'5','1','N','sysadm','2014-08-12 19:44','sysadm','2014-08-12 19:44' , '' , '' , '2','2');


-- Drop table

-- DROP TABLE SCIM.dbo.OBJ_FILE_LST GO

CREATE TABLE SCIM.dbo.OBJ_FILE_LST (
	OBJ_FILE_SEQ int NULL,
	OBJ_SEQ int NULL,
	ORG_FILE_NM varchar(200) COLLATE Korean_Wansung_CI_AS NULL,
	FILE_PATH varchar(200) COLLATE Korean_Wansung_CI_AS NULL,
	FILE_NM varchar(100) COLLATE Korean_Wansung_CI_AS NULL,
	FILE_SIZE int NULL,
	PREV_FILE_NM varchar(100) COLLATE Korean_Wansung_CI_AS NULL,
	MASTER_YN varchar(1) COLLATE Korean_Wansung_CI_AS NULL,
	REG_DT datetime NULL,
	UPD_ID varchar(20) COLLATE Korean_Wansung_CI_AS NULL,
	UPD_DT datetime NULL,
	REG_ID varchar(20) COLLATE Korean_Wansung_CI_AS NULL,
	FILE_URL_TYPE varchar(4) COLLATE Korean_Wansung_CI_AS NULL
) GO

INSERT INTO OBJ_FILE_LST VALUES (1,2422982,'8c3_0052.tif','D:/upload','/migration/170/ceddbac56560426896c23d8389154cb9.tif',35298,'','Y','2014-10-07 18:06:59','sysadm','2014-10-07 18:06:59','sysadm','1');
INSERT INTO OBJ_FILE_LST VALUES (1,2422983,'8c3_0053.tif','D:/upload','/migration/170/625946af3dfe40bdb7e2d6f65186b1f6.tif',34588,'','Y','2014-10-07 18:06:59','sysadm','2014-10-07 18:06:59','sysadm','1');
INSERT INTO OBJ_FILE_LST VALUES (1,2422984,'8c3_0054.tif','D:/upload','/migration/170/834b3eff9c2d466b81d07421ca7fd675.tif',35292,'','Y','2014-10-07 18:06:59','sysadm','2014-10-07 18:06:59','sysadm','1');

CREATE TABLE SCIM.dbo.OBJ_FLD_REL (
	OBJ_SEQ int NULL,
	FLD_SEQ int NULL,
	OBJ_TYPE_COMM_CD varchar(4) COLLATE Korean_Wansung_CI_AS NULL,
	ORG_YN varchar(1) COLLATE Korean_Wansung_CI_AS NULL,
	WORK_SEQ int NULL
) GO

INSERT INTO OBJ_FLD_REL VALUES (2449508,95511,'1','Y',0);
INSERT INTO OBJ_FLD_REL VALUES (2449509,95511,'1','Y',0);
INSERT INTO OBJ_FLD_REL VALUES (2449510,95511,'1','Y',0);

-- Drop table

-- DROP TABLE SCIM.dbo.USR_FLD_AUTH_REL GO

CREATE TABLE SCIM.dbo.USR_FLD_AUTH_REL (
	FLD_SEQ int NULL,
	GRP_SEQ int NULL,
	AUTH_VALUE varchar(100) COLLATE Korean_Wansung_CI_AS NULL,
	UPD_ID varchar(20) COLLATE Korean_Wansung_CI_AS NULL,
	UPD_DT datetime NULL,
	REG_ID varchar(20) COLLATE Korean_Wansung_CI_AS NULL,
	REG_DT datetime NULL
) GO

INSERT INTO USR_FLD_AUTH_REL VALUES (133802,1963,'111100111','sysadm','2021-07-07 10:06:36' , 'sysadm','2021-07-07 10:06:36');
INSERT INTO USR_FLD_AUTH_REL VALUES (133803,1963,'111100111','sysadm','2021-07-07 10:06:36' , 'sysadm','2021-07-07 10:06:36');
INSERT INTO USR_FLD_AUTH_REL VALUES (133804,1963,'111100111','sysadm','2021-07-07 10:06:36' , 'sysadm','2021-07-07 10:06:36');

-- Drop table

-- DROP TABLE SCIM.dbo.USR_GRP_MST GO

CREATE TABLE SCIM.dbo.USR_GRP_MST (
	GRP_SEQ int NULL,
	GRP_NM varchar(100) COLLATE Korean_Wansung_CI_AS NULL,
	GRP_DESC varchar(1000) COLLATE Korean_Wansung_CI_AS NULL,
	GRP_TYPE_COMM_CD varchar(4) COLLATE Korean_Wansung_CI_AS NULL,
	ORG_SEQ int NULL,
	USER_ID varchar(20) COLLATE Korean_Wansung_CI_AS NULL,
	UPD_ID varchar(20) COLLATE Korean_Wansung_CI_AS NULL,
	UPD_DT datetime NULL,
	REG_ID varchar(20) COLLATE Korean_Wansung_CI_AS NULL,
	REG_DT datetime NULL
) GO

INSERT INTO USR_GRP_MST VALUES (0,'every one','모든 사용자','0',null , 'sysadm','sysadm','2011-02-21 16:27:53','sysadm','2011-02-21 16:27:53');
INSERT INTO USR_GRP_MST VALUES (3013,'SH공사' ,'SH공사'	,'1',1 	  , ''		,'eckang','2014-09-12 11:15:35','eckang','2014-09-12 11:15:35');
INSERT INTO USR_GRP_MST VALUES (3067,'협력사'	 ,''		,'1',2 	  , ''		,'eckang','2011-09-26 13:22:07','eckang','2011-09-26 13:22:07');
INSERT INTO USR_GRP_MST VALUES (1038,'worker2 work2','worker2 work2','2',null , 'work2','sysadm','2014-08-19 16:24:12','sysadm','2014-08-19 16:24:12');



