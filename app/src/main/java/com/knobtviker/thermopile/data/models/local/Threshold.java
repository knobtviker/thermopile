package com.knobtviker.thermopile.data.models.local;

import com.knobtviker.thermopile.data.models.local.implementation.BaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Index;

/**
 * Created by bojan on 15/06/2017.
 */

@Entity
public class Threshold extends BaseModel {

    @Index
    public int day;

    @Index
    public int startHour;

    @Index
    public int startMinute;

    public int endHour;

    public int endMinute;

    public int temperature;

    public String color;
}
