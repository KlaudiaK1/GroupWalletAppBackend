package com.example.graph;

import com.example.graph.model.Group;
import com.example.graph.repository.GroupRepository;
import com.example.graph.repository.UserRepository;
import com.example.graph.service.DebtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class DebtServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private DebtService debtService;

    @Test
    public void Given_NoCommonGroupExists_When_AddDebtBetweenUsers_Then_ThrowIllegalArgumentException() {
        Mockito.when(groupRepository.findCommonGroupsByUser(0L, 1L)).thenReturn(new ArrayList<Group>());

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> debtService.addDebt(0L, 1L, BigDecimal.ONE, false));

        String expectedMessage = "Given users have no group in common";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
