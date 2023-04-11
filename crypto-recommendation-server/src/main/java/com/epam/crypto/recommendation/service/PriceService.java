package com.epam.crypto.recommendation.service;

import com.epam.crypto.recommendation.web.model.CryptoCurrencyPriceStatistic;
import com.epam.crypto.recommendation.web.model.Price;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PriceService {

    List<Price> addPrices(MultipartFile file);

    CryptoCurrencyPriceStatistic getPriceStatisticForTimePeriod(String cryptoCurrencySymbol, int monthsBack);
}
