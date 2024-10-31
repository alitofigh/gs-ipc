package org.gsstation.novin;


import org.gsstation.novin.server.DataReceiverServer;
import org.gsstation.novin.util.CreateGsMessage;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by A_Tofigh at 7/13/2024
 */

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        DataReceiverServer server = new DataReceiverServer();
        try {
            /*System.out.println("Starting server...");
            new Thread(() -> {
                try {
                    server.start(8589);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            System.out.println("Server started!");*/

            System.out.println(makeMainMessage());
            System.out.println(makePtInfoMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String makeMainMessage() throws Exception {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String FormatedDate = date.format(formatter);
        CreateGsMessage createGsMessage = new CreateGsMessage();
        TransactionData transactionData = new TransactionData();
        transactionData.setGsId("0040");
        transactionData.setPtId("14");
        transactionData.setProcessingCode("160000");
        transactionData.setShiftNo("2024040601");
        transactionData.setFuelTtc("414688");
        transactionData.setEpurseTtc("0");
        transactionData.setFuelTime(FormatedDate);
        transactionData.setEpurseTime(FormatedDate);
        transactionData.setFuelType("01");
        transactionData.setTransType("5");
        transactionData.setNozzleId("14");
        transactionData.setUserCardId("05befa8d55018119");
        transactionData.setFuelSamId("000000000000");
        transactionData.setTotalAmount("3.9600");
        transactionData.setN("0.0000");
        transactionData.setFuelStatus("3");
        transactionData.setX("0.0000");
        transactionData.setX1("0.0000");
        transactionData.setX2("3.9600");
        transactionData.setX3("0.0000");
        transactionData.setR("0.0000");
        transactionData.setR1("0.0000");
        transactionData.setR2("1496.0400");
        transactionData.setR3("14475592.3200");
        transactionData.setPaymentSamId("004000290014");
        transactionData.setTotalCost("118800");
        transactionData.setC("0");
        transactionData.setC1("0");
        transactionData.setC2("118800");
        transactionData.setC3("0");
        transactionData.setP("15000");
        transactionData.setP1("15010");
        transactionData.setP2("30000");
        transactionData.setP3("30010");
        transactionData.setCashPayment("118800");
        transactionData.setCardPayment("0");
        transactionData.setCtc("0");
        transactionData.setTAC("0");
        transactionData.setBeforeBalance("0");
        transactionData.setAfterBalance("0");
        transactionData.setRFU("433725847");
        transactionData.setUploadFlag("0");
        transactionData.setKey(ISOUtil.hex2byte("333333333333333333333333333333333333333333333333"));
        return createGsMessage.createGsMessage(transactionData);
    }

    private static String makePtInfoMessage() throws Exception {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String FormatedDate = date.format(formatter);
        CreateGsMessage createGsMessage = new CreateGsMessage();
        TransactionData transactionData = new TransactionData();
        transactionData.setZoneId("01");
        transactionData.setAreaId("0007");
        transactionData.setCityId("0008");
        transactionData.setGsCode("0064 ");
        transactionData.setGsId("0037");
        transactionData.setContactTelephone("02284234843");
        transactionData.setTelephone1("4234843");
        transactionData.setFax("09105163911");
        transactionData.setFuelSamId("003700240001");
        transactionData.setNozzleId("01");
        transactionData.setProcessingCode("140000");
        transactionData.setKey(ISOUtil.hex2byte("11111111111111111111111111111111"));
        return createGsMessage.createGsMessage(transactionData);
    }
}
