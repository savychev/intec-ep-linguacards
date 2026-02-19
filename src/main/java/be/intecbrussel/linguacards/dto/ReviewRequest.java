package be.intecbrussel.linguacards.dto;

import be.intecbrussel.linguacards.entity.ReviewLog;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    @NotNull
    private ReviewLog.Rating rating;
}
