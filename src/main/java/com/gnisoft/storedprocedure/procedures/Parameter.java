package com.gnisoft.storedprocedure.procedures;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlReturnType;


public class Parameter {




    private String name;
    private int type;
    private Object value;
    private Direction parameterDirection;

    private RowMapper rowMapper;

    private SqlReturnType sqlReturnType;


    public Parameter(String name, int type, Object value, Direction parameterDirection, RowMapper rowMapper) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.parameterDirection = parameterDirection;
        this.rowMapper = rowMapper;
    }

    public Parameter(String name, int type, Object value, Direction parameterDirection, RowMapper rowMapper, SqlReturnType sqlReturnType) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.parameterDirection = parameterDirection;
        this.rowMapper = rowMapper;
        this.sqlReturnType = sqlReturnType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Direction getParameterDirection() {
        return parameterDirection;
    }

    public void setParameterDirection(Direction parameterDirection) {
        this.parameterDirection = parameterDirection;
    }

    public RowMapper getRowMapper() {
        return rowMapper;
    }

    public void setRowMapper(RowMapper rowMapper) {
        this.rowMapper = rowMapper;
    }

    public SqlReturnType getSqlReturnType() {
        return sqlReturnType;
    }

    public void setSqlReturnType(SqlReturnType sqlReturnType) {
        this.sqlReturnType = sqlReturnType;
    }
}
