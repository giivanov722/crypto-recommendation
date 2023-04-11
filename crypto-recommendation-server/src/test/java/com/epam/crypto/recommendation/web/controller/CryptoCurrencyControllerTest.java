package com.epam.crypto.recommendation.web.controller;

import com.epam.crypto.recommendation.service.CryptoCurrencyService;
import com.epam.crypto.recommendation.web.model.CryptoCurrency;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CryptoCurrencyController.class)
public class CryptoCurrencyControllerTest {
    private static final String CURRENCY_SYMBOL = "ETH";
    @MockBean
    private CryptoCurrencyService cryptoCurrencyService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void createCryptoCurrencyShouldReturnTheCreatedCurrency() throws Exception {
        CryptoCurrency dummyCryptoCurrency = new CryptoCurrency(CURRENCY_SYMBOL);
        when(cryptoCurrencyService.addCryptoCurrency(Mockito.any(CryptoCurrency.class))).thenReturn(dummyCryptoCurrency);

        this.mockMvc.perform(post("/currencies").contentType(MediaType.APPLICATION_JSON).content(asJsonString(dummyCryptoCurrency))).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(convertObjectToJsonString(dummyCryptoCurrency)));

        verify(cryptoCurrencyService).addCryptoCurrency(Mockito.any(CryptoCurrency.class));
    }

    @Test
    void getAllPricesForCurrencySortedByNormalizationDescendingShouldReturnAllCurrenciesSortedByNormalization() throws Exception {
        CryptoCurrency dummyCryptoCurrency = new CryptoCurrency(CURRENCY_SYMBOL);
        when(cryptoCurrencyService.getCryptoCurrenciesSortedByNormalizationDescending()).thenReturn(Collections.singletonList(dummyCryptoCurrency));

        this.mockMvc.perform(get("/normalized/currencies")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("[" + convertObjectToJsonString(dummyCryptoCurrency) + "]"));

        verify(cryptoCurrencyService).getCryptoCurrenciesSortedByNormalizationDescending();
    }

    @Test
    void getMaxNormalizedRangeCryptoCurrencyForDateShouldReturnCurrencyWithMaxNormalizationValue() throws Exception {
        CryptoCurrency dummyCryptoCurrency = new CryptoCurrency(CURRENCY_SYMBOL);
        int year = 2023;
        int month = 12;
        int day = 12;

        when(cryptoCurrencyService.getMaxNormalizedRangeCryptoCurrencyForDate(year, month, day)).thenReturn(dummyCryptoCurrency);

        this.mockMvc.perform(get("/normalized/max/currencies").param("year", String.valueOf(year)).param("month", String.valueOf(month)).param("day", String.valueOf(day)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(convertObjectToJsonString(dummyCryptoCurrency)));

        verify(cryptoCurrencyService).getMaxNormalizedRangeCryptoCurrencyForDate(year, month, day);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String convertObjectToJsonString(Object body) throws JsonProcessingException {
        ObjectWriter writer = new ObjectMapper().writer();
        return writer.writeValueAsString(body);
    }

}
