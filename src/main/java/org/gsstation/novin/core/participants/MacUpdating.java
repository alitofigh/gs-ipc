package org.gsstation.novin.core.participants;

import org.gsstation.novin.util.security.SecurityUtil;
import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;

import static org.gsstation.novin.core.common.Constants.RESPONSE_MESSAGE_KEY;
import static org.gsstation.novin.core.common.ProtocolRulesBase.extractIsoMsg;

/**
 * Created by A_Tofigh at 08/07/2024
 */
public class MacUpdating extends GsBaseParticipant {

    @Override
    public void doCommit(Context context) throws Exception {
        ISOMsg isoMessage = context.get(RESPONSE_MESSAGE_KEY);
        byte[] computedMacBytes =
                SecurityUtil.computeGsMessageMac(isoMessage, keyManagement.getKey("key2"));
        isoMessage.set(64, computedMacBytes);
    }
}
