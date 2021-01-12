package com.example.graph.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptimizedEdge {
    private Long userId;
    private Long debtorId;
    private BigDecimal debt;
}
