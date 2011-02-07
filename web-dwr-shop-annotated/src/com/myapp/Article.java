package com.myapp;


import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;


/**
 * represents an article in a web shop.
 *
 * the annotations of this class and its methods are equivalent to:

<convert converter="bean" match="com.myapp.Article">
    <param name="include" value="id,name,description,formattedPrice"/>
</convert>

 * @author andre
 */
@DataTransferObject
public class Article {

    private String id;
    private String name;
    private String description;
    private int price;

    public Article( String id, String name, String description, int price ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    @RemoteProperty
    public String getId() {
        return id;
    }

    @RemoteProperty
    public String getName() {
        return name;
    }

    @RemoteProperty
    public int getPrice() {
        return price;
    }

    @RemoteProperty
    public String getDescription() {
        return description;
    }

    @RemoteProperty
    public String getFormattedPrice() {
        return Utils.formatCurrency( price );
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals( Object o ) {
        if ( o == null )
            return false;
        if (  ! (o instanceof Article) )
            return false;
        if ( o == this )
            return true;
        return ((( Article ) o).getId().equals( id ));
    }
}
