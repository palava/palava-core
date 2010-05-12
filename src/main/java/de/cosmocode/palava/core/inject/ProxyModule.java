package de.cosmocode.palava.core.inject;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.core.Registry.Proxy;
import de.cosmocode.palava.core.Registry.SilentProxy;

/**
 * A {@link Module} which can be used to bind dynamic proxies
 * provided by the current {@link Registry}.
 *
 * @since 
 * @author Willi Schoenborn
 */
public abstract class ProxyModule extends AbstractModule {

    /**
     * Binds a dynamic proxy for the specified type which can
     * be injected using the following code.
     * {@code @Proxy Interface instance}
     * 
     * <p>
     *   The same restrictions mentioned on {@link Registry#proxy(Class)}
     *   apply to the provided type.
     * </p>
     * 
     * @since 2.4
     * @param <T> the generic proxy type
     * @param type the proxy's class literal
     * @throws NullPointerException if type is null
     */
    protected <T> void bindProxy(Class<T> type) {
        Preconditions.checkNotNull(type, "Type");
        bind(type).annotatedWith(Proxy.class).toProvider(new ProxyProvider<T>(
            getProvider(Registry.class), type
        )).in(Singleton.class);
    }

    /**
     * Binds a dynamic silent proxy for the specified type which can
     * be injected using the following code.
     * {@code @SilentProxy Interface instance}
     * 
     * <p>
     *   The same restrictions mentioned on {@link Registry#silentProxy(Class)}
     *   apply to the provided type.
     * </p>
     * 
     * @since 2.4
     * @param <T> the generic proxy type
     * @param type the proxy's class literal
     * @throws NullPointerException if type is null
     */
    protected <T> void bindSilentProxy(Class<T> type) {
        Preconditions.checkNotNull(type, "Type");
        bind(type).annotatedWith(SilentProxy.class).toProvider(new ProxyProvider<T>(
            getProvider(Registry.class), type, true
        )).in(Singleton.class);
    }
    
    /**
     * Internal provider for dynamic proxies.
     *
     * @since 1.0
     * @author Willi Schoenborn
     * @param <T> the generic proxy type
     */
    private static final class ProxyProvider<T> implements Provider<T> {

        private final Provider<Registry> provider;
        
        private final Class<T> type;
        
        private final boolean silent;

        public ProxyProvider(Provider<Registry> provider, Class<T> type) {
            this(provider, type, false);
        }

        public ProxyProvider(Provider<Registry> provider, Class<T> type, boolean silent) {
            this.provider = Preconditions.checkNotNull(provider, "Provider");
            this.type = Preconditions.checkNotNull(type, "Type");
            this.silent = silent;
        }
        
        @Override
        public T get() {
            final Registry registry = provider.get();
            return silent ? registry.silentProxy(type) : registry.proxy(type);
        }
        
    }
    
}
