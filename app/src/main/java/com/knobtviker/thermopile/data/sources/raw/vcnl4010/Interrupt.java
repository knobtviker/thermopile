package com.knobtviker.thermopile.data.sources.raw.vcnl4010;

import static com.knobtviker.thermopile.data.sources.raw.vcnl4010.Vcnl4010.INTERRUPT_TYPE_UNKNOWN;
import static com.knobtviker.thermopile.data.sources.raw.vcnl4010.Vcnl4010.InterruptType;

/**
 * Created by bojan on 01/02/2018.
 */

public class Interrupt {

    @InterruptType
    public int type = INTERRUPT_TYPE_UNKNOWN;

    public boolean exceededLowThreshold = false;

    public boolean exceededHighThreshold = false;
}
