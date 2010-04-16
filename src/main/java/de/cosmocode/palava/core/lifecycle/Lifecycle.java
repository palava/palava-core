package de.cosmocode.palava.core.lifecycle;

import com.google.common.base.Preconditions;

/**
 * Static utitlity class for lifecycle interfaces.
 *
 * @since 2.3
 * @author Willi Schoenborn
 */
public final class Lifecycle {

    private Lifecycle() {
        
    }
    
    /**
     * Checks for {@link Initializable}, {@link Disposable} and {@link Startable}.
     * 
     * @since 2.3
     * @param service the service to check
     * @return true if the given service implements at least one of the three interfaces, false otherwise
     * @throws NullPointerException if service is null
     */
    public static boolean hasInterface(Object service) {
        Preconditions.checkNotNull(service, "Service");
        return isInterface(service.getClass());
    }
    
    /**
     * Checks for {@link Initializable}, {@link Disposable} and {@link Startable}.
     * 
     * @since 2.3
     * @param type the type to check
     * @return true if the given type is a subclass of at least one of the three interfaces, false otherwise
     * @throws NullPointerException if type is null
     */
    public static boolean isInterface(Class<?> type) {
        Preconditions.checkNotNull(type, "Type");
        return Initializable.class.isAssignableFrom(type) ||
            Disposable.class.isAssignableFrom(type) ||
            Startable.class.isAssignableFrom(type);
    }

}
