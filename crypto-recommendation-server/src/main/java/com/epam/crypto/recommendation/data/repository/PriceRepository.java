package com.epam.crypto.recommendation.data.repository;

import com.epam.crypto.recommendation.data.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<PriceEntity, Long> {
    @Query(value = "SELECT p.currency_symbol," +
            " (SELECT value FROM price WHERE currency_symbol = ?1 AND timestamp = p.oldest_timestamp) AS oldest_price," +
            " (SELECT value FROM price WHERE currency_symbol = ?1 AND timestamp = p.newest_timestamp) AS newest_price, p.min_price, p.max_price" +
            " FROM (SELECT tp.currency_symbol, MIN(tp.timestamp) AS oldest_timestamp, MAX(tp.timestamp) AS newest_timestamp, MIN(tp.value) AS min_price, MAX(tp.value) AS max_price" +
            " FROM (SELECT * FROM price WHERE currency_symbol = ?1 AND timestamp BETWEEN ?2 AND ?3) AS tp GROUP BY currency_symbol) AS p", nativeQuery = true)
    List<Object[]> getCryptoCurrencyStatisticForTimePeriod(String cryptoCurrencySymbol, long startMillis, long endMillis);

}