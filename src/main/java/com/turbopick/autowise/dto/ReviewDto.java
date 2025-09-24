
package com.turbopick.autowise.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewDto {
    @Min(1) @Max(5) @NotNull
    private Integer rating;

    @NotBlank
    private String comment;


}