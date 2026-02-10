package org.d1ff.messageservice.service.impl;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.d1ff.messageservice.config.MinioProperties;
import org.d1ff.messageservice.dto.MinioFileProperties;
import org.d1ff.messageservice.exceptions.minio.FailedToUploadMinio;
import org.d1ff.messageservice.service.interfaces.FileService;
import org.d1ff.messageservice.utils.ExtensionToTypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioProperties minioProperties;
    private final MinioClient minioClient;
    private final ExtensionToTypeConverter extensionToTypeConverter;
    @Value("${minio.file-max-size-bytes}")
    private long fileMaxSizeBytes;

    @Transactional
    @Override
    public MinioFileProperties uploadFile(MultipartFile file){
        if (file.getSize() > fileMaxSizeBytes) {
            throw new FailedToUploadMinio("File size exceeds the maximum allowed limit.");
        }
        if (file.getOriginalFilename() == null){
            throw new FailedToUploadMinio("File name is missing.");
        }

        String bucketName = extensionToTypeConverter.convert(file.getOriginalFilename())
                .name()
                .toLowerCase();

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID() + extension;

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .object(uniqueFilename)
                            .build());

            return new MinioFileProperties(bucketName,uniqueFilename);
        } catch (Exception er) {
            throw new FailedToUploadMinio(file.getOriginalFilename());
        }
    }

    @Override
    public String generatePresignedUrl(MinioFileProperties minioFileProperties) {
        try {
            Duration bucketExpiration = minioProperties.getBucketExpirations().get(minioFileProperties.bucketName());
            if (bucketExpiration == null) {
                bucketExpiration = Duration.ofHours(24);
            }

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioFileProperties.bucketName())
                            .object(minioFileProperties.objectName())
                            .expiry((int) bucketExpiration.toSeconds())
                            .build());
        } catch (Exception e) {
            throw new FailedToUploadMinio("Failed to generate presigned URL for " + minioFileProperties.objectName());
        }
    }
}
