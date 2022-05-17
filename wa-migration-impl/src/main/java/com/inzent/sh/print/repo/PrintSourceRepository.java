package com.inzent.sh.print.repo;

import java.util.List;
import java.util.Map;

import com.inzent.sh.print.entity.PrintFile;
import com.inzent.sh.print.entity.PrintFolder;
import com.quantum.mig.MigrationException;
import com.quantum.mig.entity.DefaultMigIdentity;
import com.quantum.mig.repo.MigrationSourceRepository;

public interface PrintSourceRepository extends MigrationSourceRepository{
	public int folderCount() throws MigrationException;	//폴더 전체 수
	public int fileCount() throws MigrationException;	//파일 전체 수
	public PrintFolder readFolder(DefaultMigIdentity id) throws MigrationException;
	public PrintFile readFile(DefaultMigIdentity id) throws MigrationException;
	
	public List<PrintFolder> searchFolders(Map<String,Object> params) throws MigrationException;
	public List<PrintFile> searchFiles(Map<String,Object> params) throws MigrationException;
	
}
