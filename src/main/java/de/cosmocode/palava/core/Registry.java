/**
 * palava - a java-php-bridge
 * Copyright (C) 2007-2010  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.cosmocode.palava.core;

import com.google.common.base.Preconditions;

/**
 * A {@link Registry} is used to provide a publish/subscribe
 * mechanism.
 *
 * @author Willi Schoenborn
 */
public interface Registry extends Service {

    /**
     * A key which can be used to add meta information to a type/listener
     * binding in a registry.
     *
     * @author Willi Schoenborn
     * @param <T>
     */
    public static final class Key<T> {
        
        private final Class<T> type;
        
        private final Object meta;
        
        private Key(Class<T> type, Object meta) {
            this.type = Preconditions.checkNotNull(type, "Type");
            this.meta = meta;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((meta == null) ? 0 : meta.hashCode());
            result = prime * result + ((type == null) ? 0 : type.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Key<?>)) {
                return false;
            }
            final Key<?> other = (Key<?>) obj;
            if (type == null) {
                if (other.type != null) {
                    return false;
                }
            } else if (!type.equals(other.type)) {
                return false;
            }
            if (meta == null) {
                if (other.meta != null) {
                    return false;
                }
            } else if (!meta.equals(other.meta)) {
                return false;
            }
            return true;
        }
        
        /**
         * Creates a new key using the given type.
         * 
         * @param <T> the generic type
         * @param type the type's class literal
         * @return a new key
         * @throws NullPointerException if type is null
         */
        public static <T> Key<T> get(Class<T> type) {
            return new Key<T>(type, null);
        }
        
