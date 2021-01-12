package com.example.graph.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.HashSet;
import java.util.Set;

@Data
@Node
public class Group {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.INCOMING)
    private Set<Member> members;

    public Group(String name, Set<Member> members) {
        this.name = name;
        this.members = members;
    }

    public Group() {
        this.members = new HashSet<>();
    }

    public Group(String name) {
        this.name = name;
        this.members = new HashSet<>();
    }

    public void addMember(Member member) {
        members.add(member);
    }
}
