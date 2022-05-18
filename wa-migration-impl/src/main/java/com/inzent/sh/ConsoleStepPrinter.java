package com.inzent.sh;


import com.quantum.mig.PrintStepHandler;
import com.quantum.mig.entity.MigrationAudit;
import com.quantum.mig.log.LOGGER;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleStepPrinter implements PrintStepHandler{
	int step;
	int total;
	int count;
	public ConsoleStepPrinter(int step) {
		this.step = step;
	}
	@Override
	public void print(MigrationAudit record , int total , int step_count) {
		if(step_count!=0) {
			LOGGER.process.debug(" - PROGRESS LOG => count : {}  / total : {}  / step : {} ...", step_count , total , step);
		}
	}

}
