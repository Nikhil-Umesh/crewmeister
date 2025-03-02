package com.crewmeister.cmcodingchallenge.currency.repository;

import com.crewmeister.cmcodingchallenge.currency.model.CurrencyRateModelPrimaryKey;
import com.crewmeister.cmcodingchallenge.currency.model.CurrencyRatesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRatesModel, CurrencyRateModelPrimaryKey> {
}
