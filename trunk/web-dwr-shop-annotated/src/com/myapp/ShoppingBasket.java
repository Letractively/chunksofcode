package com.myapp;


import java.util.*;
import org.directwebremoting.annotations.*;


/**
 * Represents a user's shopping basket
 *
 * the annotations of this class and its methods are equivalent to:

<create creator="new" scope="session" javascript="ShoppingBasket">
    <param name="class" value="com.myapp.ShoppingBasket"/>
    <include method="buyArticle"/>
    <include method="getFormattedTotalPrice"/>
    <include method="getShoppingBasket"/>
</create>

<convert converter="bean" match="com.myapp.ShoppingBasket">
    <param name="include" value="simpleContents,formattedTotalPrice"/>
</convert>

 * @author andre
 */
@RemoteProxy( scope = ScriptScope.SESSION )
@DataTransferObject( converter = org.directwebremoting.convert.BeanConverter.class )
public class ShoppingBasket {

    private Map<Article, Integer> contents = new HashMap<Article, Integer>();
    private int totalPrice = 0;

    /**
     * Adds an item to the shopping cart
     * @param itemId The catalogue ID of the item to add
     * @return the ShoppingBasket itself
     */
    @RemoteMethod
    public ShoppingBasket buyArticle( String itemId ) {
        Article item = Store.getArticle( itemId );
        if ( item != null ) {
            totalPrice += item.getPrice();

            int newQuantity = 1;

            Integer oldQuantity = contents.get( item );
            if ( oldQuantity != null )
                newQuantity += oldQuantity.intValue();

            contents.put( item, new Integer( newQuantity ) );
        }

        return this;
    }

    @RemoteMethod
    public ShoppingBasket getShoppingBasket() {
        return this;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    @RemoteProperty
    public String getFormattedTotalPrice() {
        return Utils.formatCurrency( totalPrice );
    }

    /**
     * Returns the cart contents in a simplified form for an Ajax client
     * @return simplified cart contents
     */
    @RemoteProperty
    public List<String> getSimpleContents() {
        List<String> simpleContents = new ArrayList<String>();

        for ( Article item : contents.keySet() )
            simpleContents.add( contents.get( item ) + " * " + item.getName() );

        return simpleContents;
    }
}
