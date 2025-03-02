package com.crewmeister.cmcodingchallenge.currency.service;

import com.crewmeister.cmcodingchallenge.currency.model.CreateForexModel;
import com.crewmeister.cmcodingchallenge.currency.model.CurrencyRatesModel;
import com.crewmeister.cmcodingchallenge.currency.model.ForexValueModel;
import com.crewmeister.cmcodingchallenge.currency.repository.CurrencyRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CurrencyRatesServiceTest {
    @Mock
    private CurrencyRateRepository currencyRateRepository;
    @InjectMocks
    private CurrencyRatesServiceImpl currencyRatesService;

    @Test
    public void testCreateNewCurrencyRatesWhenInvalidMonthIsGiven() {
        assertFalse(currencyRatesService.createNewCurrencyRates(CreateForexModel.builder().date("24-22-2001").build()));
    }

    @Test
    public void testCreateNewCurrencyRatesWhenInvalidDateIsGiven() {
        assertFalse(currencyRatesService.createNewCurrencyRates(CreateForexModel.builder().date("42-02-2001").build()));
    }

    @Test
    public void testCreateNewCurrencyRatesWhenInvalidDateFormatIsGiven() {
        assertFalse(currencyRatesService.createNewCurrencyRates(CreateForexModel.builder().date("24/02/2024").build()));
    }

    @Test
    public void testCreateNewCurrencyRatesWhenForexIsDuplicate() {
        assertFalse(currencyRatesService.createNewCurrencyRates(CreateForexModel.builder().date("24-02-2024").forexValues(List.of(ForexValueModel.builder().forexName("INR").build(), ForexValueModel.builder().forexName("INR").build())).build()));
    }

    @Test
    public void testCreateNewCurrencyRatesWhenForexValueIsInvalid() {
        assertFalse(currencyRatesService.createNewCurrencyRates(CreateForexModel.builder().date("24-02-2024").forexValues(List.of(ForexValueModel.builder().forexName("USD").value(new BigDecimal(-1)).build())).build()));

    }

    @Test
    public void testCreateNewCurrencyRatesWhenInsertionFails() {
        when(currencyRateRepository.save(any())).thenThrow(new IllegalStateException("Can not connect To DB"));
        assertFalse(currencyRatesService.createNewCurrencyRates(CreateForexModel.builder().date("24-02-2024").forexValues(Collections.singletonList(ForexValueModel.builder().forexName("INR").value(new BigDecimal(2)).build())).build()));

    }

    @Test
    public void testCreateNewCurrencyRatesWhenInsertedSuccessfully() {
        assertTrue(currencyRatesService.createNewCurrencyRates(CreateForexModel.builder().date("24-02-2024").forexValues(Collections.singletonList(ForexValueModel.builder().forexName("INR").value(new BigDecimal(2)).build())).build()));
    }

    @Test
    public void testGetAllCurrencies(){
        when(currencyRateRepository.findAll()).thenReturn(List.of(CurrencyRatesModel.builder().conversionDate("02-005-2024").forexName("INR").build(),
                CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("USD").build(),
                CurrencyRatesModel.builder().conversionDate("03-05-2024").forexName("INR").build()));
        assertEquals(currencyRatesService.getAllCurrencies(), Set.of("USD","INR"));
    }

    @Test
    public void testGetAllCurrenciesWhenNoCurrencyIsPresent(){
        when(currencyRateRepository.findAll()).thenReturn(List.of());
        assertEquals(currencyRatesService.getAllCurrencies(), Set.of());
    }

    @Test
    public void testGetAllCurrenciesWhenDBCallFails(){
        when(currencyRateRepository.findAll()).thenThrow(new IllegalStateException("Unable to connect To DB"));
        assertEquals(currencyRatesService.getAllCurrencies(), Set.of());
    }

    @Test
    public void testGetAllAvailableExchangeRate(){
        when(currencyRateRepository.findAll()).thenReturn(List.of(CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("INR").forexValue(new BigDecimal("0.2")).build(),
                CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("USD").forexValue(new BigDecimal(1)).build(),
                CurrencyRatesModel.builder().conversionDate("03-05-2024").forexName("INR").forexValue(new BigDecimal("0.1")).build()));
        assertEquals(currencyRatesService.getAllAvailableExchangeRates(),  Map.of(
                        "03-05-2024",List.of(CurrencyRatesModel.builder().conversionDate("03-05-2024").forexName("INR").forexValue(new BigDecimal("0.1")).build()),
                "02-05-2024",List.of(CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("INR").forexValue(new BigDecimal("0.2")).build(),
                CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("USD").forexValue(new BigDecimal(1)).build())));
    }

    @Test
    public void testGetAllAvailableExchangeRateReturnEmpty(){
        when(currencyRateRepository.findAll()).thenReturn(List.of());
        assertEquals(currencyRatesService.getAllAvailableExchangeRates(),Map.of());
    }

    @Test
    public void testGetAllAvailableExchangeRateDbFailed(){
        when(currencyRateRepository.findAll()).thenThrow(new IllegalStateException("Unable to connect To DB"));
        assertEquals(currencyRatesService.getAllAvailableExchangeRates(),Map.of());
    }

    @Test
    public void testGetAllAvailableExchangeRateForAGivenDate(){
        when(currencyRateRepository.findAll()).thenReturn(List.of(CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("INR").forexValue(new BigDecimal("0.2")).build(),
                CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("USD").forexValue(new BigDecimal(1)).build(),
                CurrencyRatesModel.builder().conversionDate("03-05-2024").forexName("INR").forexValue(new BigDecimal("0.1")).build()));
        assertEquals(currencyRatesService.getAvailableExchangeRatesForADate("02-05-2024"),  List.of(CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("INR").forexValue(new BigDecimal("0.2")).build(),
                        CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("USD").forexValue(new BigDecimal(1)).build()));
    }

    @Test
    public void testGetAllAvailableExchangeRateForAGivenDateWhenDBCallFails(){
        when(currencyRateRepository.findAll()).thenThrow(new IllegalStateException("Unable to connect To DB"));
        assertEquals(currencyRatesService.getAvailableExchangeRatesForADate("02-05-2024"), List.of());
    }

    @Test
    public void testGetAllAvailableExchangeRateForAGivenDateIsInvalid(){
        assertEquals(currencyRatesService.getAvailableExchangeRatesForADate("02-15-2024"),  List.of());
    }

    @Test
    public void testGetAllAvailableExchangeRateForAGivenDateIsNotPresent(){
        when(currencyRateRepository.findAll()).thenReturn(List.of(CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("INR").forexValue(new BigDecimal("0.2")).build(),
                CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("USD").forexValue(new BigDecimal(1)).build(),
                CurrencyRatesModel.builder().conversionDate("03-05-2024").forexName("INR").forexValue(new BigDecimal("0.1")).build()));
        assertEquals(currencyRatesService.getAvailableExchangeRatesForADate("02-06-2024"),  List.of());
    }

    @Test
    public void testGivenAmountInEURWithForexAndDateMentioned(){
        when(currencyRateRepository.findAll()).thenReturn(List.of(CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("INR").forexValue(new BigDecimal("0.2")).build(),
                CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("USD").forexValue(new BigDecimal("0.72")).build(),
                CurrencyRatesModel.builder().conversionDate("03-05-2024").forexName("INR").forexValue(new BigDecimal("0.1")).build()));
        assertEquals(currencyRatesService.getTotalAmountInEUR(new BigDecimal(100),"USD","02-05-2024"),  Map.of("totalAmountInEUR",new BigDecimal("72.00")));
    }

    @Test
    public void testGivenAmountInEURWithForexAndDateMentionedWithInvalidAmount(){
        assertEquals(currencyRatesService.getTotalAmountInEUR(new BigDecimal(-100),"USD","02-05-2024"),  Map.of());
    }

    @Test
    public void testGivenAmountInEURWithForexAndDateMentionedWithInvalidDate(){
        assertEquals(currencyRatesService.getTotalAmountInEUR(new BigDecimal(100),"USD","02-15-2024"),  Map.of());
    }

    @Test
    public void testGivenAmountInEURWithForexAndDateMentionedWhenForexNotExisting(){
        when(currencyRateRepository.findAll()).thenReturn(List.of(CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("INR").forexValue(new BigDecimal("0.2")).build(),
                CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("USD").forexValue(new BigDecimal("0.72")).build(),
                CurrencyRatesModel.builder().conversionDate("03-05-2024").forexName("INR").forexValue(new BigDecimal("0.1")).build()));
        assertEquals(currencyRatesService.getTotalAmountInEUR(new BigDecimal(100),"AUD","02-05-2024"),  Map.of());
    }

    @Test
    public void testGivenAmountInEURWithForexAndDateMentionedWhenDateNotExisting(){
        when(currencyRateRepository.findAll()).thenReturn(List.of(CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("INR").forexValue(new BigDecimal("0.2")).build(),
                CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("USD").forexValue(new BigDecimal("0.72")).build(),
                CurrencyRatesModel.builder().conversionDate("03-05-2024").forexName("INR").forexValue(new BigDecimal("0.1")).build()));
        assertEquals(currencyRatesService.getTotalAmountInEUR(new BigDecimal(100),"UUD","02-06-2024"),  Map.of());
    }

    @Test
    public void testGivenAmountInEURWithForexAndDateMentionedWhenDBCallFails(){
        when(currencyRateRepository.findAll()).thenThrow(new IllegalStateException("Unable to connect To DB"));
        assertEquals(currencyRatesService.getTotalAmountInEUR(new BigDecimal(100),"UUD","02-06-2024"),  Map.of());
    }
}