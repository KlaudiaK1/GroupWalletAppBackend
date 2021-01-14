package com.example.graph.init;

import com.example.graph.model.Group;
import com.example.graph.model.Member;
import com.example.graph.model.User;
import com.example.graph.repository.GroupRepository;
import com.example.graph.repository.UserRepository;
import com.example.graph.service.DebtService;
import com.example.graph.service.GroupService;
import com.example.graph.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

@Component
@Profile({"db-test", "demo"})
public class GraphInitializer implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(GraphInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private DebtService debtService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void afterPropertiesSet() {
        logger.info("Populating database with test data.");
        //DELETE if exists
        userRepository.deleteAll();
        groupRepository.deleteAll();
        //reinitialize
        User user1;
        User user2;
        User user3;
        User user4;
        User user5;
        User user6;
        User user7;
        User grace;
        User ivan;
        User judy;
        User luke;
        User mallory;
        //users
        user1 = new User( "Alice", "alice@alice.com", passwordEncoder.encode("alice111"));
        userRepository.save(user1);
        user2 = new User( "Gabe", "gabe@gabe.com", passwordEncoder.encode("gabe111"));
        userRepository.save(user2);
        user3 = new User( "Fred", "fred@fred.com", passwordEncoder.encode("fred111"));
        userRepository.save(user3);
        user4 = new User( "Ema", "ema@ema.com", passwordEncoder.encode("ema111"));
        userRepository.save(user4);
        user5 = new User( "David", "david@david.com", passwordEncoder.encode("david111"));
        userRepository.save(user5);
        user6 = new User( "Charlie", "cgarlie@charlie.com", passwordEncoder.encode("charlie111"));
        userRepository.save(user6);
        user7 = new User( "Bob", "bob@bob.com", passwordEncoder.encode("bob111"));
        userRepository.save(user7);

        grace = new User( "Grace", "grace@grace.com", passwordEncoder.encode("grace111"));
        userRepository.save(grace);
        ivan = new User( "Ivan", "ivan@ivan.com", passwordEncoder.encode("ivan111"));
        userRepository.save(ivan);
        judy = new User( "Judy", "judy@judy.com", passwordEncoder.encode("judy111"));
        userRepository.save(judy);
        luke = new User( "Luke", "luke@luke.com", passwordEncoder.encode("luke111"));
        userRepository.save(luke);
        mallory = new User( "Mallory", "mallory@mallory.com", passwordEncoder.encode("mallory111"));
        userRepository.save(mallory);

        //groups and members
        //1
        Set<Member> members = new HashSet<>();

        Member member1 = new Member(Member.Role.ADMIN, user1);
        members.add(member1);
        Member member2 = new Member(Member.Role.MEMBER, user2);
        members.add(member2);
        Member member3 = new Member(Member.Role.MEMBER, user3);
        members.add(member3);
        Member member4 = new Member(Member.Role.MEMBER, user4);
        members.add(member4);
        Member member5 = new Member(Member.Role.MEMBER, user5);
        members.add(member5);
        Member member6 = new Member(Member.Role.MEMBER, user6);
        members.add(member6);
        Member member7 = new Member(Member.Role.MEMBER, user7);
        members.add(member7);

        Set<Member> membersOfSecondGroup = new HashSet<>();
        Member member2_1 = new Member(Member.Role.ADMIN, grace);
        membersOfSecondGroup.add(member2_1);
        Member member2_2 = new Member(Member.Role.ADMIN, ivan);
        membersOfSecondGroup.add(member2_2);
        Member member2_3 = new Member(Member.Role.ADMIN, judy);
        membersOfSecondGroup.add(member2_3);
        Member member2_4= new Member(Member.Role.ADMIN, luke);
        membersOfSecondGroup.add(member2_4);
        Member member2_5 = new Member(Member.Role.ADMIN, mallory);
        membersOfSecondGroup.add(member2_5);

        Group group = new Group("My group", members);
        groupRepository.save(group);

        Group secondGroup = new Group("Second group", membersOfSecondGroup);
        groupRepository.save(secondGroup);


        //owes relationships

        debtService.addDebt(user5.getId(), user2.getId(), group.getId(), BigDecimal.valueOf(10));
        debtService.addDebt(user7.getId(), user2.getId(), group.getId(), BigDecimal.valueOf(30));
        debtService.addDebt(user4.getId(), user3.getId(), group.getId(), BigDecimal.valueOf(10));
        debtService.addDebt(user5.getId(), user3.getId(), group.getId(), BigDecimal.valueOf(10));
        debtService.addDebt(user6.getId(), user3.getId(), group.getId(), BigDecimal.valueOf(30));
        debtService.addDebt(user7.getId(), user3.getId(), group.getId(), BigDecimal.valueOf(10));
        debtService.addDebt(user4.getId(), user5.getId(), group.getId(), BigDecimal.valueOf(50));
        debtService.addDebt(user5.getId(), user6.getId(), group.getId(), BigDecimal.valueOf(20));
        debtService.addDebt(user6.getId(), user7.getId(), group.getId(), BigDecimal.valueOf(40));

        debtService.addDebt(ivan.getId(), grace.getId(), secondGroup.getId(), BigDecimal.valueOf(5));
        debtService.addDebt(judy.getId(), grace.getId(), secondGroup.getId(), BigDecimal.valueOf(3));
        debtService.addDebt(grace.getId(), ivan.getId(), secondGroup.getId(), BigDecimal.valueOf(2));
        debtService.addDebt(mallory.getId(), ivan.getId(), secondGroup.getId(), BigDecimal.valueOf(5));
        debtService.addDebt(grace.getId(), judy.getId(), secondGroup.getId(), BigDecimal.valueOf(10));
        debtService.addDebt(luke.getId(), judy.getId(), secondGroup.getId(), BigDecimal.valueOf(4));
        debtService.addDebt(mallory.getId(), judy.getId(), secondGroup.getId(), BigDecimal.valueOf(6));
        debtService.addDebt(mallory.getId(), judy.getId(), secondGroup.getId(), BigDecimal.valueOf(2));
        debtService.addDebt(ivan.getId(), luke.getId(), secondGroup.getId(), BigDecimal.valueOf(6));
        debtService.addDebt(grace.getId(), mallory.getId(), secondGroup.getId(), BigDecimal.valueOf(15));
        debtService.addDebt(luke.getId(), mallory.getId(), secondGroup.getId(), BigDecimal.valueOf(6));
        debtService.addDebt(judy.getId(), mallory.getId(), secondGroup.getId(), BigDecimal.valueOf(11));
        logger.info("Database population successful.");

        groupService.addMemberToGroup(user1.getId(), grace.getId(), group.getId(), Member.Role.MEMBER);
        groupService.addMemberToGroup(grace.getId(), user1.getId(), secondGroup.getId(), Member.Role.MEMBER);
        debtService.addDebt(user1.getId(), grace.getId(), group.getId(), BigDecimal.valueOf(100));
    }
}
