package com.crewmeister.cmcodingchallenge.currency.service;

import com.crewmeister.cmcodingchallenge.currency.model.CreateForexModel;
import com.crewmeister.cmcodingchallenge.currency.model.CurrencyRatesModel;
import com.crewmeister.cmcodingchallenge.currency.repository.CurrencyRateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.crewmeister.cmcodingchallenge.currency.util.CurrencyRateConversionUtil.*;

@Service
@Slf4j
public class CurrencyRatesServiceImpl implements CurrencyRatesService{

    public static final int INDEX = 0;
    public static final String TOTAL_AMOUNT_IN_EUR = "totalAmountInEUR";
    @Autowired
    private CurrencyRateRepository currencyRateRepository;

    /**
     * @param createForexModel The model added by the Admin user to load the different currencies
     * @return The method creates the forex values in DB by making sure the dates and values are in the right format
     */

    @Override
    public boolean createNewCurrencyRates(CreateForexModel createForexModel) {
        String date = createForexModel.getDate();
        if (checkIfDateIsValid(date)
                && checkIfGivenForexNamesAreUnique(createForexModel)
                && checkIfConversionRatesAreValid(createForexModel)) {
            try {
                createForexModel.getForexValues().forEach(forexValueModel ->
                        currencyRateRepository.save(buildCurrencyRate(forexValueModel, date)));
                return true;
            } catch (Exception e) {
                log.error("Exception occurred while loading to DB ",e);
                return false;
            }

        }
        log.error("Either the date: {} is invalid or forexNames are Not Unique or the exchange rates are below 0",date);
        return false;
    }


    @Override
    public Set<String> getAllCurrencies(){
       return getCurrencyRateFromDB()
                .map(CurrencyRatesModel::getForexName)
                .collect(Collectors.toSet());
    }

    private Stream<CurrencyRatesModel> getCurrencyRateFromDB() {
        try {
            return currencyRateRepository.findAll()
                    .stream();
        }
        catch (Exception e){
            log.error("Unable to get Items from DB ",e);
            // Return empty Stream
            return Stream.empty();
        }
    }

    /**
     * The given methods provides all the available currencies across all days
     * @return model consisting of date to the forex name and value model
     */
    @Override
    public Map<String, List<CurrencyRatesModel>> getAllAvailableExchangeRates() {
        return getCurrencyRateFromDB()
                .collect(Collectors.groupingBy(CurrencyRatesModel::getConversionDate));
    }

    /**
     *
     * @param conversionDate The date on which the conversion information is needed
     * @return model consisting of date to the forex name and value model
     */
    @Override
    public List<CurrencyRatesModel> getAvailableExchangeRatesForADate(String conversionDate) {
        if(checkIfDateIsValid(conversionDate)) {
            return getCurrencyRateFromDB()
                    .filter(currencyRatesModel -> currencyRatesModel.getConversionDate().equals(conversionDate))
                    .collect(Collectors.toList());
        }
        log.error("Invalid format of Conversion Date {}",conversionDate);
        return Collections.emptyList();
    }

    /**
     *
     * @param amount that has to be converted to EUR
     * @param forex the base forex value like AUD or USD
     * @param conversionDate the date on which the value has to be converted
     * @return the EUR value of the given amount in the given currency
     */
    @Override
    public Map<String, BigDecimal> getTotalAmountInEUR(BigDecimal amount, String forex, String conversionDate) {
        // The given amount and dates are validated
        if(checkIfDateIsValid(conversionDate) && isValuePositive(amount)) {
            List<BigDecimal> collect = getCurrencyRateFromDB()
                    .filter(currencyRatesModel -> currencyRatesModel.getConversionDate().equals(conversionDate))
                    .filter(currencyRatesModel -> currencyRatesModel.getForexName().equals(forex))
                    .map(CurrencyRatesModel::getForexValue)
                    .collect(Collectors.toList());
            if(collect.isEmpty()){
                log.error("No data found for the given date {} and forex name {}",conversionDate,forex);
                return Collections.emptyMap();
            }
            // For a given conversion date and forex only one value will exist, so performing get at first index
            BigDecimal finalAmountInEUR = amount.multiply(collect.get(INDEX));
            return Map.of(TOTAL_AMOUNT_IN_EUR,finalAmountInEUR);
        }
        log.error("Invalid format of conversion date : {} or the amount is not positive : {}",conversionDate,amount);
        return Collections.emptyMap();
    }


}
