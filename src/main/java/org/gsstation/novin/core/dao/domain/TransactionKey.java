package org.gsstation.novin.core.dao.domain;

import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by A_Tofigh at 7/25/2024
 */

@NoArgsConstructor
@Embeddable
public class TransactionKey implements Serializable {

    @Column(name = "gs_id")
    private String gsIs;
    @Column(name = "pt_id")
    private String ptId;
    @Column(name = "fuel_ttc")
    private Integer fuelTtc;

    public TransactionKey(String gsIs, String ptId, Integer fuelTtc) {
        this.gsIs = gsIs;
        this.ptId = ptId;
        this.fuelTtc = fuelTtc;
    }



    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
