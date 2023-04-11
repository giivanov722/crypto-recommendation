package com.epam.crypto.recommendation.web.controller;

import com.epam.crypto.recommendation.service.PriceService;
import com.epam.crypto.recommendation.web.model.CryptoCurrencyPriceStatistic;
import com.epam.crypto.recommendation.web.model.Price;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class PriceController {

    private PriceService priceService;


    @Autowired
    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @PostMapping("/prices")
    public List<Price> populatePrices(@RequestParam("file") MultipartFile cryptoCurrencyPricesFile){
        return priceService.addPrices(cryptoCurrencyPricesFile);
    }

    @GetMapping("/prices")
    public CryptoCurrencyPriceStatistic getPriceStatisticForCurrency(@RequestParam(value = "cryptoSymbol") String cryptoSymbol, @RequestParam(value = "monthsBack") String monthsBack) {
        return priceService.getPriceStatisticForTimePeriod(cryptoSymbol, Integer.parseInt(monthsBack));
    }

}
