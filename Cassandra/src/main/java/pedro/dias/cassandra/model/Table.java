/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedro.dias.cassandra.model;

import pedro.dias.cassandra.model.interfaces.CassandraTable;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import pedro.dias.model.annotations.CassandraColumn;
import pedro.dias.model.annotations.CassandraColumnPartitionKey;
import pedro.dias.model.annotations.CassandraJoinColumn;

/**
 * Classe que mapeia uma tabela.
 * 
 * @author paafonso
 */
public class Table implements Serializable, CassandraTable {

    public static final String TABLE_NAME = "table";
    public static final String KEYSPACE = "keyspace";
    
    @CassandraColumnPartitionKey
    @CassandraColumn(name = "entry_date_day")
    private Calendar entryDateDay;
    
    @CassandraColumn(name = "entry_date_hour")
    private Integer entryDateHour;
    
    @CassandraColumnPartitionKey
    @CassandraColumn(name = "entry_date")
    private Date entryDate;
    
    @CassandraColumn(name = "msg_type")
    private String msgType;
    
    @CassandraColumn(name = "identifier")
    private String identifier;
    
    @CassandraJoinColumn
    @CassandraColumn(name = "payload_id")
    private TablePayload payloadId;

    public Table() {
    }

    public Calendar getEntryDateDay() {
        return entryDateDay;
    }

    public void setEntryDateDay(Calendar entryDateDay) {
        this.entryDateDay = entryDateDay;
    }

    public Integer getEntryDateHour() {
        return entryDateHour;
    }

    public void setEntryDateHour(Integer entryDateHour) {
        this.entryDateHour = entryDateHour;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public TablePayload getPayloadId() {
        return payloadId;
    }

    public void setPayloadId(TablePayload payloadId) {
        this.payloadId = payloadId;
    }

    @Override
    public String toString() {
        return "Table{" + "entryDateDay=" + entryDateDay + ", entryDateHour=" + entryDateHour + ", entryDate=" + entryDate + ", msgType=" + msgType + ", identifier=" + identifier + ", payloadId=" + payloadId + '}';
    }    

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getKeySpace() {
        return KEYSPACE;
    }

}
