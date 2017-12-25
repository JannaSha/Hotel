package com.billing.repos;


import com.billing.models.Billing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillingRepository extends CrudRepository<Billing, Long> {
    List<Billing> findAll(Pageable pageable);
}
