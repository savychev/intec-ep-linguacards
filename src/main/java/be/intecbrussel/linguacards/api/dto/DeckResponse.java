package be.intecbrussel.linguacards.api.dto;

public record DeckResponse(Long id, String name, String languageCode, boolean isPrivate) {
}
