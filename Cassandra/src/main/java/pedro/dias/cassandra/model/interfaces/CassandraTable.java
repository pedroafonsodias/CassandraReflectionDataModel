/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedro.dias.cassandra.model.interfaces;

/**
 * Interface que obriga a que um objecto que mapeie uma tabela do Cassandra, retorne o keyspace e o nome da tabela.
 * 
 * @author paafonso
 */
public interface CassandraTable {
    public String getTableName();
    public String getKeySpace();
    public CassandraTable getPayloadId();
}
