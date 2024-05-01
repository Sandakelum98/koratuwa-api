package com.kingston.koratuwa.dto.request.farmer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmerLoginRequest {
    private String userName;
    private String password;
}
