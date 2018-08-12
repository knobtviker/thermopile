package com.knobtviker.thermopile.data.models.presentation;

import com.google.auto.value.AutoValue;
import com.knobtviker.thermopile.shared.constants.BluetoothState;

@AutoValue
public abstract class BluetoothEntity {

    public abstract boolean hasBluetooth();

    public abstract boolean hasBluetoothLE();

    public abstract boolean enabled();

    public abstract boolean gattRunning();

    public abstract boolean advertising();

    @BluetoothState
    public abstract int state();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder hasBluetooth(final boolean value);

        public abstract Builder hasBluetoothLE(final boolean value);

        public abstract Builder enabled(final boolean value);

        public abstract Builder gattRunning(final boolean value);

        public abstract Builder advertising(final boolean value);

        public abstract Builder state(@BluetoothState final int value);

        public abstract BluetoothEntity build();
    }

    abstract Builder toBuilder();

    public BluetoothEntity withHasBluetooth(final boolean value) {
        return toBuilder().hasBluetooth(value).build();
    }

    public BluetoothEntity withHasBluetoothLE(final boolean value) {
        return toBuilder().hasBluetoothLE(value).build();
    }

    public BluetoothEntity withEnabled(final boolean value) {
        return toBuilder().enabled(value).build();
    }

    public BluetoothEntity withGattRunning(final boolean value) {
        return toBuilder().gattRunning(value).build();
    }

    public BluetoothEntity withAdvertising(final boolean value) {
        return toBuilder().advertising(value).build();
    }

    public BluetoothEntity withState(@BluetoothState final int value) {
        return toBuilder().state(value).build();
    }

    public static Builder builder() {
        return new AutoValue_BluetoothEntity.Builder()
            .hasBluetooth(false)
            .hasBluetoothLE(false)
            .enabled(false)
            .gattRunning(false)
            .advertising(false)
            .state(BluetoothState.OFF);
    }
}
