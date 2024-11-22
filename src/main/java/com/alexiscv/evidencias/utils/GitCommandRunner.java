package com.alexiscv.evidencias.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GitCommandRunner {

    private GitCommandRunner() {
    }

    public static List<String> execute(String... command) throws IOException, InterruptedException {
        List<String> result = new ArrayList<>();
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.add(line);
            }
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Error executing command: " + String.join(" ", command));
        }
        return result;
    }

}
