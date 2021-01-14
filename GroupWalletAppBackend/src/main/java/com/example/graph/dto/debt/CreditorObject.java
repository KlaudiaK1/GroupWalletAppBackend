package com.example.graph.dto.debt;

import com.example.graph.dto.user.UserDetailObject;
import com.example.graph.model.Owes;
import com.example.graph.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditorObject {
    private UserDetailObject user;
    private BigDecimal debt;
}
