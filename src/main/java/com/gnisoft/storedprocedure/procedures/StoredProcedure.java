package com.gnisoft.storedprocedure.procedures;


import com.gnisoft.storedprocedure.exceptions.ParameterTypeException;
import com.gnisoft.storedprocedure.exceptions.ParameterTypeUncheckedException;
import com.gnisoft.storedprocedure.exceptions.ProcedureException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;


public class StoredProcedure {

    private String packageName;
    private String procedureName;
    private String schemaName;
    private JdbcTemplate jdbcTemplate;
    private List<Parameter> parameters;
    private SimpleJdbcCall jdbcCall;

    private static List<Integer> realTypes = null;

    static {
        realTypes = Arrays.stream(Types.class.getDeclaredFields()).map(i -> {
            try {
                return i.getInt(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
    }

    public String getPackageName() {
        return packageName;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public String getSchemaName() {
        return schemaName;
    }


    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public StoredProcedure procedureName(final String procedureName) {
        this.procedureName = procedureName;
        return this;
    }


    public StoredProcedure schemaName(final String schemaName) {
        this.schemaName = schemaName;
        return this;
    }

    public StoredProcedure packageName(final String packageName) {
        this.packageName = packageName;
        return this;
    }

    public StoredProcedure jdbcTemplate(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcCall = new SimpleJdbcCall(this.jdbcTemplate);
        return this;
    }


    public StoredProcedure parameters(final List<Parameter> parameters) {
        this.parameters = parameters;
        return this;
    }


    private boolean checkType(int type) {
        for (Integer a : realTypes) {
            if (a.equals(type)) {
                return true;
            }

        }
        return false;

    }


    private boolean checkParameter(final Parameter p) throws ParameterTypeException {
        if (!checkType(p.getType())) {
            throw new ParameterTypeException("Parameter type must be valid Oracle Sql Types... watch java.sql.Types...");
        }

        if (p.getParameterDirection() == Direction.OUT && p.getType() == Types.REF && p.getRowMapper() == null) {
            throw new ParameterTypeException("Ref output parameter must have rowMapper specified..");
        }

        if (p.getType() == Types.REF && (p.getParameterDirection() == Direction.IN || p.getParameterDirection() == null)) {
            throw new ParameterTypeException("Input parameter can not have a ref type......");
        }

        return true;
    }



    public Map<String, Object> execute() throws ProcedureException, ParameterTypeException {
        Map<String, Object> executionResult = null;
        try {
            final Map<String, Object> parameterValues = new HashMap<>();
            List<Parameter> returingResultSets = new ArrayList<>();
            List<SqlParameter> sqlParameters = parameters.stream().map(i -> {
                Direction direction = Optional.ofNullable(i.getParameterDirection()).orElse(Direction.IN);
                try {
                    checkParameter(i);
                } catch (ParameterTypeException e) {
                    throw new ParameterTypeUncheckedException(e.getMessage());
                }

                if (direction == Direction.OUT) {
                    if (i.getType() == Types.REF) {
                        returingResultSets.add(i);
                    }
                    if(i.getSqlReturnType()!=null){
                       return new SqlOutParameter(i.getName(),i.getType(),null,i.getSqlReturnType());
                    }
                    return new SqlOutParameter(i.getName(), i.getType());
                }else if(direction==Direction.INOUT){
                     parameterValues.put(i.getName(),i.getValue());
                     return  new SqlInOutParameter(i.getName(),i.getType());
                }
                else {
                    parameterValues.put(i.getName(), i.getValue());
                    return new SqlParameter(i.getName(), i.getType());
                }
            }).collect(Collectors.toList());
            SqlParameter[] declaredParemeters = new SqlParameter[sqlParameters.size()];
            sqlParameters.toArray(declaredParemeters);
            jdbcCall.withCatalogName(this.packageName).withSchemaName(this.schemaName).
                    withProcedureName(this.procedureName).
                    declareParameters(declaredParemeters);

            //Calculate returning resultsets ....

            returingResultSets.forEach(p -> {

                jdbcCall.returningResultSet(p.getName(),p.getRowMapper());
            });

            executionResult = jdbcCall.execute(parameterValues);

        } catch (Exception ex) {
            if (ex instanceof DataAccessException || ex instanceof DataAccessResourceFailureException)
                throw new ProcedureException(ex.getMessage());
            else if (ex instanceof ParameterTypeUncheckedException)
                throw new ParameterTypeException(ex.getMessage());
            else throw ex;
        }
        return executionResult;

    }


}










