package com.example.graph.service;

import com.example.graph.model.Group;
import com.example.graph.model.Member;
import com.example.graph.model.User;
import com.example.graph.repository.GroupRepository;
import com.example.graph.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Group> listGroups(Long userId) {
        return groupRepository.findByUser(userId);
    }

    public Group createGroup(@NotNull Long ownerId, @NotNull String groupName) {
        Group group = new Group(groupName);

        Optional<User> ownerFromRepository = userRepository.findById(ownerId);

        if (!ownerFromRepository.isPresent()) {
            throw new IllegalArgumentException("User with id = " + ownerId + " does not exist.");
        }

        Member member = new Member(Member.Role.ADMIN, ownerFromRepository.get());
        group.setMembers(Collections.singleton(member));

        groupRepository.save(group);

        return group;
    }

    public Group addMemberToGroup(@NotNull Long adminId, @NotNull Long userId, @NotNull Long groupId, @NotNull Member.Role role) {
        Optional<Group> groupFromRepository = groupRepository.findById(groupId);

        Optional<User> adminFromRepository = userRepository.findById(adminId);
        Optional<User> userFromRepository = userRepository.findById(userId);

        if (!adminFromRepository.isPresent()) {
            throw new IllegalArgumentException("User with id = " + adminId + " does not exist.");
        }

        if (!userFromRepository.isPresent()) {
            throw new IllegalArgumentException("User with id = " + userId + " does not exist.");
        }

        if (!groupFromRepository.isPresent()) {
            throw new IllegalArgumentException("Group with id = " + groupId + " does not exist.");
        }

        Group group = groupFromRepository.get();
        User admin = adminFromRepository.get();
        User user = userFromRepository.get();

        if (group.getMembers().stream().noneMatch(m -> m.getUser().equals(admin) && m.getRole().equals(Member.Role.ADMIN))) {
            throw new IllegalArgumentException("Passed user (id = " + adminId + ") does not have the necessary admin rights for this operation. ");
        }

        Member newMember = new Member(role, user);
        group.addMember(newMember);

        groupRepository.save(group);

        return group;
    }

    public void deleteGroup(@NotNull Long adminId, @NotNull Long groupId) {
        Optional<User> adminFromRepository = userRepository.findById(adminId);

        Optional<Group> groupFromRepository = groupRepository.findById(groupId);

        if (!adminFromRepository.isPresent()) {
            throw new IllegalArgumentException("User with id = " + adminFromRepository + " does not exist.");
        }

        if (!groupFromRepository.isPresent()) {
            throw new IllegalArgumentException("Group with id = " + groupId + " does not exist.");
        }

        Group group = groupFromRepository.get();
        User admin = adminFromRepository.get();

        if (group.getMembers().stream().noneMatch(m -> m.getUser().equals(admin) && m.getRole().equals(Member.Role.ADMIN))) {
            throw new IllegalArgumentException("Passed user (id = " + adminId + ") does not have the necessary admin rights for this operation. ");
        }

        groupRepository.delete(group);
    }
}
