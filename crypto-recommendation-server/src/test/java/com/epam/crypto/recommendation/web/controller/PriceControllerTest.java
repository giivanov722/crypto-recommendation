package com.epam.crypto.recommendation.web.controller;

import com.epam.crypto.recommendation.service.PriceService;
import com.epam.crypto.recommendation.web.model.CryptoCurrencyPriceStatistic;
import com.epam.crypto.recommendation.web.model.Price;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PriceController.class)
public class PriceControllerTest {

    private static final String CURRENCY_SYMBOL = "ETH";

    private static final double OLDEST_PRICE = 0.1;
    private static final double NEWEST_PRICE = 0.2;
    private static final double MIN_PRICE = 0.0;
    private static final double MAX_PRICE = 0.5;
    private static final double PRICE = 1000.01;
    private static final long TIMESTAMP = 1223456;
    @MockBean
    private PriceService priceService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void populatePricesShouldReturnPopulatedPrices() throws Exception {
        MockMultipartFile multipartFileMock = new MockMultipartFile("file", "test.txt",
                "text/plain", "ETH".getBytes());
        Price dummyPrice = new Price(TIMESTAMP, CURRENCY_SYMBOL, PRICE);
        when(priceService.addPrices(multipartFileMock)).thenReturn(Collections.singletonList(dummyPrice));

        this.mockMvc.perform(multipart("/prices").file(multipartFileMock)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("[" + convertObjectToJsonString(dummyPrice) + "]"));

        verify(priceService).addPrices(multipartFileMock);
    }

    @Test
    void getPriceStatisticForCurrencyShouldReturnCurrencyPriceStatistic() throws Exception {
        int monthsBack = 2;
        CryptoCurrencyPriceStatistic dummyPriceStatistic = new CryptoCurrencyPriceStatistic(CURRENCY_SYMBOL, OLDEST_PRICE, NEWEST_PRICE, MIN_PRICE, MAX_PRICE);
        when(priceService.getPriceStatisticForTimePeriod(CURRENCY_SYMBOL, monthsBack)).thenReturn(dummyPriceStatistic);

        this.mockMvc.perform(get("/prices").param("cryptoSymbol", CURRENCY_SYMBOL).param("monthsBack", String.valueOf(monthsBack))).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json(convertObjectToJsonString(dummyPriceStatistic)));

        verify(priceService).getPriceStatisticForTimePeriod(CURRENCY_SYMBOL, monthsBack);
    }

    private String convertObjectToJsonString(Object body) throws JsonProcessingException {
        ObjectWriter writer = new ObjectMapper().writer();
        return writer.writeValueAsString(body);
    }


}
