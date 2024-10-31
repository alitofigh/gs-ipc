package org.gsstation.novin.core.dao.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created by A_Tofigh at 7/25/2024
 */

@Data
@Entity
@Table(name = "TRANSACTION_RECORD")
public class TransactionRecord extends BaseEntityTrx {
    @EmbeddedId
    private TransactionKey id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TransactionRecord that = (TransactionRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
