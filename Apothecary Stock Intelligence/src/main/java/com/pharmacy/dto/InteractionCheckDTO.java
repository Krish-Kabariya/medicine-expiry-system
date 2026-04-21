package com.pharmacy.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InteractionCheckDTO {

    @NotEmpty(message = "At least one medicine name is required")
    private List<String> medicines;
}
