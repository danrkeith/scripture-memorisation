package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day {
    private final Passage mainPassage;
    private final List<Psalm> psalms = new ArrayList<>();

    public Day(Passage mainPassage) {
        this.mainPassage = mainPassage;
    }

    public void addPsalm(Psalm psalm) {
        psalms.add(psalm);
    }

    public int getVerses() {
        return mainPassage.getVerses() + psalms.stream()
                .mapToInt(Psalm::getVerses)
                .sum();
    }

    public String getPassages() {
        return toString();
    }

    @Override
    public String toString() {
        String str = mainPassage.toString();

        if (!psalms.isEmpty()) {
            str += ", " + psalms.stream()
                    .map(Psalm::toString)
                    .collect(Collectors.joining(", "));
        }

        return str;
    }
}
