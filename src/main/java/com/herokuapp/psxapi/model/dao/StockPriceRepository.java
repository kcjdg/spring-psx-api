package com.herokuapp.psxapi.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockPriceRepository extends CrudRepository<StockPrice, String>, JpaSpecificationExecutor<StockPrice>, JpaRepository<StockPrice,String> {

}
