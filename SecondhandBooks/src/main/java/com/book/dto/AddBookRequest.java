package com.book.dto;

public class AddBookRequest {
    private String isbn;
    private String title; // corresponds to 'name' in Book entity
    private String author;
    private String publisher;
    private String category;
    private String condition; // productNew
    private String notes; // productClassNote (e.g., 'none', 'few', 'many' mapped to description)
                          // Actually, frontend sends 'none', 'few', 'many'. We might want to map this.
                          // Let's keep it as String and handle mapping in Service or just store it.
    private String description; // productNote
    private Integer price;

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "AddBookRequest{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", category='" + category + '\'' +
                ", condition='" + condition + '\'' +
                ", notes='" + notes + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
