package com.kingston.koratuwa.dto.response.farmer;

import com.kingston.koratuwa.entity.UserBean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmerRegisterResponse {
    private UserBean user;
    private String token;
}
