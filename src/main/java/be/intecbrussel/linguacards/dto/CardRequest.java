package be.intecbrussel.linguacards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardRequest {

    @NotBlank
    @Size(max = 200)
    private String term;

    @NotBlank
    @Size(max = 2000)
    private String definition;

    @Size(max = 2000)
    private String example;

    @Size(max = 5)
    private String cefrLevel;

    @Size(max = 500)
    private String tags;
}
