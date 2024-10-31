package org.gsstation.novin.core.dao.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by A_Tofigh at 8/7/2024
 */

@Data
@Entity
@Table(name = "EMERGENCY_REPORT")
@EqualsAndHashCode(callSuper=false)
public class EmergencyReport extends BaseEntity {
    @Column(name = "emergency_kind")
    private String emergencyKind;
    @Column(name = "happen_time")
    private String happenTime;
    @Column(name = "description")
    private String description;
    @Column(name = "upload_flag")
    private String uploadFlag;
}
