package org.gsstation.novin.core.dao;

import org.gsstation.novin.core.dao.domain.BaseEntity;

/**
 * Created by A_Tofigh at 08/07/2024
 */
public class BaseEntityDao extends JpaBaseDao<BaseEntity> {
    public BaseEntityDao(String databaseInstanceName) {
        super(BaseEntity.class, databaseInstanceName);
    }
    public BaseEntityDao(Class<BaseEntity> entityType, String databaseInstanceName) {
        super(entityType, databaseInstanceName);
    }
}
