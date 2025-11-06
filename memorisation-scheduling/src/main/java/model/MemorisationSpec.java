package model;

import java.util.List;
import java.util.stream.Collectors;

public class MemorisationSpec {
    private int days;
    private List<Book> books;
    private List<Psalm> psalms;

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public List<Psalm> getPsalms() {
        return psalms;
    }

    public void setPsalms(List<Psalm> psalms) {
        this.psalms = psalms;
    }

    @Override
    public String toString() {
        return "Memorisation spec:\n"
                + "\tDays: " + days + "\n"
                + "\tBooks:\n"
                + books.stream().map(book -> "\t\t" + book).collect(Collectors.joining("\n")) + "\n"
                + "\tPsalms:\n"
                + psalms.stream().map(book -> "\t\t" + book).collect(Collectors.joining("\n"));
    }
}
