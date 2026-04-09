package com.fransys.file;

import com.fransys.common.exception.BusinessException;
import com.fransys.config.AppProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final AppProperties appProperties;

    public FileUploadResponse storeProductImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请先选择图片文件");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new BusinessException("仅支持上传图片文件");
        }

        String originalName = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();
        String extension = "";
        int lastDot = originalName.lastIndexOf('.');
        if (lastDot >= 0) {
            extension = originalName.substring(lastDot);
        }

        Path productDir = Paths.get(appProperties.getUploadDir(), "products").toAbsolutePath().normalize();
        try {
            Files.createDirectories(productDir);
            String storedName = UUID.randomUUID().toString().replace("-", "") + extension;
            Path target = productDir.resolve(storedName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return new FileUploadResponse("/uploads/products/" + storedName, originalName, file.getSize());
        } catch (IOException ex) {
            throw new BusinessException("图片上传失败");
        }
    }

    public record FileUploadResponse(String url, String originalName, long size) {
    }
}
