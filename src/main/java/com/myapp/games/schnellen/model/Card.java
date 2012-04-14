package com.myapp.games.schnellen.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * a card used in the schnellen game. cards have a value and a color.
 *
 *
 * @author andre
 *
 */
public final class Card implements Comparable<Card>, Serializable {

    private static final long serialVersionUID = 8201108060679292234L;

    /**
     * a list of type card that performs several card-game specific
     * consistency checks on structural modification operations.
     * (only when assertions are enabled)
     *
     * @author andre
     *
     */
    public static final class CardList extends LinkedList<Card> {

        private static final long serialVersionUID = 7041439014176327L;

        public CardList() {super();}
        public CardList(Collection<Card> c) {super(c);}
        /** @param capacity */
        public CardList(int capacity) {super();}

        @Override
        public boolean add(Card e) {
            assert check(e);
            return super.add(e);
        }

        @Override
        public void add(int index, Card element) {
            assert check(element);
            super.add(index, element);
        }

        @Override
        public boolean addAll(Collection<? extends Card> c) {
            for (Card card : c)
                assert check(card);
            return super.addAll(c);
        }

        private boolean check(Object element) {
            if (element == null)
                throw new IllegalArgumentException("null elements forbidden!");
            if (! (element instanceof Card))
                throw new IllegalArgumentException(
                                   "! ("+element+" instanceof "+Card.class+")");
            if (super.contains(element))
                throw new IllegalArgumentException(
                                    "illegaly containing "+element+" in "+this);
            return true;
        }

        @Override
        public Card remove(int index) {
            Card rem = super.remove(index);
            assert check(rem);
            return rem;
        }

        @Override
        public boolean remove(Object o) {
            boolean rem = super.remove(o);
            assert rem : o+" "+this;
            assert check(o);
            return rem;
        }

        @Override
        public Card set(int index, Card element) {
            if (element == null)
                throw new IllegalArgumentException("null elements forbidden!");
            Card rm = super.set(index, element);
            return rm;
        }
    }

    
    public static enum Color {
        eichel(0), herz(1), laub(2), schell(3);
        private final int intValue;
        private Color(int intValue) {this.intValue = intValue;}
    }
    
    
    public static enum Value {
        sieben     (7)   ,
        acht       (8)   ,
        neun       (9)   ,
        zehn       (10)  ,
        unter      (11)  ,
        ober       (12)  ,
        koenig     (13)  ,
        sau        (14)  ;
        private final int intValue;
        private Value(int intValue) {
            this.intValue = intValue;
        }
        public final int intValue() {
            return intValue;
        }
    }


    public static final Card PAPA;
    public static final Card WELI;

    private static final List<Card> DECK_PROTOTYPE;
    private static final Card[] grid;
    private static final int colorsNumber;
    private static final int valuesNumber;
    
    static {
        Color[] colors = Color.values();
        Value[] values = Value.values();
        
        colorsNumber = colors.length; 
        valuesNumber = values.length; 
        
        WELI = new Card(null, null, 32);
        PAPA = new Card(Color.herz, Value.koenig, 25);
        
        int cardCount = 1 + (colorsNumber * valuesNumber);
        grid = new Card[cardCount];
        
        for (Card.Color c : Card.Color.values()) {
            for (Card.Value v : Card.Value.values()) {
                int index = calcCardPoolIndex(c, v);
                Card card;

                if (v == Value.koenig && c == Color.herz) {
                    card = PAPA;
                    assert index == 25;
                } else {
                    card = new Card(c, v, index);
                }
                
                grid[index] = card;
            }
        }
        
        grid[grid.length - 1] = Card.WELI;

        List<Card> prototype = new CardList();
        for (int i = 0; i < grid.length; prototype.add(grid[i++]));

        DECK_PROTOTYPE = Collections.unmodifiableList(prototype);
    }


    private IConfig config;
    private transient int _hashCode = -1;
    private transient String _toString = null;
    
