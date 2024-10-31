package org.gsstation.novin.core.participants;

import org.gsstation.novin.core.dao.JdbcDao;
import org.gsstation.novin.core.dao.domain.GsInfo;
import org.gsstation.novin.core.dao.domain.PtInfo;
import org.gsstation.novin.core.exception.GeneralDatabaseException;
import org.gsstation.novin.core.logging.GsLogger;
import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;

import static org.gsstation.novin.core.common.GsResponseCode.*;
import static org.gsstation.novin.core.common.ProtocolRulesBase.extractIsoMsg;

/**
 * Created by A_Tofigh at 07/18/2024
 */
public class MessageDataValidation extends GsBaseParticipant {
    @Override
    public void doCommit(Context context) {
        ISOMsg requestMessage = null;
        try {
            requestMessage = extractIsoMsg(context);
            if(JdbcDao.existPtInfo(
                    requestMessage.getString(2), requestMessage.getString(23), requestMessage.getString(21))) {
                GsInfo gsInfo = JdbcDao.getGsInfo();
                //TODO: should be aware what exactly should be returned to client.
                propagateResult(TRANSACTION_SUCCEEDED);
            } else {
                GsLogger.log("There is no pt with given information.");
                propagateResult(PT_NOT_FOUND);
            }
        } catch (Exception e) {
            if (e instanceof GeneralDatabaseException) {
                GsLogger.log(e.getCause().getMessage(), "message-data-validation");
                propagateResult(DATABASE_ERROR);
            } else {
                GsLogger.log(e);
                propagateResult(SYSTEM_ERROR_OCCURRED);
            }
        }
    }
}
