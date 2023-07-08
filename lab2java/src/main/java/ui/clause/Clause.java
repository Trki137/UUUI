package ui.clause;

import java.util.*;

public class Clause {
    private final Set<String> literals;

    private String clause;

    private final int number;

    private final Pair pair;

    private ClausePair createdBy;



    public Clause(String clause, int number) {
        this(clause, number, null);
    }

    public Clause(String clause, int number, Pair pair) {
        this.clause = clause.toLowerCase();
        this.number = number;
        this.literals = setLiterals();
        this.pair = pair;
    }

    private Set<String> setLiterals() {
        String[] split = clause.split(" v ");
        Set<String> literals = new HashSet<>();

        StringBuilder sb = new StringBuilder();
        for (String literal : split) {
            boolean isAdded = literals.add(literal.trim());

            if(isAdded)
                sb.append(literal).append(" v ");
        }

        clause = sb.replace(sb.lastIndexOf(" v "),sb.length(),"").toString();

        return literals;
    }

    public boolean isTautology() {
        Set<String> hashSet = new HashSet<>();

        for (String literal : literals) {
            String literalWithoutNegation = literal.replace("~", "");
            hashSet.add(literalWithoutNegation);
        }

        return hashSet.size() != literals.size();
    }


    public Set<String> getLiterals() {
        return literals;
    }
    public String getClause() {
        return clause;
    }

    public int getNumber() {
        return number;
    }

    public void setCreatedBy(ClausePair createdBy) {
        this.createdBy = createdBy;
    }

    public ClausePair getCreatedBy() {
        return createdBy;
    }

    public Pair getPair() {
        return pair;
    }

    @Override
    public String toString() {
        if (Objects.isNull(pair) )
            return String.format("%d. %s", number, clause);

        return String.format("%d. %s %s", number, clause, pair);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clause clause1 = (Clause) o;
        return clause.equals(clause1.clause);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clause);
    }
}
