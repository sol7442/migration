package com.inzent.sh;


import com.quantum.mig.PrintStepHandler;
import com.quantum.mig.entity.MigrationAudit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleStepPrinter implements PrintStepHandler{
	int step;
	int total;
	int count;
	public ConsoleStepPrinter(int total , int step) {
		this.total = total;
		this.step = step;
	}
	@Override
	public void print(MigrationAudit record) {
		//out.count 마다 상태 하나씩 찍어줄 필요있음.  
		//total , 성공, 실패, 단위 : step
		//count 초기화 필요 
		// 자세한 거는 나중에, 
//		for(int count=0; count <= total ; step++) {
//			log.debug(" -- {}","print");
//			count++;
//		}
		
		//step 단위로 log 찍음
		for(int i=count; i<=total; i+=step) {
			log.debug("progress logs -- {} {} {} ",record.getSrcId(), record.getTagId() , record.getMsg());
//			log.debug("progress logs -- num: {} / count : {} / result : {} " , i , count , "create");
			count++;
		}
		count=0;
		
		
//		if(this.total == this.count) {
//			log.debug(" -- {}","print");
//			this.count = 0;
//		}
//		this.count++;
	}

}
