package com.epam.crypto.recommendation.service.impl;

import com.epam.crypto.recommendation.data.entity.CryptoCurrencyEntity;
import com.epam.crypto.recommendation.data.entity.PriceEntity;
import com.epam.crypto.recommendation.data.repository.CryptoCurrencyRepository;
import com.epam.crypto.recommendation.data.repository.PriceRepository;
import com.epam.crypto.recommendation.exception.NoSuchDataException;
import com.epam.crypto.recommendation.service.CryptoCurrencyService;
import com.epam.crypto.recommendation.service.PriceService;
import com.epam.crypto.recommendation.service.importer.CurrencyPriceImporter;
import com.epam.crypto.recommendation.web.model.CryptoCurrencyPriceStatistic;
import com.epam.crypto.recommendation.web.model.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import(PriceServiceImpl.class)
public class PriceServiceImplTest {

    private static final String CURRENCY_SYMBOL = "ETH";
    private static final double OLDEST_PRICE = 0.1;
    private static final double NEWEST_PRICE = 0.2;
    private static final double MIN_PRICE = 0.0;
    private static final double MAX_PRICE = 0.5;
    private static final double PRICE = 1000.01;
    private static final long TIMESTAMP = 1223456;


    @Autowired
    private PriceService priceService;
    @MockBean
    private CryptoCurrencyRepository cryptoCurrencyRepository;
    @MockBean
    private CurrencyPriceImporter currencyPriceImporter;
    @MockBean
    private CryptoCurrencyService cryptoCurrencyService;
    @MockBean
    private PriceRepository priceRepository;



    @Test
    void addPricesShouldSavePricesIfCurrencyExists() {
        MultipartFile multipartFileMock = Mockito.mock(MultipartFile.class);
        Price dummyPrice = new Price(TIMESTAMP, CURRENCY_SYMBOL, PRICE);
        CryptoCurrencyEntity currencyEntityMock = Mockito.mock(CryptoCurrencyEntity.class);
        PriceEntity priceEntity = new PriceEntity(TIMESTAMP, currencyEntityMock, PRICE);

        when(currencyPriceImporter.getCurrenciesFromFile(multipartFileMock)).thenReturn(Collections.singletonList(dummyPrice));
        when(cryptoCurrencyService.isCryptoCurrencyExistent(CURRENCY_SYMBOL)).thenReturn(true);
        when(cryptoCurrencyRepository.getReferenceById(CURRENCY_SYMBOL)).thenReturn(currencyEntityMock);
        when(priceRepository.saveAll(Mockito.anyList())).thenReturn(Collections.singletonList(priceEntity));
        when(currencyEntityMock.getSymbol()).thenReturn(CURRENCY_SYMBOL);

        List<Price> actual = priceService.addPrices(multipartFileMock);

        verify(currencyPriceImporter).getCurrenciesFromFile(multipartFileMock);
        verify(cryptoCurrencyService).isCryptoCurrencyExistent(CURRENCY_SYMBOL);
        verify(cryptoCurrencyRepository).getReferenceById(CURRENCY_SYMBOL);
        verify(priceRepository).saveAll(Mockito.anyList());
        assertEquals(CURRENCY_SYMBOL, actual.get(0).getCurrencySymbol());
        assertEquals(TIMESTAMP, actual.get(0).getTimestamp());
        assertEquals(PRICE, actual.get(0).getValue());

    }

    @Test
    void addPricesShouldThrowExceptionIfCurrencyDoesNotExist() {
        MultipartFile multipartFileMock = Mockito.mock(MultipartFile.class);
        Price dummyPrice = new Price(TIMESTAMP, CURRENCY_SYMBOL, PRICE);

        when(currencyPriceImporter.getCurrenciesFromFile(multipartFileMock)).thenReturn(Collections.singletonList(dummyPrice));
        when(cryptoCurrencyRepository.existsById(CURRENCY_SYMBOL)).thenReturn(false);

        assertThrows(NoSuchDataException.class, () -> priceService.addPrices(multipartFileMock));
    }

    @Test
    void getPriceStatisticForTimePeriodShouldReturnCryptoStatisticIfDataExists() {
        int monthsBack = 2;
        Object[] dummyQueryObject = getDummyQueryObject();

        when(cryptoCurrencyRepository.existsById(CURRENCY_SYMBOL)).thenReturn(true);
        when(priceRepository.getCryptoCurrencyStatisticForTimePeriod(Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Collections.singletonList(dummyQueryObject));

        CryptoCurrencyPriceStatistic actual = priceService.getPriceStatisticForTimePeriod(CURRENCY_SYMBOL, monthsBack);

        verify(cryptoCurrencyRepository).existsById(CURRENCY_SYMBOL);
        verify(priceRepository).getCryptoCurrencyStatisticForTimePeriod(Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong());
        assertEquals(dummyQueryObject[0], actual.getSymbol());
        assertEquals(dummyQueryObject[1], actual.getOldestPrice());
        assertEquals(dummyQueryObject[2], actual.getNewestPrice());
        assertEquals(dummyQueryObject[3], actual.getMinimumPrice());
        assertEquals(dummyQueryObject[4], actual.getMaximumPrice());

    }

    @Test
    void getPriceStatisticForTimePeriodShouldThrowExceptionIfCurrencyDoesNotExist() {
        int monthsBack = 2;
        when(cryptoCurrencyRepository.existsById(CURRENCY_SYMBOL)).thenReturn(false);

        assertThrows(NoSuchDataException.class, () -> priceService.getPriceStatisticForTimePeriod(CURRENCY_SYMBOL, monthsBack));
    }

    @Test
    void getPriceStatisticForTimePeriodShouldThrowExceptionIfDataForTimePeriodDoesNotExist() {
        int monthsBack = 2;
        when(cryptoCurrencyRepository.existsById(CURRENCY_SYMBOL)).thenReturn(false);

        when(priceRepository.getCryptoCurrencyStatisticForTimePeriod(Mockito.anyString(),Mockito.anyLong(),Mockito.anyLong()))
                .thenReturn(Collections.emptyList());

        assertThrows(NoSuchDataException.class, () -> priceService.getPriceStatisticForTimePeriod(CURRENCY_SYMBOL, monthsBack));
    }


    private static Object[] getDummyQueryObject() {
        Object[] queryResultObject = new Object[5];
        queryResultObject[0] = CURRENCY_SYMBOL;
        queryResultObject[1] = OLDEST_PRICE;
        queryResultObject[2] = NEWEST_PRICE;
        queryResultObject[3] = MIN_PRICE;
        queryResultObject[4] = MAX_PRICE;
        return queryResultObject;
    }

}
