package com.epam.crypto.recommendation.service.impl;

import com.epam.crypto.recommendation.data.entity.CryptoCurrencyEntity;
import com.epam.crypto.recommendation.data.repository.CryptoCurrencyRepository;
import com.epam.crypto.recommendation.exception.NoSuchDataException;
import com.epam.crypto.recommendation.service.CryptoCurrencyService;
import com.epam.crypto.recommendation.web.model.CryptoCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class CryptoCurrencyServiceImpl implements CryptoCurrencyService {

    private static final String DEFAULT_ZONE_ID = "UTC";
    private CryptoCurrencyRepository cryptoCurrencyRepository;

    @Autowired
    public CryptoCurrencyServiceImpl(CryptoCurrencyRepository cryptoCurrencyRepository) {
        this.cryptoCurrencyRepository = cryptoCurrencyRepository;
    }

    @Override
    public CryptoCurrency addCryptoCurrency(CryptoCurrency cryptoCurrency) {
        if (cryptoCurrencyRepository.existsById(cryptoCurrency.getSymbol())){
            CryptoCurrencyEntity currencyEntity = cryptoCurrencyRepository.findById(cryptoCurrency.getSymbol()).get();
            return generateCryptoCurrency(currencyEntity);
        }
        return generateCryptoCurrency(cryptoCurrencyRepository.save(generateCryptoCurrencyEntity(cryptoCurrency)));
    }

    @Override
    public List<CryptoCurrency> getCryptoCurrenciesSortedByNormalizationDescending() {
        List<Object[]> queryResult = cryptoCurrencyRepository.getCryptoCurrenciesOrderedByNormalizedRangeDescending();
        if (queryResult.isEmpty()) {
            throw new NoSuchDataException("There are no crypto currencies available");
        }
        return queryResult.stream().map(this::generateCryptoCurrency).toList();
    }

    @Override
    public CryptoCurrency getMaxNormalizedRangeCryptoCurrencyForDate(int year, int month, int day) {
        long dateStartMillis = LocalDate.of(year, month, day).atStartOfDay().atZone(ZoneId.of(DEFAULT_ZONE_ID)).toInstant().toEpochMilli();
        long dateEndMillis = LocalDate.of(year, month, day).atTime(LocalTime.MAX).atZone(ZoneId.of(DEFAULT_ZONE_ID)).toInstant().toEpochMilli();

        List<Object[]> queryResult = cryptoCurrencyRepository.getMaxNormalizedRangeCryptoCurrencyForTimestamps(dateStartMillis, dateEndMillis);
        if (queryResult.isEmpty()) {
            throw new NoSuchDataException("There are no price entries for the specified date");
        }

        return generateCryptoCurrency(queryResult.get(0));
    }

    @Override
    public boolean isCryptoCurrencyExistent(String cryptoCurrencySymbol) {
        return cryptoCurrencyRepository.existsById(cryptoCurrencySymbol);
    }

    private CryptoCurrency generateCryptoCurrency(Object[] queryResult) {
        CryptoCurrency cryptoCurrency = new CryptoCurrency();
        cryptoCurrency.setSymbol((String) queryResult[0]);
        return cryptoCurrency;
    }

    private CryptoCurrency generateCryptoCurrency(CryptoCurrencyEntity cryptoCurrencyEntity) {
        CryptoCurrency cryptoCurrency = new CryptoCurrency();
        cryptoCurrency.setSymbol(cryptoCurrencyEntity.getSymbol());
        return cryptoCurrency;
    }

    private CryptoCurrencyEntity generateCryptoCurrencyEntity(CryptoCurrency cryptoCurrency) {
        CryptoCurrencyEntity cryptoCurrencyEntity= new CryptoCurrencyEntity();
        cryptoCurrencyEntity.setSymbol(cryptoCurrency.getSymbol());
        return cryptoCurrencyEntity;
    }

}
