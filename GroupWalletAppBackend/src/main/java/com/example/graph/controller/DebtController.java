package com.example.graph.controller;

import com.example.graph.dto.debt.CreditorObject;
import com.example.graph.dto.debt.GroupDebtObject;
import com.example.graph.dto.debt.PayObject;
import com.example.graph.model.Owes;
import com.example.graph.model.User;
import com.example.graph.service.DebtService;
import com.example.graph.service.GraphUserDetailsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/services/controller/debt")
public class DebtController {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DebtService debtService;

    @Autowired
    private GraphUserDetailsService graphUserDetailsService;

    @GetMapping("/list-debtors")
    @ResponseStatus(HttpStatus.OK)
    public List<Owes> listYourDebtors() {
        User userFromSession = graphUserDetailsService.getUserFromSession();

        return debtService.getDebtorsForUser(userFromSession.getId());
    }

    @GetMapping("/list-creditors")
    @ResponseStatus(HttpStatus.OK)
    public List<CreditorObject> listYourCreditors() {
        User userFromSession = graphUserDetailsService.getUserFromSession();

        return debtService.getCreditorsForUser(userFromSession.getId());
    }

    @PostMapping("/add-proportional-group-debt")
    @ResponseStatus(HttpStatus.OK)
    public void addProportionalGroupDebt(@RequestBody @Valid GroupDebtObject groupDebtObject) {
        User userFromSession = graphUserDetailsService.getUserFromSession();

        debtService.addProportionalDebtForGroup(userFromSession.getId(), groupDebtObject.getGroupId(), groupDebtObject.getDebt());
    }

    @PostMapping("/pay")
    @ResponseStatus(HttpStatus.OK)
    public void pay(@RequestBody @Valid PayObject payObject) {
        User userFromSession = graphUserDetailsService.getUserFromSession();

        debtService.addDebt(userFromSession.getId(), payObject.getUserReceivingMoneyId(), payObject.getMoney(), true);
    }
}
