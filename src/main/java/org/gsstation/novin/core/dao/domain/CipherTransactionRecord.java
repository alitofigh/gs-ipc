package org.gsstation.novin.core.dao.domain;

import lombok.Data;

import javax.persistence.*;
import java.sql.Blob;

/**
 * Created by A_Tofigh at 8/7/2024
 */

@Data
@Entity
@Table(name = "CIPHER_TRANSACTION_RECORD")
public class CipherTransactionRecord {
    @EmbeddedId
    private TransactionKey id;
    @Column(name = "epurse_ttc")
    private Integer epurseTtc;
    @Column(name = "fuel_sam_id")
    private String fuelSamId;
    @Column(name = "payment_sam_id")
    private String paymentSamId;
    @Column(name = "usercard_id")
    private String userCardId;
    @Lob
    @Column(name = "fuel_record_cipher")
    private Blob fuelRecordCipher;
    @Lob
    @Column(name = "payment_record_cipher")
    private Blob paymentRecordCipher;
    @Column(name = "upload_flag")
    private String uploadFlag;

}
