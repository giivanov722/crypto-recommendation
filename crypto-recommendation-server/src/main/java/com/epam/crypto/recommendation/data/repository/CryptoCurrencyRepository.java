package com.epam.crypto.recommendation.data.repository;

import com.epam.crypto.recommendation.data.entity.CryptoCurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CryptoCurrencyRepository extends JpaRepository<CryptoCurrencyEntity, String > {
    @Query(value = "SELECT c.* FROM price INNER JOIN crypto_currency AS c ON price.currency_symbol = c.symbol GROUP BY c.symbol ORDER BY ((MAX(value) - MIN(value)) / MIN(value)) DESC", nativeQuery = true)
    List<Object[]> getCryptoCurrenciesOrderedByNormalizedRangeDescending();

    @Query(value = "SELECT c.* FROM " +
            "(SELECT * FROM price WHERE timestamp BETWEEN ?1 AND ?2) AS p INNER JOIN crypto_currency AS c ON p.currency_symbol = c.symbol GROUP BY c.symbol ORDER BY ((MAX(value) - MIN(value)) / MIN(value)) DESC", nativeQuery = true)
    List<Object[]> getMaxNormalizedRangeCryptoCurrencyForTimestamps(long startMillis, long endMillis);

}
