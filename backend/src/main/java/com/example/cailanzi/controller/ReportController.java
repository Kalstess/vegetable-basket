package com.example.cailanzi.controller;

import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.entity.ReportSubmission;
import com.example.cailanzi.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "填报记录管理", description = "数据填报记录管理API")
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    @Operation(summary = "获取所有填报记录列表")
    public ResponseEntity<ResponseMessage<List<ReportSubmission>>> getAllReports() {
        return ResponseEntity.ok(ResponseMessage.success(reportService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取填报记录详情")
    public ResponseEntity<ResponseMessage<ReportSubmission>> getReportById(@PathVariable Long id) {
        return reportService.findById(id)
                .map(dto -> ResponseEntity.ok(ResponseMessage.success(dto)))
                .orElse(ResponseEntity.status(404).body(ResponseMessage.error(404, "填报记录不存在")));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "根据企业ID获取填报记录列表")
    public ResponseEntity<ResponseMessage<List<ReportSubmission>>> getReportsByCompanyId(@PathVariable Long companyId) {
        return ResponseEntity.ok(ResponseMessage.success(reportService.findByCompanyId(companyId)));
    }

    @GetMapping("/company/{companyId}/type/{submitType}")
    @Operation(summary = "根据企业ID和提交类型获取填报记录列表")
    public ResponseEntity<ResponseMessage<List<ReportSubmission>>> getReportsByCompanyIdAndType(
            @PathVariable Long companyId,
            @PathVariable ReportSubmission.SubmitType submitType) {
        return ResponseEntity.ok(ResponseMessage.success(reportService.findByCompanyIdAndSubmitType(companyId, submitType)));
    }

    @GetMapping("/company/{companyId}/type/{submitType}/latest")
    @Operation(summary = "获取企业最新的指定类型填报记录")
    public ResponseEntity<ResponseMessage<List<ReportSubmission>>> getLatestReportsByCompanyAndType(
            @PathVariable Long companyId,
            @PathVariable ReportSubmission.SubmitType submitType) {
        return ResponseEntity.ok(ResponseMessage.success(reportService.findLatestByCompanyAndType(companyId, submitType)));
    }

    @PostMapping
    @Operation(summary = "创建填报记录")
    public ResponseEntity<ResponseMessage<ReportSubmission>> createReport(@Valid @RequestBody ReportSubmission reportSubmission) {
        ReportSubmission saved = reportService.save(reportSubmission);
        return ResponseEntity.ok(ResponseMessage.success("填报记录创建成功", saved));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除填报记录")
    public ResponseEntity<ResponseMessage<Void>> deleteReport(@PathVariable Long id) {
        reportService.deleteById(id);
        return ResponseEntity.ok(ResponseMessage.success("填报记录删除成功", null));
    }
}
