package projekti.utils;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends Exception {
    private List<String> causes = new ArrayList<>();
    private String effects = "";

    public ValidationException() {
    }

    public ValidationException(List<String> causes) {
        this.causes = causes;
    }

    public ValidationException(String effects) {
        this.effects = effects;
    }

    public ValidationException(String effects, List<String> causes) {
        this.effects = effects;
        this.causes = causes;
    }

    public void addCause(String cause) {
        this.causes.add(cause);
    }

    public List<String> getCauses() {
        return this.causes;
    }

    public void addEffect(String effect) {
        this.effects += effect;
    }

    public String getEffects() {
        return this.effects;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (String c : this.causes) {
            result.append(c).append("\n");
        }

        return result.toString();
    }

    @Override
    public String getMessage() {
        return toString();
    }

    public boolean noErrors() {
        return this.causes.isEmpty() && this.effects.isEmpty();
    }

    public boolean noEffects() {
        return this.effects.isEmpty();
    }

    public boolean noCauses() {
        return this.causes.isEmpty();
    }
}
