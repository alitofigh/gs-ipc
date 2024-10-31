package org.gsstation.novin.core.dao;

import org.gsstation.novin.core.dao.domain.CipherTransactionRecord;

/**
 * Created by A_Tofigh at 08/07/2024
 */
public class CipherTransactionDao extends JpaBaseDao<CipherTransactionRecord> {
    public CipherTransactionDao(String databaseInstanceName) {
        super(CipherTransactionRecord.class, databaseInstanceName);
    }
    public CipherTransactionDao(Class<CipherTransactionRecord> entityType, String databaseInstanceName) {
        super(entityType, databaseInstanceName);
    }
}
