package be.intecbrussel.linguacards.dto;

import be.intecbrussel.linguacards.entity.ReviewRating;
import jakarta.validation.constraints.NotNull;

public class ReviewRequest {

    @NotNull
    private ReviewRating rating;

    public ReviewRequest() {
    }

    public ReviewRating getRating() {
        return rating;
    }
}