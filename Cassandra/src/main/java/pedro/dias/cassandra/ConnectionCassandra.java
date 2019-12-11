package pedro.dias.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.ExponentialReconnectionPolicy;
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.ReconnectionPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PreDestroy;
import pedro.dias.cassandra.model.constants.Constants;

/**
 * Classe responsável por gerir a ligação ao Cassandra, numa lógica de
 * reutilização da sessão.
 *
 * @author paafonso
 */
public class ConnectionCassandra {

    public static Cluster cluster;
    public static Session session;
    public static StatementCache statementCache;

    /**
     * Código responsável por fazer a conexão com o Cassandra.
     *
     */
    private static void connect() {

        String cassandraHosts = System.getProperty(Constants.PROPERTY_HOSTS);
        String cassandraUser = System.getProperty(Constants.PROPERTY_USER);
        String cassandraPwd = System.getProperty(Constants.PROPERTY_PASSWORD);

        ReconnectionPolicy reconnectionPolicy = new ExponentialReconnectionPolicy(Constants.DEFAULT_RECONNECT_BASE_DELAY_MS, Constants.DEFAULT_RECONNECT_MAX_DELAY_MS);
        LoadBalancingPolicy loadBalancingPolicy = new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder().build());
        cluster = Cluster.builder()
                .addContactPoints(cassandraHosts.split(Constants.SEPARATOR_COMMA))
                .withPort(Constants.CASSANDRA_PORT)
                .withCredentials(cassandraUser, cassandraPwd)
                .withReconnectionPolicy(reconnectionPolicy)
                .withLoadBalancingPolicy(loadBalancingPolicy)
                .withProtocolVersion(ProtocolVersion.NEWEST_SUPPORTED)
                .build();
//        final Metadata metadata = cluster.getMetadata();
//        Logger.getGlobal().log(Level.INFO, "Connected to cluster: " + metadata.getClusterName());

        session = cluster.connect();
        statementCache = new StatementCache();
    }

    /**
     * Código responsável por fornecer uma {@link Session} do Cassandra.
     *
     * @return {@link Session}
     */
    public static synchronized Session connectionCassandra() {
        if (cluster == null
                || session == null
                || (cluster != null && cluster.isClosed())
                || (session != null && session.isClosed())) {
            connect();
        }
        return session;
    }

    /**
     * Código responsável fechar a conexão do Cassandra.
     *
     */
    public static void close() {
        if (session != null) {
            session.close();
        }
        if (cluster != null) {
            cluster.close();
        }
    }

    /**
     * Código responsável por entregar um {@link PreparedStatement} para o cql
     * passado no parâmetro.
     *
     * @param cql
     * @return {@link PreparedStatement}
     */
    public static PreparedStatement getPreparedStatement(String cql) {
        connectionCassandra();

        return statementCache.getStatement(cql);
    }

    @PreDestroy
    public static void end() {
        close();
    }

    /**
     * Código responsável por retornar um novo {@link PreparedStatement}.
     *
     */
    private static class StatementCache {

        Map<String, PreparedStatement> statementCache = new HashMap<>();

        public PreparedStatement getStatement(String cql) {
            PreparedStatement preparedStatement = statementCache.get(cql);

            if (preparedStatement == null) {
                synchronized (this) {
                    preparedStatement = statementCache.get(cql);
                    if (preparedStatement == null) {
                        preparedStatement = session.prepare(cql);
                        statementCache.put(cql, preparedStatement);
                    }
                    String CASSANDRA_CONSISTENCY_LEVEL = System.getProperty(Constants.PROPERTY_CONSISTENCY_LEVEL, Constants.PROPERTY_CONSISTENCY_LEVEL_DEFAULT_VALUE);
                    preparedStatement.setConsistencyLevel(ConsistencyLevel.valueOf(CASSANDRA_CONSISTENCY_LEVEL));

                }
            }
            return preparedStatement;
        }
    }

    public static Session getSession() {
        return connectionCassandra();
    }

}
