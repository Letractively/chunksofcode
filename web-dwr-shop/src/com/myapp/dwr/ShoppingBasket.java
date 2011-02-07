package com.myapp.dwr;

import java.util.*;
import java.util.ArrayList;

/**
 * Represents a user's shopping cart
 *
 * @author andre
 */
public class ShoppingBasket {

    private Map<Article, Integer> contents = new HashMap<Article, Integer>();
    private int totalPrice = 0;

    /**
     * Adds an item to the shopping cart
     * @param itemId The catalogue ID of the item to add
     * @return the ShoppingBasket itself
     */
    public ShoppingBasket buyArticle(String itemId) {
        Article item = new Store().getArticle(itemId);
        if (item != null) {
            totalPrice += item.getPrice();

            int newQuantity = 1;

            Integer oldQuantity = contents.get(item);
            if (oldQuantity != null) {
                newQuantity += oldQuantity.intValue();
            }

            contents.put(item, new Integer(newQuantity));
        }

        return this;
    }

    public ShoppingBasket getShoppingBasket() {
        return this;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public String getFormattedTotalPrice() {
        return Utils.formatCurrency(totalPrice);
    }

    /**
     * Returns the cart contents in a simplified form for an Ajax client
     * @return simplified cart contents
     */
    public List<String> getSimpleContents() {
        List<String> simpleContents = new ArrayList<String>();

        for (Article item : contents.keySet()) {
            simpleContents.add(contents.get(item) + " * " + item.getName());
        }

        return simpleContents;
    }
}
