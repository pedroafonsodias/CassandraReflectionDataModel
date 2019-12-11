/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedro.dias.cassandra.model.constants;

/**
 *  Elementos estáticos de apoio ao projecto Cassandra.
 * 
 * @author paafonso
 */
public class Constants {
    // conection constants
    public static final int CASSANDRA_PORT = 9042;
    public static final int DEFAULT_RECONNECT_BASE_DELAY_MS = 1000;
    public static final int DEFAULT_RECONNECT_MAX_DELAY_MS = 60000;
    //properties definition
    public static final String PROPERTY_HOSTS = "Cassandra_Hosts";
    public static final String PROPERTY_USER = "Cassandra_User";
    public static final String PROPERTY_PASSWORD = "Cassandra_Pass";
    public static final String PROPERTY_CONSISTENCY_LEVEL = "Cassandra_Consistency_Level";
    public static final String PROPERTY_CONSISTENCY_LEVEL_DEFAULT_VALUE = "ALL";
    
    // constants
    public static final String PACKAGE_NAME_MODEL = "pedro.dias.cassandra.model";
    public static final String UNDEFINED = "UNDEFINED";
    
    public static final String METHOD_GET_TABLE_NAME = "getTableName";    
    public static final String METHOD_GET_KEYSPACE_NAME = "getKeySpace";    
    
    // sql decompile clauses
    public static final String REGEX_KEYSPACE_TABLE_SEPARATOR = "\\.";
    public static final String REGEX_WHERE_CLAUSES = "[=\\s]{1}";
    public static final String SEPARATOR_COMMA = ",";
    public static final String SEPARATOR_SPACE = " ";
    public static final String SEPARATOR_QUOTE = "'";
    public static final String SEPARATOR_DOT = ".";
    public static final String SEPARATOR_EQUAL = "=";
    public static final String SEPARATOR_PARENTHESIS_LEFT = "(";
    public static final String SEPARATOR_PARENTHESIS_RIGHT = ")";
    public static final String CLAUSE_FROM = "from";
    public static final String CLAUSE_WHERE = "where";
    public static final String CLAUSE_AND = "and";
    public static final String CLAUSE_SELECT = "select";
    public static final String CLAUSE_SELECT_ALL = "select *";
    public static final String CLAUSE_CONTAINS = "contains";
    public static final String CLAUSE_ALLOW_FILTERING = "allow filtering";
    public static final String CLAUSE_INSERT_INTO = "insert into";
    public static final String CLAUSE_VALUES = "values";
    public static final String CLAUSE_USING_TTL = "using ttl";
    public static final String VALUE_QUESTION_MARK = "?";
    public static final String VALUE_EMPTY = "";
    
    // messages
    public static final String MESSAGE_KEYSPACE_TABLE_ERROR = "Ocorreu um erro ao obter keyspace ou nome da tabela";
    public static final String MESSAGE_KEYSPACE_TABLE_NOT_FOUND = "Impossível inferir tabela do Cassandra";
    
}
