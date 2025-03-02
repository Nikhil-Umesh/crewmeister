package com.crewmeister.cmcodingchallenge.currency.controller;

import com.crewmeister.cmcodingchallenge.CmCodingChallengeApplication;
import com.crewmeister.cmcodingchallenge.currency.model.CreateForexModel;
import com.crewmeister.cmcodingchallenge.currency.model.CurrencyRatesModel;
import com.crewmeister.cmcodingchallenge.currency.service.CurrencyRatesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = CmCodingChallengeApplication.class)
@AutoConfigureMockMvc
class CurrencyControllerTest {
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    @MockBean
    private CurrencyRatesService currencyRatesService;

    @Test
    public void testGetCurrency() throws Exception {
        when(currencyRatesService.getAllCurrencies()).thenReturn(Set.of("USD", "INR"));
        mvc.perform(get("/api/v1/all-currencies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(String.valueOf(Set.of("USD", "INR"))));
    }

    @Test
    public void testGetCurrencyReturnsEmpty() throws Exception {
        when(currencyRatesService.getAllCurrencies()).thenReturn(Set.of());
        mvc.perform(get("/api/v1/all-currencies")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(content().json(String.valueOf(Set.of())));
    }

    @Test
    public void testCreateCurrenciesSuccess() throws Exception {
        when(currencyRatesService.createNewCurrencyRates(CreateForexModel.builder().build())).thenReturn(true);
        mvc.perform(post("/api/v1/create/currencyRates").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(CreateForexModel.builder().build())))
                .andExpect(status().is(201))
                .andExpect(content().string("The new currency rates created Successfully"));
    }

    @Test
    public void testCreateCurrenciesFails() throws Exception {
        when(currencyRatesService.createNewCurrencyRates(CreateForexModel.builder().build())).thenReturn(false);
        mvc.perform(post("/api/v1/create/currencyRates").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(CreateForexModel.builder().build())))
                .andExpect(status().is(400))
                .andExpect(content().string("The currency rates failed to create. Please check the date format is in dd-mm-yyyy format and forex values "));
    }

    @Test
    public void testGetAllAvailableForex() throws Exception {
        Map<String, List<CurrencyRatesModel>> forexModel = Map.of("03-05-2024", List.of(CurrencyRatesModel.builder().conversionDate("03-05-2024").forexName("INR").forexValue(new BigDecimal("0.1")).build()), "02-05-2024", List.of(CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("INR").forexValue(new BigDecimal("0.2")).build(), CurrencyRatesModel.builder().conversionDate("02-05-2024").forexName("USD").forexValue(new BigDecimal(1)).build()));
        when(currencyRatesService.getAllAvailableExchangeRates()).thenReturn(forexModel);
        mvc.perform(get("/api/v1/forex-rates-across-all-days").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(forexModel)));
    }

    @Test
    public void testGetAllAvailableForexReturnsEmpty() throws Exception {
        when(currencyRatesService.getAllAvailableExchangeRates()).thenReturn(Collections.emptyMap());
        mvc.perform(get("/api/v1/forex-rates-across-all-days").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyMap())));
    }

    @Test
    public void testGetAllAvailableForexForAGivenDate() throws Exception {
        List<CurrencyRatesModel> currencyRatesModelList = List.of(CurrencyRatesModel.builder().conversionDate("02-03-2024").forexValue(new BigDecimal(5)).forexName("INR").build());
        when(currencyRatesService.getAvailableExchangeRatesForADate("02-03-2024")).thenReturn(currencyRatesModelList);
        mvc.perform(get("/api/v1/forex-rates-for/02-03-2024").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(currencyRatesModelList)));
    }

    @Test
    public void testGetAllAvailableForexForAGivenDateReturnsEmpty() throws Exception {
        when(currencyRatesService.getAvailableExchangeRatesForADate("02-03-2024")).thenReturn(Collections.emptyList());
        mvc.perform(get("/api/v1/forex-rates-for/02-03-2024").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(content().string("No data found for the given date please check the format of date is in dd-mm-yyyy"));
    }

    @Test
    public void testCalculateAmountInEURForForexAndGivenDate() throws Exception {
        when(currencyRatesService.getTotalAmountInEUR(new BigDecimal("100"),"INR","02-03-2024")).thenReturn(Map.of("total Amount in EUR",new BigDecimal(1000)));
        mvc.perform(get("/api/v1/calculate-amount-in-EUR/for-amount/100/forex/INR/date/02-03-2024").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Map.of("total Amount in EUR",new BigDecimal(1000)))));
    }

    @Test
    public void testCalculateAmountInEURForForexAndGivenDateNotFound() throws Exception {
        when(currencyRatesService.getTotalAmountInEUR(new BigDecimal("100"),"INR","02-03-2024")).thenReturn(Collections.emptyMap());
        mvc.perform(get("/api/v1/calculate-amount-in-EUR/for-amount/100/forex/INR/date/02-03-2024").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(content().string("No data found for the given date please check the format of date is in dd-mm-yyyy and forex is present and the amount is more than 0"));
    }

}