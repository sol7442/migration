package com.inzent.sh.kms.entity;

import java.util.Date;

import lombok.Data;
@Data
public class KmsFolder {
	private String MAP_ID;
	private String UP_MAP_ID;
	private String USE_AT;
	private String CREATE_USER;
	private Date CREATE_DATE;
	private Date UPDATE_DATE;
	private String DATA_NM;
	private String DATA_CN;
}
