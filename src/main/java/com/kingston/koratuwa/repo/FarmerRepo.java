package com.kingston.koratuwa.repo;

import com.kingston.koratuwa.entity.FarmerBean;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmerRepo extends JpaRepository<FarmerBean, Integer> {

    FarmerBean getByUserName(String userName);

}
