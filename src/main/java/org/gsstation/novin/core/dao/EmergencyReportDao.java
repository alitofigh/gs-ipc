package org.gsstation.novin.core.dao;

import org.gsstation.novin.core.dao.domain.BaseEntity;

/**
 * Created by A_Tofigh at 08/07/2024
 */
public class EmergencyReportDao extends JpaBaseDao<BaseEntity> {
    public EmergencyReportDao(String databaseInstanceName) {
        super(BaseEntity.class, databaseInstanceName);
    }
    public EmergencyReportDao(Class<BaseEntity> entityType, String databaseInstanceName) {
        super(entityType, databaseInstanceName);
    }
}
