package org.gsstation.novin.core.dao.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;


@Data
@Entity
@Table(name = "DAILY_TRANSACTION_RECORD")
public class DailyTransactionRecord extends BaseEntityTrx {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "gs_id")
    private String gsId;
    @Column(name = "pt_id")
    private String ptId;
    @Column(name = "fuel_ttc")
    private Integer fuelTtc;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DailyTransactionRecord that = (DailyTransactionRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
