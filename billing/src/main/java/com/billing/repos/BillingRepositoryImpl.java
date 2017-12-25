//package com.billing.repos;
//
//import com.billing.models.Billing;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//
//public class BillingRepositoryImpl implements BillingRepository {
//
//    @Autowired
//    private BillingRepository billingRepository;
//
//
//    @Override
//    public <S extends Billing> S save(S entity) {
//        return billingRepository.save(entity);
//    }
//
//    @Override
//    public <S extends Billing> Iterable<S> save(Iterable<S> entities) {
//        return billingRepository.save(entities);
//    }
//
//    @Override
//    public Billing findOne(Long aLong) {
//        return null;
//    }
//
//    @Override
//    public boolean exists(Long aLong) {
//        return false;
//    }
//
//    @Override
//    public List<Billing> findAll() {
//        return billingRepository.findAll();
//    }
//
//    @Override
//    public Iterable<Billing> findAll(Iterable<Long> longs) {
//        return billingRepository.findAll(longs);
//    }
//
//    @Override
//    public long count() {
//        return billingRepository.count();
//    }
//
//    @Override
//    public void delete(Long aLong) {
//        billingRepository.delete(aLong);
//    }
//
//    @Override
//    public void delete(Billing entity) {
//        billingRepository.delete(entity);
//
//    }
//
//    @Override
//    public void delete(Iterable<? extends Billing> entities) {
//        billingRepository.delete(entities);
//    }
//
//    @Override
//    public void deleteAll() {
//        billingRepository.deleteAll();
//
//    }
//
//}
