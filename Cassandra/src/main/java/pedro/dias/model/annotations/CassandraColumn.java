/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedro.dias.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para efectuar mapeamento do nome da coluna do cassandra para o campo da classe correspondente no modelo de dados.
 *
 * @author paafonso
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CassandraColumn {
    /**
     * Elemento identificador da coluna correspondete no cassandra.
     * @return 
     */
    public String name();
}
