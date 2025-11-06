package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Plan {
    private final List<Day> days = new ArrayList<>();

    public Plan() { }

    public Plan(Day day) {
        days.add(day);
    }

    public void add(Day day) {
        days.add(day);
    }

    public void add(Plan plan) {
        days.addAll(plan.days);
    }

    public List<Day> getDays() {
        return days;
    }

    private int getVerses() {
        return days.stream()
                .mapToInt(Day::getVerses)
                .sum();
    }

    @Override
    public String toString() {
        return "Plan (" + getVerses() + " verses)\n" +
                IntStream.range(0, days.size())
                        .mapToObj(i -> "\tDay " + (i + 1) + " (" + days.get(i).getVerses() + " verses): \t" + days.get(i))
                        .collect(Collectors.joining("\n"));
    }
}
