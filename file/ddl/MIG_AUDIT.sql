CREATE TABLE `MIG_AUDIT` (
  `SRC_ID` varchar(100) NOT NULL COMMENT '원데이터 id',
  `RESULT` varchar(10) DEFAULT NULL COMMENT 'API 호출 결과 코드 (0:성공 , 그외: 실패)',
  `ACTION` varchar(20) DEFAULT NULL COMMENT 'CREAT , UPDATE , IGNORE , ERROR',
  `TIME` varchar(20) DEFAULT NULL COMMENT '실행 시간',
  `MSG` varchar(100) DEFAULT NULL COMMENT '실행 메시지',
  `TAG_ID` varchar(100) DEFAULT NULL COMMENT '타겟데이터 id',
  `CREATOR` varchar(20) NOT NULL,
  `SEQ_VALUE` varchar(100) NOT NULL,
  PRIMARY KEY (`SRC_ID`,`SEQ_VALUE`)
) ;