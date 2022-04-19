package com.quantum.mig.repo;

import com.quantum.mig.entity.MigrationRecord;
import com.quantum.mig.entity.MigrationResult;

public interface MigrationResultRepository {
	//함수, 내용 바꿔도 됨
	//한줄 한줄에 대한 결과 - ID 기반
	//A.ID ->마이그 ->B.ID //잘됬다
	public void record(MigrationRecord data);
	//스케줄러가 한번 돌았을때의 결과
	public void save(MigrationResult result);
}
