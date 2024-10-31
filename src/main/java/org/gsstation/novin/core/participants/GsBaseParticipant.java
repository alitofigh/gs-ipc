package org.gsstation.novin.core.participants;


import lombok.SneakyThrows;
import org.gsstation.novin.core.dao.domain.*;
import org.gsstation.novin.core.module.KeyManagement;
import org.jpos.iso.ISOMsg;
import org.jpos.util.NameRegistrar;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.gsstation.novin.core.dao.domain.EntityTypes.*;

/**
 * Created by A_Tofigh at 08/01/2024
 */
public abstract class GsBaseParticipant extends BaseParticipant {

    KeyManagement keyManagement;

    @SneakyThrows
    protected BaseEntityTrx convertIsoToBaseEntityTrx(ISOMsg isoMsg, EntityTypes entityType) {
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BaseEntityTrx entity = null;
        if (entityType == DAILY_TRANSACTION_RECORD) {
            entity = new DailyTransactionRecord();
            ((DailyTransactionRecord) entity).setGsId(isoMsg.getString(2));
            ((DailyTransactionRecord) entity).setPtId(isoMsg.getString(3));
            entity.setShiftNo(isoMsg.getString(13));
            entity.setDailyNo(isoMsg.getString(14));
            ((DailyTransactionRecord) entity).setFuelTtc(Integer.parseInt(isoMsg.getString(15)));
            entity.setEpurseTtc(Integer.parseInt(isoMsg.getString(16)));
            entity.setFuelTime(sourceFormat.parse(isoMsg.getString(17)));
            entity.setEpurseTime(sourceFormat.parse(isoMsg.getString(18)));
            entity.setFuelType(isoMsg.getString(19));
            entity.setTransType(isoMsg.getString(20));
            entity.setNozzleId(isoMsg.getString(21));
            entity.setUserCardId(isoMsg.getString(22));
            entity.setFuelSamId(isoMsg.getString(23));
            entity.setTotalAmount(Double.parseDouble(isoMsg.getString(24)));
            entity.setN(Double.parseDouble(isoMsg.getString(25)));
            entity.setFuelStatus(isoMsg.getString(26));
            entity.setX(Double.parseDouble(isoMsg.getString(27)));
            entity.setX1(Double.parseDouble(isoMsg.getString(28)));
            entity.setX2(Double.parseDouble(isoMsg.getString(29)));
            entity.setX3(Double.parseDouble(isoMsg.getString(30)));
            entity.setR(Double.parseDouble(isoMsg.getString(31)));
            entity.setR1(Double.parseDouble(isoMsg.getString(32)));
            entity.setR2(Double.parseDouble(isoMsg.getString(33)));
            entity.setR3(Double.parseDouble(isoMsg.getString(34)));
            entity.setPaymentSamId(isoMsg.getString(36));
            entity.setTotalCost(Integer.parseInt(isoMsg.getString(37)));
            entity.setC(Integer.parseInt(isoMsg.getString(38)));
            entity.setC1(Integer.parseInt(isoMsg.getString(40)));
            entity.setC2(Integer.parseInt(isoMsg.getString(41)));
            entity.setC3(Integer.parseInt(isoMsg.getString(42)));
            entity.setP(Integer.parseInt(isoMsg.getString(43)));
            entity.setP1(Integer.parseInt(isoMsg.getString(44)));
            entity.setP2(Integer.parseInt(isoMsg.getString(45)));
            entity.setP3(Integer.parseInt(isoMsg.getString(46)));
            entity.setCashPayment(Integer.parseInt(isoMsg.getString(47)));
            entity.setCardPayment(Integer.parseInt(isoMsg.getString(48)));
            entity.setCtc(Integer.parseInt(isoMsg.getString(49)));
            entity.setTAC(Integer.parseInt(isoMsg.getString(50)));
            entity.setBeforeBalance(Integer.parseInt(isoMsg.getString(51)));
            entity.setAfterBalance(Integer.parseInt(isoMsg.getString(52)));
            entity.setRFU(Integer.parseInt(isoMsg.getString(53)));
        } else if (entityType == TRANSACTION_RECORD) {
            entity = new TransactionRecord();
            TransactionKey transactionKey = new TransactionKey(
                    isoMsg.getString(2),
                    isoMsg.getString(3),
                    Integer.parseInt(isoMsg.getString(15)));
            ((TransactionRecord) entity).setId(transactionKey);
            entity.setShiftNo(isoMsg.getString(13));
            entity.setDailyNo(isoMsg.getString(14));
            entity.setEpurseTtc(Integer.parseInt(isoMsg.getString(16)));
            entity.setFuelTime(sourceFormat.parse(isoMsg.getString(17)));
            entity.setEpurseTime(sourceFormat.parse(isoMsg.getString(18)));
            entity.setFuelType(isoMsg.getString(19));
            entity.setTransType(isoMsg.getString(20));
            entity.setNozzleId(isoMsg.getString(21));
            entity.setUserCardId(isoMsg.getString(22));
            entity.setFuelSamId(isoMsg.getString(23));
            entity.setTotalAmount(Double.parseDouble(isoMsg.getString(24)));
            entity.setN(Double.parseDouble(isoMsg.getString(25)));
            entity.setFuelStatus(isoMsg.getString(26));
            entity.setX(Double.parseDouble(isoMsg.getString(27)));
            entity.setX1(Double.parseDouble(isoMsg.getString(28)));
            entity.setX2(Double.parseDouble(isoMsg.getString(29)));
            entity.setX3(Double.parseDouble(isoMsg.getString(30)));
            entity.setR(Double.parseDouble(isoMsg.getString(31)));
            entity.setR1(Double.parseDouble(isoMsg.getString(32)));
            entity.setR2(Double.parseDouble(isoMsg.getString(33)));
            entity.setR3(Double.parseDouble(isoMsg.getString(34)));
            entity.setPaymentSamId(isoMsg.getString(36));
            entity.setTotalCost(Integer.parseInt(isoMsg.getString(37)));
            entity.setC(Integer.parseInt(isoMsg.getString(38)));
            entity.setC1(Integer.parseInt(isoMsg.getString(40)));
            entity.setC2(Integer.parseInt(isoMsg.getString(41)));
            entity.setC3(Integer.parseInt(isoMsg.getString(42)));
            entity.setP(Integer.parseInt(isoMsg.getString(43)));
            entity.setP1(Integer.parseInt(isoMsg.getString(44)));
            entity.setP2(Integer.parseInt(isoMsg.getString(45)));
            entity.setP3(Integer.parseInt(isoMsg.getString(46)));
            entity.setCashPayment(Integer.parseInt(isoMsg.getString(47)));
            entity.setCardPayment(Integer.parseInt(isoMsg.getString(48)));
            entity.setCtc(Integer.parseInt(isoMsg.getString(49)));
            entity.setTAC(Integer.parseInt(isoMsg.getString(50)));
            entity.setBeforeBalance(Integer.parseInt(isoMsg.getString(51)));
            entity.setAfterBalance(Integer.parseInt(isoMsg.getString(52)));
            entity.setRFU(Integer.parseInt(isoMsg.getString(53)));
        } else {
            entity = new ShiftTransactionRecord();
            TransactionKey transactionKey = new TransactionKey(
                    isoMsg.getString(2),
                    isoMsg.getString(3),
                    Integer.parseInt(isoMsg.getString(15)));
            ((ShiftTransactionRecord) entity).setId(transactionKey);
            entity.setShiftNo(isoMsg.getString(13));
            entity.setDailyNo(isoMsg.getString(14));
            entity.setEpurseTtc(Integer.parseInt(isoMsg.getString(16)));
            entity.setFuelTime(sourceFormat.parse(isoMsg.getString(17)));
            entity.setEpurseTime(sourceFormat.parse(isoMsg.getString(18)));
            entity.setFuelType(isoMsg.getString(19));
            entity.setTransType(isoMsg.getString(20));
            entity.setNozzleId(isoMsg.getString(21));
            entity.setUserCardId(isoMsg.getString(22));
            entity.setFuelSamId(isoMsg.getString(23));
            entity.setTotalAmount(Double.parseDouble(isoMsg.getString(24)));
            entity.setN(Double.parseDouble(isoMsg.getString(25)));
            entity.setFuelStatus(isoMsg.getString(26));
            entity.setX(Double.parseDouble(isoMsg.getString(27)));
            entity.setX1(Double.parseDouble(isoMsg.getString(28)));
            entity.setX2(Double.parseDouble(isoMsg.getString(29)));
            entity.setX3(Double.parseDouble(isoMsg.getString(30)));
            entity.setR(Double.parseDouble(isoMsg.getString(31)));
            entity.setR1(Double.parseDouble(isoMsg.getString(32)));
            entity.setR2(Double.parseDouble(isoMsg.getString(33)));
            entity.setR3(Double.parseDouble(isoMsg.getString(34)));
            entity.setPaymentSamId(isoMsg.getString(36));
            entity.setTotalCost(Integer.parseInt(isoMsg.getString(37)));
            entity.setC(Integer.parseInt(isoMsg.getString(38)));
            entity.setC1(Integer.parseInt(isoMsg.getString(40)));
            entity.setC2(Integer.parseInt(isoMsg.getString(41)));
            entity.setC3(Integer.parseInt(isoMsg.getString(42)));
            entity.setP(Integer.parseInt(isoMsg.getString(43)));
            entity.setP1(Integer.parseInt(isoMsg.getString(44)));
            entity.setP2(Integer.parseInt(isoMsg.getString(45)));
            entity.setP3(Integer.parseInt(isoMsg.getString(46)));
            entity.setCashPayment(Integer.parseInt(isoMsg.getString(47)));
            entity.setCardPayment(Integer.parseInt(isoMsg.getString(48)));
            entity.setCtc(Integer.parseInt(isoMsg.getString(49)));
            entity.setTAC(Integer.parseInt(isoMsg.getString(50)));
            entity.setBeforeBalance(Integer.parseInt(isoMsg.getString(51)));
            entity.setAfterBalance(Integer.parseInt(isoMsg.getString(52)));
            entity.setRFU(Integer.parseInt(isoMsg.getString(53)));
        }
        return entity;
    }

