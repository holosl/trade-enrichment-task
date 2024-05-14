package com.verygoodbank.tes;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TradeEnrichmentServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenValidInputReturnCorrectOutput() throws Exception {
        String uri = "/api/v1/enrich";

        String input = Resources.toString(Resources.getResource("input_trades.csv"), StandardCharsets.UTF_8);
        String expected = Resources.toString(Resources.getResource("expected_output.csv"), StandardCharsets.UTF_8);

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "trades.csv",
                MediaType.TEXT_PLAIN_VALUE, input.getBytes());

        MvcResult result = mockMvc.perform(multipart(uri).file(file)).andExpect(status().isOk()).andReturn();
        Assertions.assertEquals(expected, result.getResponse().getContentAsString());
    }

    @Test
    void whenWrongColumnsReturnsBadRequest() throws Exception {
        String uri = "/api/v1/enrich";
        String input = Resources.toString(Resources.getResource("wrong.csv"), StandardCharsets.UTF_8);


        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "trades.csv",
                MediaType.TEXT_PLAIN_VALUE, input.getBytes());

        MvcResult result = mockMvc.perform(multipart(uri).file(file)).andExpect(status().isBadRequest()).andReturn();
        Assertions.assertTrue(result.getResponse().getErrorMessage().contains("Expected columns: 'date,product_id,currency,price'."));
    }
}
