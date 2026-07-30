package be.intecbrussel.linguacards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CardUpdateRequest {

    @NotBlank
    @Size(min = 1, max = 200)
    private String term;

    @NotBlank
    @Size(min = 1, max = 2000)
    private String definition;

    @Size(max = 500)
    private String example;

    @Size(max = 5)
    private String cefr;

    @Size(max = 200)
    private String tags;

    public CardUpdateRequest() {
    }

    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }

    public String getExample() {
        return example;
    }

    public String getCefr() {
        return cefr;
    }

    public String getTags() {
        return tags;
    }
}