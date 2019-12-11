/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedro.dias.cassandra.model.interfaces;

import java.util.UUID;

/**
 * Interface que obriga a que acrescenta a obrigatoriedade de um id a uma tabela do cassandra. 
 * 
 * @author paafonso
 */
public interface CassandraTablePayload extends CassandraTable{
    public UUID getId();
    
    public void setId(UUID id);
}
