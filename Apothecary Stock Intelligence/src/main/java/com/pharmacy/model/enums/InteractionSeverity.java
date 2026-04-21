package com.pharmacy.model.enums;

import lombok.Getter;

@Getter
public enum InteractionSeverity {
    SEVERE("Severe - Avoid Combination"),
    MODERATE("Moderate - Use with Caution"),
    MILD("Mild - Monitor Patient");

    private final String description;

    InteractionSeverity(String description) {
        this.description = description;
    }
}
