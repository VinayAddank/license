/**
 * 
 */
package org.rta.citizen.common.exception;

/**
 * @author arun.verma
 *
 */
public class ApplicationNotFoundException extends NotFoundException {

    /**
     * 
     */
    private static final long serialVersionUID = 6760739744273736650L;

    public ApplicationNotFoundException() {
        super();
    }

    public ApplicationNotFoundException(String message) {
        super(message);
    }
}
