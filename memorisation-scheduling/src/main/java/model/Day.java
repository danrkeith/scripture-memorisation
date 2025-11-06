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
        List<Passage> passages = new ArrayList<>();
        passages.add(mainPassage);
        passages.addAll(psalms);
        return passages.stream().map(Passage::getName).collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        List<Passage> passages = new ArrayList<>();
        passages.add(mainPassage);
        passages.addAll(psalms);
        return passages.stream().map(Passage::toString).collect(Collectors.joining(", "));
    }
}
