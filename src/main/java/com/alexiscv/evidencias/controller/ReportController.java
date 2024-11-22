package com.alexiscv.evidencias.controller;

import com.alexiscv.evidencias.domain.ReportRequestDTO;
import com.alexiscv.evidencias.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generateReport")
    public ResponseEntity<String> generateReport(@RequestBody ReportRequestDTO requestDTO) throws IOException, InterruptedException {
        reportService.generateReport(requestDTO);
        return ResponseEntity.ok("Report generated successfully.");
    }

}