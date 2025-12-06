package com.example.cailanzi.dto;

import com.example.cailanzi.entity.Feedback;
import lombok.Data;

@Data
public class FeedbackDTO {
    private Long id;
    private Long companyId;
    private String companyName;
    private Long vehicleId;
    private String plateNumber;
    private Integer reportYear;
    private Integer reportMonth;
    private String mainOperationalDifficulties;
    private String policySuggestions;
    private String contactPerson;
    private String contactPhone;
    private Feedback.FeedbackStatus status;
    private String response;
}
