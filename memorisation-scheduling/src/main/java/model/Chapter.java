package model;

public class Chapter implements Passage {
    private int chapter;
    private int verses;

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
}
