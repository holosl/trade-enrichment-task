package com.verygoodbank.tes.web.config;


import com.verygoodbank.tes.web.transform.ProductMap;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class TradeEnrichmentConfig {

    @Bean
    public ProductMap productMapper() {
        try {
            return ProductMap.fromCsv("/product.csv");
        } catch (Exception ex) {
            throw new BeanInstantiationException(ProductMap.class, "Could not instantiate the product mapper bean.", ex);
        }
    }
}
