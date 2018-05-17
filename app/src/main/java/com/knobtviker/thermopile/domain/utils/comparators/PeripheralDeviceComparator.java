package com.knobtviker.thermopile.domain.utils.comparators;

import com.knobtviker.thermopile.data.models.local.PeripheralDevice;

import java.util.Comparator;

public class PeripheralDeviceComparator implements Comparator<PeripheralDevice> {

    public static PeripheralDeviceComparator create() {
        return new PeripheralDeviceComparator();
    }

    private PeripheralDeviceComparator() {

    }

    @Override
    public int compare(PeripheralDevice item, PeripheralDevice otherItem) {
        final int byVendor = item.vendor.compareToIgnoreCase(otherItem.vendor);
        final int byName = item.name.compareToIgnoreCase(otherItem.name);
        final int byAddress = Integer.compare(item.address, otherItem.address);

        if (byVendor == 0) {
            if (byName == 0) {
                return byAddress;
            } else {
                return byName;
            }
        } else {
            return byVendor;
        }
    }
}
