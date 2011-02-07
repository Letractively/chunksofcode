package com.myapp.util.soundsorter.wizard.model;




public final class Match implements Comparable<Match> 
{
    private final String value1, value2;
    private final double matchRate;

    public Match(String value1, String value2, double matchRate) {
        if (value1 == null) throw new NullPointerException();
        if (value2 == null) throw new NullPointerException();
        if (matchRate <= 0) throw new IllegalArgumentException(String.valueOf(matchRate));

        this.value1 = value1;
        this.value2 = value2;
        this.matchRate = matchRate;
    }

    public String getValue1() {
        return value1;
    }

    public String getValue2() {
        return value2;
    }

    public double getMatchRate() {
        return matchRate;
    }

    public int hashCode() {
        int prime = 31, result = 1;
        result = prime * result + ((value1 == null) ? 0 : value1.hashCode());
        result = prime * result + ((value2 == null) ? 0 : value2.hashCode());
        return result;
    }

    public boolean equals(Object o) {
        Match m = (Match) o;
        return this == o || (value1.equals(m.value1) && value2.equals(m.value2));
    }

    public int compareTo(Match o) {
        return Double.valueOf(matchRate).compareTo(Double.valueOf(o.matchRate));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Match [matchRate=");
        builder.append(matchRate);
        builder.append(", value1=");
        builder.append(value1);
        builder.append(", value2=");
        builder.append(value2);
        builder.append("]");
        return builder.toString();
    }
}
