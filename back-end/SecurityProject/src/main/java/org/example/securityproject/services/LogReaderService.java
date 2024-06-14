package org.example.securityproject.services;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LogReaderService {

    private static final String LOG_FILE_DIR = "logs";
    private static final String LOG_FILE_PATTERN = "app-logback";
    private static final String DEFAULT_LOG_FILE = "logs/app-logback.log";

    public List<String> readLastNLogs(int n) throws IOException {
        Path logFilePath = getLatestLogFilePath();
        if (logFilePath == null) {
            throw new IOException("No log files found in directory: " + LOG_FILE_DIR);
        }
        System.out.println("Reading last n log file: " + logFilePath);
        try (Stream<String> stream = Files.lines(logFilePath)) {
            List<String> logs = stream.collect(Collectors.toList());
            return logs.subList(Math.max(logs.size() - n, 0), logs.size());
        }
    }

    public Path getLatestLogFilePath() throws IOException {
        try (Stream<Path> files = Files.list(Paths.get(LOG_FILE_DIR))) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().startsWith(LOG_FILE_PATTERN) && !file.getFileName().toString().equals("app-logback.log"))
                    .max(Comparator.comparingLong(this::getFileLastModifiedTime))
                    .orElse(Paths.get(DEFAULT_LOG_FILE));
        }
    }

    private long getFileLastModifiedTime(Path path) {
        try {
            return Files.readAttributes(path, BasicFileAttributes.class).lastModifiedTime().toMillis();
        } catch (IOException e) {
            throw new RuntimeException("Failed to get last modified time for file: " + path, e);
        }
    }
}
