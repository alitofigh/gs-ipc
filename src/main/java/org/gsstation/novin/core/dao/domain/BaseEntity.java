package org.gsstation.novin.core.dao.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

/**
 * Created by A_Tofigh at 8/5/2024
 */

@Data
@MappedSuperclass
public class BaseEntity {

    @Id
    @Column(name = "serial")
    String serialId;
    @Column(name = "gs_id")
    private String gsId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(serialId, that.serialId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(serialId);
    }
}
