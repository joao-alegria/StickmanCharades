package pt.ua.deti.es.g54.listeners;

public class MissingKeyException extends Exception {

    private String missingKey;

    public MissingKeyException(String missingKey) {
        this.missingKey = missingKey;
    }

    public String getMissingKey() {
        return missingKey;
    }
}
