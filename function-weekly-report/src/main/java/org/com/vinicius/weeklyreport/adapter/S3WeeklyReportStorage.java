package org.com.vinicius.weeklyreport.adapter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.com.vinicius.domain.entity.WeeklyReport;
import org.com.vinicius.domain.gateway.WeeklyReportStorage;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class S3WeeklyReportStorage implements WeeklyReportStorage {

    private final S3Client s3;
    private final WeeklyReportFormatter formatter;
    private final String bucketName;

    @Inject
    public S3WeeklyReportStorage(S3Client s3,
                                 WeeklyReportFormatter formatter,
                                 @ConfigProperty(name = "report.bucket-name") String bucketName) {
        this.s3 = s3;
        this.formatter = formatter;
        this.bucketName = bucketName;
    }

    @Override
    public String store(WeeklyReport report) {
        String key = "weekly-reports/%s_%s.txt".formatted(report.periodStart(), report.periodEnd());

        s3.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType("text/plain; charset=utf-8")
                        .build(),
                RequestBody.fromString(formatter.format(report), StandardCharsets.UTF_8));

        return "s3://%s/%s".formatted(bucketName, key);
    }
}
