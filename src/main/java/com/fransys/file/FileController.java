package com.fransys.file;

import com.fransys.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload/product-image")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_OPERATIONS')")
    public ApiResponse<FileStorageService.FileUploadResponse> uploadProductImage(@RequestParam("file") MultipartFile file) {
        return ApiResponse.success(fileStorageService.storeProductImage(file));
    }
}
