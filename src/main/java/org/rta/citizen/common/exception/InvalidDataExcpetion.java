/**
 * 
 */
package org.rta.citizen.common.exception;

/**
 * @author arun.verma
 *
 */
public class InvalidDataExcpetion extends Exception {

    private static final long serialVersionUID = -3433379698722004944L;

    public InvalidDataExcpetion() {
        super();
    }

    public InvalidDataExcpetion(String msg) {
        super(msg);
    }

    public InvalidDataExcpetion(String msg, Throwable cuse) {
        super(msg, cuse);
    }
}
