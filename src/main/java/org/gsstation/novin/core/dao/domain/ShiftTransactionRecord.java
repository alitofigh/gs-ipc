package org.gsstation.novin.core.dao.domain;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Created by A_Tofigh at 8/5/2024
 */

@Data
@Entity
@Table(name = "SHIFT_TRANSACTION_RECORD")
public class ShiftTransactionRecord extends BaseEntityTrx {
    @EmbeddedId
    private TransactionKey id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ShiftTransactionRecord that = (ShiftTransactionRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
