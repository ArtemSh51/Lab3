package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkCrewDto {
    private Long id;
    private String name;
    private List<String> supportedObjectTypes;
    private Integer activeTasksLimit;
}