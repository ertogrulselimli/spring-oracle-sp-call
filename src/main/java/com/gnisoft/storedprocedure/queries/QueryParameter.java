package com.gnisoft.storedprocedure.queries;




public  final class QueryParameter {


    private String name;

    private Object value;

    public QueryParameter(String name,Object value){
        this.name=name;
        this.value=value;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
