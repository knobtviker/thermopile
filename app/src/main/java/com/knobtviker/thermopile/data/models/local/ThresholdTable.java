package com.knobtviker.thermopile.data.models.local;

import io.requery.Column;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Table;

/**
 * Created by bojan on 15/06/2017.
 */

@Entity
@Table(name = "_threshold_")
public interface ThresholdTable {

    @Key
    @Column("_id_")
    String id();

    @Column("_day_")
    int day();

    @Column("_start_hour_")
    int startHour();

    @Column("_start_minute_")
    int startMinute();

    @Column("_end_hour_")
    int endHour();

    @Column("_end_minute_")
    int endMinute();

    @Column("_temperature_")
    int temperature();

    @Column("_color_")
    int color();
}
