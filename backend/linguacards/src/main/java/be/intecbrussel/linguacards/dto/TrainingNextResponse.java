package be.intecbrussel.linguacards.dto;

public class TrainingNextResponse {

    private boolean hasCard;
    private String reason; // EMPTY_DECK | NO_CARDS_TO_TRAIN
    private TrainingCardResponse card;

    public TrainingNextResponse() {
    }

    private TrainingNextResponse(boolean hasCard, String reason, TrainingCardResponse card) {
        this.hasCard = hasCard;
        this.reason = reason;
        this.card = card;
    }

    public static TrainingNextResponse withCard(TrainingCardResponse card) {
        return new TrainingNextResponse(true, null, card);
    }

    public static TrainingNextResponse noCard(String reason) {
        return new TrainingNextResponse(false, reason, null);
    }

    public boolean isHasCard() {
        return hasCard;
    }

    public String getReason() {
        return reason;
    }

    public TrainingCardResponse getCard() {
        return card;
    }
}