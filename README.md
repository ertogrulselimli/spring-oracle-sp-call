## Oracle Stored Procedure call library developed on top of Spring jdbc

#### Below is the sample for calling Oracle Stored Procedure and Sql query



```
        List<Parameter> parameters=new ArrayList<>();
        parameters.add(new ParameterBuilder("P_PARAM_NAME1",Types.VARCHAR,"test1").createParameter());
        parameters.add(new ParameterBuilder("P_PARAM_NAME2",Types.VARCHAR,"test2").createParameter());
        parameters.add(new ParameterBuilder("P_PARAM_NAME3",Types.VARCHAR,"test3").createParameter());
        parameters.add(new ParameterBuilder("P_PARAM_NAME4",Types.REF).withDirection(Direction.OUT).withRowMapper(
                new BeanPropertyRowMapper(ExampleModel.class)
        ).createParameter());
        parameters.add(new ParameterBuilder("P_ERROR_CODE",Types.INTEGER).withDirection(Direction.OUT).createParameter());
        parameters.add(new ParameterBuilder("P_ERROR_DESC",Types.VARCHAR).withDirection(Direction.OUT).createParameter());

        Map<String, Object> execute = new StoredProcedure().
                procedureName("P_TEST_PROCEDURE").
                packageName("P_TEST_PACKAGE").
                schemaName("TEST_SCHEMA").jdbcTemplate(jdbcTemplate).parameters(parameters).execute();
        List<ExampleModel> p_accounts = (List<ExampleModel>) execute.get("P_PARAM_NAME4");
    
```
   
```
SqlQuery sql=new SqlQuery("SELECT ID ID ,FIRST_NAME FIRSTNAME,LAST_NAME LASTNAME FROM TESTTABLE where id=:P_ID",namedParameterJdbcTemplate);
           List<TestModel> query = sql.
                   addParameter("P_ID",1).
                   query(new BeanPropertyRowMapper<>(TestModel.class));







```
    
    
### Usage 


```  
     after cloning the project mvn clean install 
     because no remote repository exists you will use your local repository 
     then import in your project like this 
     
             <dependency>
                 <groupId>com.gnisoft</groupId>
                 <artifactId>oracle-sp-call</artifactId>
                 <version>1.0.0</version>
             </dependency>  
             
      Now enjoy the library      
      
      ```
       
      
                   