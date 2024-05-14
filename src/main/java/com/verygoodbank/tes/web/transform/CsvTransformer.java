package com.verygoodbank.tes.web.transform;

import com.opencsv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class CsvTransformer {
    final static Logger log = LoggerFactory.getLogger(CsvTransformer.class);
    final static String DELIMITER = ",";

    /**
     * Utility for reading in a Csv from an InputStream, transforming the rows and writing them to OutputStream
     *
     * @param inputStream  source csv
     * @param outputStream target csv
     * @param transformer  changes the input rows to be written to the target csv
     * @throws IOException
     */
    public static void transform(InputStream inputStream, OutputStream outputStream, EntryTransformer transformer) throws IOException, ParsingException {
        CSVParser parser = new CSVParser();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String[] inputColumns = parser.parseLine(reader.readLine());
        if (null == inputColumns) {
            inputColumns = new String[0];
        }

        transformer.validateInputColumns(inputColumns);
        writeLine(transformer.getOutputColumns(), outputStream);
        reader.lines().forEach(l -> {
            try {
                String[] transformed = transformer.transform(parser.parseLine(l));
                writeLine(transformed, outputStream);
            } catch (IOException ex) {
                throw new TransformAbortedException(ex);
            } catch (Exception ex) {
                log.warn(ex.getMessage());
            }
        });
    }

    private static void writeLine(String[] line, OutputStream outputStream) throws IOException {
        final byte NEWLINE = 10;
        outputStream.write(String.join(DELIMITER, line).getBytes());
        outputStream.write(NEWLINE);
    }
}