        /**
         * Creates a new key using the specified type and meta
         * information. The meta information will be associated 
         * with the type to allow registrations on the same type
         * with different semantics.
         * 
         * <p>
         *   <strong>Note</strong>: You are strongly encouraged to implement
         *   {@link Object#equals(Object)} and {@link Object#hashCode()} of
         *   meta properly. Otherwise we can't guarantee that future retrieve
         *   operations will succeed. In fact we can guarantee that these
         *   operations will fail if equals is not implemented properly.
         * </p>
         * 
         * @param <T> the generic type
         * @param type the type's class literal
         * @param meta the meta information of the new binding key
         *        for the specified type.
         * @return a new key
         * @throws NullPointerException if type or meta is null
         */
        public static <T> Key<T> get(Class<T> type, Object meta) {
            Preconditions.checkNotNull(meta, "Meta");
            return new Key<T>(type, meta);
        }
        
    }
    
    /**
     * Register a listener for a specific type. Registering the same listener
     * for a type twice does not result in a double binding. The listener
     * will be notified once and only once per notify invocation.
     *
     * @param <T> the generic type
     * @param type the type's class
     * @param listener the listener
     * @throws NullPointerException if type or listener is null
     */
    <T> void register(Class<T> type, T listener);
    
    /**
     * Registers a listener for a specific key. Registering the same listener
     * for a key twice does not result in a double binding. The listener
     * will be notified once and only once per notify invocation.
     * 
     * @param <T> the generic type
     * @param key the binding key
     * @param listener the listener
     * @throws NullPointerException if key or listener is null
     */
    <T> void register(Key<T> key, T listener);

    /**
     * Provide all listeners for a specific type.
     *
     * <p>
     *   Note: Implementations may provide live views.
     * </p>
     *
     * @param <T> the generic type
     * @param type the type's class
     * @return an unmodifable iterable over all found listeners for that type
     * @throws NullPointerException if type is null
     */
    <T> Iterable<T> getListeners(Class<T> type);
    
    /**
     * Provides all listeners for a specific type.
     * 
     * <p>
     *   Note: Implementations may provide live views.
     * </p>
     * 
     * @param <T> the generic type
     * @param key the binding key
     * @return an unmodifiable iterable over all found listeners for that type
     * @throws NullPointerException if key is null
     */
    <T> Iterable<T> getListeners(Key<T> key);

    /**
     * Creates a proxy of type T which can be used in third-party
     * event/callback frameworks to integrate in this registry.
     * This is used for event systems which do not allow hot un/loading
     * of listeners.
     * 
     * @param <T> the generic type
     * @param type the type's class literal
     * @return an instance of type T which itself is not registered
     *         in this registry but delegates to all listeners registered 
     *         in this registry at invocation time
     * @throws NullPointerException if type is null
     * @throws IllegalArgumentException if type is not an interface (annotations are not allowed)
     * @throws IllegalStateException when a method is invoked which does not return
     *         void. <strong>Note</strong>: This exception is thrown at invocation time
     *         not at construction time.
     */
    <T> T proxy(Class<T> type);
    
    /**
     * Creates a proxy of type T which can be used in third-party
     * event/callback frameworks to integrate in this registry.
     * This is used for event systems which do not allow hot un/loading
     * of listeners.
     * 
     * @param <T> the generic type
     * @param key the binding key
     * @return an instance of type T which itself is not registered
     *         in this registry but delegates to all listeners registered 
     *         in this registry at invocation time
     * @throws NullPointerException if key is null
     * @throws IllegalArgumentException if T is not an interface (annotations are not allowed)
     * @throws IllegalStateException when a method is invoked which does not return
     *         void. <strong>Note</strong>: This exception is thrown at invocation time
     *         not at construction time.
     */
    <T> T proxy(Key<T> key);
    
    /**
     * Notify all listeners for a specific type
     * by invoking command on every found listener.
     *
     * @param <T> the generic type
     * @param type the type's class
     * @param command the command being invoked on every listener
     * @throws NullPointerException if type or command is null
     * @throws RuntimeException if notifying a listener failed, which
     *         will abort all following notifications
     */
    <T> void notify(Class<T> type, Procedure<? super T> command);
    
    /**
     * Notify all listeners for a specific binding key
     * by invoking command on every found listener.
     * 
     * @param <T> the generic type
     * @param key the binding key
     * @param command the command being invoked on every listener
     * @throws NullPointerException if key or command is null
     * @throws RuntimeException if notifying a listener failed, which
     *         will abort all following notifications
     */
    <T> void notify(Key<T> key, Procedure<? super T> command);

    /**
     * Notify all listeners for a specific type
     * by invoking command on every found listener.
     *
     * @param <T> the generic type
     * @param type the type's class
     * @param command the command being invoked on every listener
     * @throws NullPointerException if type or command is null
     */
    <T> void notifySilent(Class<T> type, Procedure<? super T> command);

    /**
     * Notify all listeners for a specific binding key
     * by invoking command on every found listener.
     * 
     * @param <T> the generic type
     * @param key the binding key
     * @param command the command being invoked on every listener
     * @throws NullPointerException if key or command is null
     */
    <T> void notifiySilent(Key<T> key, Procedure<? super T> command);
    
    /**
     * Remove a specific listener interested in type from this registry.
     * If the same listener is also registered for other types,
     * he will still get notified for those.
     *
     * @param <T> the generic type
     * @param type the type's class
     * @param listener the listener being removed from this registry
     * @return true if listener was registered for type before
     * @throws NullPointerException if type or listener is null
     */
    <T> boolean remove(Class<T> type, T listener);

    /**
     * Remove a specific listener interested for the specified binding key
     * from this registry. If the same listener is also registered for other types,
     * he will still get notified for those.
     * 
     * @param <T> the generic type
     * @param key the binding key
     * @param listener the listener being removed from this registry
     * @return true if listener was registered for type before
     * @throws NullPointerException if key or listener is null
     */
    <T> boolean remove(Key<T> key, T listener);
    
    /**
     * Removes a listener completely from this registry. If the listener
     * is registered for multiple types, he won't get notified for those
     * after this method has been called.
     *
     * @param <T> the generic type
     * @param listener the listener being removed
     * @return true if listener was registered before
     * @throws NullPointerException if listener is null
     */
    <T> boolean remove(T listener);

    /**
     * Removes a type and its listeners completely from this registry.
     * If the a listener is also registered for other types,
     * he will still get notified for those.
     * 
     * @param <T> the generic type
     * @param type the type being removed
     * @return an iterable of all listeners that were registered for that type
     * @throws NullPointerException if type is null
     */
    <T> Iterable<T> removeAll(Class<T> type);
    
    /**
     * Removes a binding key and its listeners completely from this registry.
     * If the a listener is also registered for other keys,
     * he will still get notified for those.
     * 
     * @param <T> the generic type
     * @param key the binding key
     * @return an iterable of all listeners that were registered for the specified key
     * @throws NullPointerException if key is null
     */
    <T> Iterable<T> removeAll(Key<T> key);

}
