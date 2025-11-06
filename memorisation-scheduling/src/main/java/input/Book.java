package input;

import java.util.List;

public record Book(String title, List<Chapter> chapters, List<ChapterGroup> chapterGroups) { }
