package com.verygoodbank.tes.web.transform;

/**
 * Interface for transformation of Csv entries
 */
public interface EntryTransformer {
    /**
     * @return names of the output columns
     */
    String[] getOutputColumns();

    /**
     * Transfrom a csv row
     *
     * @param input
     * @return
     * @throws ParsingException
     */
    String[] transform(String[] input) throws ParsingException;

    /**
     * @param columns
     * @throws ParsingException when the columns are not as expected
     */
    void validateInputColumns(String[] columns) throws ParsingException;
}
