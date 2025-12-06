package com.example.cailanzi.service;

import com.example.cailanzi.entity.Company;
import com.example.cailanzi.entity.ReportSubmission;
import com.example.cailanzi.repository.CompanyRepository;
import com.example.cailanzi.repository.ReportSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportSubmissionRepository reportSubmissionRepository;
    private final CompanyRepository companyRepository;

    public List<ReportSubmission> findAll() {
        return reportSubmissionRepository.findAll();
    }

    public Optional<ReportSubmission> findById(Long id) {
        return reportSubmissionRepository.findById(id);
    }

    public List<ReportSubmission> findByCompanyId(Long companyId) {
        return reportSubmissionRepository.findByCompanyId(companyId);
    }

    public List<ReportSubmission> findByCompanyIdAndSubmitType(Long companyId, ReportSubmission.SubmitType submitType) {
        return reportSubmissionRepository.findByCompanyIdAndSubmitType(companyId, submitType);
    }

    public ReportSubmission save(ReportSubmission reportSubmission) {
        // 检查企业是否存在
        Company company = companyRepository.findById(reportSubmission.getCompany().getId())
                .orElseThrow(() -> new IllegalArgumentException("企业不存在，ID: " + reportSubmission.getCompany().getId()));

        reportSubmission.setCompany(company);
        
        // 如果rawPayload是对象，转换为JSON字符串
        if (reportSubmission.getRawPayload() != null && !reportSubmission.getRawPayload().trim().startsWith("{")) {
            // 如果已经是JSON字符串，保持不变
        }

        return reportSubmissionRepository.save(reportSubmission);
    }

    public void deleteById(Long id) {
        if (!reportSubmissionRepository.existsById(id)) {
            throw new IllegalArgumentException("填报记录不存在，ID: " + id);
        }
        reportSubmissionRepository.deleteById(id);
    }

    public List<ReportSubmission> findLatestByCompanyAndType(Long companyId, ReportSubmission.SubmitType submitType) {
        return reportSubmissionRepository.findLatestByCompanyAndType(companyId, submitType);
    }
}
