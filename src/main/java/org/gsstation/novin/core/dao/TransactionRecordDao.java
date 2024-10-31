package org.gsstation.novin.core.dao;

import org.gsstation.novin.core.dao.domain.BaseEntityTrx;

/**
 * Created by A_Tofigh at 08/07/2024
 */
public class TransactionRecordDao extends JpaBaseDao<BaseEntityTrx> {
    public TransactionRecordDao(String databaseInstanceName) {
        super(BaseEntityTrx.class, databaseInstanceName);
    }
    public TransactionRecordDao(Class<BaseEntityTrx> entityType, String databaseInstanceName) {
        super(entityType, databaseInstanceName);
    }
}
