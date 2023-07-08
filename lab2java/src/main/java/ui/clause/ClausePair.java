package ui.clause;

public class ClausePair {

    private final Clause firstClause;

    private final Clause secondClause;

    public ClausePair(Clause firstClause, Clause secondClause) {
        this.firstClause = firstClause;
        this.secondClause = secondClause;
    }

    public Clause resolve(int number) {

        for (String firstClauseLiteral : firstClause.getLiterals()) {
            boolean isNegated = firstClauseLiteral.contains("~");

            for (String secondClauseLiteral : secondClause.getLiterals()) {

                if(isNegated){
                    if(secondClauseLiteral.equals(firstClauseLiteral.replace("~","")))
                        return constructClause(firstClauseLiteral,number);

                }else {
                    if(secondClauseLiteral.equals("~"+firstClauseLiteral))
                        return constructClause(firstClauseLiteral,number);
                }

            }
        }

        return null;
    }

    private Clause constructClause(String removedLiteral, int number) {
        Pair pair = new Pair(firstClause.getNumber(), secondClause.getNumber());

        removedLiteral = removedLiteral.replace("~", "");
        StringBuilder sb = new StringBuilder();

        if (firstClause.getLiterals().size() == 1
                && secondClause.getLiterals().size() == 1) return null;

        removedLiteral = removedLiteral.replace("~", "");

        for (String literal : firstClause.getLiterals()) {
            if (literal.replace("~", "").equals(removedLiteral)) continue;

            sb.append(literal).append(" v ");
        }

        for (String literal : secondClause.getLiterals()) {
            if (literal.replace("~", "").equals(removedLiteral)) continue;

            sb.append(literal).append(" v ");
        }

        sb.replace(sb.lastIndexOf(" v "), sb.length(), "");

        return new Clause(sb.toString(), number, pair);
    }


    public static boolean isCompatible(Clause firstClause, Clause secondClause) {
        for (String firstClauseLiteral : firstClause.getLiterals()) {
            boolean isNegated = firstClauseLiteral.contains("~");
            for (String secondClauseLiteral : secondClause.getLiterals()) {
                if(firstClauseLiteral.equals(secondClauseLiteral)) continue;

                if (isNegated) {
                    if (secondClauseLiteral.equals(firstClauseLiteral.replace("~", "")) && !secondClauseLiteral.contains("~"))
                        return true;
                } else {
                    if (secondClauseLiteral.equals("~"+firstClauseLiteral) && secondClauseLiteral.contains("~"))
                        return true;
                }
            }
        }
        return false;
    }


    public Clause getFirstClause() {
        return firstClause;
    }

    public Clause getSecondClause() {
        return secondClause;
    }
}

