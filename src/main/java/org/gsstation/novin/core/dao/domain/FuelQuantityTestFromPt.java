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
@Table(name = "FUEL_QUANTITY_TEST_FROM_PT")
@EqualsAndHashCode(callSuper=false)
public class FuelQuantityTestFromPt extends BaseEntity {
    @Column(name = "test_man")
    private String testMan;
    @Column(name = "test_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date testTime;
    @Column(name = "pt_id")
    private String ptId;
    @Column(name = "fuel_type")
    private String fuelType;
    @Column(name = "test_amount", precision = 20, scale = 4)
    private Double testAmount;
    @Column(name = "deference_amount")
    private Double deferenceAmount;
    @Column(name = "test_describe")
    private String testDescribe;
    @Column(name = "upload_flag")
    private String uploadFlag;
}
