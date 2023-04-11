package com.epam.crypto.recommendation.service.importer;

import com.epam.crypto.recommendation.web.model.Price;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvCurrencyPriceImporter implements CurrencyPriceImporter {

    @Override
    public List<Price> getCurrenciesFromFile(MultipartFile file) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

            List<CSVRecord> csvRecords = csvParser.getRecords();
            return csvRecords.stream().map(this::generatePriceFromCsvRecord).toList();

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
    }

    private Price generatePriceFromCsvRecord(CSVRecord csvRecord) {
        Price price = new Price();
        price.setTimestamp(Long.parseLong(csvRecord.get(0)));
        price.setCurrencySymbol(csvRecord.get(1));
        price.setValue(Double.parseDouble(csvRecord.get(2)));
        return price;
    }
}
