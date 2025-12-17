package com.example.cailanzi.controller;

import com.example.cailanzi.dto.FeedbackDTO;
import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.entity.Feedback;
import com.example.cailanzi.entity.User;
import com.example.cailanzi.service.FeedbackService;
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
@RequestMapping("/feedback")
@RequiredArgsConstructor
@Tag(name = "反馈管理", description = "需求与问题反馈管理API")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping
    @Operation(summary = "获取反馈列表（管理员查看所有，企业/司机用户查看自己企业的）")
    public ResponseEntity<ResponseMessage<List<FeedbackDTO>>> getAllFeedback(HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        List<FeedbackDTO> feedback;
        
        if (currentUser.getRole() == User.UserRole.ADMIN 
                || currentUser.getRole() == User.UserRole.BUSINESS_COMMISSION) {
            // 管理员和商务委可以查看所有反馈
            feedback = feedbackService.findAll();
        } else if ((currentUser.getRole() == User.UserRole.COMPANY 
                || currentUser.getRole() == User.UserRole.COMPANY_ADMIN
                || currentUser.getRole() == User.UserRole.COMPANY_USER) 
                && currentUser.getCompany() != null) {
            // 企业用户查看自己企业的反馈
            feedback = feedbackService.findByCompanyId(currentUser.getCompany().getId());
        } else if (currentUser.getRole() == User.UserRole.DRIVER 
                && currentUser.getVehicle() != null 
                && currentUser.getVehicle().getCompany() != null) {
            // 司机用户查看自己所属企业的反馈
            feedback = feedbackService.findByCompanyId(currentUser.getVehicle().getCompany().getId());
        } else {
            feedback = List.of();
        }
        
        return ResponseEntity.ok(ResponseMessage.success(feedback));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取反馈详情")
    public ResponseEntity<ResponseMessage<FeedbackDTO>> getFeedbackById(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        return feedbackService.findById(id)
                .map(dto -> {
                    // 权限检查
                    if (currentUser.getRole() == User.UserRole.COMPANY && 
                        (currentUser.getCompany() == null || !dto.getCompanyId().equals(currentUser.getCompany().getId()))) {
                        return ResponseEntity.status(403).body(ResponseMessage.<FeedbackDTO>error(403, "无权限访问"));
                    }
                    return ResponseEntity.ok(ResponseMessage.success(dto));
                })
                .orElse(ResponseEntity.status(404).body(ResponseMessage.error(404, "反馈记录不存在")));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "根据企业ID获取反馈列表")
    public ResponseEntity<ResponseMessage<List<FeedbackDTO>>> getFeedbackByCompanyId(@PathVariable Long companyId, HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        
        // 企业用户/司机用户只能查看自己企业的数据
        if (currentUser.getRole() == User.UserRole.COMPANY && 
            (currentUser.getCompany() == null || !currentUser.getCompany().getId().equals(companyId))) {
            return ResponseEntity.status(403).body(ResponseMessage.error(403, "无权限访问"));
        }
        if (currentUser.getRole() == User.UserRole.DRIVER &&
            (currentUser.getVehicle() == null 
                || currentUser.getVehicle().getCompany() == null 
                || !currentUser.getVehicle().getCompany().getId().equals(companyId))) {
            return ResponseEntity.status(403).body(ResponseMessage.error(403, "无权限访问"));
        }
        
        return ResponseEntity.ok(ResponseMessage.success(feedbackService.findByCompanyId(companyId)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "根据状态获取反馈列表（仅管理员）")
    public ResponseEntity<ResponseMessage<List<FeedbackDTO>>> getFeedbackByStatus(@PathVariable Feedback.FeedbackStatus status,
                                                                                  HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        if (currentUser.getRole() != User.UserRole.ADMIN) {
            return ResponseEntity.status(403).body(ResponseMessage.error(403, "只有管理员可以按状态筛选反馈"));
        }
        return ResponseEntity.ok(ResponseMessage.success(feedbackService.findByStatus(status)));
    }

    @PostMapping
    @Operation(summary = "创建反馈记录")
    public ResponseEntity<ResponseMessage<FeedbackDTO>> createFeedback(@Valid @RequestBody Feedback feedback,
                                                                       HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        // 非管理员创建时，强制绑定为当前用户所属企业，防止越权
        if (currentUser.getRole() == User.UserRole.COMPANY && currentUser.getCompany() != null) {
            feedback.setCompany(currentUser.getCompany());
        } else if (currentUser.getRole() == User.UserRole.DRIVER 
                && currentUser.getVehicle() != null 
                && currentUser.getVehicle().getCompany() != null) {
            feedback.setCompany(currentUser.getVehicle().getCompany());
        }
        FeedbackDTO saved = feedbackService.save(feedback);
        return ResponseEntity.ok(ResponseMessage.success("反馈记录创建成功", saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新反馈记录（仅管理员）")
    public ResponseEntity<ResponseMessage<FeedbackDTO>> updateFeedback(
            @PathVariable Long id,
            @Valid @RequestBody Feedback feedback,
            HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        if (currentUser.getRole() != User.UserRole.ADMIN) {
            return ResponseEntity.status(403).body(ResponseMessage.error(403, "只有管理员可以修改反馈记录"));
        }
        FeedbackDTO updated = feedbackService.update(id, feedback);
        return ResponseEntity.ok(ResponseMessage.success("反馈记录更新成功", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除反馈记录（仅管理员）")
    public ResponseEntity<ResponseMessage<Void>> deleteFeedback(@PathVariable Long id,
                                                                HttpServletRequest request) {
        User currentUser = UserContext.getCurrentUser(request);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ResponseMessage.error(401, "未授权"));
        }
        if (currentUser.getRole() != User.UserRole.ADMIN) {
            return ResponseEntity.status(403).body(ResponseMessage.error(403, "只有管理员可以删除反馈记录"));
        }
        feedbackService.deleteById(id);
        return ResponseEntity.ok(ResponseMessage.success("反馈记录删除成功", null));
    }
}
