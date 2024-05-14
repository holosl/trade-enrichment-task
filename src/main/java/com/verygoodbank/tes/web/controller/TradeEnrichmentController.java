package com.verygoodbank.tes.web.controller;

import com.verygoodbank.tes.web.transform.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.EOFException;
import java.io.IOException;

@RestController
@RequestMapping("api/v1")
public class TradeEnrichmentController {
    Logger log = LoggerFactory.getLogger(TradeEnrichmentController.class);

    @Autowired
    ProductMap productMap;

    /**
     * This method maps csv entries adding a new column product_name based on column produc_id
     *
     * @param file     submitted file
     * @param response
     */
    @PostMapping(value = "/enrich", produces = "text/csv")
    public @ResponseBody void enrichWithProductName(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        try {
            CsvTransformer.transform(file.getInputStream(), response.getOutputStream(), new ProductIdTransformer(productMap));
            response.flushBuffer();
        } catch (ParsingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (TransformAbortedException ex) {
            log.warn(ex.toString());
        } catch (Exception e) {
            log.error("Failed enriching trades. Reason: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed processing the request.");
        }
    }

    /**
     * This method returns the file as was read, can be used to established baseline for performance tests
     *
     * @param file     submitted file
     * @param response
     */
    @PostMapping(value = "/noop", produces = "text/csv")
    public @ResponseBody void noop(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        try {
            file.getInputStream().transferTo(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Request was aborted before it started being processed
     */
    @ExceptionHandler({EOFException.class})
    public void requestAborted() {
        log.warn("Connection reset");
    }

}


