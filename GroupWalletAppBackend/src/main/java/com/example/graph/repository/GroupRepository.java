package com.example.graph.repository;

import com.example.graph.model.Group;
import com.example.graph.model.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface GroupRepository extends Neo4jRepository<Group, Long> {

    Optional<Group> findById(Long id);

    @Query("MATCH (n:Group)<-[:BELONGS_TO]-(u:User) WHERE ID(u) = $userId RETURN n")
    List<Group> findByUser(@Param("userId") Long userId);

    @Query("MATCH (u1:User)-[:BELONGS_TO]->(n:Group)<-[:BELONGS_TO]-(u2:User) WHERE ID(u1) = $userId1 AND ID(u2) = $userId2 RETURN n")
    List<Group> findCommonGroupsByUser(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("MATCH (n:Group)<-[:BELONGS_TO]-(:User)-[r:OWES]->(:User)-[:BELONGS_TO]->(n:Group) WHERE ID(n) = $groupId DELETE r")
    void deleteOwesRelationshipsBetweenUsersInGroup(@Param("groupId") Long groupId);
}
