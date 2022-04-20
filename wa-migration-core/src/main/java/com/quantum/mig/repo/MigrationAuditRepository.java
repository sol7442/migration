package com.quantum.mig.repo;

import com.quantum.mig.MigrationException;
import com.quantum.mig.entity.MigrationAudit;
import com.quantum.mig.entity.MigrationResult;

//record
public interface MigrationAuditRepository {
	//함수, 내용 바꿔도 됨
	//한줄 한줄에 대한 결과 - ID 기반
	
	//A.ID ->마이그 ->B.ID //잘됬다 (한줄 한줄에 대한 기록용)
	public void record(MigrationAudit data) throws MigrationException;
	//스케줄러가 한번 돌았을때의 결과 (전체결과,,?)
	//public void save(MigrationResult result);
}
