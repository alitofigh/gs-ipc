package org.gsstation.novin.util;

import org.gsstation.novin.TransactionData;
import org.gsstation.novin.packager.GsPackager;
import org.jpos.iso.ISOMsg;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.gsstation.novin.util.security.SecurityUtil.computeGsMessageMac;

/**
 * Created by A_Tofigh at 7/14/2024
 */

public class CreateGsMessage {

    ISOMsg message;

    public String createGsMessage(TransactionData transactionData) throws Exception {
        long currentMillis = System.currentTimeMillis();
        message = new ISOMsg();
        message.setPackager(new GsPackager());
        message.setMTI("0100");
        message.set(2, transactionData.getGsId());
        if (transactionData.getPtId() != null && !transactionData.getPtId().isEmpty())
            message.set(3, transactionData.getPtId());
        message.set(4, transactionData.getProcessingCode());
        message.set(5, ("" + currentMillis).substring(7));
        message.set(6, new SimpleDateFormat("yyMMddHHmmss").format(new Date(currentMillis)));
        if (transactionData.getZoneId() != null && !transactionData.getZoneId().isEmpty())
            message.set(7, transactionData.getZoneId());
        if (transactionData.getCityId() != null && !transactionData.getCityId().isEmpty())
            message.set(8, transactionData.getCityId());
        if (transactionData.getGsCode() != null && !transactionData.getGsCode().isEmpty())
            message.set(9, transactionData.getGsCode());
        if (transactionData.getContactTelephone() != null && !transactionData.getContactTelephone().isEmpty())
            message.set(10, transactionData.getContactTelephone());
        if (transactionData.getTelephone1() != null && !transactionData.getTelephone1().isEmpty())
            message.set(11, transactionData.getTelephone1());
        if (transactionData.getFax() != null && !transactionData.getFax().isEmpty())
            message.set(12, transactionData.getFax());
        if (transactionData.getShiftNo() != null && !transactionData.getShiftNo().isEmpty())
            message.set(13, transactionData.getShiftNo());
        if (transactionData.getDailyNo() != null && !transactionData.getDailyNo().isEmpty())
            message.set(14, transactionData.getDailyNo());
        if (transactionData.getFuelTtc() != null && !transactionData.getFuelTtc().isEmpty())
            message.set(15, transactionData.getFuelTtc());
        if (transactionData.getEpurseTtc() != null && !transactionData.getEpurseTtc().isEmpty())
            message.set(16, transactionData.getEpurseTtc());
        if (transactionData.getFuelTime() != null && !transactionData.getFuelTime().isEmpty())
            message.set(17, transactionData.getFuelTime());
        if (transactionData.getEpurseTime() != null && !transactionData.getEpurseTime().isEmpty())
            message.set(18, transactionData.getEpurseTime());
        if (transactionData.getFuelType() != null && !transactionData.getFuelType().isEmpty())
            message.set(19, transactionData.getFuelType());
        if (transactionData.getTransType() != null && !transactionData.getTransType().isEmpty())
            message.set(20, transactionData.getTransType());
        if (transactionData.getNozzleId() != null && !transactionData.getNozzleId().isEmpty())
            message.set(21, transactionData.getNozzleId());
        if (transactionData.getUserCardId() != null && !transactionData.getUserCardId().isEmpty())
            message.set(22, transactionData.getUserCardId());
        if (transactionData.getFuelSamId() != null && !transactionData.getFuelSamId().isEmpty())
            message.set(23, transactionData.getFuelSamId());
        if (transactionData.getTotalAmount() != null && !transactionData.getTotalAmount().isEmpty())
            message.set(24, transactionData.getTotalAmount());
        if (transactionData.getN() != null && !transactionData.getN().isEmpty())
            message.set(25, transactionData.getN());
        message.set(26, transactionData.getFuelStatus());
        if (transactionData.getX() != null && !transactionData.getX().isEmpty())
            message.set(27, transactionData.getX());
        if (transactionData.getX1() != null && !transactionData.getX1().isEmpty())
            message.set(28, transactionData.getX1());
        if (transactionData.getX2() != null && !transactionData.getX2().isEmpty())
            message.set(29, transactionData.getX2());
        if (transactionData.getX3() != null && !transactionData.getX3().isEmpty())
            message.set(30, transactionData.getX3());
        if (transactionData.getR() != null && !transactionData.getR().isEmpty())
            message.set(31, transactionData.getR());
        if (transactionData.getR1() != null && !transactionData.getR1().isEmpty())
            message.set(32, transactionData.getR1());
        if (transactionData.getR2() != null && !transactionData.getR2().isEmpty())
            message.set(33, transactionData.getR2());
        if (transactionData.getR3() != null && !transactionData.getR3().isEmpty())
            message.set(34, transactionData.getR3());
        if (transactionData.getFTC() != null && !transactionData.getFTC().isEmpty())
            message.set(35, transactionData.getFTC());
        if (transactionData.getPaymentSamId() != null && !transactionData.getPaymentSamId().isEmpty())
            message.set(36, transactionData.getPaymentSamId());
        if (transactionData.getTotalCost() != null && !transactionData.getTotalCost().isEmpty())
            message.set(37, transactionData.getTotalCost());
        if (transactionData.getC() != null && !transactionData.getC().isEmpty())
            message.set(38, transactionData.getC());
        if (transactionData.getC1() != null && !transactionData.getC1().isEmpty())
            message.set(40, transactionData.getC1());
        if (transactionData.getC2() != null && !transactionData.getC2().isEmpty())
            message.set(41, transactionData.getC2());
        if (transactionData.getC3() != null && !transactionData.getC3().isEmpty())
            message.set(42, transactionData.getC3());
        if (transactionData.getP() != null && !transactionData.getP().isEmpty())
            message.set(43, transactionData.getP());
        if (transactionData.getP1() != null && !transactionData.getP1().isEmpty())
            message.set(44, transactionData.getP1());
        if (transactionData.getP2() != null && !transactionData.getP2().isEmpty())
            message.set(45, transactionData.getP2());
        if (transactionData.getP3() != null && !transactionData.getP3().isEmpty())
            message.set(46, transactionData.getP3());
        if (transactionData.getCashPayment() != null && !transactionData.getCashPayment().isEmpty())
            message.set(47, transactionData.getCashPayment());
        if (transactionData.getCardPayment() != null && !transactionData.getCardPayment().isEmpty())
            message.set(48, transactionData.getCardPayment());
        if (transactionData.getCtc() != null && !transactionData.getCtc().isEmpty())
            message.set(49, transactionData.getCtc());
        if (transactionData.getTAC() != null && !transactionData.getTAC().isEmpty())
            message.set(50, transactionData.getTAC());
        if (transactionData.getBeforeBalance() != null && !transactionData.getBeforeBalance().isEmpty())
            message.set(51, transactionData.getBeforeBalance());
        if (transactionData.getAfterBalance() != null && !transactionData.getAfterBalance().isEmpty())
            message.set(52, transactionData.getAfterBalance());
        if (transactionData.getRFU() != null && !transactionData.getRFU().isEmpty())
            message.set(53, transactionData.getRFU());
        if (transactionData.getUploadFlag() != null && !transactionData.getUploadFlag().isEmpty())
            message.set(54, transactionData.getUploadFlag());
        message.set(64, computeGsMessageMac(message, transactionData.getKey()));
        return new String(message.pack());
    }
}
