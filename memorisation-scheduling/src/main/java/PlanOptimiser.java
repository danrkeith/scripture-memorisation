import model.Book;
import model.Passage;

import java.util.List;

public class PlanOptimiser {
    public static int minMaxVersesPerDay(List<Book> books, int days) {
        // Increase until a feasible max verses per day is found
        int infeasibleMax = 0;
        int feasibleMax = 1;

        while (!isFeasible(books, days, feasibleMax)) {
            infeasibleMax = feasibleMax;
            feasibleMax *= 2;
        }

        // Converge with a binary search
        while (feasibleMax > infeasibleMax + 1) {
            int middleMax = (feasibleMax + infeasibleMax) / 2;

            if (isFeasible(books, days, middleMax)) {
                feasibleMax = middleMax;
            } else {
                infeasibleMax = middleMax;
            }
        }

        // The smallest possible feasible max verses per day has been found
        return feasibleMax;
    }

    private static boolean isFeasible(List<Book> books, int days, int maxVersesPerDay) {
        return isFeasible(books, days, maxVersesPerDay, 0);
    }

    private static boolean isFeasible(List<Book> books, int days, int maxVersesPerDay, int startIndex) {
        int booksRemaining = books.size() - startIndex;

        // Do not allocate multiple books to one day
        if (booksRemaining > days) {
            return false;
        }

        // Only last book left to check feasibility
        if (booksRemaining == 1) {
            return isFeasible(books.getLast(), days, maxVersesPerDay);
        }

        // Find a feasible number of days to allocate to the first book
        // (leaving at least 1 day for each remaining book)
        // such that the remaining books are also feasible
        for (int daysAllocatedToFirstBook = 1; daysAllocatedToFirstBook <= days - (booksRemaining - 1); daysAllocatedToFirstBook++) {
            if (
                    isFeasible(books.get(startIndex), daysAllocatedToFirstBook, maxVersesPerDay)
                            && isFeasible(books, days - daysAllocatedToFirstBook, maxVersesPerDay, startIndex + 1)
            ) {
                return true;
            }
        }

        return false;
    }

    private static boolean isFeasible(Book book, int days, int maxVersesPerDay) {
        int currentDay = 1;
        int versesInDay = 0;

        for (Passage passage : book.getChapterGroups()) {
            if (passage.getVerses() > maxVersesPerDay) {
                return false;
            }

            // Can the chapter be memorised today?
            if (versesInDay + passage.getVerses() <= maxVersesPerDay) {
                // Memorise it today
                versesInDay += passage.getVerses();
            } else {
                // Memorise it tomorrow
                currentDay++;
                versesInDay = passage.getVerses();
            }
        }

        return currentDay <= days;
    }
}
