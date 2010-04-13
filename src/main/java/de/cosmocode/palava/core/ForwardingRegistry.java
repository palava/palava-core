package de.cosmocode.palava.core;

import com.google.common.collect.ForwardingObject;

import de.cosmocode.collections.Procedure;

/**
 * Abstract decorator for {@link Registry}s.
 *
 * @author Willi Schoenborn
 */
public abstract class ForwardingRegistry extends ForwardingObject implements Registry {

    @Override
    protected abstract Registry delegate();
    
    @Override
    public <T> Iterable<T> getListeners(Class<T> type) {
        return delegate().getListeners(type);
    }

    @Override
    public <T> Iterable<T> getListeners(Key<T> key) {
        return delegate().getListeners(key);
    }

    @Override
    public <T> void notify(Class<T> type, Procedure<? super T> command) {
        delegate().notify(type, command);
    }

    @Override
    public <T> void notify(Key<T> key, Procedure<? super T> command) {
        delegate().notify(key, command);
    }

    @Override
    public <T> void notifySilent(Class<T> type, Procedure<? super T> command) {
        delegate().notifySilent(type, command);
    }

    @Override
    public <T> void notifySilent(Key<T> key, Procedure<? super T> command) {
        delegate().notifySilent(key, command);
    }

    @Override
    public <T> T proxy(Class<T> type) {
        return delegate().proxy(type);
    }

    @Override
    public <T> T proxy(Key<T> key) {
        return delegate().proxy(key);
    }

    @Override
    public <T> void register(Class<T> type, T listener) {
        delegate().register(type, listener);
    }

    @Override
    public <T> void register(Key<T> key, T listener) {
        delegate().register(key, listener);
    }

    @Override
    public <T> boolean remove(Class<T> type, T listener) {
        return delegate().remove(type, listener);
    }

    @Override
    public <T> boolean remove(Key<T> key, T listener) {
        return delegate().remove(key, listener);
    }

    @Override
    public <T> boolean remove(T listener) {
        return delegate().remove(listener);
    }

    @Override
    public <T> Iterable<T> removeAll(Class<T> type) {
        return delegate().removeAll(type);
    }

    @Override
    public <T> Iterable<T> removeAll(Key<T> key) {
        return delegate().removeAll(key);
    }
    
}
