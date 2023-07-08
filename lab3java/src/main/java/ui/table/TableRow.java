package ui.table;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class TableRow implements Iterable<String>{
    private final String[] values;

    private final String finalFeatureValue;

    public TableRow(String[] values){
        Objects.requireNonNull(values);
        this.values = Arrays.stream(values, 0, values.length - 1).toArray(String[]::new);
        this.finalFeatureValue = values[values.length - 1];
    }

    public String getValue(int index){
        if(index == values.length) return finalFeatureValue;

        if(index < 0 || index > values.length-1)
            throw new IllegalArgumentException("Invalid index.");

        return values[index];
    }

    public int size(){
        return values.length;
    }

    public String getFinalFeatureValue() {
        return finalFeatureValue;
    }

    @Override
    public Iterator<String> iterator() {
        return new RowIterator(values);
    }

    private static class RowIterator implements Iterator<String>{
        private final String[] values;
        private int current;

        public RowIterator(String[] values){
            this.values = values;
            this.current = 0;
        }
        @Override
        public boolean hasNext() {
            return current < values.length;
        }

        @Override
        public String next() {
            if(!hasNext())
                throw new NoSuchElementException();

            return values[current++];
        }
    }
}
