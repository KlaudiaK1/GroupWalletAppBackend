package com.example.graph.dto.debt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDebtObject {
    @NotNull
    private Long groupId;
    @NotNull
    private BigDecimal debt;
}
