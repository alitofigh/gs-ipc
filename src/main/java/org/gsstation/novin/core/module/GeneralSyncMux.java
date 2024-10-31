package org.gsstation.novin.core.module;

import org.gsstation.novin.core.common.KeyedObject;
import org.gsstation.novin.core.exception.GsException;
import org.jdom2.Element;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.MUX;
import org.jpos.q2.QBeanSupport;
import org.jpos.q2.QFactory;
import org.jpos.space.LocalSpace;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;
import org.jpos.space.SpaceListener;
import org.jpos.transaction.Context;
import org.jpos.util.Loggeable;
import org.jpos.util.NameRegistrar;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import static org.gsstation.novin.core.common.Constants.TRANSACTION_ID;

/**
 * Created by A_Tofigh at 08/01/2024
 */
public class GeneralSyncMux <T, U> extends QBeanSupport
        implements SpaceListener, Loggeable {
    private LocalSpace sp;
    private String in, out, unhandled;
    private String[] ready;
    private List listeners;
    private int rx, tx, rxExpired, txExpired,
            rxPending, rxUnhandled, rxForwarded;
    private long lastTxn = 0L;

    public GeneralSyncMux() {
        super();
        listeners = new ArrayList();
    }

    /**
     * @return MUX with name using NameRegistrar
     * @throws NameRegistrar.NotFoundException
     * @see NameRegistrar
     */
    public static MUX getMUX(String name)
            throws NameRegistrar.NotFoundException {
        return (MUX) NameRegistrar.get("mux." + name);
    }

    protected void initService() throws ConfigurationException {
        Element e = getPersist();
        sp = grabSpace(e.getChild("space"));
        in = e.getChildTextTrim("in");
        out = e.getChildTextTrim("out");
        ready = toStringArray(e.getChildTextTrim("ready"));
        addListeners();
        unhandled = e.getChildTextTrim("unhandled");
        sp.addListener(in, this);
        NameRegistrar.register("mux." + getName(), this);
    }

    protected void startService() {
        if (getState() == STOPPED) {
            sp.addListener(in, this);
            NameRegistrar.register("mux." + getName(), this);
        }
    }

    protected void stopService() {
        NameRegistrar.unregister("mux." + getName());
        sp.removeListener(in, this);
    }

    public T request(KeyedObject<U> keyedObject, long timeout)
            throws Exception {
        String key = getKey(keyedObject);
        String req = key + ".req";
        sp.out(req, keyedObject);
        if (timeout > 0)
            sp.out(out, keyedObject, timeout);
        else
            sp.out(out, keyedObject);
        Object resp = null;
        try {
            synchronized (this) {
                tx++;
                rxPending++;
            }
            for (; ; ) {
                resp = sp.rd(key, timeout);
                sp.inp(key);
                break;
            }
            if (resp == null && sp.inp(req) == null) {
                // possible race condition, retry for a few extra seconds
                resp = sp.in(key, 10000);
            }
            synchronized (this) {
                if (resp != null) {
                    rx++;
                    lastTxn = System.currentTimeMillis();
                } else {
                    rxExpired++;
                }
            }
        } finally {
            synchronized (this) {
                rxPending--;
            }
        }
        //noinspection unchecked
        return (T) resp;
    }

    public Object request(Context context, long timeout)
            throws Exception {
        String key = getKey(context);
        String req = key + ".req";
        sp.out(req, context);
        if (timeout > 0)
            sp.out(out, context, timeout);
        else
            sp.out(out, context);
        Object resp = null;
        try {
            synchronized (this) {
                tx++;
                rxPending++;
            }
            for (; ; ) {
                resp = sp.rd(key, timeout);
                sp.inp(key);
                break;
            }
            if (resp == null && sp.inp(req) == null) {
                // possible race condition, retry for a few extra seconds
                resp = sp.in(key, 10000);
            }
            synchronized (this) {
                if (resp != null) {
                    rx++;
                    lastTxn = System.currentTimeMillis();
                } else {
                    rxExpired++;
                }
            }
        } finally {
            synchronized (this) {
                rxPending--;
            }
        }
        return resp;
    }

    public void notify(Object k, Object value) {
        Object obj = sp.inp(k);
        try {
            String key;
            if (obj instanceof KeyedObject)
                key = getKey((KeyedObject) obj);
            else
                throw new GsException(
                        "GeneralSyncMux notified with an unexpected type: "
                                + obj);
            String req = key + ".req";
            Object r = sp.inp(req);
            if (r != null) {
                sp.out(key, obj);
            }
        } catch (Exception e) {
            getLog().warn("notify", e);
        }
    }

    public String getKey(KeyedObject<?> keyedObject) throws Exception {
        StringBuilder sb = new StringBuilder(out);
        sb.append('.');
        Object key = keyedObject.getKey();
        sb.append(key != null ? key.toString() : "");
        return sb.toString();
    }

    public String getKey(Context context) throws Exception {
        StringBuilder sb = new StringBuilder(out);
        sb.append('.');
        String objectKey = null;
        if (context.get(TRANSACTION_ID) != null)
            objectKey = context.get(TRANSACTION_ID).toString();
        if (objectKey == null || objectKey.isEmpty())
            throw new GsException(
                    "Context should contain a unique key to identify "
                            + "transaction instance but nothing found!");
        sb.append(objectKey);
        return sb.toString();
    }

    /**
     * @jmx:managed-attribute description="input queue"
     */
    public String getInQueue() {
        return in;
    }

    /**
     * @jmx:managed-attribute description="input queue"
     */
    public synchronized void setInQueue(String in) {
        this.in = in;
        getPersist().getChild("in").setText(in);
        setModified(true);
    }

    /**
     * @jmx:managed-attribute description="output queue"
     */
    public String getOutQueue() {
        return out;
    }

    /**
     * @jmx:managed-attribute description="output queue"
     */
    public synchronized void setOutQueue(String out) {
        this.out = out;
        getPersist().getChild("out").setText(out);
        setModified(true);
    }

    public Space getSpace() {
        return sp;
    }

    /**
     * @jmx:managed-attribute description="unhandled queue"
     */
    public String getUnhandledQueue() {
        return unhandled;

    }

    /**
     * @jmx:managed-attribute description="unhandled queue"
     */
    public synchronized void setUnhandledQueue(String unhandled) {
        this.unhandled = unhandled;
        getPersist().getChild("unhandled").setText(unhandled);
        setModified(true);
    }

    private void addListeners()
            throws ConfigurationException {
        QFactory factory = getFactory();
        Iterator iter = getPersist().getChildren(
                "request-listener"
        ).iterator();
        while (iter.hasNext()) {
            Element l = (Element) iter.next();
            ISORequestListener listener = (ISORequestListener)
                    factory.newInstance(l.getAttributeValue("class"));
            factory.setLogger(listener, l);
            factory.setConfiguration(listener, l);
            addISORequestListener(listener);
        }
    }

    public void addISORequestListener(ISORequestListener l) {
        listeners.add(l);
    }

    public boolean removeISORequestListener(ISORequestListener l) {
        return listeners.remove(l);
    }

    public synchronized void resetCounters() {
        rx = tx = rxExpired = txExpired = rxPending = rxUnhandled = rxForwarded = 0;
        lastTxn = 0l;
    }

    public String getCountersAsString() {
        StringBuffer sb = new StringBuffer();
        append(sb, "tx=", tx);
        append(sb, ", rx=", rx);
        append(sb, ", tx_expired=", txExpired);
        append(sb, ", tx_pending=", sp.size(out));
        append(sb, ", rx_expired=", rxExpired);
        append(sb, ", rx_pending=", rxPending);
        append(sb, ", rx_unhandled=", rxUnhandled);
        append(sb, ", rx_forwarded=", rxForwarded);
        sb.append(", connected=");
        sb.append(Boolean.toString(isConnected()));
        sb.append(", last=");
        sb.append(lastTxn);
        if (lastTxn > 0) {
            sb.append(", idle=");
            sb.append(System.currentTimeMillis() - lastTxn);
            sb.append("ms");
        }
        return sb.toString();
    }

    public int getTXCounter() {
        return tx;
    }

    public int getRXCounter() {
        return rx;
    }

    public long getLastTxnTimestampInMillis() {
        return lastTxn;
    }

    public long getIdleTimeInMillis() {
        return lastTxn > 0L ? System.currentTimeMillis() - lastTxn : -1L;
    }

    private LocalSpace grabSpace(Element e)
            throws ConfigurationException {
        String uri = e != null ? e.getText() : "";
        Space sp = SpaceFactory.getSpace(uri);
        if (sp instanceof LocalSpace) {
            return (LocalSpace) sp;
        }
        throw new ConfigurationException("Invalid space " + uri);
    }

    public boolean isConnected() {
        if (ready != null && ready.length > 0) {
            for (int i = 0; i < ready.length; i++)
                if (sp.rdp(ready[i]) != null)
                    return true;
            return false;
        } else
            return true;
    }

    public void dump(PrintStream p, String indent) {
        p.println(indent + getCountersAsString());
    }

    private String[] toStringArray(String s) {
        String[] ready = null;
        if (s != null && s.length() > 0) {
            StringTokenizer st = new StringTokenizer(s);
            ready = new String[st.countTokens()];
            for (int i = 0; st.hasMoreTokens(); i++)
                ready[i] = st.nextToken();
        }
        return ready;
    }

    private int[] toIntArray(String s)
            throws ConfigurationException {
        if (s == null || s.length() == 0)
            s = "41, 11";
        try {
            int[] k = null;
            StringTokenizer st = new StringTokenizer(s, ", ");
            k = new int[st.countTokens()];
            for (int i = 0; st.hasMoreTokens(); i++)
                k[i] = Integer.parseInt(st.nextToken());
            return k;
        } catch (NumberFormatException e) {
            throw new ConfigurationException(e);
        }
    }

    private void append(StringBuffer sb, String name, int value) {
        sb.append(name);
        sb.append(value);
    }
}
