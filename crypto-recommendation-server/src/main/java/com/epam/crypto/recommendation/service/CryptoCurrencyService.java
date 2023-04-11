package com.epam.crypto.recommendation.service;

import com.epam.crypto.recommendation.web.model.CryptoCurrency;

import java.util.List;

public interface CryptoCurrencyService {

    CryptoCurrency addCryptoCurrency(CryptoCurrency cryptoCurrency);

    List<CryptoCurrency> getCryptoCurrenciesSortedByNormalizationDescending();

    CryptoCurrency getMaxNormalizedRangeCryptoCurrencyForDate(int year, int month, int day);

    boolean isCryptoCurrencyExistent(String cryptoCurrencySymbol);
}
