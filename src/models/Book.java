package models;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private int quantity;
    private int availableQuantity;

    public Book(int bookId, String title, String author, String isbn, int quantity, int availableQuantity) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
    }

    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public int getQuantity() { return quantity; }
    public int getAvailableQuantity() { return availableQuantity; }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
}
