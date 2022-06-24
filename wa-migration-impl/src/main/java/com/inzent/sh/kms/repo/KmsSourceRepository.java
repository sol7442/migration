package com.inzent.sh.kms.repo;

import java.util.List;
import java.util.Map;

import com.inzent.sh.kms.entity.KmsFile;
import com.inzent.sh.kms.entity.KmsFolder;
import com.quantum.mig.MigrationException;
import com.quantum.mig.repo.MigrationSourceRepository;

public interface KmsSourceRepository extends MigrationSourceRepository{
	public List<KmsFile> searchFiles(Map<String,Object> params) throws MigrationException;
	public List<KmsFolder> searchFolders(Map<String,Object> params) throws MigrationException;
	public String searchFolderIds(Map<String,Object> params) throws MigrationException;
}
