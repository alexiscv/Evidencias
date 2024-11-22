package com.alexiscv.evidencias.service;

import com.alexiscv.evidencias.domain.ReportRequestDTO;
import com.alexiscv.evidencias.utils.GitCommandRunner;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void generateReport(ReportRequestDTO requestDTO) throws IOException, InterruptedException {
        LocalDate startDate = getStartDateOfMonth(requestDTO.getMonth());
        LocalDate endDate = getEndDateOfMonth(requestDTO.getMonth());
        Path repoPath = Paths.get(requestDTO.getRepositoryPath());
        Path repoPathGit = Paths.get(requestDTO.getRepositoryPath() + "/.git");

        List<String> summaryReport = GitCommandRunner.execute(generateGitCommandSummary(repoPathGit.toString(), repoPath.toString(), requestDTO.getUsername(), startDate.format(FORMATTER), endDate.format(FORMATTER)));
        List<String> detailedReport = GitCommandRunner.execute(generateGitCommandDetail(repoPathGit.toString(), repoPath.toString(), requestDTO.getUsername(), startDate.format(FORMATTER), endDate.format(FORMATTER)));

        String summaryHeader = "SUMMARY REPORT\n##########################\n\n";
        String detailedHeader = "\n\nDETAILED REPORT\n##########################\n\n";

        String mergedReport = summaryHeader + summaryReport.stream().collect(Collectors.joining("\n"))
                + detailedHeader + detailedReport.stream().collect(Collectors.joining("\n"));

        generatePDF("Report-" + Month.of(requestDTO.getMonth()).name(), mergedReport);
    }

    /**
     * Generate a GitCommand based on parameters for obtain repo data summary.
     *
     * @param repoPathGit
     * @param repoPath
     * @param username
     * @param startDate
     * @param endDate
     * @return
     */
    private String[] generateGitCommandSummary(String repoPathGit, String repoPath, String username, String startDate, String endDate) {

        return new String[]{
                "git",
                "--no-pager",
                "--git-dir=" + repoPathGit,
                "--work-tree=" + repoPath,
                "log",
                "--graph",
                "--pretty",
                "--author=" + username,
                "--since=" + startDate,
                "--before=" + endDate,
                "--no-merges"
        };

    }

    /**
     * Generate a GitCommand based on parameters for obtain full repo data.
     *
     * @param repoPathGit
     * @param repoPath
     * @param username
     * @param startDate
     * @param endDate
     * @return
     */
    private String[] generateGitCommandDetail(String repoPathGit, String repoPath, String username, String startDate, String endDate) {

        return new String[]{
                "git",
                "--no-pager",
                "--git-dir=" + repoPathGit,
                "--work-tree=" + repoPath,
                "log",
                "-p",
                "--author=" + username,
                "--since=" + startDate,
                "--before=" + endDate,
                "--no-merges"
        };

    }

    private LocalDate getStartDateOfMonth(int month) {
        int currentYear = LocalDate.now().getYear();
        return LocalDate.of(currentYear, month, 1);
    }

    private LocalDate getEndDateOfMonth(int month) {
        int currentYear = LocalDate.now().getYear();
        int lastDayOfMonth = LocalDate.of(currentYear, month, 1).lengthOfMonth();
        return LocalDate.of(currentYear, month, lastDayOfMonth);
    }

    private void generatePDF(String filename, String content) {

        // Ruta y nombre de archivo PDF que deseas generar
        String rutaArchivoPDF = filename + ".pdf";

        // Genera el archivo PDF
        Document document = new Document();
        try (FileOutputStream fos = new FileOutputStream(rutaArchivoPDF)) {

            PdfWriter.getInstance(document, fos);
            document.open();

            document.add(new Paragraph(content));
            document.close();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

    }

}
