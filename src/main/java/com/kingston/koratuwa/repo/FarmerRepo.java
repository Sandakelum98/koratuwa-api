package com.kingston.koratuwa.repo;

import com.kingston.koratuwa.entity.FarmerDetailBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FarmerRepo extends JpaRepository<FarmerDetailBean, Integer> {

    @Query("SELECT f FROM FarmerDetailBean f LEFT JOIN FETCH f.user u WHERE u.id = :userId")
    FarmerDetailBean getFarmerDetailsById(@Param("userId") Integer userId);

    @Query("SELECT f FROM FarmerDetailBean f WHERE f.user.id = :userId")
    FarmerDetailBean getFarmerById(@Param("userId") Integer userId);

}
