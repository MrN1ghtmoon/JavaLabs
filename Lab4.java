package LabsForJava;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Objects;
public class Lab4 {
    public static void main(String[] args) {
        Library library = new Library();


        Book book1 = new Book("Crime and punishment", "Fyodor Dostoevsky", 1866 );
        Book book2 = new Book("Flowers for Algernon", "Daniel Keyes", 1966);
        Book book3 = new Book("Pride and prejudice", "Jane Austen", 1813);
        Book book4 = new Book("Ulysses", "James Joyce", 1922);
        Book book5 = new Book("The Great Gatsby", "Francis Scott Fitzgerald", 1925);

        library.AddBook(book1);
        library.AddBook(book2);
        library.AddBook(book3);
        library.AddBook(book4);
        library.AddBook(book5);

        System.out.println("all books in the library:");
        library.printAllBooks();
        System.out.println();

        System.out.println("unique authors:");
        library.printUniqueAuthors();
        System.out.println();

        System.out.println("statistics on authors:");
        library.printAuthorStatistics();
        System.out.println();

        System.out.println("Books by Fyodor Dostoevsky:");
        library.findBooksByAuthor("Fyodor Dostoevsky").forEach(System.out::println);
        System.out.println();

        System.out.println("books published in 1866:");
        library.findBooksByYear(1866).forEach(System.out::println);
        System.out.println();

        System.out.println("deleting a book");
        library.RemoveBook(book5);

        System.out.println("all books after deletion:");
        library.printAllBooks();
        System.out.println();

        System.out.println("statistics on authors:");
        library.printAuthorStatistics();
    }
}
class Library {
    private List<Book> books = new ArrayList<>();
    private Set<String> authors = new HashSet<>();
    private Map<String, Integer> statistics = new HashMap<>();

    public void AddBook(Book book){
        books.add(book);
        authors.add(book.getAuthor());
        statistics.merge(book.getAuthor(), 1, Integer::sum);
    }
    public void RemoveBook(Book book){
        if (books.remove(book)) {

            boolean authorHasMoreBooks = books.stream()
                    .anyMatch(b -> b.getAuthor().equals(book.getAuthor()));

            if (!authorHasMoreBooks) {
                authors.remove(book.getAuthor());
            }

            statistics.computeIfPresent(book.getAuthor(), (key, count) -> count > 1 ? count - 1 : null);
        }
    }

    public List<Book> findBooksByAuthor(String author) {
        return books.stream()
                .filter(book -> book.getAuthor().equals(author))
                .collect(Collectors.toList());
    }

    public List<Book> findBooksByYear(int year) {
        return books.stream()
                .filter(book -> book.getYear() == year)
                .collect(Collectors.toList());
    }
    public List<Book> findBooksByTitle(String title) {
        return books.stream()
                .filter(book -> book.getTitle() == title)
                .collect(Collectors.toList());
    }

    public void printAllBooks() {
        books.forEach(System.out::println);
    }

    public void printUniqueAuthors() {
        authors.forEach(System.out::println);
    }

    public void printAuthorStatistics() {
        statistics.forEach((author, count) ->
                System.out.println(author + ": " + count + " book(s)"));
    }
}
class Book {
    private String title;
    private String author;
    private int year;

    public Book(String title, String author, int year){
        this.title = title;
        this.author = author;
        this.year = year;
    }
    public String getTitle(){
        return  title;
    }
    public String getAuthor(){
        return  author;
    }
    public int getName(){
        return  year;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString(){
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, year);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return year == book.year &&
                Objects.equals(title, book.title) &&
                Objects.equals(author, book.author);
    }
}