    private final Color color;
    private final boolean isPictureCard;
    private final Value value;
    private final int gridIndex;

    private Card(Color color, Value value, int gridIndex) {
        this.color = color;
        this.value = value;
        this.gridIndex = gridIndex;
        
        isPictureCard = (color == null && value == null) 
                        ? false // WELI
                        : isPictureValue(value);
    }
    
    private Card(Card original, IConfig config) {
        this.color = original.color;
        this.value = original.value;
        this.gridIndex = original.gridIndex;
        this.isPictureCard = original.isPictureCard;
        this.config = config;
    }

    public Color getColor() {
        return color;
    }

    public Value getValue() {
        return value;
    }

    public boolean isPictureCard() {
        return isPictureCard;
    }

    public boolean isSpecialCard() {
        if (this.equals(PAPA))
            return config.isPapaHighest();

        if (this.equals(WELI))
            return true;

        return false;
    }
    
    @Override
    public int compareTo(Card o) {
        return (gridIndex == o.gridIndex) ? 0 : (gridIndex > o.gridIndex ? 1 : -1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        
        Card other = (Card) obj;
        if (color != other.color) return false;
        if (value != other.value) return false;
        
        return true;
    }
    
    @Override
    public int hashCode() {
        if (_hashCode == -1) {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((color == null) ? 0 : color.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            _hashCode = result;
        }
        return _hashCode;
    }


    @Override
    public String toString() {
        if (_toString == null) {
            if (this.equals(WELI)) {
                _toString = "WELI";
                return _toString;
            }

            if (this.equals(PAPA) && config.isPapaHighest())
                return "PAPA";// won't be cached, depends on config

            StringBuilder sb = new StringBuilder();
            String name = color.name();
            sb.append(name.substring(0, 1).toUpperCase());
            sb.append(name.substring(1, name.length()).toLowerCase());
            sb.append("-");
            sb.append(isPictureCard ? value.toString().toLowerCase()
                                    : Integer.toString(value.intValue()));

            if (this.equals(PAPA))
                return sb.toString(); // won't be cached, depends on config

            _toString = sb.toString();
        }
        return _toString;
    }
    
    


    /**address calculation:
     * <pre>
     * multiply relative value to lowest card's value (7)...
     *      --> 7 = 0, 8 = 1, ..., koenig = 6, sau = 7
     * ... with total number of colors (4)
     *      --> 7 = 0, 8 = 4, ..., koenig = 24, sau = 28
     * 
     * color is taken as offset:
     *      --> eichel = 0, herz = 1, laub = 2, schell = 3
     * 
     * addr = (4 * valueRel) + coloroffset
     * --> eichel7 = 0, eichel8 = 4, ... eichelKoenig = 24, eichelSau = 28
     * --> ...
     * --> schell7 = 3, schell8 = 7, ... schellKoenig = 27, schellSau = 31
     * 
     * weli is last (32)
     * </pre>
     */
    private static int calcCardPoolIndex(Color color, Value value) {
        if (color == null || value == null) {
            assert value == null : value;
            assert color == null : color;
            return grid.length - 1; // weli is last (32)
        }

        // savely use numeral "4" below:
        assert Color.values().length == 4 : Color.values().length;
        
        return 4 * (value.intValue - Value.sieben.intValue) + color.intValue;
    }
    
    public static Card card(Color color, Value value) {
        return grid[calcCardPoolIndex(color, value)];
    }


    public static List<Card> newCardDeck(IConfig config) {
        List<Card> deck = new CardList(DECK_PROTOTYPE.size());

        for (Card c : DECK_PROTOTYPE) {
            Card c2 = new Card(c, config);
            deck.add(c2);
        }
        
        return deck;
    }
    
    static boolean isPictureValue(Value value) {
        switch (value) {
            case koenig:
            case ober:
            case sau:
            case unter:
                return true;
        }
        return false;
    }
}
