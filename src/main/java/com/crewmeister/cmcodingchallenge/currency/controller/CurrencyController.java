package com.crewmeister.cmcodingchallenge.currency.controller;

import com.crewmeister.cmcodingchallenge.currency.CurrencyConversionRates;
import com.crewmeister.cmcodingchallenge.currency.model.CreateForexModel;
import com.crewmeister.cmcodingchallenge.currency.model.CurrencyRatesModel;

import com.crewmeister.cmcodingchallenge.currency.service.CurrencyRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController()
@RequestMapping("/api")
public class CurrencyController {
    @Autowired
    private CurrencyRatesService currencyRatesService;

    /**
     * @param createForexModel The Model for loading the currency conversion in DB
     * @return The response based on whether the creation was successful or not
     */
    @PostMapping("v1/create/currencyRates")
    public ResponseEntity<String> createCurrencyRates(@RequestBody CreateForexModel createForexModel) {
        boolean isNewCurrencyRatesCreated = currencyRatesService.createNewCurrencyRates(createForexModel);
        if (isNewCurrencyRatesCreated) {
            return new ResponseEntity<>("The new currency rates created Successfully", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("The currency rates failed to create. Please check the date format is in dd-mm-yyyy format and forex values ", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("currencies")
    public ResponseEntity<ArrayList<CurrencyConversionRates>> getCurrencies() {
        ArrayList<CurrencyConversionRates> currencyConversionRates = new ArrayList<CurrencyConversionRates>();
        currencyConversionRates.add(new CurrencyConversionRates(2.5));

        return new ResponseEntity<ArrayList<CurrencyConversionRates>>(currencyConversionRates, HttpStatus.OK);
    }

    /**
     * @return all available currencies combined across all days
     */
    @GetMapping("v1/all-currencies")
    public ResponseEntity<Set<String>> getAllAvailableCurrencies() {
        return new ResponseEntity<>(currencyRatesService.getAllCurrencies(), HttpStatus.OK);
    }

    /**
     * @return gets forex names and values for all available dates
     */
    @GetMapping("v1/forex-rates-across-all-days")
    public ResponseEntity<Map<String, List<CurrencyRatesModel>>> getAllAvailableForexRates() {
        return new ResponseEntity<>(currencyRatesService.getAllAvailableExchangeRates(), HttpStatus.OK);
    }

    /**
     * @param conversionDate the date on which the forex needs to be retried
     * @return fetches all the available forex and its values for a given date
     */
    @GetMapping("v1/forex-rates-for/{conversionDate}")
    public ResponseEntity<?> getAllAvailableForexRatesForADate(@PathVariable String conversionDate) {
        List<CurrencyRatesModel> availableExchangeRatesForADate = currencyRatesService.getAvailableExchangeRatesForADate(conversionDate);
        if (!availableExchangeRatesForADate.isEmpty()) {
            return new ResponseEntity<>(availableExchangeRatesForADate, HttpStatus.OK);
        }
        return new ResponseEntity<>("No data found for the given date please check the format of date is in dd-mm-yyyy", HttpStatus.BAD_REQUEST);
    }

    /**
     * @param amount         the amount given by the user for conversion
     * @param forex          the currency which has to be converted to EUR
     * @param conversionDate the date on which the conversion has to be done
     * @return calculates the rate in EUR for given amount for given forex on a particular day
     */
    @GetMapping("v1/calculate-amount-in-EUR/for-amount/{amount}/forex/{forex}/date/{conversionDate}")
    public ResponseEntity<?> getTotalAmount(@PathVariable BigDecimal amount,
                                            @PathVariable String forex,
                                            @PathVariable String conversionDate) {
        Map<String, BigDecimal> availableExchangeRatesForADate = currencyRatesService.getTotalAmountInEUR(amount, forex, conversionDate);
        if (!availableExchangeRatesForADate.isEmpty()) {
            return new ResponseEntity<>(availableExchangeRatesForADate, HttpStatus.OK);
        }
        return new ResponseEntity<>("No data found for the given date please check the format of date is in dd-mm-yyyy and forex is present and the amount is more than 0", HttpStatus.BAD_REQUEST);
    }
}