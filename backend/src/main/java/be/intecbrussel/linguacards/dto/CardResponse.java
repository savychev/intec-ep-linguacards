package be.intecbrussel.linguacards.dto;

public class CardResponse {

    private Long id;
    private Long deckId;
    private String term;
    private String definition;
    private String example;
    private String cefr;
    private String tags;

    public CardResponse(Long id, Long deckId, String term, String definition, String example, String cefr, String tags) {
        this.id = id;
        this.deckId = deckId;
        this.term = term;
        this.definition = definition;
        this.example = example;
        this.cefr = cefr;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public Long getDeckId() {
        return deckId;
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