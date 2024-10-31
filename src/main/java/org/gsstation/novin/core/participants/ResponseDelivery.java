package org.gsstation.novin.core.participants;

import org.gsstation.novin.core.logging.GsLogger;
import org.jpos.core.Configuration;
import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;

import static org.gsstation.novin.core.common.Constants.RESPONSE_MESSAGE_KEY;

/**
 * Created by A_Tofigh at 07/31/2024
 */
@AlwaysExecutingParticipant
public class ResponseDelivery extends GsBaseParticipant {
    public static final String THIS_CLASS_NAME = "response-delivery";

    private Configuration configuration;
    private String outputQueue;

    @Override
    public void doCommit(Context context) {
        ISOMsg responseMessage = null;
        try {
            responseMessage = (ISOMsg) context.get(RESPONSE_MESSAGE_KEY);
        } catch (Exception e) {
            GsLogger.log(e, context, THIS_CLASS_NAME);
        } finally {
            interconnectSpace.out(outputQueue, responseMessage);
        }

    }

    @Override
    public void setConfiguration(Configuration cfg) {
        this.configuration = cfg;
        updateConfiguration();
    }

    private void updateConfiguration() {
        outputQueue =
                configuration.get("output-queue", "terminal-rx-queue");
    }
}
