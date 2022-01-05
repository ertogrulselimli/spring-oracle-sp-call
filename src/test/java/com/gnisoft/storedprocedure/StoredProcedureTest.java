package com.gnisoft.storedprocedure;


import com.gnisoft.storedprocedure.exceptions.ParameterTypeException;
import com.gnisoft.storedprocedure.exceptions.ParameterTypeUncheckedException;
import com.gnisoft.storedprocedure.exceptions.ProcedureException;
import com.gnisoft.storedprocedure.procedures.Parameter;
import com.gnisoft.storedprocedure.procedures.StoredProcedure;
import com.gnisoft.storedprocedure.queries.SqlQuery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.sql.Types;
import java.util.*;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StoredProcedureTest {

    private static final String SCHEMA_NAME = "SCHEMA";
    private static final String PACKAGE_NAME_VALUE = "PACKAGE";
    private static final String PROCEDURE_NAME = "PROCEDURE";
    private static final String OBJ_KEY = "KEY";
    private static final Object OBJ_VALUE = "VALUE";
    private static final String PARAMETER_TYPE_EXCEPTION_MSG = "PARAMETER_TYPE_EXCEPTION";
    private static final String DATA_INTEGRITY_VIOLATION_EXCEPTION_MSG = "INTEGRITY VIOLATION EXCEPTION";
    private static final String RUNTIME_EXCEPTION_MSG = "RUN TIME EXCEPTION MESSAGE";
    private static final Integer MAP_RESULT_SIZE = 1;
    private static final Integer CAPTOR_VALUES_SIZE = 3;
    @InjectMocks
    public final StoredProcedure storedProcedure = new StoredProcedure();
    @Spy
    private List<SqlParameter> list = new ArrayList<>();
    @Mock
    private SimpleJdbcCall simpleJdbcCall;
    @Mock
    private List<Parameter> parameters;
    @Mock
    private Stream<Parameter> parameterStream;
    @Mock
    private Stream<SqlParameter> sqlParameterStream;
    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;
    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUpMock() {
        setUpDataForStoredProcedure();
        setUpMockJdbcCall();
    }

    @Test
    public void givenWhenSqlParametersThenThrowDataAccessException() throws ParameterTypeException, ProcedureException {
        //Arrange
        setUpCommonStubbing();
        when(simpleJdbcCall.execute(anyMap())).thenThrow(new DataIntegrityViolationException(DATA_INTEGRITY_VIOLATION_EXCEPTION_MSG));
        //Act
        exception.expect(ProcedureException.class);
        exception.expectMessage(DATA_INTEGRITY_VIOLATION_EXCEPTION_MSG);
        storedProcedure.execute();

    }

    @Test
    public void givenWhenSqlParametersThenThrowParameterTypeException() throws ParameterTypeException, ProcedureException {
        //Arrange
        setUpCommonStubbing();
        when(simpleJdbcCall.execute(anyMap())).thenThrow(new ParameterTypeUncheckedException(PARAMETER_TYPE_EXCEPTION_MSG));
        //Act
        exception.expect(ParameterTypeException.class);
        exception.expectMessage(PARAMETER_TYPE_EXCEPTION_MSG);
        storedProcedure.execute();
    }

    @Test
    public void givenWhenSqlParametersThenThrowRuntimeException() throws ParameterTypeException, ProcedureException {
        //Arrange
        setUpCommonStubbing();
        when(simpleJdbcCall.execute(anyMap())).thenThrow(new RuntimeException(RUNTIME_EXCEPTION_MSG));
        //Act
        exception.expect(RuntimeException.class);
        exception.expectMessage(RUNTIME_EXCEPTION_MSG);
        storedProcedure.execute();
    }

    @Test
    public void givenSqlParameterWhenNotNullThenResult() throws ParameterTypeException, ProcedureException {

        //Arrange
        setUpCommonStubbing();
        Map<String, Object> objectMap = createMapResult();
        when(simpleJdbcCall.execute(anyMap())).thenReturn(objectMap);
        //Act
        Map<String, Object> mapResult = storedProcedure.execute();
        //Assert
        verify(simpleJdbcCall).withCatalogName(stringArgumentCaptor.capture());
        verify(simpleJdbcCall).withSchemaName(stringArgumentCaptor.capture());
        verify(simpleJdbcCall).withProcedureName(stringArgumentCaptor.capture());
        verify(simpleJdbcCall).execute(anyMap());
        verify(list).toArray(any(SqlParameter[].class));
        errorCollector.checkThat(mapResult.size(), equalTo(MAP_RESULT_SIZE));
        errorCollector.checkThat(mapResult.containsKey(OBJ_KEY), is(true));
        errorCollector.checkThat(mapResult.containsValue(OBJ_VALUE), is(true));
        final List<String> values = stringArgumentCaptor.getAllValues();
        errorCollector.checkThat(values.size(), equalTo(CAPTOR_VALUES_SIZE));
    }

    private Map<String, Object> createMapResult() {
        final Map<String, Object> objectMap = new HashMap<>();
        objectMap.put(OBJ_KEY, OBJ_VALUE);
        return objectMap;
    }


    private List<SqlParameter> createSqlParameter() {
        final SqlParameter sqlParameterInteger = new SqlParameter(Types.INTEGER);
        list.add(sqlParameterInteger);
        return list;

    }

    private void setUpDataForStoredProcedure() {
        storedProcedure.schemaName(SCHEMA_NAME);
        storedProcedure.packageName(PACKAGE_NAME_VALUE);
        storedProcedure.procedureName(PROCEDURE_NAME);
    }

    private void setUpMockJdbcCall() {
        when(simpleJdbcCall.withCatalogName(eq(PACKAGE_NAME_VALUE))).thenReturn(simpleJdbcCall);
        when(simpleJdbcCall.withSchemaName(eq(SCHEMA_NAME))).thenReturn(simpleJdbcCall);
        when(simpleJdbcCall.withProcedureName(eq(PROCEDURE_NAME))).thenReturn(simpleJdbcCall);
    }

    private void setUpCommonStubbing() {
        List<SqlParameter> sqlParameters = createSqlParameter();
        when(sqlParameterStream.collect(any())).thenReturn(sqlParameters);
        when(parameterStream.map(Matchers.<Function<Parameter, SqlParameter>>any())).thenReturn(sqlParameterStream);
        when(parameters.stream()).thenReturn(parameterStream);
    }




}
