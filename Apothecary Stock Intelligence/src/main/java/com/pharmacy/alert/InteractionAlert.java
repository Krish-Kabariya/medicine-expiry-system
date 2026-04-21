package com.pharmacy.alert;

import com.pharmacy.model.enums.AlertSeverity;
import com.pharmacy.model.enums.InteractionSeverity;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InteractionAlert extends Alert {
    private String drug1;
    private String drug2;
    private InteractionSeverity interactionSeverity;

    public InteractionAlert(String drug1, String drug2, InteractionSeverity interactionSeverity, String effect) {
        super(
            "Drug Interaction: " + drug1 + " + " + drug2,
            effect,
            interactionSeverity == InteractionSeverity.SEVERE ? AlertSeverity.CRITICAL : AlertSeverity.HIGH,
            null,
            drug1 + " + " + drug2,
            "INTERACTION"
        );
        this.drug1 = drug1;
        this.drug2 = drug2;
        this.interactionSeverity = interactionSeverity;
    }
}
