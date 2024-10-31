package org.gsstation.novin.core.dao;

import org.gsstation.novin.core.dao.domain.BaseEntityTrx;

/**
 * Created by A_Tofigh at 07/22/2024
 */
public class BaseEntityTransactionDao extends JpaBaseDao<BaseEntityTrx> {
    public BaseEntityTransactionDao(String databaseInstanceName) {
        super(BaseEntityTrx.class, databaseInstanceName);
    }
    public BaseEntityTransactionDao(Class<BaseEntityTrx> entityType, String databaseInstanceName) {
        super(entityType, databaseInstanceName);
    }


}
