package com.example.cailanzi.controller;

import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.dto.SurveyQuestionnaireDTO;
import com.example.cailanzi.entity.User;
import com.example.cailanzi.service.SurveyService;
import com.example.cailanzi.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/survey")
@RequiredArgsConstructor
@Tag(name = "问卷调查管理", description = "问卷调查管理API")
public class SurveyController {

    private final SurveyService surveyService;

    @GetMapping
    @Operation(summary = "获取问卷调查列表（管理员查看所有，企业用户查看自己企业的）")
    public ResponseEntity<ResponseMessage<List<SurveyQuestionnaireDTO>>> getAllSurveys(HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        List<SurveyQuestionnaireDTO> surveys;
        
        if (currentUser.getRole() == User.UserRole.ADMIN) {
            surveys = surveyService.findAll();
        } else if (currentUser.getRole() == User.UserRole.COMPANY && currentUser.getCompany() != null) {
            surveys = surveyService.findByCompanyId(currentUser.getCompany().getId());
        } else {
            surveys = List.of();
        }
        
        return ResponseEntity.ok(ResponseMessage.success(surveys));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取问卷调查详情")
    public ResponseEntity<ResponseMessage<SurveyQuestionnaireDTO>> getSurveyById(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        return surveyService.findById(id)
                .map(survey -> ResponseEntity.ok(ResponseMessage.success(survey)))
                .orElse(ResponseEntity.status(404).body(ResponseMessage.error(404, "问卷调查记录不存在")));
    }

    @PostMapping
    @Operation(summary = "创建或更新问卷调查（保存为草稿）")
    public ResponseEntity<ResponseMessage<SurveyQuestionnaireDTO>> createOrUpdateSurvey(
            @Valid @RequestBody SurveyQuestionnaireDTO dto, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        try {
            SurveyQuestionnaireDTO saved = surveyService.save(dto, currentUser.getId());
            return ResponseEntity.ok(ResponseMessage.success(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ResponseMessage.error(400, e.getMessage()));
        }
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "提交问卷调查")
    public ResponseEntity<ResponseMessage<SurveyQuestionnaireDTO>> submitSurvey(
            @PathVariable Long id, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        try {
            SurveyQuestionnaireDTO submitted = surveyService.submit(id, currentUser.getId());
            return ResponseEntity.ok(ResponseMessage.success(submitted));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ResponseMessage.error(400, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除问卷调查")
    public ResponseEntity<ResponseMessage<Void>> deleteSurvey(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        // 只有管理员可以删除
        if (currentUser.getRole() != User.UserRole.ADMIN) {
            return ResponseEntity.status(403).body(ResponseMessage.error(403, "无权限删除"));
        }
        
        try {
            surveyService.deleteById(id);
            return ResponseEntity.ok(ResponseMessage.success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ResponseMessage.error(400, e.getMessage()));
        }
    }

    @GetMapping("/year/{year}")
    @Operation(summary = "根据年份获取问卷调查列表")
    public ResponseEntity<ResponseMessage<List<SurveyQuestionnaireDTO>>> getSurveysByYear(
            @PathVariable Integer year, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        List<SurveyQuestionnaireDTO> surveys = surveyService.findBySurveyYear(year);
        return ResponseEntity.ok(ResponseMessage.success(surveys));
    }
}

