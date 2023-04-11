package com.epam.crypto.recommendation.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class Price {

    @Getter
    @Setter
    private long timestamp;

    @Getter
    @Setter
    private String currencySymbol;

    @Getter
    @Setter
    private double value;

}
