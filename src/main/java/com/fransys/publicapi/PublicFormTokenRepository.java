package com.fransys.publicapi;

import com.fransys.common.enums.PublicFormType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicFormTokenRepository extends JpaRepository<PublicFormToken, Long> {
    Optional<PublicFormToken> findByToken(String token);
    Optional<PublicFormToken> findFirstByLeadIdAndFormTypeOrderByCreatedAtDesc(Long leadId, PublicFormType formType);
}
