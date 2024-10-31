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
@Table(name = "GS_INFO")
public class GsInfo {
    @Id
    @Column(name = "gs_id")
    private String gsId;
    @Column(name = "zone_id")
    private String zoneId;
    @Column(name = "area_id")
    private String areaId;
    @Column(name = "city_id")
    private String cityId;
    @Column(name = "gs_code")
    private String gsCode;
    @Column(name = "gs_name")
    private String gsName;
    @Column(name = "gs_type")
    private String gsType;
    @Column(name = "addr")
    private String addr;
    @Column(name = "incharge_man")
    private String inchargeMan;
    @Column(name = "email")
    private String email;
    @Column(name = "contact_man")
    private String contactMan;
    @Column(name = "contact_telphone")
    private String contactTelephone;
    @Column(name = "telephone1")
    private String telephone1;
    @Column(name = "telephone2")
    private String telephone2;
    @Column(name = "fax")
    private String fax;
    @Column(name = "zip_code")
    private String zipCode;
    @Column(name = "open_date")
    private String openDate;
    @Column(name = "node_id")
    private String nodeId;
    @Column(name = "dailysettle_begin")
    private String dailySettleBegin;
    @Column(name = "dailysettle_end")
    private String dailySettleEnd;
    @Column(name = "shiftsettle_standard")
    private String shiftSettleStandard;
    @Column(name = "validity")
    private String validity;
}
