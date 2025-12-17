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
        
        if (currentUser.getRole() == User.UserRole.ADMIN 
                || currentUser.getRole() == User.UserRole.BUSINESS_COMMISSION) {
            // 管理员和商务委可以查看所有问卷
            surveys = surveyService.findAll();
        } else if ((currentUser.getRole() == User.UserRole.COMPANY 
                || currentUser.getRole() == User.UserRole.COMPANY_ADMIN
                || currentUser.getRole() == User.UserRole.COMPANY_USER) 
                && currentUser.getCompany() != null) {
            // 企业用户（包括企业管理员和普通用户）只能查看自己企业的问卷
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
                .map(survey -> {
                    // 权限检查：企业用户只能查看自己企业的问卷
                    if (currentUser.getRole() != User.UserRole.ADMIN 
                            && currentUser.getRole() != User.UserRole.BUSINESS_COMMISSION) {
                        if (currentUser.getCompany() == null 
                                || !survey.getCompanyId().equals(currentUser.getCompany().getId())) {
                            return ResponseEntity.status(403).body(ResponseMessage.<SurveyQuestionnaireDTO>error(403, "无权限访问该问卷"));
                        }
                    }
                    return ResponseEntity.ok(ResponseMessage.success(survey));
                })
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
        
        // 权限检查：企业用户只能创建或修改自己企业的问卷
        if (currentUser.getRole() != User.UserRole.ADMIN 
                && currentUser.getRole() != User.UserRole.BUSINESS_COMMISSION) {
            if (currentUser.getCompany() == null) {
                return ResponseEntity.status(403).body(ResponseMessage.error(403, "企业用户必须关联企业"));
            }
            // 如果是更新操作，检查是否是自己企业的问卷
            if (dto.getId() != null) {
                SurveyQuestionnaireDTO existing = surveyService.findById(dto.getId())
                        .orElse(null);
                if (existing != null && !existing.getCompanyId().equals(currentUser.getCompany().getId())) {
                    return ResponseEntity.status(403).body(ResponseMessage.error(403, "无权限修改其他企业的问卷"));
                }
            }
            // 强制设置为当前用户的企业
            dto.setCompanyId(currentUser.getCompany().getId());
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
        
        // 权限检查：管理员和商务委可以删除任何问卷，企业用户只能删除自己企业的问卷
        if (currentUser.getRole() != User.UserRole.ADMIN 
                && currentUser.getRole() != User.UserRole.BUSINESS_COMMISSION) {
            SurveyQuestionnaireDTO survey = surveyService.findById(id).orElse(null);
            if (survey == null) {
                return ResponseEntity.status(404).body(ResponseMessage.error(404, "问卷调查记录不存在"));
            }
            if (currentUser.getCompany() == null 
                    || !survey.getCompanyId().equals(currentUser.getCompany().getId())) {
                return ResponseEntity.status(403).body(ResponseMessage.error(403, "无权限删除其他企业的问卷"));
            }
        }
        
        try {
            surveyService.deleteById(id);
            return ResponseEntity.ok(ResponseMessage.success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(ResponseMessage.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ResponseMessage.error(500, "删除失败: " + e.getMessage()));
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

