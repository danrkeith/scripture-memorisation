package model;

import java.util.ArrayList;
import java.util.List;

public class ChapterGroup implements Passage {
    private final String bookTitle;
    private final List<Chapter> chapters = new ArrayList<>();

    public ChapterGroup(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public ChapterGroup(String bookTitle, Chapter chapter) {
        this(bookTitle);
        this.chapters.add(chapter);
    }

    public ChapterGroup(String bookTitle, List<Chapter> chapters) {
        this(bookTitle);
        this.chapters.addAll(chapters);
    }

    public void add(Chapter chapter) {
        chapters.add(chapter);
    }

    public void add(ChapterGroup chapterGroup) {
        chapters.addAll(chapterGroup.chapters);
    }

    public int getVerses() {
        return chapters.stream()
                .mapToInt(Chapter::getVerses)
                .sum();
    }

    @Override
    public String getName() {
        String chaptersStr = chapters.getFirst().getChapter() + (
                chapters.size() == 1 ? "" : "-" + chapters.getLast().getChapter()
        );

        return bookTitle + " " + chaptersStr;
    }

    @Override
    public String toString() {
        return getName() + " (" + getVerses() + " verses)";
    }
}
