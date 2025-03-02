package com.crewmeister.cmcodingchallenge.currency.service;

import com.crewmeister.cmcodingchallenge.currency.model.CreateForexModel;
import com.crewmeister.cmcodingchallenge.currency.model.CurrencyRatesModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CurrencyRatesService {
    public boolean createNewCurrencyRates(CreateForexModel createForexModel) ;

    public Map<String, List<CurrencyRatesModel>> getAllAvailableExchangeRates();
    public List<CurrencyRatesModel> getAvailableExchangeRatesForADate(String conversionDate);

    public Map<String, BigDecimal> getTotalAmountInEUR(BigDecimal amount, String forex, String conversionDate);

    public Set<String> getAllCurrencies();
}
