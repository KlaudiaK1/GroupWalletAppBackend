package com.example.graph.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.math.BigDecimal;

@Data
@RelationshipProperties
public class Owes {
    @Id
    @GeneratedValue
    private Long id;

    private BigDecimal debt;

    @TargetNode
    private User debtor;
}
