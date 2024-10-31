package org.gsstation.novin.core.dao;

import org.gsstation.novin.core.dao.domain.GsInfo;
import org.gsstation.novin.core.dao.domain.PtInfo;
import org.gsstation.novin.core.exception.GeneralDatabaseException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * Created by A_Tofigh at 08/07/2024
 */
public class JdbcDao {

    public static boolean existPtInfo(String gsId, String fuelSamId, String nozzleId) {
        EntityManager entityManager = null;
        try {
            entityManager = EntityManagerFactory.newEntityManager("", null);
            TypedQuery<PtInfo> typedQuery =
                    entityManager.createQuery(
                            "SELECT pt FROM PtInfo pt WHERE pt.gsId= :gsId  and pt.fuelSamId= :fuelSamId and pt.nozzleId= :nozzleId",
                            PtInfo.class);
            typedQuery.setParameter("gsId", gsId);
            typedQuery.setParameter("fuelSamId", fuelSamId);
            typedQuery.setParameter("nozzleId", nozzleId);

            List<PtInfo> resultList = typedQuery.getResultList();
            if (!resultList.isEmpty()) {
               return true;
            }
        } finally {
            if (entityManager != null)
                entityManager.close();
        }
        return false;
    }

    public static GsInfo getGsInfo() {
        EntityManager entityManager = null;
        try {
            entityManager = EntityManagerFactory.newEntityManager("", null);
            TypedQuery<GsInfo> typedQuery =
                    entityManager.createQuery(
                            "SELECT gs FROM GsInfo gs",
                            GsInfo.class);
            List<GsInfo> resultList = typedQuery.getResultList();
            if (!resultList.isEmpty()) {
                return resultList.get(0);
            }
        } finally {
            if (entityManager != null)
                entityManager.close();
        }
        return null;
    }
}
