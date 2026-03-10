package project.dto;

import lombok.Data;

@Data
public class CourseDTO {
    private Long id;
    private String disciplineName;
    private String courseName;
    private String description;
    private Double priceUsd;
    private Double priceUah;
    private String wikiInfo;
}
