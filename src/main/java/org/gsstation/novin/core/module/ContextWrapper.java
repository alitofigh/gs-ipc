package org.gsstation.novin.core.module;

import org.gsstation.novin.core.common.Constants;
import org.gsstation.novin.core.logging.GsLogger;
import org.jdom2.Element;
import org.jpos.core.Configuration;
import org.jpos.core.XmlConfigurable;
import org.jpos.iso.ISOMsg;
import org.jpos.q2.QBeanSupport;
import org.jpos.space.LocalSpace;
import org.jpos.space.SpaceFactory;
import org.jpos.space.SpaceListener;
import org.jpos.transaction.Context;
import org.jpos.util.NameRegistrar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.gsstation.novin.core.common.ProtocolRulesBase.*;

/**
 * Created by A_Tofigh at 07/18/2024
 */
public class ContextWrapper extends QBeanSupport
        implements SpaceListener, XmlConfigurable {
    private static final String THIS_CLASS_NAME = "context-wrapper";

    private LocalSpace interconnectSpace;
    private String inputQueue;
    private String outputQueue;
    private long inSpaceTimeout;
    private Element configuration;
    private Map<String, String> additionalContextData = new HashMap<>();
    private static String originalMessageKey;

    @Override
    public void notify(Object key, Object value) {
        Object inSpaceObject = interconnectSpace.inp(key);
        // Check for possible race condition as pointed to in jPOS source code
        if (inSpaceObject == null)
            return;
        Context context = new Context();
        try {
            if (inSpaceObject instanceof ISOMsg) {
                ISOMsg isoMessage = (ISOMsg) inSpaceObject;
                if (isoMessage.hasField(MESSAGE_ARRIVAL_TIMESTAMP_FIELD)) {
                    context.put(MESSAGE_ARRIVAL_TIMESTAMP,
                            isoMessage.getString(
                                    MESSAGE_ARRIVAL_TIMESTAMP_FIELD));
                    isoMessage.unset(MESSAGE_ARRIVAL_TIMESTAMP_FIELD);
                }
                if (isoMessage.hasField(LISTENER_MODULE_FIELD)) {
                    context.put(LISTENER_MODULE,
                            isoMessage.getString(LISTENER_MODULE_FIELD));
                    isoMessage.unset(LISTENER_MODULE_FIELD);
                }
                if (isoMessage.getSource() != null) {
                    context.put(REMOTE_ENDPOINT, isoMessage.getSource());
                }
            }
            if (context.get(MESSAGE_ARRIVAL_TIMESTAMP) == null)
                context.put(MESSAGE_ARRIVAL_TIMESTAMP,
                        System.currentTimeMillis());
            // To preserve compatibility with old code, do this extra step
            context.put(ORIGINAL_MESSAGE_KEY, getOriginalMessageKey());
            context.put(getOriginalMessageKey(), inSpaceObject);
            context.put(Constants.PREVIOUS_RESULT, true);
            for (Map.Entry<String, String> additionalData
                    : additionalContextData.entrySet())
                context.put(additionalData.getKey(), additionalData.getValue());
            interconnectSpace.out(outputQueue, context, inSpaceTimeout);
        } catch (Exception e) {
            GsLogger.log(e, THIS_CLASS_NAME);
        }
    }

    public static String getOriginalMessageKey() {
        // originalMessageKey still may be null because there may be no
        // ContextWrapper qbean deployed in app
        return originalMessageKey == null
                ? DEFAULT_ORIGINAL_MESSAGE_KEY : originalMessageKey;
    }

    @Override
    protected void initService() throws Exception {
        super.initService();
    }

    @Override
    protected void startService() throws Exception {
        super.startService();
        updateConfiguration();
        interconnectSpace.addListener(inputQueue, this);
        NameRegistrar.register(getName(), this);
    }

    @Override
    protected void stopService() throws Exception {
        NameRegistrar.unregister(getName());
        interconnectSpace.removeListener(inputQueue, this);
        super.stopService();
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        cfg = configuration;
    }

    @Override
    public void setConfiguration(Element configuration) {
        this.configuration = configuration;
    }

    @SuppressWarnings("unchecked")
    private void updateConfiguration() throws Exception {
        String spaceUri = cfg.get(SPACE_URI, DEFAULT_SPACE_URI);
        interconnectSpace = (LocalSpace) SpaceFactory.getSpace(spaceUri);
        inputQueue = cfg.get(INPUT_QUEUE);
        outputQueue = cfg.get(OUTPUT_QUEUE);
        inSpaceTimeout = (int) (cfg.getDouble(
                IN_SPACE_TIMEOUT, DEFAULT_IN_SPACE_TIMEOUT) * 1000);
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
        originalMessageKey = cfg.get(
                ORIGINAL_MESSAGE_KEY, DEFAULT_ORIGINAL_MESSAGE_KEY);
    }
}
