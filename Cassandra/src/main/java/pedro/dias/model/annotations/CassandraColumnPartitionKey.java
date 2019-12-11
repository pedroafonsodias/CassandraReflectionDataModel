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
 * Anotação responsável por identificar os elementos que compõem a chave de partição no cassandra para poder de forma automática acrescentar a cláusula allow filtering sempre que necessário. 
 * 
 * @author paafonso
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CassandraColumnPartitionKey {
}
