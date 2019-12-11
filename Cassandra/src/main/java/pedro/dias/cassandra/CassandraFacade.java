/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedro.dias.cassandra;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.LocalDate;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import pedro.dias.model.annotations.CassandraColumn;
import pedro.dias.model.annotations.CassandraColumnPartitionKey;
import pedro.dias.model.annotations.CassandraJoinColumn;
import pedro.dias.model.annotations.CassandraViewTable;
import pedro.dias.cassandra.model.constants.Constants;
import pedro.dias.cassandra.model.interfaces.CassandraTable;
import pedro.dias.cassandra.model.interfaces.CassandraTablePayload;

/**
 * Classe responsável por disponíbilizar uma abstração ao driver do cassandra
 * para inserção e pesquisa de dados.
 *
 * @author paafonso
 */
public class CassandraFacade {

    /**
     * Método que permite inserir um objecto na base de dados, que implemente a
     * interface CassandraTable.
     *
     * @param <T extends
     * @link{pedro.dias.cassandra.model.interfaces.CassandraTable}>
     * @param entry
     * @throws Exception
     */
    public static <T extends CassandraTable> void insert(T entry) throws Exception {
        insert(entry, null);
    }

    /**
     *
     * Método que permite inserir um objecto na base de dados, que implemente a
     * interface CassandraTable, com indicação de tempo de vida desse registo
     * (Time To Live).
     *
     * @param <T>extends {@link
     * pedro.dias.cassandra.model.interfaces.CassandraTable}>
     * @param entry
     * @param timeToLive @link{pedro.dias.cassandra.enums.CassandraTTLEnum}
     * @throws Exception
     */
    public static <T extends CassandraTable> void insert(T entry, Integer timeToLive) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.CLAUSE_INSERT_INTO).append(Constants.SEPARATOR_SPACE).append(entry.getKeySpace()).append(Constants.SEPARATOR_DOT).append(entry.getTableName()).append(Constants.SEPARATOR_SPACE);
        Map<String, Object> mapFields = objectToCQL(entry, sb);
        if (null != timeToLive && timeToLive > 0) {
            sb.append(Constants.SEPARATOR_SPACE).append(Constants.CLAUSE_USING_TTL).append(Constants.SEPARATOR_SPACE).append(timeToLive);
        }
        String cql = sb.toString();

        PreparedStatement insertStatementLog = ConnectionCassandra.getPreparedStatement(cql);
        insertStatementLog.setConsistencyLevel(ConsistencyLevel.valueOf(System.getProperty(Constants.PROPERTY_CONSISTENCY_LEVEL, Constants.PROPERTY_CONSISTENCY_LEVEL_DEFAULT_VALUE)));
        ConnectionCassandra.getSession().execute(bindStatement(insertStatementLog, mapFields));

        if (null != entry.getPayloadId()) {
            insert(entry.getPayloadId(), timeToLive);
        }
    }

    /**
     * Método que permite a pesquisa retornando a uma tabela específica (clazz).
     * A query é construida com base nos filtros introduzidos no mapa entries.
     *
     * @param <T>extends {@link
     * pedro.dias.cassandra.model.interfaces.CassandraTable}>
     * @param clazz objecto que mapeia a tabela pretendida
     * @param entries mapa de filtros a utilizar na query.
     * @return
     * @throws Exception
     */
    public static <T extends CassandraTable> List<T> query(Class clazz, Map<String, Object> entries) throws Exception {
        return query(clazz, entries, null);
    }

    /**
     * Método que permite a pesquisa retornando a uma tabela específica (clazz).
     * A query é construida com base nos filtros introduzidos no mapa entries.
     * Permite obter colunas específicas da tabela selecionada.
     *
     * @param <T>extends {@link
     * pedro.dias.cassandra.model.interfaces.CassandraTable}>
     * @param clazz objecto que mapeia a tabela pretendida
     * @param entries mapa de filtros a utilizar na query.
     * @param columnsToRetrieve filtra resultados para obter colunas
     * específicas
     * @return
     * @throws Exception
     */
    public static <T extends CassandraTable> List<T> query(Class clazz, Map<String, Object> entries, List<String> columnsToRetrieve) throws Exception {
        Method getNameMethod = clazz.getMethod(Constants.METHOD_GET_TABLE_NAME);
        Method getKeySpaceMethod = clazz.getMethod(Constants.METHOD_GET_KEYSPACE_NAME);
        Object clazzInstance = clazz.newInstance();
        String tableName = (String) getNameMethod.invoke(clazzInstance);
        boolean addAllowFiltering = !entries.keySet().containsAll(getPartitionKeyFromClass(clazz));

        String keyspace = (String) getKeySpaceMethod.invoke(clazzInstance);
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.CLAUSE_SELECT_ALL).append(Constants.CLAUSE_FROM).append(Constants.SEPARATOR_SPACE)
                .append(keyspace).append(Constants.SEPARATOR_DOT).append(tableName).append(Constants.SEPARATOR_SPACE)
                .append(Constants.CLAUSE_WHERE).append(Constants.SEPARATOR_SPACE);
        int countWhereClauses = entries.entrySet().size();
        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            if (entry.getValue() instanceof UUID
                    || entry.getValue() instanceof Integer
                    || entry.getValue() instanceof Float
                    || entry.getValue() instanceof Long
                    || entry.getValue() instanceof Double
                    || entry.getValue() instanceof Boolean) {

                sb.append(entry.getKey()).append(Constants.SEPARATOR_EQUAL).append(entry.getValue().toString());
            } else if (entry.getValue() instanceof Map) {
                HashMap<String, String> map = (HashMap<String, String>) entry.getValue();
                Iterator<String> it = map.keySet().iterator();
                if (map.keySet().size() > 1) {
                    addAllowFiltering = true;
                }
                int countArguments = map.keySet().size();
                while (it.hasNext()) {
                    String key = it.next();
                    sb.append(entry.getKey()).append(Constants.SEPARATOR_SPACE).append(Constants.CLAUSE_CONTAINS).append(Constants.SEPARATOR_SPACE)
                            .append(Constants.SEPARATOR_QUOTE).append(map.get(key)).append(Constants.SEPARATOR_QUOTE);
                    countArguments--;
                    if (countArguments > 0) {
                        sb.append(Constants.SEPARATOR_SPACE).append(Constants.CLAUSE_AND).append(Constants.SEPARATOR_SPACE);
                    }

                }
            } else {
                sb.append(entry.getKey()).append(Constants.SEPARATOR_EQUAL).append(Constants.SEPARATOR_QUOTE).append(entry.getValue().toString()).append(Constants.SEPARATOR_QUOTE);
            }
            countWhereClauses--;
            if (countWhereClauses > 0) {
                sb.append(Constants.SEPARATOR_SPACE).append(Constants.CLAUSE_AND).append(Constants.SEPARATOR_SPACE);
            }
        }
        String select = sb.toString();
        if (addAllowFiltering) {
            select = select + Constants.SEPARATOR_SPACE + Constants.CLAUSE_ALLOW_FILTERING;
        }
        return query(select);
    }

    /**
     * Método que retorna os campos que compões a partition key de uma @link{CassandraTable}.
     * 
     * @param clazz
     * @return 
     */
    private static List<String> getPartitionKeyFromClass(Class clazz) {
        List<String> listaResultados = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(CassandraColumnPartitionKey.class)) {
                listaResultados.add(field.getAnnotation(CassandraColumn.class).name());
            }
        }
        return listaResultados;
    }

    /**
     * Método que decompões uma query, inferindo e retornando os resultados na classe pretendida.
     * Suporta a utilização das views.
     * 
     * @param <T extends @link{CassandraTable}>
     * @param cql 
     * @return
     * @throws Exception 
     */
    public static <T extends CassandraTable> List<T> query(String cql) throws Exception {
        List<T> listaResultados = new ArrayList<>();
        List<String> columnsToRetrieve = new ArrayList<>();
        List<String> filteringColumns = new ArrayList<>();
        String tableName;
        Class className = null;

        try {
            tableName = cql.toLowerCase().split(Constants.CLAUSE_FROM)[1].split(Constants.CLAUSE_WHERE)[0].split(Constants.REGEX_KEYSPACE_TABLE_SEPARATOR)[1].trim();
        } catch (Exception ex) {
            throw new Exception(Constants.MESSAGE_KEYSPACE_TABLE_ERROR, ex);
        }

        for (Class clazz : Utils.getClasses(Constants.PACKAGE_NAME_MODEL)) {

            Method getName = clazz.getDeclaredMethod(Constants.METHOD_GET_TABLE_NAME);
            String name = (String) getName.invoke(clazz.newInstance());
            if (name.equals(tableName)) {
                className = clazz;
            }
        }
        if (className == null) {
            throw new Exception(Constants.MESSAGE_KEYSPACE_TABLE_NOT_FOUND);
        }

        if (cql.toLowerCase().contains(Constants.CLAUSE_SELECT_ALL)) {
            Class tempClass = null;
            if (className.isAnnotationPresent(CassandraViewTable.class)) {
                tempClass = className.getSuperclass();
            } else {
                tempClass = className;
            }
            for (Field field : tempClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(CassandraColumn.class)) {
                    CassandraColumn annotation = (CassandraColumn) field.getAnnotation(CassandraColumn.class);
                    columnsToRetrieve.add(annotation.name());
                }
            }
        }

        String selectClause = cql.toLowerCase().split(Constants.CLAUSE_FROM)[0];
        selectClause = selectClause.replace(Constants.CLAUSE_SELECT, Constants.VALUE_EMPTY);
        if (selectClause.contains(Constants.SEPARATOR_COMMA)) {
            for (String splitted : selectClause.split(Constants.SEPARATOR_COMMA)) {
                columnsToRetrieve.add(splitted.trim());
            }
        } else {
            columnsToRetrieve.add(selectClause.trim());
        }
        if (cql.toLowerCase().split(Constants.CLAUSE_WHERE).length > 1) {
            for (String andClause : cql.toLowerCase().split(Constants.CLAUSE_WHERE)[1].split(Constants.CLAUSE_AND)) {
                filteringColumns.add(andClause.trim().split(Constants.REGEX_WHERE_CLAUSES)[0]);
            }
        }

        if (!filteringColumns.containsAll(getPartitionKeyFromClass(className)) && !cql.toLowerCase().contains(Constants.CLAUSE_ALLOW_FILTERING)) {
            cql = cql + Constants.SEPARATOR_SPACE + Constants.CLAUSE_ALLOW_FILTERING;
        }

        ResultSet resultSet = ConnectionCassandra.getSession().execute(cql);
        Iterator<Row> iter = resultSet.iterator();
        Row row = null;

        while (iter.hasNext()) {
            if (!resultSet.isFullyFetched()) {
                resultSet.fetchMoreResults();
            }
            row = iter.next();
            if (null != row) {
                T object = (T) rowToObject(row, columnsToRetrieve, className);
                listaResultados.add(object);
            }
        }

        return listaResultados;
    }

    /**
     * Método auxiliar para mapear os elementos retornados pelo driver do
     * Cassandra para a classe correspondente no modelo de dados.
     *
     * @param <T>
     * @param row
     * @param columnsToRetrieve
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private static <T extends CassandraTable> CassandraTable rowToObject(Row row, List<String> columnsToRetrieve, Class clazz) throws InstantiationException, IllegalAccessException {
        if (clazz.isAnnotationPresent(CassandraViewTable.class)) {
            clazz = clazz.getSuperclass();
        }

        T objectToReturn = (T) clazz.newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(CassandraColumn.class)) {
                CassandraColumn annotation = (CassandraColumn) field.getAnnotation(CassandraColumn.class);

                if (columnsToRetrieve.contains(annotation.name())) {

                    if (field.isAnnotationPresent(CassandraJoinColumn.class)) {
                        Class subClazz = field.getType();
                        CassandraTablePayload newObj = (CassandraTablePayload) subClazz.newInstance();
                        newObj.setId(row.get(annotation.name(), UUID.class));
                        field.set(objectToReturn, newObj);
                    } else if (field.getType().equals(Map.class)) {
                        field.set(objectToReturn, row.getMap(annotation.name(), String.class, String.class));
                    } else if (field.getType().equals(Calendar.class)) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(row.getDate(annotation.name()).getMillisSinceEpoch());
                        field.set(objectToReturn, cal);
                    } else {
                        field.set(objectToReturn, row.get(annotation.name(), field.getType()));
                    }
                }
            }
        }

        return objectToReturn;
    }

    /**
     * Método auxiliar para mapear os elementos da classe do modelo de dados
     * para a tabela correspondente no modelo de dados do Cassandra.
     *
     * @param <T>
     * @param entry
     * @param sb
     * @return
     * @throws Exception
     */
    private static <T extends CassandraTable> Map<String, Object> objectToCQL(T entry, StringBuilder sb) throws Exception {
        Map<String, Object> mapFieldValue = new HashMap<>();

        for (Class clazz : Utils.getClasses(Constants.PACKAGE_NAME_MODEL)) {
            if (clazz.isInstance(entry)) {
                Object castedObject = clazz.cast(entry);
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);

                    if (null != field.get(castedObject)) {
                        if (field.isAnnotationPresent(CassandraColumn.class)) {
                            CassandraColumn annotation = (CassandraColumn) field.getAnnotation(CassandraColumn.class);

                            if (field.isAnnotationPresent(CassandraJoinColumn.class)) {
                                CassandraTablePayload castedSubObject = (CassandraTablePayload) field.get(castedObject);
                                mapFieldValue.put(annotation.name(), castedSubObject.getId());
                            } else {
                                mapFieldValue.put(annotation.name(), field.get(castedObject));
                            }
                        }
                    }
                }
            }
        }

        sb.append(Constants.SEPARATOR_PARENTHESIS_LEFT);

        for (String field : mapFieldValue.keySet()) {
            sb.append(field).append(Constants.SEPARATOR_COMMA);
        }
        sb.replace(sb.lastIndexOf(Constants.SEPARATOR_COMMA), sb.length(), Constants.SEPARATOR_PARENTHESIS_RIGHT);

        sb.append(Constants.SEPARATOR_SPACE).append(Constants.CLAUSE_VALUES).append(Constants.SEPARATOR_SPACE).append(Constants.SEPARATOR_PARENTHESIS_LEFT);

        for (int i = 0; i < mapFieldValue.size(); i++) {
            sb.append(Constants.VALUE_QUESTION_MARK);
            if (i < mapFieldValue.size() - 1) {
                sb.append(Constants.SEPARATOR_COMMA).append(Constants.SEPARATOR_SPACE);
            }
        }
        sb.append(Constants.SEPARATOR_PARENTHESIS_RIGHT);

        return mapFieldValue;
    }

    /**
     * Método auxiliar para mapear os elementos da classe do modelo de dados
     * para a tabela correspondente no modelo de dados do Cassandra, para
     * efectuar o binding customizado de alguns elementos.
     *
     * @param preparedStatementInsert
     * @param fields
     * @return
     */
    private static BoundStatement bindStatement(PreparedStatement preparedStatementInsert, Map<String, Object> fields) {
        BoundStatement boundStatement = preparedStatementInsert.bind();
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            Class clazz = entry.getValue().getClass();

            if (entry.getValue() instanceof Map) {
                boundStatement.setMap(entry.getKey(), (HashMap<String, String>) entry.getValue(), String.class, String.class);
            } else if (entry.getValue() instanceof Calendar) {
                boundStatement.setDate(entry.getKey(), LocalDate.fromMillisSinceEpoch(((Calendar) entry.getValue()).getTimeInMillis()));
            } else {
                boundStatement.set(entry.getKey(), entry.getValue(), clazz);
            }
        }
        return boundStatement;
    }
}
