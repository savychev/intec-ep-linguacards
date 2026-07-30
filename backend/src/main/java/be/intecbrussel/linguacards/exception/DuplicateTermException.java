package be.intecbrussel.linguacards.exception;

public class DuplicateTermException extends RuntimeException {
    public DuplicateTermException(String term) {
        super("Duplicate term in deck: " + term);
    }
}