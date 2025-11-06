package model;

import java.util.ArrayList;
import java.util.List;

public class Book implements Passage {
    private String title;
    private List<Chapter> chapters;
    private List<ChapterGroupRange> chapterGroupRanges;

    private List<ChapterGroup> chapterGroups;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setChapters(List<Chapter> chapters) {
        chapters.forEach(chapter -> chapter.setBookTitle(title));
        this.chapters = chapters;
    }

    public void setChapterGroupRanges(List<ChapterGroupRange> chapterGroupRanges) {
        this.chapterGroupRanges = chapterGroupRanges;
    }

    public List<ChapterGroup> getChapterGroups() {
        if (chapterGroups == null) {
            buildChapterGroups();
        }

        return chapterGroups;
    }

    private void buildChapterGroups() {
        chapterGroups = new ArrayList<>();

        int i = 0;
        while (i < chapters.size()) {
            Chapter chapter = chapters.get(i);
            ChapterGroupRange groupRange = findGroupRangeStartingAt(i + 1);

            // Is the chapter the first in a group?
            if (groupRange == null) {
                // No; add chapter alone
                ChapterGroup chapterGroup = new ChapterGroup(title, chapter);
                chapterGroups.add(chapterGroup);
                i++;
            } else {
                // Yes; add group
                ChapterGroup chapterGroup = new ChapterGroup(
                        title,
                        chapters.subList(groupRange.getStart() - 1, groupRange.getEnd())
                );
                chapterGroups.add(chapterGroup);

                // Skip ahead past grouped chapters
                i = groupRange.getEnd();
            }
        }
    }

    private ChapterGroupRange findGroupRangeStartingAt(int chapter) {
        if (chapterGroupRanges == null) {
            return null;
        }

        for (ChapterGroupRange group : chapterGroupRanges) {
            if (group.getStart() == chapter) {
                return group;
            }
        }

        return null;
    }

    @Override
    public int getVerses() {
        return chapters.stream().mapToInt(Chapter::getVerses).sum();
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    public String toString() {
        return getName() + " (" + getVerses() + " verses)";
    }
}
