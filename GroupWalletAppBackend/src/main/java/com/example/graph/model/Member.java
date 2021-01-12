package com.example.graph.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Data
@RelationshipProperties
public class Member {
    @Id
    @GeneratedValue
    private Long id;

    private Role role;

    @TargetNode
    private User user;

    public enum Role {
        ADMIN,
        MEMBER
    }

    public Member(Role role, User user) {
        this.role = role;
        this.user = user;
    }
}
