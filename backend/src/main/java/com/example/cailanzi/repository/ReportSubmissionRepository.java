package com.example.cailanzi.repository;

import com.example.cailanzi.entity.ReportSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportSubmissionRepository extends JpaRepository<ReportSubmission, Long> {

    List<ReportSubmission> findByCompanyId(Long companyId);

    List<ReportSubmission> findByCompanyIdAndSubmitType(Long companyId, ReportSubmission.SubmitType submitType);

    List<ReportSubmission> findBySourcePlatform(ReportSubmission.SourcePlatform sourcePlatform);

    List<ReportSubmission> findBySubmitTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT rs FROM ReportSubmission rs WHERE rs.company.id = :companyId AND rs.submitType = :submitType ORDER BY rs.submitTime DESC")
    List<ReportSubmission> findLatestByCompanyAndType(Long companyId, ReportSubmission.SubmitType submitType);
}
