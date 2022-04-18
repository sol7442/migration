package com.quantum.mig;


import com.quantum.mig.entity.MigrationRecord;

public class ConsoleStepPrinter implements PrintStepHandler{
	final int step;
	int count;
	public ConsoleStepPrinter(int step) {
		this.step = step;
	}
	@Override
	public void print(MigrationRecord record) {
		// TODO Auto-generated method stub
		
	}

}
