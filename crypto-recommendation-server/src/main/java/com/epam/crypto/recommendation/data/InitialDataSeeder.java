package com.epam.crypto.recommendation.data;

import com.epam.crypto.recommendation.data.entity.CryptoCurrencyEntity;
import com.epam.crypto.recommendation.data.repository.CryptoCurrencyRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InitialDataSeeder {
    private final CryptoCurrencyRepository cryptoCurrencyRepository;

    public InitialDataSeeder(CryptoCurrencyRepository cryptoCurrencyRepository) {
        this.cryptoCurrencyRepository = cryptoCurrencyRepository;
    }

    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        cryptoCurrencyRepository.saveAll(generateInitialCryptoCurrencies());
    }


    private static List<CryptoCurrencyEntity> generateInitialCryptoCurrencies() {
        List<CryptoCurrencyEntity> cryptoCurrencyEntities = new ArrayList<>();
        cryptoCurrencyEntities.add(new CryptoCurrencyEntity("BTC"));
        cryptoCurrencyEntities.add(new CryptoCurrencyEntity("DOGE"));
        cryptoCurrencyEntities.add(new CryptoCurrencyEntity("ETH"));
        cryptoCurrencyEntities.add(new CryptoCurrencyEntity("LTC"));
        cryptoCurrencyEntities.add(new CryptoCurrencyEntity("XRP"));
        return cryptoCurrencyEntities;
    }

}

