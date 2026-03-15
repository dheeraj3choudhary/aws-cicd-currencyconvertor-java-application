package com.portfolio.currencyconverter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "EXCHANGE_API_KEY=test-key"
})
class CurrencyConverterApplicationTests {

    @Test
    void contextLoads() {
    }

}