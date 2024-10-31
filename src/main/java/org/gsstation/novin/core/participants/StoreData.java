package org.gsstation.novin.core.participants;

import org.gsstation.novin.core.dao.BaseEntityDao;
import org.gsstation.novin.core.dao.BaseEntityTransactionDao;
import org.gsstation.novin.core.logging.GsLogger;
import org.gsstation.novin.core.logging.MainLogger;
import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;

import static org.gsstation.novin.core.common.GsResponseCode.TRANSACTION_SUCCEEDED;
import static org.gsstation.novin.core.common.ProtocolRulesBase.extractIsoMsg;
import static org.gsstation.novin.core.dao.domain.EntityTypes.*;

/**
 * Created by A_Tofigh at 07/18/2024
 */
public class StoreData extends GsBaseParticipant {

    @Override
    public void doCommit(Context context) {
        ISOMsg isoMessage = extractIsoMsg(context);
        BaseEntityTransactionDao dao = new BaseEntityTransactionDao("");
        dao.store(convertIsoToBaseEntityTrx(isoMessage, DAILY_TRANSACTION_RECORD));
        dao.store(convertIsoToBaseEntityTrx(isoMessage, SHIFT_TRANSACTION_RECORD));
        dao.store(convertIsoToBaseEntityTrx(isoMessage, TRANSACTION_RECORD));
        BaseEntityDao baseEntityDao = new BaseEntityDao("");
        baseEntityDao.store(convertIsoToBaseEntity(isoMessage, EMERGENCY_REPORT));
        baseEntityDao.store(convertIsoToBaseEntity(isoMessage, FUEL_QUALITY_TEST_FROM_PT));
        baseEntityDao.store(convertIsoToBaseEntity(isoMessage, FUEL_QUANTITY_TEST_FROM_PT));
        baseEntityDao.store(convertIsoToBaseEntity(isoMessage, TRANSACTION_RECEIVE_LOG));
        /*List<BaseEntityTrx> entities = new ArrayList<>();
        entities.add(convertIso(isoMessage, DAILY_TRANSACTION_RECORD));
        entities.add(convertIso(isoMessage, TRANSACTION_RECORD));
        entities.add(convertIso(isoMessage, SHIFT_TRANSACTION_RECORD));*/
        propagateResult(TRANSACTION_SUCCEEDED);
        MainLogger.log("data was done in tables successfully.");
    }
}
