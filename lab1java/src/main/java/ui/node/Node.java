package ui.node;

import java.util.List;
import java.util.Objects;

public class Node {

    private final Node parent;

    private final double cost;

    private final String value;

    private final double heuristicValue;


    public Node(Node parent,double cost, String value, double heuristicValue){
        this.parent = parent;
        this.cost = cost;
        this.value = value;
        this.heuristicValue = heuristicValue;
    }

    public Node(Node parent, double cost, String value){
        this(parent,cost,value,0.0);
    }

    public double getCost() {
        return cost;
    }

    public double getHeuristicValue() {
        return heuristicValue;
    }

    public Node getParent() {
        return parent;
    }

    public String getValue() {
        return value;
    }


    @Override
    public String toString() {
        return String.format("Name: %s, Cost: %s, heuristicCost: %f ,parent: %s", value,cost,heuristicValue ,Objects.isNull(parent) ? "" : parent.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(value, node.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
