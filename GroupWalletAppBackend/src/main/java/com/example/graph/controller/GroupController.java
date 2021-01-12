package com.example.graph.controller;

import com.example.graph.dto.group.SingleIdObject;
import com.example.graph.dto.group.NewGroupObject;
import com.example.graph.dto.group.NewMemberObject;
import com.example.graph.model.Group;
import com.example.graph.model.Member;
import com.example.graph.model.User;
import com.example.graph.service.GraphUserDetailsService;
import com.example.graph.service.GroupService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/services/controller/group")
public class GroupController {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GroupService groupService;

    @Autowired
    private GraphUserDetailsService graphUserDetailsService;

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteGroup(@RequestBody @Valid SingleIdObject singleIdObject) {
        User userFromSession = graphUserDetailsService.getUserFromSession();

        groupService.deleteGroup(userFromSession.getId(), singleIdObject.getId());
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public List<Group> listYourGroups() {
        User userFromSession = graphUserDetailsService.getUserFromSession();

        return groupService.listGroups(userFromSession.getId());
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Group createGroup(@RequestBody @Valid NewGroupObject newGroupObject) {
        return groupService.createGroup(graphUserDetailsService.getUserFromSession().getId(), newGroupObject.getName());
    }

    @PostMapping("/member/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Group addMemberToGroup(@RequestBody @Valid NewMemberObject newMemberObject) {
        try {
            Member.Role role = Member.Role.valueOf(newMemberObject.getRole());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role " + newMemberObject.getRole() + " does not exist.", e);
        }

        User userFromSession = graphUserDetailsService.getUserFromSession();

        return groupService.addMemberToGroup(userFromSession.getId(),
                newMemberObject.getUserId(),
                newMemberObject.getGroupId(),
                Member.Role.valueOf(newMemberObject.getRole()));
    }
}
