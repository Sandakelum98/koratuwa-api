package com.kingston.koratuwa.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "farmer_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerDetailBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String phoneNo;
    private String address;
    private Integer approveStatus;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserBean user;

    public FarmerDetailBean(Integer approveStatus, UserBean user) {
        this.approveStatus = approveStatus;
        this.user = user;
    }
}
