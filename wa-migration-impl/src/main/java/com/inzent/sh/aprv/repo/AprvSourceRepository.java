package com.inzent.sh.aprv.repo;

import java.util.List;
import java.util.Map;

import com.inzent.sh.aprv.entity.AprvFile;
import com.inzent.sh.aprv.entity.AprvPerson;
import com.inzent.sh.print.entity.PrintFile;
import com.inzent.sh.print.entity.PrintFolder;
import com.quantum.mig.MigrationException;
import com.quantum.mig.entity.DefaultMigIdentity;
import com.quantum.mig.repo.MigrationSourceRepository;

public interface AprvSourceRepository extends MigrationSourceRepository{
//	public int folderCount() throws MigrationException;	//폴더 전체 수
//	public int fileCount() throws MigrationException;	//파일 전체 수
//	public PrintFolder readFolder(DefaultMigIdentity id) throws MigrationException;
//	public PrintFile readFile(DefaultMigIdentity id) throws MigrationException;
//	
	public List<AprvPerson> searchPersonsFromPrgrHist(AprvFile file) throws MigrationException;	//결재 관련자 조회
	public List<AprvPerson> searchPersonsFromHist(AprvFile file) throws MigrationException;	//결재 관련자 조회
	public List<AprvFile> searchFiles(Map<String,Object> params) throws MigrationException;
	
}
