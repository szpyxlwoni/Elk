package com.thoughtworks.elk.container;

import java.util.ArrayList;

public class Bean {
    private String id;
    private String clazz;
    private ArrayList ref;
    private ArrayList type;

    public void setId(String id) {
        this.id = id;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public void setRef(ArrayList ref) {
        this.ref = ref;
    }

    public String getId() {
        return id;
    }

    public ArrayList getRef() {
        return ref;
    }

    public String getClazz() {
        return clazz;
    }

    public void setType(ArrayList type) {
        this.type = type;
    }

    public ArrayList getType() {
        return type;
    }
}
