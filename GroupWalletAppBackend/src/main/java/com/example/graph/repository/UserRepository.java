package com.example.graph.repository;

import com.example.graph.model.Owes;
import com.example.graph.model.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends Neo4jRepository<User, Long> {
    Optional<User> findFirstByEmail(String email);

    @Query("MATCH (u:User)<-[:OWES]-(d:User) WHERE ID(d) = $userId RETURN u")
    List<User> findCreditorsByUser(@Param("userId") Long userId);

    @Query("MATCH (n:Group)<-[:BELONGS_TO]-(u:User) WHERE ID(n) = $groupId RETURN u")
    List<User> findUsersByGroupId(@Param("groupId") Long groupId);
}
