package com.crewmeister.cmcodingchallenge.currency.util;

import com.crewmeister.cmcodingchallenge.currency.model.CreateForexModel;
import com.crewmeister.cmcodingchallenge.currency.model.CurrencyRatesModel;
import com.crewmeister.cmcodingchallenge.currency.model.ForexValueModel;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public  class CurrencyRateConversionUtil {
    public static CurrencyRatesModel buildCurrencyRate(ForexValueModel forexValueModel, String date) {
        return CurrencyRatesModel.builder()
                .conversionDate(date)
                .forexName(forexValueModel.getForexName().toUpperCase())
                .forexValue(forexValueModel.getValue())
                .build();
    }

    public static boolean checkIfDateIsValid(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        simpleDateFormat.setLenient(false);
        try {
            simpleDateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            log.error("Given Date is not in a valid format {} ",date,e);
            return false;
        }
    }

    public static boolean checkIfGivenForexNamesAreUnique(CreateForexModel createForexModel) {
        List<String> forexNameList = createForexModel.getForexValues()
                .stream()
                .map(forexValueModel -> forexValueModel.getForexName().toUpperCase())
                .collect(Collectors.toList());
        Set<String> forexNameSet = createForexModel.getForexValues()
                .stream()
                .map(forexValueModel -> forexValueModel.getForexName().toUpperCase())
                .collect(Collectors.toSet());
        return forexNameSet.size() == forexNameList.size();
    }
    public static boolean checkIfConversionRatesAreValid(CreateForexModel createForexModel){
        Optional<ForexValueModel> negativeValueOptional = createForexModel
                .getForexValues()
                .stream()
                .filter(forexValueModel -> !isValuePositive(forexValueModel.getValue()))
                .findFirst();
        return !negativeValueOptional.isPresent();
    }

    public static boolean isValuePositive(BigDecimal valueToBeCompared) {
        return valueToBeCompared.compareTo(new BigDecimal(0)) > 0;
    }
}
