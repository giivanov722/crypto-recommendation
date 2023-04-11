package com.epam.crypto.recommendation.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Table(name = "price")
public class PriceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Getter
    @Setter
    private long id;

    @Column(name = "timestamp")
    @Getter
    @Setter
    private long timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_symbol", nullable = false)
    @Getter
    @Setter
    private CryptoCurrencyEntity currency;

    @Column(name = "value")
    @Getter
    @Setter
    private double priceValue;

    public PriceEntity(long timestamp, CryptoCurrencyEntity currency, double priceValue) {
        this.timestamp = timestamp;
        this.currency = currency;
        this.priceValue = priceValue;
    }
}
