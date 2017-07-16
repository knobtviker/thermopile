package com.knobtviker.thermopile.data.models.local;

import io.requery.Column;
import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.Table;

/**
 * Created by bojan on 15/06/2017.
 */

@Entity
@Table(name = "_reading_")
public interface ReadingTable {

    @Key
    @Generated
    @Column("_id_")
    long id();

    @Column("_timestamp_")
    long timestamp();

    @Column("_temperature_")
    float temperature();

    @Column("_humidity_")
    float humidity();

    @Column("_pressure_")
    float pressure();
}
