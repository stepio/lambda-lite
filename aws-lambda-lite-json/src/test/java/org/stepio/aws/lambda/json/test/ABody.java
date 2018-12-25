package org.stepio.aws.lambda.json.test;

import java.util.List;

public class ABody {

    private String name;
    private Integer quantity;
    private List<APart> elements;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<APart> getElements() {
        return elements;
    }

    public void setElements(List<APart> elements) {
        this.elements = elements;
    }
}
