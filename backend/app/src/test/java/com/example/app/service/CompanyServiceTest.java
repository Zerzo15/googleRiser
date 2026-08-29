package com.example.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class CompanyServiceTest {

    @Test
    void normalizeDomainAcceptsHostAndCompleteUrl() {
        assertEquals("vng.com.vn", CompanyService.normalizeDomain("https://vng.com.vn/"));
        assertEquals("vng.com.vn", CompanyService.normalizeDomain("www.VNG.com.vn/about?ref=home"));
        assertEquals("vng.com.vn", CompanyService.normalizeDomain("vng.com.vn"));
        assertNull(CompanyService.normalizeDomain("  "));
    }
}
