package org.gsstation.novin.core.dao.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by A_Tofigh at 7/25/2024
 */

@Data
@MappedSuperclass
public class BaseEntityTrx {
    @Column(name = "shift_no")
    private String shiftNo;
    @Column(name = "daily_no")
    private String dailyNo;
    @Column(name = "epurse_ttc")
    private Integer epurseTtc;
    @Column(name = "fuel_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fuelTime;
    @Column(name = "epurse_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date epurseTime;
    @Column(name = "fuel_type")
    private String fuelType;
    @Column(name = "trans_type")
    private String transType;
    @Column(name = "nozzle_id")
    private String nozzleId;
    @Column(name = "usercard_id")
    private String userCardId;
    @Column(name = "fuel_sam_id")
    private String fuelSamId;
    @Column(name = "total_amount", precision = 20, scale = 4)
    private Double totalAmount;
    @Column(name = "N", precision = 20, scale = 4)
    private Double n;
    @Column(name = "fuel_status")
    private String fuelStatus;
    @Column(name = "X", precision = 20, scale = 4)
    private Double x;
    @Column(name = "X1", precision = 20, scale = 4)
    private Double x1;
    @Column(name = "X2", precision = 20, scale = 4)
    private Double x2;
    @Column(name = "X3", precision = 20, scale = 4)
    private Double x3;
    @Column(name = "R", precision = 20, scale = 4)
    private Double r;
    @Column(name = "R1", precision = 20, scale = 4)
    private Double r1;
    @Column(name = "R2", precision = 20, scale = 4)
    private Double r2;
    @Column(name = "R3", precision = 20, scale = 4)
    private Double r3;
    @Column(name = "FTC")
    private Integer FTC;
    @Column(name = "payment_sam_id")
    private String paymentSamId;
    @Column(name = "total_cost")
    private Integer totalCost;
    @Column(name = "C")
    private Integer c;
    @Column(name = "C1")
    private Integer c1;
    @Column(name = "C2")
    private Integer c2;
    @Column(name = "C3")
    private Integer c3;
    @Column(name = "P")
    private Integer p;
    @Column(name = "P1")
    private Integer p1;
    @Column(name = "P2")
    private Integer p2;
    @Column(name = "P3")
    private Integer p3;
    @Column(name = "cash_payment")
    private Integer cashPayment;
    @Column(name = "card_payment")
    private Integer cardPayment;
    @Column(name = "ctc")
    private Integer ctc;
    @Column(name = "TAC")
    private Integer TAC;
    @Column(name = "before_balance")
    private Integer beforeBalance;
    @Column(name = "after_balance")
    private Integer afterBalance;
    @Column(name = "RFU")
    private Integer RFU;
    @Column(name = "upload_flag")
    private String uploadFlag;
}
