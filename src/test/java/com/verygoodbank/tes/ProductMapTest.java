package com.verygoodbank.tes;

import com.verygoodbank.tes.web.transform.ParsingException;
import com.verygoodbank.tes.web.transform.ProductMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

public class ProductMapTest {

    @Test
    public void whenFileNotFoundThrowsException() {
        Assertions.assertThrows(ParsingException.class, () -> ProductMap.fromCsv("/nosuchfile.csv", true));
    }

    @Test
    public void whenFileFoundParsesIt() throws ParsingException {
        ProductMap mapper = ProductMap.fromCsv("/test_product.csv", true);
        Assertions.assertEquals(3, mapper.size());
        Assertions.assertEquals("Treasury Bills Domestic", mapper.getProductNameOrDefault(1, ""));
        Assertions.assertEquals("Corporate Bonds Domestic", mapper.getProductNameOrDefault(2, ""));
        Assertions.assertEquals("REPO Domestic", mapper.getProductNameOrDefault(3, ""));
    }

    @Test
    public void whenCorrectEntryThenParses() throws ParsingException {
        ProductMap mapper = ProductMap.fromStream(new ByteArrayInputStream("1,Gold".getBytes()), false);
        Assertions.assertEquals(1, mapper.size());
    }

    @Test
    public void whenTooManyColsThrows() throws ParsingException {
        Assertions.assertThrows(ParsingException.class, () -> ProductMap.fromStream(new ByteArrayInputStream("1,Gold,x".getBytes()), false));
    }

    @Test
    public void whenTooFewColsThrows() {
        Assertions.assertThrows(ParsingException.class, () -> ProductMap.fromStream(new ByteArrayInputStream("1".getBytes()), false));
    }

    @Test
    public void whenIdTooBigThenThrows() {
        Assertions.assertThrows(ParsingException.class, () -> ProductMap.fromStream(new ByteArrayInputStream("1000000000000,Gold".getBytes()), false));
    }

    @Test
    public void whenIdNotIntThenThrows() {
        Assertions.assertThrows(ParsingException.class, () -> ProductMap.fromStream(new ByteArrayInputStream("A,Gold".getBytes()), false));
    }

    @Test
    public void whenIdMissingThenThrows() {
        Assertions.assertThrows(ParsingException.class, () -> ProductMap.fromStream(new ByteArrayInputStream(" ,Gold".getBytes()), false));
    }

    @Test
    public void whenNameMissingThenThrows() {
        Assertions.assertThrows(ParsingException.class, () -> ProductMap.fromStream(new ByteArrayInputStream("1, ".getBytes()), false));
    }
}
