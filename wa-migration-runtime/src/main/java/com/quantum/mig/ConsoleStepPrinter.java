package com.quantum.mig;


import com.quantum.mig.entity.MigrationRecord;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleStepPrinter implements PrintStepHandler{
	final int step;
	int count;
	public ConsoleStepPrinter(int total , int step) {
		this.step = step;
	}
	@Override
	public void print(MigrationRecord record) {
		//out.count 마다 상태 하나씩 찍어줄 필요있음.  
		//total , 성공, 실패, 단위 : step
		//count 초기화 필요 
		// 자세한 거는 나중에, 
		if(this.step == this.count) {
			log.debug(" -- {}","print");
			this.count = 0;
		}
		this.count++;
	}

}
