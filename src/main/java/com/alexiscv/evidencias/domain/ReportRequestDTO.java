package com.alexiscv.evidencias.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequestDTO implements Serializable {
    private String repositoryPath;
    private String username;
    private int month;
}
