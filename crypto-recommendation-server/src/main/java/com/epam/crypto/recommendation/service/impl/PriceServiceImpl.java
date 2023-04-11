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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class PriceServiceImpl implements PriceService {

    private static final String DEFAULT_ZONE_ID = "UTC";
    private CurrencyPriceImporter currencyPriceImporter;
    private CryptoCurrencyService cryptoCurrencyService;
    private CryptoCurrencyRepository cryptoCurrencyRepository;
    private PriceRepository priceRepository;

    @Autowired
    public PriceServiceImpl(CurrencyPriceImporter currencyPriceImporter, CryptoCurrencyService cryptoCurrencyService, CryptoCurrencyRepository currencyRepository, PriceRepository priceRepository) {
        this.currencyPriceImporter = currencyPriceImporter;
        this.cryptoCurrencyService = cryptoCurrencyService;
        this.cryptoCurrencyRepository = currencyRepository;
        this.priceRepository = priceRepository;
    }

    @Override
    public List<Price> addPrices(MultipartFile file) {
        List<Price> prices = currencyPriceImporter.getCurrenciesFromFile(file);
        String cryptoCurrencySymbolForPrices = prices.get(0).getCurrencySymbol();
        if (!cryptoCurrencyService.isCryptoCurrencyExistent(cryptoCurrencySymbolForPrices)) {
            throw new NoSuchDataException("There is no available crypto currency with symbol: " + cryptoCurrencySymbolForPrices);
        }
        List<PriceEntity> priceEntities = new ArrayList<>();
        prices.forEach(price -> {
            String cryptoCurrencySymbol = price.getCurrencySymbol();
            CryptoCurrencyEntity currencyEntity;
            currencyEntity = cryptoCurrencyRepository.getReferenceById(cryptoCurrencySymbol);
            priceEntities.add(new PriceEntity(price.getTimestamp(), currencyEntity, price.getValue()));
        });

        List<PriceEntity> savedPrices = priceRepository.saveAll(priceEntities);
        return savedPrices.stream().map(this::generatePrice).toList();
    }

    @Override
    public CryptoCurrencyPriceStatistic getPriceStatisticForTimePeriod(String cryptoCurrencySymbol, int monthsBack) {

        if (!cryptoCurrencyRepository.existsById(cryptoCurrencySymbol)) {
            throw new NoSuchDataException("There is no available crypto currency with symbol: " + cryptoCurrencySymbol);
        }

        long startTimestamp = LocalDate.now().minusMonths(monthsBack).withDayOfMonth(1).atStartOfDay().atZone(ZoneId.of(DEFAULT_ZONE_ID)).toInstant().toEpochMilli();

        List<Object[]> queryResult = priceRepository.getCryptoCurrencyStatisticForTimePeriod(cryptoCurrencySymbol,startTimestamp, System.currentTimeMillis());

        if (queryResult.isEmpty()) {
            throw new NoSuchDataException("There is no pricing statistic for the selected time period");
        }
        return generateCryptoCurrencyStatistic(queryResult.get(0));
    }

    private CryptoCurrencyPriceStatistic generateCryptoCurrencyStatistic(Object[] queryResult) {
        CryptoCurrencyPriceStatistic cryptoCurrencyStatistic = new CryptoCurrencyPriceStatistic();
        cryptoCurrencyStatistic.setSymbol((String)queryResult[0]);
        cryptoCurrencyStatistic.setOldestPrice((double)queryResult[1]);
        cryptoCurrencyStatistic.setNewestPrice((double)queryResult[2]);
        cryptoCurrencyStatistic.setMinimumPrice((double)queryResult[3]);
        cryptoCurrencyStatistic.setMaximumPrice((double)queryResult[4]);
        return cryptoCurrencyStatistic;
    }

    private Price generatePrice(PriceEntity priceEntity) {
        Price price = new Price();
        price.setTimestamp(priceEntity.getTimestamp());
        price.setCurrencySymbol(priceEntity.getCurrency().getSymbol());
        price.setValue(priceEntity.getPriceValue());
        return price;
    }


}
