package com.fransys.enterprise;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {
    List<Enterprise> findByActiveTrueOrderByIdDesc();
}
