package com.example.Lync.Repository;

import com.example.Lync.Entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {

    @Query("SELECT o FROM OrderStatus o WHERE o.osId= :osId")
    OrderStatus findByOsId(Long osId);

}
