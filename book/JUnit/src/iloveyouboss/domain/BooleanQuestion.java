package iloveyouboss.domain;

import java.util.*;
import javax.persistence.*;

@Entity
@DiscriminatorValue(value="boolean")
public class BooleanQuestion extends Question {
    private static final long serialVersionUID = 1L;

    public BooleanQuestion() {}
    public BooleanQuestion(String text) {
        super(text);
    }

    @Override
    public List<String> getAnswerChoices() {
        return Arrays.asList(new String[] { "No", "Yes" });
    }

    @Override
    public boolean match(int expected, int actual) {
        return expected == actual;
    }
}
