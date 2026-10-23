package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CityObjectDto {
    private Long id;
    private String address;
    private String type;
    private String condition;
}