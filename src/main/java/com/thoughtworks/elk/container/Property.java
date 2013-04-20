package com.thoughtworks.elk.container;

public class Property {
    private String name;
    private String ref;
    private String type;

    public Property(String name, String ref, String type) {
        this.name = name;
        this.ref = ref;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getRef() {
        return ref;
    }

    public String getType() {
        return type;
    }
}
