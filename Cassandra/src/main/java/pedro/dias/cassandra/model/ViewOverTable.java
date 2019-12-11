/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedro.dias.cassandra.model;

import pedro.dias.model.annotations.CassandraViewTable;

/**
 * Classe que mapeia uma view.
 * 
 * @author paafonso
 */
@CassandraViewTable
public class ViewOverTable extends Table {
    
    public static String TABLE_NAME = "view_over_table";
    
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
