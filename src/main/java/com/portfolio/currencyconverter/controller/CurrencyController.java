package com.portfolio.currencyconverter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CurrencyController {

    // Injected from EXCHANGE_API_KEY environment variable
    // set by Elastic Beanstalk via the infra pipeline SSM resolution
    @Value("${EXCHANGE_API_KEY}")
    private String apiKey;

    /**
     * Returns the API key to the frontend so it can call
     * exchangerate-api.com directly from the browser.
     * The key is never hardcoded — it comes from SSM via Beanstalk env var.
     */
    @GetMapping("/config")
    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("apiKey", apiKey);
        return config;
    }

    /**
     * Health check endpoint — used by Elastic Beanstalk enhanced health reporting.
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        return status;
    }
}