package org.gsstation.novin.core.participants;

import org.gsstation.novin.core.exception.InvalidMacException;
import org.gsstation.novin.util.security.SecurityUtil;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.transaction.Context;

import java.util.Arrays;

import static org.gsstation.novin.core.common.Constants.PREVIOUS_RESULT;
import static org.gsstation.novin.core.common.GsResponseCode.SECURITY_MEASURES_VIOLATED;
import static org.gsstation.novin.core.common.ProtocolRulesBase.TARGET_TRANSACTION_GROUP_NAME;
import static org.gsstation.novin.core.common.ProtocolRulesBase.extractIsoMsg;

/**
 * Created by A_Tofigh at 08/03/2024
 */
public class MacValidation extends GsBaseParticipant {

    @Override
    public void doCommit(Context context) throws Exception {
        String targetTransactionGroup = context.get(TARGET_TRANSACTION_GROUP_NAME);
        ISOMsg requestMessage = extractIsoMsg(context);
        byte[] mac = requestMessage.getBytes(64);
        byte[] computedMacBytes;
        if ("gs-info".equals(targetTransactionGroup)) {
            computedMacBytes =
                    SecurityUtil.computeGsMessageMac(requestMessage, keyManagement.getKey("key1"));
        } else {
            byte[] exchangedKey = keyManagement.getKey(requestMessage.getString(3)
                    + requestMessage.getString(23));
            if (exchangedKey == null) {
                throw new InvalidMacException("This pt terminal has not done key exchange.");
            }
            computedMacBytes =
                    SecurityUtil.computeGsMessageMac(requestMessage,
                            keyManagement.getKey(requestMessage.getString(3)
                                    + requestMessage.getString(23)));
        }
        if (!Arrays.equals(computedMacBytes, mac)) {
            propagateResult(SECURITY_MEASURES_VIOLATED);
            throw new InvalidMacException();
        }


    }
}
