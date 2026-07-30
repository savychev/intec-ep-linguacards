package be.intecbrussel.linguacards.dto;

public class DeckResponse {

    private Long id;
    private String name;
    private String languageCode;
    private boolean isPrivate;

    public DeckResponse(Long id, String name, String languageCode, boolean isPrivate) {
        this.id = id;
        this.name = name;
        this.languageCode = languageCode;
        this.isPrivate = isPrivate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public boolean isPrivate() {
        return isPrivate;
    }
}