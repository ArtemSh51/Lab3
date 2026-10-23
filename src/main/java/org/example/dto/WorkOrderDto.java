package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.example.entity.OrderStatus;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkOrderDto {
    private Long id;
    private LocalDateTime deadline;
    private OrderStatus status;
    private String resultDescription;
    private CityReportDto report;
    private WorkCrewDto crew;

    private Long reportId;
    private Long crewId;
}