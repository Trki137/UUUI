package ui.table;

import java.util.*;

public class Table {
    private final List<String> header;

    private final List<TableRow> rows;

    private String finalFeature;


    public Table(List<String> data) {
        Objects.requireNonNull(data);
        this.header = new ArrayList<>();
        this.rows = new ArrayList<>();

        constructTable(data);
    }

    public Table(List<TableRow> rows, List<String> header,String finalFeature) {
        this.rows = rows;
        this.header = header;
        this.finalFeature = finalFeature;
    }

    private void constructTable(List<String> data) {
        int numOfColumns = -1;
        for (String line : data) {
            String[] split = line.split(",");
            if (numOfColumns == -1) {
                numOfColumns = split.length;
                header.addAll(new ArrayList<>(List.of(split)));
                finalFeature = header.get(header.size() - 1);
                header.remove(header.size()-1);
                continue;
            }
            rows.add(new TableRow(split));
        }
    }

    public Table getSubTable(String attributeName, String value) {
        int index =attributeName.equals(finalFeature) ? header.size() : this.header.indexOf(attributeName);

        if (index < 0)
            throw new IllegalArgumentException("Invalid header name.");

        List<TableRow> subTable = this.rows.stream().filter(row -> Objects.equals(row.getValue(index), value)).toList();

        return new Table(subTable, this.header,this.finalFeature);
    }

    public Table getSubTable(List<String> header, List<String> value) {
        List<TableRow> subTable = new ArrayList<>(rows);

        for (int i = 0; i < header.size(); i++) {
            String headerValue = header.get(i);
            String attributeValue = value.get(i);

            int index = this.header.indexOf(headerValue);
            if (index < 0)
                throw new IllegalArgumentException("Invalid header name.");

            subTable = subTable.stream().filter(row -> Objects.equals(row.getValue(index), attributeValue)).toList();
        }

        return new Table(subTable, this.header,this.finalFeature);
    }

    public Set<String> finalFeatureValues() {
        Set<String> classes = new HashSet<>();
        int lastIndex = header.size();

        rows.forEach(row -> classes.add(row.getValue(lastIndex)));

        return classes;
    }

    public List<String> getAttributeValues(String attributeName) {
        Set<String> attributes = new HashSet<>();
        int index = header.indexOf(attributeName);

        rows.forEach(row -> attributes.add(row.getValue(index)));

        return new ArrayList<>(attributes);
    }

    public List<String> getHeader() {
        return header;
    }

    public List<TableRow> getRows() {
        return rows;
    }

    public int numOfRows(){
        return rows.size();
    }

    public String getFinalFeature() {
        return finalFeature;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        header.forEach(value -> sb.append(value).append(" ".repeat(3)));

        sb.append("\n");
        header.forEach(value -> sb.append("=".repeat(3 + value.length())));
        sb.append("\n");

        rows.forEach(row -> {
            row.forEach(rowValue -> sb.append(rowValue).append(" ".repeat(3)));
            sb.append("\n");
        });

        return sb.toString();
    }
}
