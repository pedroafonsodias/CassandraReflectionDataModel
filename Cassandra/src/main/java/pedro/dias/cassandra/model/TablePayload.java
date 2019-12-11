/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedro.dias.cassandra.model;

import java.io.Serializable;
import java.util.UUID;
import pedro.dias.model.annotations.CassandraColumn;
import pedro.dias.cassandra.model.interfaces.CassandraTable;
import pedro.dias.cassandra.model.interfaces.CassandraTablePayload;

/**
 * Classe que mapeia uma tabela de payload.
 * 
 * @author paafonso
 */
public class TablePayload implements Serializable, CassandraTablePayload {

    public static final String TABLE_NAME = "table_payload";
    public static final String KEYSPACE = "keyspace";

    @CassandraColumn(name = "id")
    private UUID id;

    @CassandraColumn(name = "payload")
    private String payload;

    public TablePayload() {
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "TablePayload{" + "id=" + id + ", payload=" + payload + '}';
    }


    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getKeySpace() {
        return KEYSPACE;
    }

    @Override
    public CassandraTable getPayloadId() {
        return null;
    }

}
