package model;

public class Chapter implements Passage {
    private String bookTitle;
    private int chapter;
    private int verses;

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    @Override
    public int getVerses() {
        return verses;
    }

    public void setVerses(int verses) {
        this.verses = verses;
    }

    @Override
    public String getName() {
        return bookTitle + " " + chapter;
    }

    @Override
    public String toString() {
        return getName() + " (" + getVerses() + ")";
    }
}
