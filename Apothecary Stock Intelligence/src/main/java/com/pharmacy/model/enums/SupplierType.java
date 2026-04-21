package com.pharmacy.model.enums;

import lombok.Getter;

@Getter
public enum SupplierType {
    LOCAL("Local Supplier"),
    WHOLESALE("Wholesale Distributor"),
    GOVERNMENT("Government Supplier");

    private final String displayName;

    SupplierType(String displayName) {
        this.displayName = displayName;
    }
}
