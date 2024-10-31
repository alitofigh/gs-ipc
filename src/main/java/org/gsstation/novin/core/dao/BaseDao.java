package org.gsstation.novin.core.dao;

import lombok.Data;

/**
 * Created by A_Tofigh at 07/22/2024
 */

@Data
public abstract class BaseDao<T> {
    protected String databaseInstanceName;

    public BaseDao(String databaseInstanceName) {
        if (databaseInstanceName == null)
            databaseInstanceName = "";
        this.databaseInstanceName = databaseInstanceName;
    }

    public BaseDao() {}
}
