package org.gsstation.novin.core.dao;

import org.gsstation.novin.core.dao.domain.ShiftTransactionRecord;

/**
 * Created by A_Tofigh at 08/07/2024
 */
public class ShiftTransactionDao extends JpaBaseDao<ShiftTransactionRecord> {
    public ShiftTransactionDao(String databaseInstanceName) {
        super(ShiftTransactionRecord.class, databaseInstanceName);
    }

    public ShiftTransactionDao(Class<ShiftTransactionRecord> entityType, String databaseInstanceName) {
        super(entityType, databaseInstanceName);
    }
}
