package com.epam.crypto.recommendation.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "crypto_currency")
public class CryptoCurrencyEntity {

    @Id
    @Column(name = "symbol")
    @Getter
    @Setter
    private String symbol;

    @OneToMany(mappedBy="currency", fetch = FetchType.LAZY)
    @Getter
    private Set<PriceEntity> prices;

    public CryptoCurrencyEntity(String symbol) {
        this.symbol = symbol;
    }
}
