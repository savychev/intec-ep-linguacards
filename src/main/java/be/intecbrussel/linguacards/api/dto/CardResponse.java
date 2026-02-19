package be.intecbrussel.linguacards.api.dto;

public record CardResponse(Long id, String term, String definition, String example, String cefrLevel, String tags) {
}
