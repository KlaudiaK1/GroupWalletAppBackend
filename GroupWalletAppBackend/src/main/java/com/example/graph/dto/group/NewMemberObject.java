package com.example.graph.dto.group;

import com.example.graph.model.Member;
import com.sun.istack.internal.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewMemberObject {
    @NotNull
    private Long userId;
    @NotNull
    private Long groupId;
    @NotNull
    private String role;
}
