package com.thoughtworks.elk.container;

public class Bean {
    private String id;
    private String clazz;
    private String ref;
    private String type;

    public void setId(String id) {
        this.id = id;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getId() {
        return id;
    }

    public String getRef() {
        return ref;
    }

    public String getClazz() {
        return clazz;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
