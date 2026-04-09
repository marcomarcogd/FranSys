package com.fransys.product;

import com.fransys.common.exception.BusinessException;
import com.fransys.enterprise.Enterprise;
import com.fransys.enterprise.EnterpriseRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductPackageRepository productPackageRepository;
    private final ProductPackageItemRepository productPackageItemRepository;
    private final EnterpriseRepository enterpriseRepository;

    public List<ProductDtos.ProductView> listProducts(Long enterpriseId, String category, Boolean active) {
        return productRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt")).stream()
                .filter(product -> enterpriseId == null || Objects.equals(product.getEnterpriseId(), enterpriseId))
                .filter(product -> category == null || category.isBlank() || Objects.equals(product.getCategory(), category))
                .filter(product -> active == null || Objects.equals(product.getActive(), active))
                .map(this::toProductView)
                .toList();
    }

    @Transactional
    public Product saveProduct(ProductDtos.ProductUpsertRequest request) {
        Product product = request.id() == null
                ? new Product()
                : productRepository.findById(request.id()).orElseThrow(() -> new BusinessException("产品不存在"));
        Enterprise enterprise = enterpriseRepository.findById(request.enterpriseId())
                .orElseThrow(() -> new BusinessException("所属企业不存在"));
        product.setEnterpriseId(enterprise.getId());
        product.setEnterpriseName(enterprise.getName());
        product.setName(request.name());
        product.setSpecification(request.specification());
        product.setCategory(request.category());
        product.setApplicablePeople(request.applicablePeople());
        product.setPrice(request.price());
        product.setImageUrl(request.imageUrl());
        product.setServiceProcess(request.serviceProcess());
        product.setRegulatoryInfo(request.regulatoryInfo());
        product.setDescription(request.description());
        product.setActive(request.active() == null || request.active());
        return productRepository.save(product);
    }

    public List<ProductDtos.ProductPackageView> listPackages(Boolean active) {
        return productPackageRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt")).stream()
                .filter(productPackage -> active == null || Objects.equals(productPackage.getActive(), active))
                .map(this::toProductPackageView)
                .toList();
    }

    @Transactional
    public ProductPackage savePackage(ProductDtos.ProductPackageUpsertRequest request) {
        ProductPackage productPackage = request.id() == null
                ? new ProductPackage()
                : productPackageRepository.findById(request.id()).orElseThrow(() -> new BusinessException("套餐包不存在"));
        productPackage.setName(request.name());
        productPackage.setApplicableScene(request.applicableScene());
        productPackage.setPrice(request.price());
        productPackage.setDescription(request.description());
        productPackage.setActive(request.active() == null || request.active());
        ProductPackage saved = productPackageRepository.save(productPackage);

        productPackageItemRepository.deleteByPackageId(saved.getId());
        int index = 0;
        for (ProductDtos.PackageItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new BusinessException("套餐产品不存在"));
            ProductPackageItem item = new ProductPackageItem();
            item.setPackageId(saved.getId());
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setEnterpriseName(product.getEnterpriseName());
            item.setQuantity(itemRequest.quantity() == null || itemRequest.quantity() < 1 ? 1 : itemRequest.quantity());
            item.setItemNote(itemRequest.itemNote());
            item.setSortOrder(itemRequest.sortOrder() == null ? index + 1 : itemRequest.sortOrder());
            productPackageItemRepository.save(item);
            index++;
        }
        return saved;
    }

    private ProductDtos.ProductView toProductView(Product product) {
        return new ProductDtos.ProductView(
                product.getId(),
                product.getEnterpriseId(),
                product.getEnterpriseName(),
                product.getName(),
                product.getSpecification(),
                product.getCategory(),
                product.getApplicablePeople(),
                product.getPrice(),
                product.getImageUrl(),
                product.getServiceProcess(),
                product.getRegulatoryInfo(),
                product.getDescription(),
                product.getActive(),
                product.getUpdatedAt()
        );
    }

    private ProductDtos.ProductPackageView toProductPackageView(ProductPackage productPackage) {
        List<ProductDtos.ProductPackageItemView> items = productPackageItemRepository.findByPackageIdOrderBySortOrderAscIdAsc(productPackage.getId())
                .stream()
                .map(item -> new ProductDtos.ProductPackageItemView(
                        item.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getEnterpriseName(),
                        item.getQuantity(),
                        item.getItemNote(),
                        item.getSortOrder()))
                .toList();
        return new ProductDtos.ProductPackageView(
                productPackage.getId(),
                productPackage.getName(),
                productPackage.getApplicableScene(),
                productPackage.getPrice(),
                productPackage.getDescription(),
                productPackage.getActive(),
                items.size(),
                productPackage.getUpdatedAt(),
                items
        );
    }
}
