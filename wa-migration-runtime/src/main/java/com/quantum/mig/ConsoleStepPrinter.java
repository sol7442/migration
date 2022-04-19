package com.quantum.mig;


import com.quantum.mig.entity.MigrationRecord;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleStepPrinter implements PrintStepHandler{
	final int step;
	int count;
	public ConsoleStepPrinter(int step) {
		this.step = step;
	}
	@Override
	public void print(MigrationRecord record) {
		if(this.step == this.count) {
			log.debug(" -- {}","print");
			this.count = 0;
		}
		this.count++;
	}

}
