package com.myapp.dwr;


/**
 * represents an article in a web shop.
 * @author andre
 */
public class Article {
    private String id;
    private String name;
    private String description;
    private int price;

    public Article(String id, String name, String description, int price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }


    public String getFormattedPrice() {
        return Utils.formatCurrency(price);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Article)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        return (((Article) o).getId().equals(id));
    }
}
