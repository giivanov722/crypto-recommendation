package com.epam.crypto.recommendation.service.importer;

import com.epam.crypto.recommendation.web.model.Price;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CurrencyPriceImporter {
    List<Price> getCurrenciesFromFile(MultipartFile file);
}
