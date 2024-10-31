package org.gsstation.novin.core.module;

import org.gsstation.novin.core.common.Constants;
import org.gsstation.novin.core.logging.GsLogger;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;
import org.jpos.q2.iso.QMUX;
import org.jpos.util.LogEvent;
import org.jpos.util.LogSource;
import org.jpos.util.Logger;
import org.jpos.util.NameRegistrar;

import java.io.IOException;

/**
 * Created by A_Tofigh at 07/17/2024
 */
public class ListenerTwoMux implements ISORequestListener, Configurable {
    public static final String FINANCIAL_MUX_CONFIG_KEY = "financial-mux";
    public static final String CLIENT_TIMEOUT_CONFIG_KEY = "timeout";

    private QMUX financialMux;
    private long timeout;
    private boolean debug;

    public ListenerTwoMux() throws Exception {
        super();
    }

    @Override
    public boolean process(ISOSource source, ISOMsg msg) {
        /**
         * in order to get channel properties (like ip/port) we must cast the source as below
         */
        //BaseChannel asciiChannel = (BaseChannel) source;
        try {
            ISOMsg resp;
            if (debug) {
                LogEvent event =
                        new LogEvent((LogSource) source, "Message Received");
                //event.addMessage(ISOUtil.hexdump(msg.getOrginalData()));
                Logger.log(event);
            }
            resp = financialMux.request(msg, timeout);
            if (resp != null) {
                source.send(resp);
            } else {
                LogEvent event =
                        new LogEvent((LogSource) source, "Message Sent");
                event.addMessage("Null Response");
                Logger.log(event);
            }
            return true;
        } catch (ISOException e) {
            GsLogger.log(e, this.getClass());
            return false;
        } catch (IOException e) {
            GsLogger.log(e, this.getClass());
            return false;
        }
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        debug = cfg.getBoolean("debug", false);
        // since there are deployments which depend on "financialMuxKey", we change the code as below to have also "financialMux"
        String financialMuxKey =
                cfg.get(FINANCIAL_MUX_CONFIG_KEY, Constants.TERMINAL_MUX);
        if (financialMuxKey == null || financialMuxKey.isEmpty()
                || financialMuxKey.equals(Constants.TERMINAL_MUX))
            financialMuxKey =
                    cfg.get("financialMuxKey", Constants.TERMINAL_MUX);
        String muxTimeoutKey = cfg.get(CLIENT_TIMEOUT_CONFIG_KEY, "muxTimeout");
        if (financialMuxKey == null || financialMuxKey.isEmpty()
                || financialMuxKey.equals("muxTimeout"))
            muxTimeoutKey = "muxTimeout";
        try {
            financialMux = (QMUX) QMUX.getMUX(financialMuxKey);
            timeout = (long) (cfg.getDouble(muxTimeoutKey, 30) * 1000);
        } catch (NameRegistrar.NotFoundException e) {
            GsLogger.log(e, this.getClass());
        }
    }
}
