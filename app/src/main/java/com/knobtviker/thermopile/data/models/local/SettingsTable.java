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
public interface SettingsTable {

    @Key
    @Generated
    @Column("_id_")
    long id();

    @Column("_timezone_")
    String timezone();

    @Column("_format_clock_")
    int formatClock();

    @Column("_format_date_")
    int formatDate();

    @Column("_format_time_")
    int formatTime();

    @Column("_unit_temperature_") //Celsius, Farenheit, Kelvin
    int unitTemperature();

    @Column("_unit_pressure_")
    int unitPressure();
}
