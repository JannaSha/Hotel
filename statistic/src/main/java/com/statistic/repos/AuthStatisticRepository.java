package com.statistic.repos;

import com.statistic.model.AuthStatistic;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthStatisticRepository extends CrudRepository<AuthStatistic, Long> {
    List<AuthStatistic> findAll();
}
