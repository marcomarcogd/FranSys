package com.fransys.product;

import com.fransys.common.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/api/products")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SALES','ROLE_OPERATIONS')")
    public ApiResponse<List<ProductDtos.ProductView>> products(
            @RequestParam(required = false) Long enterpriseId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean active) {
        return ApiResponse.success(productService.listProducts(enterpriseId, category, active));
    }

    @PostMapping("/api/products")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_OPERATIONS')")
    public ApiResponse<Product> saveProduct(@Valid @RequestBody ProductDtos.ProductUpsertRequest request) {
        return ApiResponse.success(productService.saveProduct(request));
    }

    @GetMapping("/api/product-packages")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SALES','ROLE_OPERATIONS')")
    public ApiResponse<List<ProductDtos.ProductPackageView>> packages(@RequestParam(required = false) Boolean active) {
        return ApiResponse.success(productService.listPackages(active));
    }

    @PostMapping("/api/product-packages")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_OPERATIONS')")
    public ApiResponse<ProductPackage> savePackage(@Valid @RequestBody ProductDtos.ProductPackageUpsertRequest request) {
        return ApiResponse.success(productService.savePackage(request));
    }
}
