package org.gsstation.novin.core.dao.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by A_Tofigh at 08/12/2024
 */
@Data
@Entity
@Table(name = "PT_INFO")
public class PtInfo {
    @Id
    @Column(name = "fuel_sam_id")
    private String fuelSamId;
    @Column(name = "gs_id")
    private String gsId;
    @Column(name = "pay_sam_id")
    private String paySamId;
    @Column(name = "fuel_type")
    private String fuelType;
    @Column(name = "ipc_ip_addr")
    private String ipcIpAddress;
    @Column(name = "pt_ip_addr")
    private String ptIpAddress;
    @Column(name = "nozzle_id")
    private String nozzleId;
    @Column(name = "pt_id")
    private String ptId;
    @Column(name = "oilcan_id")
    private String oilcanId;
    @Column(name = "dispenser_type")
    private String dispenserType;
    @Column(name = "f_fuel_public_key_E")
    private String fFuelPublicKeyE;
    @Column(name = "f_fuel_public_key_N")
    private String fFuelPublicKeyN;
    @Column(name = "f_pay_public_key_E")
    private String fPayPublicKeyE;
    @Column(name = "f_pay_public_key_N")
    private String fPayPublicKeyN;
    @Column(name = "s_fuel_public_key_E")
    private String sFuelPublicKeyE;
    @Column(name = "s_fuel_public_key_N")
    private String sFuelPublicKeyN;
    @Column(name = "s_pay_public_key_E")
    private String sPayPublicKeyE;
    @Column(name = "s_pay_public_key_N")
    private String sPayPublicKeyN;
    @Column(name = "validity")
    private String validity;
    @Column(name = "stand_no")
    private String standNo;
    @Column(name = "activity")
    private String activity;
}
