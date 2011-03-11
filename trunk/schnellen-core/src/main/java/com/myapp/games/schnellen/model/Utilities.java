package com.myapp.games.schnellen.model;

import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.myapp.games.schnellen.model.Card.Color;
import com.myapp.games.schnellen.model.Card.Value;


public final class Utilities {
    
    
    private static final SpecialCardComparator SPECIAL_CARD_COMPARATOR = new SpecialCardComparator();


    private static Comparator<Card> handSorter = new Utilities.SortByColor();

    /**
     * sorts the cards in the player's hand
     * 
     * @param name
     *            the player's name
     */
    static final void sortHand(List<Card> hand) {
        sort(hand, reverseOrder(handSorter));
    }
    
    /**
     * sorts cards by color, value, special cards first
     *
     * @author andre
     *
     */
    public static final class SortByColor implements Comparator<Card> {
        @Override
        public int compare(Card o1, Card o2) {
            int special = SPECIAL_CARD_COMPARATOR.compare(o1, o2);
            if (special != 0)
                return special; 

            Color c1 = o1.getColor();
            Color c2 = o2.getColor();

            if (c1 == null)
                return -1;
            if (c2 == null)
                return 1;
            if (c1 != c2)
                return c1.compareTo(c2);

            return o1.getValue().compareTo(o2.getValue());
        }
    }

    /**
     * sorts cards by value, color, special cards first
     *
     * @author andre
     *
     */
    public static final class SortByValue implements Comparator<Card> {
        @Override
        public int compare(Card o1, Card o2) {
            int special = SPECIAL_CARD_COMPARATOR.compare(o1, o2);
            if (special != 0)
                return special;

            Value v1 = o1.getValue();
            Value v2 = o2.getValue();

            if (v1 == null)
                return -1;
            if (v2 == null)
                return 1;
            if (v1 != v2)
                return v1.compareTo(v2);

            return o1.getColor().compareTo(o2.getColor());
        }
    }

    /**
     *  sorts special cards first
     * @author andre
     *
     */
    private static final class SpecialCardComparator implements Comparator<Card> {
        @Override
        public int compare(Card o1, Card o2) {
            assert o1 != o2 : o1;
            boolean o1Special = o1.isSpecialCard();
            boolean o2Special = o2.isSpecialCard();

            if (! o1Special && o2Special)
                return -1;
            if (o1Special && ! o2Special)
                return 1;

            if (o1Special && o2Special) {
                assert    (o1.equals(Card.PAPA) && o2.equals(Card.WELI))
                       || (o1.equals(Card.WELI) && o2.equals(Card.PAPA)) : o1 + " " + o2;
                return o1.equals(Card.PAPA) ? 1 : 0;
            }

            return 0; // no special cards
        }
    }



    static final class EntriesByValueComparator
                                 implements Comparator<Entry<String, Integer>> {
        @Override
        public int compare(Entry<String,Integer> o1, Entry<String,Integer> o2) {
            return o1.getValue().compareTo(o2.getValue());
        }
    }

    static final class MapByValueComparator <K, V extends Comparable<V>>
                                                    implements Comparator<K> {
        Map<K, V> _m;

        public MapByValueComparator(Map<K, V> m) {
            _m = m;
        }

        @Override
        public int compare(K o1, K o2) {
            V v1 = _m.get(o1), v2 = _m.get(o2);
            assert v1 != null;
            assert v2 != null;
            return v1.compareTo(v2);
        }
    }


    static final class RotatingIterable<T> implements Iterable<T> {
        private final List<T> list;
        private final int offset;

        public RotatingIterable(List<T> list, int offset) {
            this.offset = offset;
            this.list = list;
        }
        @Override
        public Iterator<T> iterator() {
            return new RotatingIterator<T>(list, offset);
        }
    }


    static final class RotatingIterator<T> implements Iterator<T> {

        private final int first;
        private int i;
        private boolean initial = true;
        private final List<T> list;

        private RotatingIterator(List<T> list, int offset) {
            this.list = list;
            if (offset < 0 || offset > this.list.size() - 1)
                throw new IllegalArgumentException(offset+" , "+this.list);
            i = first = offset;
        }

        @Override
        public boolean hasNext() {
            return i != first || initial;
        }

        @Override
        public T next() {
            i = ((i + 1) % list.size());
            T next = list.get(i);
            initial = false;
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    static final int longestStringLength(Collection<String> strings) {
        int longestNameLength = -1;
        for (String s : strings)
            if (s.length() > longestNameLength)
                longestNameLength = s.length();
        return longestNameLength;
    }
}
