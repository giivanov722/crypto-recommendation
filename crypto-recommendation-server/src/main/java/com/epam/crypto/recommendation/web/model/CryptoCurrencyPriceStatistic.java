package com.epam.crypto.recommendation.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class CryptoCurrencyPriceStatistic {

    @Getter
    @Setter
    private String symbol;

    @Getter
    @Setter
    private double oldestPrice;

    @Getter
    @Setter
    private double newestPrice;

    @Getter
    @Setter
    private double minimumPrice;

    @Getter
    @Setter
    private double maximumPrice;

}
