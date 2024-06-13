package org.example.securityproject.monitoring;

import org.example.securityproject.services.LogAnalyzerService;
import org.example.securityproject.services.LogReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class LogMonitoringTask {

    @Autowired
    private LogReaderService logReaderService;

    @Autowired
    private LogAnalyzerService logAnalyzerService;

    @Scheduled(fixedRate = 3000)
    public void monitorLogs() {
        try {
            System.out.println("------------UICTAVAM LOGOVE I ANALIZIRAM------------------");
            List<String> logs = logReaderService.readLastNLogs(20);
            logAnalyzerService.analyzeLogs(logs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}