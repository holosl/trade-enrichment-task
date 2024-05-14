package com.verygoodbank.tes.web.transform;

import com.google.common.collect.ImmutableMap;
import com.opencsv.CSVParser;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class maps product id into product name.
 * It is thread safe: the internal map is effectively immutable.
 * The get and size methods of this immutable collection are thread-safe.
 */
public class ProductMap {

    /**
     * This map MUST NOT be modified, otherwise the class will be no longer thread-safe
     */
    private final ImmutableMap<Integer, String> map;

    private ProductMap(ImmutableMap<Integer, String> map) {
        this.map = map;
    }

    /**
     * Read in a csv
     *
     * @param path path to the csv
     * @return mapping
     * @throws ParsingException
     */
    public static ProductMap fromCsv(String path) throws ParsingException {
        return fromCsv(path, true);
    }

    /**
     * Read in a csv
     *
     * @param path       path to the csv
     * @param skipHeader true if the csv has column names, false otherwise
     * @return mapping
     * @throws ParsingException
     */
    public static ProductMap fromCsv(String path, boolean skipHeader) throws ParsingException {
        try {
            return fromStream(new ClassPathResource(path).getInputStream(), skipHeader);
        } catch (IOException ex) {
            throw new ParsingException(String.format("Could not load the product mapping file '%s'. Reason: %s", path, ex));
        }
    }

    /**
     * Read in a csv
     *
     * @param stream     input csv
     * @param skipHeader true if the csv has column names, false otherwise
     * @return mapping
     * @throws ParsingException
     */
    public static ProductMap fromStream(InputStream stream, boolean skipHeader) throws ParsingException {
        HashMap<Integer, String> m = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            Iterator<String> it = reader.lines().iterator();
            if (skipHeader && it.hasNext()) {
                it.next();
            }
            CSVParser parser = new CSVParser();
            while (it.hasNext()) {
                String line = it.next();
                String[] parts = parser.parseLine(line);
                if (parts.length != 2 || parts[1].trim().isEmpty()) {
                    throw new ParsingException(String.format("Malformed line '%s'", line));
                }

                m.put(Integer.parseInt(parts[0]), parts[1]);

            }
        } catch (Exception ex) {
            throw new ParsingException(String.format("Could not load the product mapping file. Reason: %s", ex));
        }

        return new ProductMap(ImmutableMap.copyOf(m));
    }

    /**
     * Get product name if present in the mapping, else return the default value
     *
     * @param id           id of the product
     * @param defaultValue is returned when id not present
     * @return String encoded as String
     */
    public String getProductNameOrDefault(int id, String defaultValue) {
        return map.getOrDefault(id, defaultValue);
    }

    /**
     * Return number of products
     *
     * @return
     */
    public int size() {
        return map.size();
    }
}
