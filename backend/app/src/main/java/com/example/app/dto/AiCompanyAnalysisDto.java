package com.example.app.dto;

import java.util.List;

public record AiCompanyAnalysisDto(
    String sector,
    String scale,
    String products,
    String market,
    String taxId,
    List<SourceItem> sources
) {
    public record SourceItem(String platformName, String url, String snippet) {}
}