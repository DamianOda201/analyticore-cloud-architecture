package com.analyticore.javaservice.infrastructure.web.dto;

/**
 * DTO (Data Transfer Object) para la entrada REST desde el servicio Python.
 */
public class AnalyzeRequestDto {
    private String jobId;
    private String text;

    public AnalyzeRequestDto() {}

    public AnalyzeRequestDto(String jobId, String text) {
        this.jobId = jobId;
        this.text = text;
    }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
