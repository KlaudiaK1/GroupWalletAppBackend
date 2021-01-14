package com.example.graph.service;

import com.example.graph.algorithm.OptimizeGraph;
import com.example.graph.algorithm.OptimizedEdge;
import com.example.graph.dto.debt.CreditorObject;
import com.example.graph.dto.user.UserDetailObject;
import com.example.graph.model.Group;
import com.example.graph.model.Member;
import com.example.graph.model.Owes;
import com.example.graph.model.User;
import com.example.graph.repository.GroupRepository;
import com.example.graph.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class DebtService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;

    public List<CreditorObject> getCreditorsForUser(@NotNull Long userId) {
        Optional<User> userFromRepository = userRepository.findById(userId);

        if (!userFromRepository.isPresent()) {
            throw new IllegalArgumentException("User with id = " + userId + " does not exist.");
        }

        List<User> creditorsByUser = userRepository.findCreditorsByUser(userId);

        creditorsByUser = creditorsByUser.stream().map(u -> userRepository.findById(u.getId()).get()).collect(Collectors.toList());

        List<CreditorObject> result = creditorsByUser.stream().map(u -> {
            Owes owes = u.getDebtors().stream()
                    .filter(d -> d.getDebtor().getId().equals(userId))
                    .findFirst()
                    .get();

            UserDetailObject userDetailObject = new UserDetailObject(u.getUsername(), u.getEmail());
            return new CreditorObject(userDetailObject, owes.getDebt());
        }).collect(Collectors.toList());

        return result;
    }

    public List<Owes> getDebtorsForUser(@NotNull Long userId) {
        Optional<User> userFromRepository = userRepository.findById(userId);

        if (!userFromRepository.isPresent()) {
            throw new IllegalArgumentException("User with id = " + userId + " does not exist.");
        }

        return userFromRepository.get().getDebtors();
    }

    public void addProportionalDebtForGroup(@NotNull Long userGivingMoneyId, @NotNull Long groupId, @NotNull BigDecimal totalDebt) {
        Optional<Group> groupFromRepository = groupRepository.findById(groupId);

        if (!groupFromRepository.isPresent()) {
            throw new IllegalArgumentException("Group with id = " + groupId + " does not exist.");
        }

        Set<Member> groupMembers = groupFromRepository.get().getMembers();

        BigDecimal individualDebt = totalDebt.divide(BigDecimal.valueOf(groupMembers.size()), 2, RoundingMode.HALF_EVEN);

        groupMembers.stream().filter(m -> !m.getUser().getId().equals(userGivingMoneyId)).forEach(m -> addDebt(userGivingMoneyId, m.getUser().getId(), groupId, individualDebt, false));
        optimizeGraph(groupFromRepository.get());
    }

    public void addDebt(@NotNull Long userGivingMoneyId, @NotNull Long userReceivingMoneyId, @NotNull Long commonGroupId, @NotNull BigDecimal money) {
        addDebt(userGivingMoneyId, userReceivingMoneyId, commonGroupId, money, true);
    }

    public void addDebt(@NotNull Long userGivingMoneyId, @NotNull Long userReceivingMoneyId, @NotNull BigDecimal money, boolean optimize) {
        List<Group> commonGroupsByUser = groupRepository.findCommonGroupsByUser(userGivingMoneyId, userReceivingMoneyId);

        if (commonGroupsByUser.isEmpty()) {
            throw new IllegalArgumentException("Given users have no group in common.");
        }

        addDebt(userGivingMoneyId, userReceivingMoneyId, commonGroupsByUser.get(0).getId(), money, optimize);
    }

    public void addDebt(@NotNull Long userGivingMoneyId, @NotNull Long userReceivingMoneyId, @NotNull Long commonGroupId, @NotNull BigDecimal money, boolean optimize) {
        Optional<User> userGivingMoneyFromRepository = userRepository.findById(userGivingMoneyId);
        Optional<User> userReceivingMoneyFromRepository = userRepository.findById(userReceivingMoneyId);
        Optional<Group> commonGroupFromRepository = groupRepository.findById(commonGroupId);

        if (!userGivingMoneyFromRepository.isPresent()) {
            throw new IllegalArgumentException("User with id = " + userGivingMoneyId + " does not exist.");
        }

        if (!userReceivingMoneyFromRepository.isPresent()) {
            throw new IllegalArgumentException("User with id = " + userReceivingMoneyId + " does not exist.");
        }

        if (!commonGroupFromRepository.isPresent()) {
            throw new IllegalArgumentException("Group with id = " + commonGroupId + " does not exist.");
        }

        addDebt(userGivingMoneyFromRepository.get(), userReceivingMoneyFromRepository.get(), commonGroupFromRepository.get(), money, optimize);
    }

    private void optimizeGraph(Group group) {
        List<User> userList = userRepository.findUsersByGroupId(group.getId());
        userList = userList.stream().map(u -> userRepository.findById(u.getId()).get()).collect(Collectors.toList());

        List<OptimizedEdge> optimizedEdges = OptimizeGraph.calculateOptimizedEdges(userList);

        groupRepository.deleteOwesRelationshipsBetweenUsersInGroup(group.getId());

        optimizedEdges.forEach(e -> this.addDebt(e.getUserId(), e.getDebtorId(), group.getId(), e.getDebt(), false));
    }

    private void addDebt(User userGivingMoney, User userReceivingMoney, Group commonGroup, BigDecimal money, boolean optimize) {
        //if money is negative, reverse the users
        if (money.signum() == -1) {
            throw new IllegalArgumentException("Debt cannot be less than 0.");
        }
        if (!(commonGroup.getMembers().stream().anyMatch(m -> m.getUser().equals(userGivingMoney)) &&
                commonGroup.getMembers().stream().anyMatch(m -> m.getUser().equals(userReceivingMoney)))) {
            throw new IllegalArgumentException("userGivingMoney and userReceivingMoney are not members of the given group.");
        }
        //get the pre-existing owes relationship if it exists
        Optional<Owes> relationship;
        relationship = userGivingMoney.getDebtors().stream().filter(o -> o.getDebtor().equals(userReceivingMoney)).findFirst();

        //1. check if userReceivingMoney is already indebted to userGivingMoney
        if (relationship.isPresent()) {
            //just add new money on top
            Owes owes = relationship.get();
            //sum up the money
            owes.setDebt(owes.getDebt().add(money));

            userRepository.save(userGivingMoney);
            return;
        }

        //2. check if userGivingMoney is indebted to userReceivingMoney and is now paying off their money — reverse money flow
        Optional<Owes> reverseRelationship;
        reverseRelationship = userReceivingMoney.getDebtors().stream().filter(o -> o.getDebtor().equals(userGivingMoney)).findFirst();

        if (reverseRelationship.isPresent()) {
            //2b. calculate if relationship has to change its direction
            Owes owes = reverseRelationship.get();
            int compareToResult = money.compareTo(owes.getDebt());

            switch (compareToResult) {
                //if money is settled, we can delete the relationship
                case 0:
                    removeRelationship(userReceivingMoney, owes);
                    //do not optimize the graph further
                    return;
                //if money is only reduced, we have to modify the pre-existing money
                case -1:
                    owes.setDebt(owes.getDebt().subtract(money));

                    userRepository.save(userReceivingMoney);
                    //do not optimize the graph further
                    return;
                //if the money received exceeds the current money, we have to reverse the relationship
                case 1:
                    BigDecimal newDebt = money.subtract(owes.getDebt());
                    //remove the old relationship
                    removeRelationship(userReceivingMoney, owes);
                    //add a new relationship in reverse
                    addRelationship(userGivingMoney, userReceivingMoney, newDebt);
                    //since this reversal can change the graph considerably, graph optimization can be made
                    if (optimize) {
                        optimizeGraph(commonGroup);
                    }
                    return;
            }
        }
        //3. there is no pre-existing relationship, so add a new one
        addRelationship(userGivingMoney, userReceivingMoney, money);

        if (optimize) {
            optimizeGraph(commonGroup);
        }
    }

    private void removeRelationship(User user, Owes relationship) {
        user.getDebtors().remove(relationship);

        userRepository.save(user);
    }

    private void addRelationship(User user, User debtor, BigDecimal money) {
        Owes newOwes = new Owes();
        newOwes.setDebt(money);
        newOwes.setDebtor(debtor);

        user.getDebtors().add(newOwes);

        userRepository.save(user);
    }
}
