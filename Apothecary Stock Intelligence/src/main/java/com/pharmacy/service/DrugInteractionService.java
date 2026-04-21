package com.pharmacy.service;

import com.pharmacy.model.enums.InteractionSeverity;
import jakarta.annotation.PostConstruct;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DrugInteractionService {

    private final Map<String, DrugInteraction> interactionDatabase = new HashMap<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DrugInteraction {
        private String drug1;
        private String drug2;
        private InteractionSeverity severity;
        private String effect;
        private String recommendation;
    }

    private String makeKey(String d1, String d2) {
        String[] sorted = {d1.toUpperCase().trim(), d2.toUpperCase().trim()};
        Arrays.sort(sorted);
        return sorted[0] + "+" + sorted[1];
    }

    private void addInteraction(String d1, String d2, InteractionSeverity severity, String effect, String recommendation) {
        String key = makeKey(d1, d2);
        interactionDatabase.put(key, DrugInteraction.builder()
                .drug1(d1.toUpperCase().trim())
                .drug2(d2.toUpperCase().trim())
                .severity(severity)
                .effect(effect)
                .recommendation(recommendation)
                .build());
    }

    @PostConstruct
    public void initializeDatabase() {
        // SEVERE interactions
        addInteraction("WARFARIN", "ASPIRIN", InteractionSeverity.SEVERE, "Increased bleeding risk", "Avoid combination. Use alternative pain relief.");
        addInteraction("ATORVASTATIN", "ERYTHROMYCIN", InteractionSeverity.SEVERE, "Rhabdomyolysis risk", "Avoid combination. Consider azithromycin as alternative.");
        addInteraction("DIGOXIN", "AMIODARONE", InteractionSeverity.SEVERE, "Digoxin toxicity", "Reduce digoxin dose by 50%. Monitor serum levels closely.");
        addInteraction("SSRI", "TRAMADOL", InteractionSeverity.SEVERE, "Serotonin syndrome risk", "Avoid combination. Use alternative analgesic.");
        addInteraction("METHOTREXATE", "ASPIRIN", InteractionSeverity.SEVERE, "Methotrexate toxicity", "Avoid NSAIDs with methotrexate. Monitor blood counts.");
        addInteraction("WARFARIN", "METRONIDAZOLE", InteractionSeverity.SEVERE, "Enhanced anticoagulation", "Reduce warfarin dose. Monitor INR closely.");
        addInteraction("LITHIUM", "ACE_INHIBITOR", InteractionSeverity.SEVERE, "Lithium toxicity", "Avoid combination or monitor lithium levels frequently.");
        addInteraction("MAO_INHIBITOR", "SSRI", InteractionSeverity.SEVERE, "Serotonin syndrome", "Absolutely contraindicated. Wait 14 days between agents.");
        addInteraction("SIMVASTATIN", "CLARITHROMYCIN", InteractionSeverity.SEVERE, "Rhabdomyolysis risk", "Suspend statin during clarithromycin course.");
        addInteraction("CLOPIDOGREL", "OMEPRAZOLE", InteractionSeverity.SEVERE, "Reduced antiplatelet effect", "Use pantoprazole instead of omeprazole.");
        addInteraction("CISAPRIDE", "KETOCONAZOLE", InteractionSeverity.SEVERE, "QT prolongation / cardiac arrest", "Combination contraindicated.");
        addInteraction("THEOPHYLLINE", "CIPROFLOXACIN", InteractionSeverity.SEVERE, "Theophylline toxicity", "Reduce theophylline dose by 50%. Monitor levels.");
        addInteraction("POTASSIUM", "SPIRONOLACTONE", InteractionSeverity.SEVERE, "Hyperkalemia risk", "Avoid potassium supplements with potassium-sparing diuretics.");
        addInteraction("CARBAMAZEPINE", "ERYTHROMYCIN", InteractionSeverity.SEVERE, "Carbamazepine toxicity", "Avoid macrolide. Use azithromycin if needed.");
        addInteraction("SILDENAFIL", "NITRATE", InteractionSeverity.SEVERE, "Severe hypotension", "Combination absolutely contraindicated.");

        // MODERATE interactions
        addInteraction("METFORMIN", "ALCOHOL", InteractionSeverity.MODERATE, "Lactic acidosis risk", "Counsel patient to limit alcohol intake.");
        addInteraction("AMOXICILLIN", "WARFARIN", InteractionSeverity.MODERATE, "Enhanced warfarin effect", "Monitor INR more frequently during antibiotic course.");
        addInteraction("CIPROFLOXACIN", "ANTACID", InteractionSeverity.MODERATE, "Reduced ciprofloxacin absorption", "Take ciprofloxacin 2 hours before or 6 hours after antacid.");
        addInteraction("PARACETAMOL", "ALCOHOL", InteractionSeverity.MODERATE, "Liver damage risk", "Limit paracetamol dose. Counsel on alcohol reduction.");
        addInteraction("LITHIUM", "IBUPROFEN", InteractionSeverity.MODERATE, "Elevated lithium levels", "Monitor lithium serum levels. Consider paracetamol instead.");
        addInteraction("METFORMIN", "CONTRAST_DYE", InteractionSeverity.MODERATE, "Lactic acidosis risk", "Withhold metformin 48h before and after contrast procedures.");
        addInteraction("ACE_INHIBITOR", "POTASSIUM", InteractionSeverity.MODERATE, "Hyperkalemia", "Monitor serum potassium regularly.");
        addInteraction("DIGOXIN", "FUROSEMIDE", InteractionSeverity.MODERATE, "Hypokalemia increases digoxin toxicity", "Monitor potassium. Supplement if needed.");
        addInteraction("WARFARIN", "PARACETAMOL", InteractionSeverity.MODERATE, "Increased INR with regular use", "Monitor INR if using paracetamol regularly (>2g/day).");
        addInteraction("AMLODIPINE", "SIMVASTATIN", InteractionSeverity.MODERATE, "Increased statin exposure", "Limit simvastatin to 20mg daily with amlodipine.");
        addInteraction("METFORMIN", "CIMETIDINE", InteractionSeverity.MODERATE, "Increased metformin levels", "Use alternative H2 blocker. Monitor blood glucose.");
        addInteraction("FLUCONAZOLE", "WARFARIN", InteractionSeverity.MODERATE, "Enhanced anticoagulation", "Reduce warfarin dose. Monitor INR daily.");
        addInteraction("IBUPROFEN", "ASPIRIN", InteractionSeverity.MODERATE, "Reduced cardioprotective effect of aspirin", "Take aspirin 30 min before ibuprofen.");
        addInteraction("CIPROFLOXACIN", "THEOPHYLLINE", InteractionSeverity.MODERATE, "Increased theophylline levels", "Monitor theophylline levels and reduce dose if needed.");
        addInteraction("PHENYTOIN", "FLUCONAZOLE", InteractionSeverity.MODERATE, "Increased phenytoin levels", "Monitor phenytoin levels and adjust dose.");
        addInteraction("ATENOLOL", "VERAPAMIL", InteractionSeverity.MODERATE, "Bradycardia / heart block", "Avoid combination or use with caution. Monitor HR.");
        addInteraction("PREDNISONE", "IBUPROFEN", InteractionSeverity.MODERATE, "GI bleeding risk", "Use gastroprotection if combination necessary.");
        addInteraction("LOSARTAN", "POTASSIUM", InteractionSeverity.MODERATE, "Hyperkalemia risk", "Monitor serum potassium regularly.");
        addInteraction("DIAZEPAM", "ALCOHOL", InteractionSeverity.MODERATE, "Enhanced CNS depression", "Counsel patient to avoid alcohol.");
        addInteraction("DOXYCYCLINE", "ANTACID", InteractionSeverity.MODERATE, "Reduced doxycycline absorption", "Separate administration by 2-3 hours.");
        addInteraction("AZITHROMYCIN", "ANTACID", InteractionSeverity.MODERATE, "Reduced absorption", "Take azithromycin 1 hour before or 2 hours after antacid.");
        addInteraction("METOPROLOL", "FLUOXETINE", InteractionSeverity.MODERATE, "Increased metoprolol levels", "Monitor heart rate and blood pressure.");
        addInteraction("LEVOTHYROXINE", "CALCIUM", InteractionSeverity.MODERATE, "Reduced thyroid hormone absorption", "Separate by at least 4 hours.");
        addInteraction("LEVOTHYROXINE", "IRON", InteractionSeverity.MODERATE, "Reduced thyroid hormone absorption", "Separate by at least 4 hours.");
        addInteraction("CIPROFLOXACIN", "IRON", InteractionSeverity.MODERATE, "Reduced ciprofloxacin absorption", "Separate by at least 2 hours.");
        addInteraction("TETRACYCLINE", "CALCIUM", InteractionSeverity.MODERATE, "Reduced antibiotic absorption", "Separate by 2-3 hours.");
        addInteraction("METFORMIN", "DEXAMETHASONE", InteractionSeverity.MODERATE, "Hyperglycemia", "Monitor blood glucose closely. Adjust metformin dose.");
        addInteraction("INSULIN", "BETA_BLOCKER", InteractionSeverity.MODERATE, "Masked hypoglycemia symptoms", "Monitor blood glucose more frequently.");
        addInteraction("LISINOPRIL", "IBUPROFEN", InteractionSeverity.MODERATE, "Reduced antihypertensive effect", "Monitor blood pressure. Consider paracetamol.");
        addInteraction("CITALOPRAM", "OMEPRAZOLE", InteractionSeverity.MODERATE, "Increased citalopram levels", "Limit citalopram to 20mg with omeprazole.");

        // MILD interactions
        addInteraction("CETIRIZINE", "ALCOHOL", InteractionSeverity.MILD, "Increased drowsiness", "Counsel patient about enhanced sedation.");
        addInteraction("AMOXICILLIN", "ORAL_CONTRACEPTIVE", InteractionSeverity.MILD, "Possibly reduced contraceptive efficacy", "Use additional contraception during antibiotic course.");
        addInteraction("PANTOPRAZOLE", "IRON", InteractionSeverity.MILD, "Reduced iron absorption", "Monitor iron levels in long-term PPI use.");
        addInteraction("OMEPRAZOLE", "CALCIUM", InteractionSeverity.MILD, "Reduced calcium absorption long-term", "Supplement calcium. Monitor bone density.");
        addInteraction("METFORMIN", "VITAMIN_B12", InteractionSeverity.MILD, "Reduced B12 absorption", "Monitor B12 levels annually. Supplement if needed.");
        addInteraction("ASPIRIN", "GINKGO", InteractionSeverity.MILD, "Increased bleeding risk", "Counsel about herbal supplement use.");
        addInteraction("WARFARIN", "VITAMIN_K", InteractionSeverity.MILD, "Reduced anticoagulant effect", "Maintain consistent vitamin K intake.");
        addInteraction("ATORVASTATIN", "GRAPEFRUIT", InteractionSeverity.MILD, "Increased statin levels", "Avoid large amounts of grapefruit.");
        addInteraction("AMLODIPINE", "GRAPEFRUIT", InteractionSeverity.MILD, "Increased amlodipine levels", "Limit grapefruit consumption.");
        addInteraction("DICLOFENAC", "ASPIRIN", InteractionSeverity.MILD, "GI irritation", "Use gastroprotection if needed.");
    }

    public Optional<DrugInteraction> checkInteraction(String d1, String d2) {
        String key = makeKey(d1, d2);
        return Optional.ofNullable(interactionDatabase.get(key));
    }

    public List<DrugInteraction> checkPrescription(List<String> medicines) {
        List<DrugInteraction> found = new ArrayList<>();
        for (int i = 0; i < medicines.size(); i++) {
            for (int j = i + 1; j < medicines.size(); j++) {
                checkInteraction(medicines.get(i), medicines.get(j))
                        .ifPresent(found::add);
            }
        }
        return found;
    }

    public int getTotalInteractionCount() {
        return interactionDatabase.size();
    }
}
