package org.example.securityproject.services;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LogReaderService {

    private static final String LOG_FILE_PATH = "logs/app-logback.log";

    public List<String> readLastNLogs(int n) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(LOG_FILE_PATH))) {
            List<String> logs = stream.collect(Collectors.toList());
            return logs.subList(Math.max(logs.size() - n, 0), logs.size());
        }
    }
}
