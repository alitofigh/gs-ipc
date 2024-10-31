package org.gsstation.novin;

import lombok.Data;
import org.gsstation.novin.core.common.KeyedObject;
import org.gsstation.novin.packager.GsPackager;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by A_Tofigh at 7/13/2024
 */

@Data
public class TransactionData implements KeyedObject<byte[]>, Cloneable {


    private byte[] key;
    private ISOMsg isoMsg;
    private String zoneId;
    private String areaId;
    private String processingCode;
    private String cityId;
    private String gsCode;
    private String contactTelephone;
    private String telephone1;
    private String fax;
    private String gsId;
    private String ptId;
    private String shiftNo;
    private String dailyNo;
    private String fuelTtc;
    private String epurseTtc;
    private String fuelTime;
    private String epurseTime;
    private String fuelType;
    private String transType;
    private String nozzleId;
    private String userCardId;
    private String fuelSamId;
    private String totalAmount;
    private String n;
    private String fuelStatus;
    private String x;
    private String x1;
    private String x2;
    private String x3;
    private String r;
    private String r1;
    private String r2;
    private String r3;
    private String FTC;
    private String paymentSamId;
    private String totalCost;
    private String c;
    private String c1;
    private String c2;
    private String c3;
    private String p;
    private String p1;
    private String p2;
    private String p3;
    private String cashPayment;
    private String cardPayment;
    private String ctc;
    private String TAC;
    private String beforeBalance;
    private String afterBalance;
    private String RFU;
    private String uploadFlag;
    private ISOMsg gsMessage;


    public TransactionData() {
        isoMsg = new ISOMsg();
        isoMsg.setPackager(new GsPackager());
    }

    public TransactionData(byte[] receivedStream) throws ISOException {
        gsMessage = new ISOMsg();
        gsMessage.setPackager(new GsPackager());
        gsMessage.unpack(receivedStream);
    }

    public void set(int fieldNo, String value) {
        isoMsg.set(fieldNo, value);
    }

    public void set(int fieldNo, byte[] value) {
        isoMsg.set(fieldNo, value);
    }

    public boolean hasField(int fieldNo) {
        return isoMsg.hasField(fieldNo);
    }

    @Override
    public byte[] getKey() {
        return key;
    }

    @Override
    public void setKey(byte[] key) {
        this.key = key;
    }


}
