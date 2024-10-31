package org.gsstation.novin.core.dao.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by A_Tofigh at 8/5/2024
 */

@Data
@Entity
@Table(name = "TRANSACTION_RECEIVE_LOG")
@EqualsAndHashCode(callSuper=false)
public class TransactionReceiveLog extends BaseEntity {

    @Column(name = "log_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logTime;
    @Column(name = "pt_id")
    private String ptId;
    @Column(name = "fuel_ttc")
    private Integer fuelTtc;
    @Column(name = "state")
    private String state;
}