    protected BaseEntity convertIsoToBaseEntity(ISOMsg isoMsg, EntityTypes entityType) {
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BaseEntity entity = null;
        if (entityType == EMERGENCY_REPORT) {
            entity = new EmergencyReport();
            entity.setGsId(isoMsg.getString(2));
            ((EmergencyReport)entity).setUploadFlag(isoMsg.getString(54));
            entity.setSerialId(isoMsg.getString(57));
            String additionalData = isoMsg.getString(58);
            if (additionalData != null && !additionalData.isEmpty()) {
                String[] split = additionalData.split(";");
                ((EmergencyReport)entity).setEmergencyKind(split[0]);
                ((EmergencyReport)entity).setHappenTime(split[1]);
                ((EmergencyReport)entity).setDescription(split[2]);
            }
        } else if (entityType == FUEL_QUALITY_TEST_FROM_PT) {
            entity = new FuelQualityTestFromPt();
            entity.setGsId(isoMsg.getString(2));
            ((FuelQualityTestFromPt) entity).setPtId(isoMsg.getString(3));
            ((FuelQualityTestFromPt) entity).setTestTime(new Date());
            ((FuelQualityTestFromPt) entity).setFuelType(isoMsg.getString(19));
            ((FuelQualityTestFromPt) entity).setTestAmount(Double.parseDouble("20.0000"));
            ((FuelQualityTestFromPt) entity).setTestResult(" ");
            ((FuelQualityTestFromPt) entity).setUploadFlag(isoMsg.getString(54));
            entity.setSerialId(isoMsg.getString(57));
        } else if (entityType == FUEL_QUANTITY_TEST_FROM_PT) {
            entity = new FuelQuantityTestFromPt();
            entity.setGsId(isoMsg.getString(2));
            ((FuelQuantityTestFromPt) entity).setPtId(isoMsg.getString(3));
            ((FuelQuantityTestFromPt) entity).setFuelType(isoMsg.getString(19));
            ((FuelQuantityTestFromPt) entity).setTestMan("operator");
            ((FuelQuantityTestFromPt) entity).setTestAmount(Double.parseDouble("20.0000"));
            ((FuelQuantityTestFromPt) entity).setDeferenceAmount(Double.parseDouble("-19.7500"));
            ((FuelQuantityTestFromPt) entity).setTestTime(new Date());
            ((FuelQuantityTestFromPt) entity).setUploadFlag(isoMsg.getString(54));
            entity.setSerialId(isoMsg.getString(57));
        } else {
            entity = new TransactionReceiveLog();
            entity.setGsId(isoMsg.getString(2));
            ((TransactionReceiveLog)entity).setPtId(isoMsg.getString(3));
            ((TransactionReceiveLog) entity).setLogTime(new Date());
            ((TransactionReceiveLog) entity).setFuelTtc(15);
            ((TransactionReceiveLog) entity).setState(isoMsg.getString(26)); //TODO: should be received from pt or not?
            entity.setSerialId(isoMsg.getString(57));
        }
        return entity;
    }

    protected CipherTransactionRecord convertIsoToCipherTrx(ISOMsg isoMsg) {
        CipherTransactionRecord entity = new CipherTransactionRecord();
        TransactionKey transactionKey = new TransactionKey(
                isoMsg.getString(2),
                isoMsg.getString(3),
                Integer.parseInt(isoMsg.getString(15)));
        entity.setId(transactionKey);
        entity.setEpurseTtc(Integer.parseInt(isoMsg.getString(16)));
        entity.setUserCardId(isoMsg.getString(22));
        entity.setFuelSamId(isoMsg.getString(23));
        entity.setPaymentSamId(isoMsg.getString(36));
        entity.setFuelRecordCipher(null);
        entity.setPaymentRecordCipher(null);
        entity.setUploadFlag(isoMsg.getString(54));
        return entity;
    }

    @Override
    public int prepare(long id, Serializable serializable) {
        int parentPreparationResult = super.prepare(id, serializable);
        try {
            keyManagement = NameRegistrar.get("key-management");
        } catch (Exception ignored) {}
        if (parentPreparationResult != PREPARED)
            return parentPreparationResult;
        else
            return PREPARED;
    }
}
