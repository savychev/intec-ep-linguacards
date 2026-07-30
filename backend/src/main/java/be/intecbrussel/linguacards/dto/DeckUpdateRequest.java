package be.intecbrussel.linguacards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DeckUpdateRequest {

    @NotBlank
    @Size(min = 1, max = 120)
    private String name;

    @NotBlank
    @Size(min = 2, max = 10)
    private String languageCode;

    private Boolean isPrivate = true;

    public DeckUpdateRequest() {
    }

    public String getName() {
        return name;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }
}