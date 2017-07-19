package com.knobtviker.thermopile.data.models.local;

import io.requery.Column;
import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.Generated;
import io.requery.Key;
import io.requery.OneToOne;
import io.requery.Table;

/**
 * Created by bojan on 15/06/2017.
 */

@Entity
@Table(name = "_mode_")
public interface ModeTable {

    @Key
    @Generated
    @Column("_id_")
    long id();

    @Column("_name_")
    String name();

    @ForeignKey
    @OneToOne
    ThresholdTableEntity threshold();
}
