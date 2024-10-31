package org.gsstation.novin.core.participants;

import org.gsstation.novin.core.common.Constants;
import org.gsstation.novin.core.common.ResponseCode;
import org.gsstation.novin.core.exception.GeneralDatabaseException;
import org.gsstation.novin.core.exception.InvalidMacException;
import org.gsstation.novin.core.exception.TransactionProcessingTimeoutException;
import org.gsstation.novin.core.logging.GsLogger;
import org.gsstation.novin.util.configuration.EasyConfiguration;
import org.jdom2.Element;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.XmlConfigurable;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;
import org.jpos.transaction.Context;
import org.jpos.transaction.TransactionParticipant;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;

import static org.gsstation.novin.core.common.Constants.RESPONSE_CODE_KEY;
import static org.gsstation.novin.core.common.GsResponseCode.*;
import static org.gsstation.novin.core.common.ProtocolRulesBase.*;
import static org.gsstation.novin.core.exception.TransactionProcessingTimeoutException.TRANSACTION_PROCESSING_TIMEOUT_MESSAGE;

/**
 * Created by A_Tofigh at 07/18/2024
 */
public abstract class BaseParticipant
        implements TransactionParticipant, Configurable, XmlConfigurable {

    private static final String THIS_CLASS_NAME = "base-participant";
    protected static final String PARTICIPANT_CHAIN_EXECUTION_STATE =
            "participant-chain-execution-state";
    protected static final String PARTICIPANT_START_TIMESTAMP =
            "participant-start-timestamp";
    protected static final String PARTICIPANT_END_TIMESTAMP =
            "participant-end-timestamp";

    protected Space interconnectSpace;
    protected Configuration configuration;
    protected EasyConfiguration easyConfig;
    protected int responseTimeout;
    protected int inSpaceTimeout;
    /**
     * NB The instance variables below are not thread-safe regarding jPOS
     * participants design (singleton), so protect them against concurrency
     * in case anyone is not itself thread-safe (like the case for jPOS context)
     */
    protected static final ThreadLocal<Context>
            threadOwnTransactionContext = new ThreadLocal<>();

    public abstract void doCommit(Context context) throws Exception;

    @Override
    public int prepare(long id, Serializable context) {
        try {
            // TODO get space-uri from config and use it to create space
            interconnectSpace = SpaceFactory.getSpace();

                responseTimeout = DEFAULT_IN_SPACE_TIMEOUT * 1000;
                inSpaceTimeout = DEFAULT_IN_SPACE_TIMEOUT * 1000;

            return PREPARED;
        } catch (Throwable e) {
            GsLogger.log(e, context, THIS_CLASS_NAME);
            return RETRY;
        }
    }

    @Override
    public void commit(long id, Serializable context) {
        Context theContext = null;
        try {
            theContext = (Context) context;
            theContext.put(
                    PARTICIPANT_START_TIMESTAMP, System.currentTimeMillis());
            threadOwnTransactionContext.set(theContext);
            boolean ensureExecutingParticipant = this.getClass().getAnnotation(
                    AlwaysExecutingParticipant.class) != null;
            Boolean previousResultSuccessful = (Boolean)
                    theContext.get(Constants.PREVIOUS_RESULT);
            if (previousResultSuccessful == null)
                previousResultSuccessful = false;
            // Should any participant specifically been coded to behave as such,
            // ensure it gets executed in chain
            if (ensureExecutingParticipant) {
                doCommit(theContext);
                //recordParticipantExecution();
            } else if (previousResultSuccessful) {
                checkTransactionProcessingTimeout();
                doCommit(theContext);
                //recordParticipantExecution();
            }
        } catch (Throwable e) {
                GsLogger.log(e, context, THIS_CLASS_NAME);
                if (e instanceof InvalidMacException)
                    propagateResult(SECURITY_MEASURES_VIOLATED);
                if (e instanceof GeneralDatabaseException)
                    propagateResult(DATABASE_ERROR);
            if(theContext.get(RESPONSE_CODE_KEY) == null || "".equals(theContext.get(RESPONSE_CODE_KEY)))
                propagateResult(SYSTEM_ERROR_OCCURRED);
        }
    }

    @Override
    public final void abort(long id, Serializable context) {

    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void setConfiguration(Element element) {
        this.easyConfig = new EasyConfiguration(element);
    }

    public void propagateResult(ResponseCode responseCode) {
        propagateResult(responseCode, responseCode == TRANSACTION_SUCCEEDED, null);
    }

    public void propagateResult(String key, Object result) {
        // NB null response code means success
        propagateResult(null, true, new Map.Entry[]{
                new AbstractMap.SimpleEntry<>(key, result)});
    }

    public void propagateResult(
            Object responseCode, boolean successResponse,
            Map.Entry[] resultEntries) {
        Context context = threadOwnTransactionContext.get();
        if (responseCode != null)
            context.put(RESPONSE_CODE_KEY, responseCode);
        // NB! Severely prohibited to change previous result once set false
        if ((Boolean) context.get(Constants.PREVIOUS_RESULT)) {
            if (responseCode == null || successResponse)
                context.put(Constants.PREVIOUS_RESULT, true);
            else
                context.put(Constants.PREVIOUS_RESULT, false);
        }
        if (resultEntries != null && resultEntries.length != 0)
            for (Map.Entry result : resultEntries)
                context.put(result.getKey(), result.getValue());
    }

    private void checkTransactionProcessingTimeout()
            throws TransactionProcessingTimeoutException {
        Context context = threadOwnTransactionContext.get();
        int transactionProcessingTimeout =
                DEFAULT_TRANSACTION_PROCESSING_TIMEOUT * 1000;
        /*if (generalConfiguration != null) {
            transactionProcessingTimeout = (int)
                    (generalConfiguration.getDouble(
                            TRANSACTION_PROCESSING_TIMEOUT_KEY,
                            DEFAULT_TRANSACTION_PROCESSING_TIMEOUT) * 1000);
        }*/
        long processingTimeSoFar = System.currentTimeMillis() -
                (Long) context.get(MESSAGE_ARRIVAL_TIMESTAMP);
        if (processingTimeSoFar > transactionProcessingTimeout)
            throw new TransactionProcessingTimeoutException(
                    String.format(TRANSACTION_PROCESSING_TIMEOUT_MESSAGE,
                            processingTimeSoFar, transactionProcessingTimeout));
    }
}
