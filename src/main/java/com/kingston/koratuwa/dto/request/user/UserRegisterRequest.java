package com.kingston.koratuwa.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest {
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String password;
    private Integer type;
}
