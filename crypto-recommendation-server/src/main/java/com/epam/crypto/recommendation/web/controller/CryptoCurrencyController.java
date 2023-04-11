package com.epam.crypto.recommendation.web.controller;

import com.epam.crypto.recommendation.service.CryptoCurrencyService;
import com.epam.crypto.recommendation.web.model.CryptoCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CryptoCurrencyController {

    private CryptoCurrencyService currencyService;

    @Autowired
    public CryptoCurrencyController(CryptoCurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping("/currencies")
    public CryptoCurrency createCryptoCurrency(@RequestBody CryptoCurrency cryptoCurrency){
        return currencyService.addCryptoCurrency(cryptoCurrency);
    }

    @GetMapping("normalized/currencies")
    public List<CryptoCurrency> getAllPricesForCurrencySortedByNormalizationDescending() {
        return currencyService.getCryptoCurrenciesSortedByNormalizationDescending();
    }

    @GetMapping("normalized/max/currencies")
    public CryptoCurrency getMaxNormalizedRangeCryptoCurrencyForDate(@RequestParam(value = "year") String year, @RequestParam(value = "month") String month, @RequestParam(value = "day") String day) {
        return currencyService.getMaxNormalizedRangeCryptoCurrencyForDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
    }

}
