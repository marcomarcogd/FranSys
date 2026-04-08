package com.fransys.match;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseMatchCandidateRepository extends JpaRepository<EnterpriseMatchCandidate, Long> {
    List<EnterpriseMatchCandidate> findByMatchIdOrderBySlotCodeAsc(Long matchId);
    void deleteByMatchId(Long matchId);
}
