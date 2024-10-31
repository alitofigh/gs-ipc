package org.gsstation.novin.core.participants;

import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import static org.gsstation.novin.core.common.Constants.Security_KEY;
import static org.gsstation.novin.core.common.ProtocolRulesBase.extractIsoMsg;
import static org.gsstation.novin.util.security.SecurityUtil.encryptAES;

/**
 * Created by A_Tofigh at 08/12/2024
 */
public class KeyGeneration extends GsBaseParticipant {

    @Override
    public void doCommit(Context context) throws Exception {
        ISOMsg requestMessage = extractIsoMsg(context);
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey key = keyGenerator.generateKey();
        keyManagement.setKey(requestMessage.getString(3) + requestMessage.getString(23), key.getEncoded());
        context.put(Security_KEY,  encryptAES(key.getEncoded(), keyManagement.getKey("key2")));
    }
}
