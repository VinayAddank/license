/**
 * 
 */
package org.rta.citizen.common.exception;

/**
 * @author arun.verma
 *
 */
public class VehicleNotFinanced extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = 3513258706938890190L;

    public VehicleNotFinanced() {
        super();
    }

    public VehicleNotFinanced(String message) {
        super(message);
    }
}
