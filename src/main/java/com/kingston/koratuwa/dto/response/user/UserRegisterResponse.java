package com.kingston.koratuwa.dto.response.user;

import com.kingston.koratuwa.entity.user.UserBean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterResponse {
    private UserBean user;
    private String token;
}
