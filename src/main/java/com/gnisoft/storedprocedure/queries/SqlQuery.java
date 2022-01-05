package com.gnisoft.storedprocedure.queries;

import com.gnisoft.storedprocedure.exceptions.SqlQueryException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SqlQuery {

    private String sql;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private List<QueryParameter> queryParameters=new ArrayList<>();


    public SqlQuery() {

    }


    public SqlQuery(String sql) {
        this.sql = sql;
    }

    public SqlQuery(String sql, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.sql = sql;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }


    public SqlQuery jdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate){
        this.namedParameterJdbcTemplate=jdbcTemplate;
        return this;
    }


    public SqlQuery sql(String sql){
        this.sql=sql;
        return this;

    }


    public SqlQuery addParameter(String name,Object value){

        this.queryParameters.add(new QueryParameter(name,value));
        return this;
    }



    public <T> List<T>   query(RowMapper<T> mapper) throws SqlQueryException {
        List<T> result=null;
        try {
        Map<String, Object> parameters = new HashMap<>();
        this.queryParameters.stream().forEach(i -> {
            parameters.put(i.getName(), i.getValue());
        });

         result = namedParameterJdbcTemplate.query(this.sql, parameters, mapper);
        }catch (Exception ex){
            if(ex instanceof DataAccessException || ex instanceof DataAccessResourceFailureException){
                throw new SqlQueryException(ex.getMessage());
            }else{
                throw ex;
            }
        }
        return result;
    }


    public <T> T queryForObject(RowMapper<T> mapper) throws SqlQueryException{
        T result=null;
        try {
            Map<String, Object> parameters = new HashMap<>();
            this.queryParameters.stream().forEach(i -> {
                parameters.put(i.getName(), i.getValue());
            });

            result = namedParameterJdbcTemplate.queryForObject(this.sql, parameters, mapper);
        }catch (Exception ex){
            if(ex instanceof DataAccessException || ex instanceof DataAccessResourceFailureException){
                throw new SqlQueryException(ex.getMessage());
            }else{
                throw ex;
            }
        }
        return result;

    }



    public <T> T queryForValue(Class<T> requiredType) throws SqlQueryException{
        T t=null;
        try {

            Map<String, Object> parameters = new HashMap<>();
            this.queryParameters.stream().forEach(i -> {
                parameters.put(i.getName(), i.getValue());
            });

           t = namedParameterJdbcTemplate.queryForObject(this.sql, parameters, requiredType);

        }catch (Exception ex){
            if(ex instanceof DataAccessResourceFailureException || ex instanceof DataAccessException) {
                throw new SqlQueryException(ex.getMessage());

            }else{
                throw ex;
            }

        }
        return t;
    }




}
