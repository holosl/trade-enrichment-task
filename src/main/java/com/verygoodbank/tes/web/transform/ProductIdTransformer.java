package com.verygoodbank.tes.web.transform;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

public class ProductIdTransformer implements EntryTransformer {
    final static String DEFAULT_NAME = "Missing Product Name";
    final ProductMap productMap;
    final String[] outputColumns = new String[]{"date", "product_name", "currency", "price"};
    final String[] inputColumns = new String[]{"date", "product_id", "currency", "price"};

    public ProductIdTransformer(ProductMap productMap) {
        this.productMap = productMap;
    }

    static void validateDate(String d) throws ParsingException {
        try {
            LocalDate.parse(d, DateTimeFormatter.BASIC_ISO_DATE);
        } catch (DateTimeParseException ex) {
            throw new ParsingException(String.format("Invalid date '%s'", d));
        }
    }

    public String[] getOutputColumns() {
        return outputColumns;
    }

    @Override
    public void validateInputColumns(String[] columns) throws ParsingException {
        if (!Arrays.equals(columns, inputColumns)) {
            throw new ParsingException(String.format("Expected columns: '%s'. Got: '%s'",
                    String.join(",", inputColumns), String.join(",", columns)));
        }
    }

    public String[] transform(String[] p) throws ParsingException {
        if (p.length != 4) {
            throw new ParsingException(String.format("Malformed line '%s'", String.join(",", p)));
        }
        validateDate(p[0]);
        int id = Integer.parseInt(p[1]);
        return new String[]{p[0], productMap.getProductNameOrDefault(id, DEFAULT_NAME), p[2], p[3]};
    }


}
