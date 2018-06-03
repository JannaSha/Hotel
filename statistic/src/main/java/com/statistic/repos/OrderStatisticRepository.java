package com.statistic.repos;

import com.statistic.model.OrderStatistic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatisticRepository extends CrudRepository<OrderStatistic, Long> {
    @Query(value="select count(*) as amount, sum(cost) as cost, sum(night_amount), room_type_id " +
            "from order_statistic group by room_type_id", nativeQuery = true)
    public List<Object[]> statisticRoomTypes();

    @Query(value="select count(*) as amount, sum(cost) as cost, sum(night_amount) as nights, " +
            "user_id from order_statistic group by user_id", nativeQuery = true)
    public List<Object[]> statisticUserId();
}
