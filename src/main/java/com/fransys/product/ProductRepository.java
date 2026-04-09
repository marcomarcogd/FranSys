package com.fransys.product;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByEnterpriseIdOrderByUpdatedAtDesc(Long enterpriseId);
}
