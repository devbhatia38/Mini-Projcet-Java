package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public final class ReportGenerator {
    private ReportGenerator() {
    }

    public static String generate(Path logsFolder, Path reportFile) throws IOException {
        long deposits = 0;
        long withdrawals = 0;
        StringBuilder report = new StringBuilder();
        Files.createDirectories(logsFolder);
        try (DirectoryStream<Path> files = Files.newDirectoryStream(logsFolder, "*.log")) {
            for (Path file : files) {
                if (!Files.isRegularFile(file) || Files.size(file) == 0) {
                    continue;
                }
                BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class);
                report.append(file.getFileName()).append(" | ").append(attributes.size())
                        .append(" bytes | ").append(attributes.lastModifiedTime()).append(System.lineSeparator());
                try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.trim().split("\\s+");
                        if (parts.length < 3) {
                            continue;
                        }
                        long amount;
                        try {
                            amount = Long.parseLong(parts[2]);
                        } catch (NumberFormatException exception) {
                            continue;
                        }
                        if (parts[0].equals("DEPOSIT")) {
                            deposits = Math.addExact(deposits, amount);
                        } else if (parts[0].equals("WITHDRAW")) {
                            withdrawals = Math.addExact(withdrawals, amount);
                        }
                    }
                }
            }
        }
        report.append("Total deposits: ").append(deposits).append(System.lineSeparator())
                .append("Total withdrawals: ").append(withdrawals).append(System.lineSeparator())
                .append("Net change: ").append(deposits - withdrawals);
        Path parent = reportFile.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(reportFile, report.toString(), StandardCharsets.UTF_8);
        return report.toString();
    }
}
