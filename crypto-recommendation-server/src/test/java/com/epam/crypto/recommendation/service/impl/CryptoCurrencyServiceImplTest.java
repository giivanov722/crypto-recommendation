package com.epam.crypto.recommendation.service.impl;

import com.epam.crypto.recommendation.data.entity.CryptoCurrencyEntity;
import com.epam.crypto.recommendation.data.repository.CryptoCurrencyRepository;
import com.epam.crypto.recommendation.exception.NoSuchDataException;
import com.epam.crypto.recommendation.service.CryptoCurrencyService;
import com.epam.crypto.recommendation.web.model.CryptoCurrency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import(CryptoCurrencyServiceImpl.class)
public class CryptoCurrencyServiceImplTest {

    private final String CURRENCY_SYMBOL = "ETH";
    private final int YEAR = 2023;
    private final int MONTH = 12;
    private final int DAY = 12;
    private final String DEFAULT_ZONE_ID = "UTC";

    @Autowired
    private CryptoCurrencyService cryptoCurrencyService;

    @MockBean
    private CryptoCurrencyRepository cryptoCurrencyRepository;


    @Test
    void addCryptoCurrencyShouldAddCurrencyIfDoesNotExist() {
        CryptoCurrency dummyCryptoCurrency = new CryptoCurrency(CURRENCY_SYMBOL);
        CryptoCurrencyEntity dummyCryptoCurrencyEntity = new CryptoCurrencyEntity(CURRENCY_SYMBOL);

        when(cryptoCurrencyRepository.existsById(dummyCryptoCurrency.getSymbol())).thenReturn(false);
        when(cryptoCurrencyRepository.save(Mockito.any(CryptoCurrencyEntity.class))).thenReturn(dummyCryptoCurrencyEntity);

        CryptoCurrency actual = cryptoCurrencyService.addCryptoCurrency(dummyCryptoCurrency);

        verify(cryptoCurrencyRepository).existsById(dummyCryptoCurrency.getSymbol());
        verify(cryptoCurrencyRepository).save(Mockito.any(CryptoCurrencyEntity.class));
        assertEquals(dummyCryptoCurrency.getSymbol(), actual.getSymbol());

    }

    @Test
    void addCryptoCurrencyShouldReturnExistingCurrencyIfAlreadyCreated() {
        CryptoCurrency dummyCryptoCurrency = new CryptoCurrency(CURRENCY_SYMBOL);
        CryptoCurrencyEntity dummyCryptoCurrencyEntity = new CryptoCurrencyEntity(CURRENCY_SYMBOL);
        Optional<CryptoCurrencyEntity> dummyOptionalCryptoCurrencyEntity = Optional.of(dummyCryptoCurrencyEntity);

        when(cryptoCurrencyRepository.existsById(dummyCryptoCurrency.getSymbol())).thenReturn(true);
        when(cryptoCurrencyRepository.findById(dummyCryptoCurrency.getSymbol())).thenReturn(dummyOptionalCryptoCurrencyEntity);

        CryptoCurrency actual = cryptoCurrencyService.addCryptoCurrency(dummyCryptoCurrency);

        verify(cryptoCurrencyRepository).existsById(dummyCryptoCurrency.getSymbol());
        verify(cryptoCurrencyRepository).findById(dummyCryptoCurrency.getSymbol());
        assertEquals(dummyCryptoCurrency.getSymbol(), actual.getSymbol());
    }

    @Test
    void getCryptoCurrenciesSortedByNormalizationDescendingShouldReturnCurrenciesIfDataIsAvailable() {
        Object[] dummyQueryResult = new Object[1];
        dummyQueryResult[0] = CURRENCY_SYMBOL;

        when(cryptoCurrencyRepository.getCryptoCurrenciesOrderedByNormalizedRangeDescending())
                .thenReturn(Collections.singletonList(dummyQueryResult));

        List<CryptoCurrency> actual = cryptoCurrencyService.getCryptoCurrenciesSortedByNormalizationDescending();

        verify(cryptoCurrencyRepository).getCryptoCurrenciesOrderedByNormalizedRangeDescending();
        assertFalse(actual.isEmpty());
        assertEquals(dummyQueryResult[0], actual.get(0).getSymbol());
    }

    @Test
    void getCryptoCurrenciesSortedByNormalizationDescendingShouldThrowExceptionIfDataIsNotAvailable() {
        when(cryptoCurrencyRepository.getCryptoCurrenciesOrderedByNormalizedRangeDescending())
                .thenReturn(Collections.emptyList());

        assertThrows(NoSuchDataException.class, () -> cryptoCurrencyService.getCryptoCurrenciesSortedByNormalizationDescending());
    }

    @Test
    void getMaxNormalizedRangeCryptoCurrencyShouldReturnCurrencyIfThereArePricesForTheDate() {
        long dateStartMillis = getStartOfTheDayInMillis();
        long dateEndMillis = getEndOfTheDayInMillis();
        Object[] dummyQueryResult = new Object[1];
        dummyQueryResult[0] = CURRENCY_SYMBOL;

        when(cryptoCurrencyRepository.getMaxNormalizedRangeCryptoCurrencyForTimestamps(dateStartMillis, dateEndMillis))
                .thenReturn(Collections.singletonList(dummyQueryResult));

        CryptoCurrency actual = cryptoCurrencyService.getMaxNormalizedRangeCryptoCurrencyForDate(YEAR, MONTH, DAY);

        verify(cryptoCurrencyRepository).getMaxNormalizedRangeCryptoCurrencyForTimestamps(dateStartMillis, dateEndMillis);
        assertEquals(CURRENCY_SYMBOL, actual.getSymbol());
    }

    @Test
    void getMaxNormalizedRangeCryptoCurrencyShouldThrowExceptionIfThereAreNoPricesForTheDate() {
        long dateStartMillis = getStartOfTheDayInMillis();
        long dateEndMillis = getEndOfTheDayInMillis();

        when(cryptoCurrencyRepository.getMaxNormalizedRangeCryptoCurrencyForTimestamps(dateStartMillis, dateEndMillis))
                .thenReturn(Collections.emptyList());

        assertThrows(NoSuchDataException.class, () -> cryptoCurrencyService.getMaxNormalizedRangeCryptoCurrencyForDate(YEAR, MONTH, DAY));
    }

    @Test
    void isCryptoCurrencyExistentShouldReturnTrueIfEntityDoesExist() {
        when(cryptoCurrencyRepository.existsById(CURRENCY_SYMBOL))
                .thenReturn(true);
        assertTrue(cryptoCurrencyService.isCryptoCurrencyExistent(CURRENCY_SYMBOL));
    }

    @Test
    void isCryptoCurrencyExistentShouldReturnFalseIfEntityDoesNotExist() {
        when(cryptoCurrencyRepository.existsById(CURRENCY_SYMBOL))
                .thenReturn(false);
        assertFalse(cryptoCurrencyService.isCryptoCurrencyExistent(CURRENCY_SYMBOL));
    }


    private long getStartOfTheDayInMillis() {
        return LocalDate.of(YEAR, MONTH, DAY).atStartOfDay().atZone(ZoneId.of(DEFAULT_ZONE_ID)).toInstant().toEpochMilli();
    }

    private long getEndOfTheDayInMillis() {
        return LocalDate.of(YEAR, MONTH, DAY).atTime(LocalTime.MAX).atZone(ZoneId.of(DEFAULT_ZONE_ID)).toInstant().toEpochMilli();
    }
}
