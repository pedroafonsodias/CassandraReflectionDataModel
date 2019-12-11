
import java.util.List;
import pedro.dias.cassandra.CassandraFacade;
import pedro.dias.cassandra.ConnectionCassandra;
import pedro.dias.cassandra.model.Table;
import pedro.dias.cassandra.model.constants.Constants;
import pedro.dias.cassandra.model.interfaces.CassandraTable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author paafonso
 */
public class Main {
    public static void main(String[] args) throws Exception {
        System.setProperty(Constants.PROPERTY_HOSTS, "255.255.255.255");
        System.setProperty(Constants.PROPERTY_USER, "user");
        System.setProperty(Constants.PROPERTY_PASSWORD, "password");
        
        List<CassandraTable> listaResultados  = CassandraFacade.query("select * from keyspace.table where entry_date_day= '2019-12-11'");
        for(CassandraTable cass : listaResultados){
            if(cass instanceof Table){
                System.out.println(((Table)cass).toString());
            }
        }
        ConnectionCassandra.close();
    }
}
