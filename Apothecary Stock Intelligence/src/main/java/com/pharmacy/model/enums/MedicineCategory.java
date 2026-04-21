package com.pharmacy.model.enums;

import lombok.Getter;

@Getter
public enum MedicineCategory {
    ANTIBIOTIC("Antibiotic", false),
    ANALGESIC("Analgesic", false),
    ANTACID("Antacid", false),
    VITAMIN("Vitamin", false),
    VACCINE("Vaccine", true),
    CARDIAC("Cardiac", false),
    DIABETIC("Diabetic", false),
    ANTIFUNGAL("Antifungal", false),
    ANTIHISTAMINE("Antihistamine", false),
    OTHER("Other", false);

    private final String displayName;
    private final boolean isColdChain;

    MedicineCategory(String displayName, boolean isColdChain) {
        this.displayName = displayName;
        this.isColdChain = isColdChain;
    }
}
