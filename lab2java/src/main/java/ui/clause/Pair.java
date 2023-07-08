package ui.clause;

import java.util.Objects;

public class Pair {
    private final Integer generatedByClause1;

    private final Integer generatedByClause2;


    public Pair(Integer generatedByClause1, Integer generatedByClause2) {
        this.generatedByClause1 = generatedByClause1;
        this.generatedByClause2 = generatedByClause2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return generatedByClause1.equals(pair.generatedByClause1) && generatedByClause2.equals(pair.generatedByClause2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(generatedByClause1, generatedByClause2);
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", generatedByClause1,generatedByClause2);
    }
}
