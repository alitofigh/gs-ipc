package org.gsstation.novin.core.dao.domain;

/**
 * Created by A_Tofigh at 7/25/2024
 */

public enum EntityTypes {
    TRANSACTION_RECORD(TransactionRecord.class),
    DAILY_TRANSACTION_RECORD(DailyTransactionRecord.class),
    SHIFT_TRANSACTION_RECORD(DailyTransactionRecord.class),
    TRANSACTION_RECEIVE_LOG(TransactionReceiveLog.class),
    FUEL_QUALITY_TEST_FROM_PT(FuelQualityTestFromPt.class),
    FUEL_QUANTITY_TEST_FROM_PT(FuelQuantityTestFromPt.class),
    CIPHER_TRANSACTION_RECORD(CipherTransactionRecord.class),
    EMERGENCY_REPORT(EmergencyReport.class)
    ;


    Class c;

    EntityTypes (Class c) {
        this.c = c;
    }
}
