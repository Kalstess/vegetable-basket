package com.example.cailanzi.controller;

import com.example.cailanzi.dto.ResponseMessage;
import com.example.cailanzi.dto.StatisticsDTO;
import com.example.cailanzi.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Tag(name = "统计信息", description = "系统统计数据API")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    @Operation(summary = "获取系统统计数据")
    public ResponseEntity<ResponseMessage<StatisticsDTO>> getStatistics() {
        StatisticsDTO statistics = statisticsService.getStatistics();
        return ResponseEntity.ok(ResponseMessage.success(statistics));
    }
}

