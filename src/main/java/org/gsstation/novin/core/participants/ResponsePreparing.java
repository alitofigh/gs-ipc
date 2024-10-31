package org.gsstation.novin.core.participants;

import org.gsstation.novin.core.common.ResponseCode;
import org.gsstation.novin.util.security.SecurityUtil;
import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;

import static org.gsstation.novin.core.common.Constants.*;
import static org.gsstation.novin.core.common.GsResponseCode.TRANSACTION_SUCCEEDED;
import static org.gsstation.novin.core.common.ProtocolRulesBase.TARGET_TRANSACTION_GROUP_NAME;
import static org.gsstation.novin.core.common.ProtocolRulesBase.extractIsoMsg;
import static org.gsstation.novin.core.common.GsResponseCode.TRANSACTION_NOT_COMPLETED;

/**
 * Created by A_Tofigh at 08/01/2024
 */
@AlwaysExecutingParticipant
public class ResponsePreparing extends GsBaseParticipant {

    public static final int[] COMMON_RESPONSE_FIELDS =
            new int[]{0, 2, 3, 4, 5, 6, 7, 11, 12, 13, 32, 37, 41, 42, 48, 53};

    @Override
    public void doCommit(Context context) throws Exception {
        ISOMsg requestMessage = extractIsoMsg(context);
        ISOMsg responseMessage;
        ResponseCode responseCode =
                (ResponseCode) context.get(RESPONSE_CODE_KEY);
        if (responseCode == null)
            responseCode = TRANSACTION_NOT_COMPLETED;
        responseMessage = (ISOMsg) requestMessage
                .clone(COMMON_RESPONSE_FIELDS);
        responseMessage.setResponseMTI();
        byte[] computedMacBytes = null;
        if ("gs-info".equals(context.get(TARGET_TRANSACTION_GROUP_NAME))) {
            if (responseCode == TRANSACTION_SUCCEEDED) {
                responseMessage.set(63, (byte[]) context.get(Security_KEY));
                responseMessage.set(39, responseCode.code());
                computedMacBytes =
                        SecurityUtil.computeGsMessageMac(responseMessage, keyManagement.getKey("key2"));
            }
        } else {
            responseMessage.set(39, responseCode.code());
            computedMacBytes =
                    SecurityUtil.computeGsMessageMac(responseMessage,
                            keyManagement.getKey(requestMessage.getString(3)
                                    + requestMessage.getString(23)));
        }
        responseMessage.set(64, computedMacBytes);
        propagateResult(RESPONSE_MESSAGE_KEY, responseMessage);
    }
}
