package com.gnisoft.storedprocedure.procedures;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlReturnType;

import java.util.function.Consumer;

public class ParameterBuilder {

    public String name;
    public int type;
    public Object value;
    public Direction parameterDirection;
    public RowMapper rowMapper;

    private SqlReturnType sqlReturnType;


    public ParameterBuilder(String name, int type){
        this.name=name;
        this.type=type;
    }

    public ParameterBuilder(String name, int type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }


    public ParameterBuilder withDirection(Direction direction){
        this.parameterDirection=direction;
        return this;

    }

    public ParameterBuilder withValue(Object value){
        this.value=value;
        return this;
    }

    public ParameterBuilder withSqlReturnType(SqlReturnType type){
        this.sqlReturnType=type;
        return this;
    }

    public ParameterBuilder withRowMapper(RowMapper rowMapper){
        this.rowMapper=rowMapper;
        return this;
    }


    public ParameterBuilder with(
            Consumer<ParameterBuilder> builderFunction) {
        builderFunction.accept(this);
        return this;
    }

    public Parameter createParameter() {
        return
                new Parameter(name, type, value, parameterDirection,rowMapper,sqlReturnType);
    }
}
