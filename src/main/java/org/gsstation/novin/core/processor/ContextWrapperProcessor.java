package org.gsstation.novin.core.processor;

import org.gsstation.novin.core.common.Constants;
import org.gsstation.novin.core.logging.GsLogger;
import org.gsstation.novin.util.configuration.ConfigurationHelper;
import org.gsstation.novin.util.configuration.EasyConfiguration;
import org.jdom2.Element;
import org.jpos.core.XmlConfigurable;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;
import org.jpos.transaction.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.gsstation.novin.core.common.ProtocolRulesBase.*;

/**
 * Created by A_Tofigh at 08/01/2024
 */
public class ContextWrapperProcessor implements ISORequestListener, XmlConfigurable {
    protected Space interconnectSpace;
    protected String outputQueue;
    protected int inSpaceTimeout;
    protected String originalMessageKey;
    protected Map<String, String> additionalContextData = new HashMap<>();

    @Override
    public boolean process(ISOSource source, ISOMsg receivedMessage) {
        receivedMessage.setSource(source);
        Context context = new Context();
        context.put(MESSAGE_ARRIVAL_TIMESTAMP, System.currentTimeMillis());
        context.put(REMOTE_ENDPOINT, source);
        // TODO conceive a way to get listener module (QServer) and set it here
        context.put(LISTENER_MODULE, null);
        // To preserve compatibility with old code, do this extra step
        context.put(ORIGINAL_MESSAGE_KEY, originalMessageKey);
        context.put(originalMessageKey, receivedMessage);
        context.put(Constants.PREVIOUS_RESULT, true);
        context.put("transaction-source-name", "socket");
        for (Map.Entry<String, String> additionalData
                : additionalContextData.entrySet())
            context.put(additionalData.getKey(), additionalData.getValue());
        if (outputQueue != null) {
            String[] outputQueueList =
                    ConfigurationHelper.getStringArray(outputQueue);
            for (String outputQueue : outputQueueList)
                interconnectSpace.out(outputQueue, context, inSpaceTimeout);
        }
        return true;
    }

    @Override
    public void setConfiguration(Element configuration) {
        try {
            updateConfiguration(configuration);
        } catch (Exception e) {
            GsLogger.log(e, this.getClass());
        }
    }

    @SuppressWarnings("unchecked")
    protected void updateConfiguration(Element configuration)
            throws Exception {
        EasyConfiguration easyConfiguration =
                new EasyConfiguration(configuration);
        String spaceUri = easyConfiguration.getAttributeValue(
                SPACE_URI, DEFAULT_SPACE_URI);
        interconnectSpace = SpaceFactory.getSpace(spaceUri);
        outputQueue = easyConfiguration.getAttributeValue(OUTPUT_QUEUE);
        originalMessageKey = easyConfiguration.getAttributeValue(
                ORIGINAL_MESSAGE_KEY, DEFAULT_ORIGINAL_MESSAGE_KEY);
        inSpaceTimeout = (int) (Double.parseDouble(
                easyConfiguration.getAttributeValue(IN_SPACE_TIMEOUT,
                        "" + DEFAULT_IN_SPACE_TIMEOUT)) * 1000);
        Element additionalDataElement =
                configuration.getChild(CONTEXT_ADDITIONAL_DATA);
        if (additionalDataElement != null) {
            List<Element> contextProperties =
                    additionalDataElement.getChildren("property");
            if (contextProperties != null) {
                for (Element dataElement : contextProperties) {
                    String attributeName =
                            dataElement.getAttributeValue("name");
                    if (attributeName != null)
                        additionalContextData.put(attributeName,
                                dataElement.getAttributeValue("value"));
                }
            }
        }
    }
}
