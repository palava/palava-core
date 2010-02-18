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

import java.util.concurrent.TimeUnit;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import de.cosmocode.palava.core.Registry.Key;

/**
 * Tests {@link Registry} implementations.
 * 
 * @author Willi Schoenborn
 */
public abstract class AbstractRegistryTest {
    
    /**
     * Hidden interface to easily mock listeners.
     *
     * @author Willi Schoenborn
     */
    private static interface Listener {

        void doAnything();
        
    }

    /**
     * Provides the unit under testing.
     * 
     * @return a new {@link Registry}
     */
    protected abstract Registry unit();
    
    /**
     * Implementations can decide whether they live views
     * when using {@link Registry#getListeners(Class)}.
     * 
     * @return true if live view is supported, false otherwise
     */
    protected abstract boolean supportsLiveView();
    
    /**
     * Tests {@link Registry#register(Class, Object)}.
     */
    @Test
    public void registerType() {
        final Registry unit = unit();
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        EasyMock.replay(listener);
        unit.register(Listener.class, listener);
        Assert.assertSame(listener, Iterables.getOnlyElement(unit.getListeners(Listener.class)));
    }
    
    /**
     * Tests {@link Registry#register(Key, Object)}.
     */
    @Test
    public void registerKey() {
        final Registry unit = unit();
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        EasyMock.replay(listener);
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        unit.register(key, listener);
        Assert.assertSame(listener, Iterables.getOnlyElement(unit.getListeners(key)));
    }
    
    /**
     * Tests {@link Registry#register(Class, Object)} using the same pair of
     * type and listener twice.
     */
    @Test
    public void registerTypeSame() {
        final Registry unit = unit();
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        EasyMock.replay(listener);
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Listener.class)));
        unit.register(Listener.class, listener);
        Assert.assertFalse(Iterables.isEmpty(unit.getListeners(Listener.class)));
        unit.register(Listener.class, listener);
        Assert.assertFalse(Iterables.isEmpty(unit.getListeners(Listener.class)));
        Assert.assertSame(listener, Iterables.getOnlyElement(unit.getListeners(Listener.class)));
    }
    
    /**
     * Tests {@link Registry#register(Key, Object)} using the same pair of
     * type and listener twice. 
     */
    @Test
    public void registerKeySame() {
        final Registry unit = unit();
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        EasyMock.replay(listener);
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(key)));
        unit.register(key, listener);
        Assert.assertFalse(Iterables.isEmpty(unit.getListeners(key)));
        unit.register(key, listener);
        Assert.assertFalse(Iterables.isEmpty(unit.getListeners(key)));
        Assert.assertSame(listener, Iterables.getOnlyElement(unit.getListeners(key)));
    }
    
    /**
     * Tests {@link Registry#register(Class, Object)} using the same listener 
     * for different types.
     */
    @Test
    public void registerTypeDifferentTypes() {
        final Registry unit = unit();
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        EasyMock.replay(listener);
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Listener.class)));
        unit.register(Listener.class, listener);
        Assert.assertFalse(Iterables.isEmpty(unit.getListeners(Listener.class)));
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Object.class)));
        unit.register(Object.class, listener);
        Assert.assertFalse(Iterables.isEmpty(unit.getListeners(Listener.class)));
        Assert.assertFalse(Iterables.isEmpty(unit.getListeners(Object.class)));
        Assert.assertSame(listener, Iterables.getOnlyElement(unit.getListeners(Listener.class)));
        Assert.assertSame(listener, Iterables.getOnlyElement(unit.getListeners(Object.class)));
    }
    
    /**
     * Tests {@link Registry#register(Key, Object)} using the same listener
     * for different types.
     */
    @Test
    public void registerKeyDifferentTypes() {
        final Registry unit = unit();
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        EasyMock.replay(listener);
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(key)));
        unit.register(key, listener);
        Assert.assertFalse(Iterables.isEmpty(unit.getListeners(key)));
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Object.class)));
        unit.register(Object.class, listener);
        Assert.assertFalse(Iterables.isEmpty(unit.getListeners(key)));
        Assert.assertFalse(Iterables.isEmpty(unit.getListeners(Object.class)));
        Assert.assertSame(listener, Iterables.getOnlyElement(unit.getListeners(key)));
        Assert.assertSame(listener, Iterables.getOnlyElement(unit.getListeners(Object.class)));
    }
    
    /**
     * Tests {@link Registry#register(Class, Object)} with a null type.
     */
    @Test(expected = NullPointerException.class)
    public void registerTypeNullType() {
        final Class<Object> nullType = null;
        unit().register(nullType, new Object());
    }
    
    /**
     * Tests {@link Registry#register(Class, Object)} with a null listener.
     */
    @Test(expected = NullPointerException.class)
    public void registerTypeNullListener() {
        unit().register(Object.class, null);
    }
    
    /**
     * Tests {@link Registry#register(Class, Object)} with a null type and
     * a null listener.
     */
    @Test(expected = NullPointerException.class)
    public void registerTypeNulls() {
        final Class<Object> nullType = null;
        unit().register(nullType, null);
    }
    
    /**
     * Tests {@link Registry#register(Class, Object)} with a null key.
     */
    @Test(expected = NullPointerException.class)
    public void registerKeyNullKey() {
        final Key<Object> nullKey = null;
        unit().register(nullKey, new Object());
    }
    
    /**
     * Tests {@link Registry#register(Class, Object)} with a null listener.
     */
    @Test(expected = NullPointerException.class)
    public void registerKeyNullListener() {
        unit().register(Key.get(Object.class, Deprecated.class), null);
    }
    
    /**
     * Tests {@link Registry#register(Class, Object)} with a null key and
     * a null listener.
     */
    @Test(expected = NullPointerException.class)
    public void registerKeyNulls() {
        final Key<Object> nullKey = null;
        unit().register(nullKey, null);
    }
    
    /**
     * Tests {@link Registry#register(Key, Object)} with a matcher key.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void registerMatcherKey() {
        unit().register(Key.matcher(Object.class, Predicates.alwaysTrue()), new Object());
    }
    
    /**
     * Tests {@link Registry#getListeners(Class)}.
     */
    @Test
    public void getListenersType() {
        final Registry unit = unit();
        final Listener first = EasyMock.createMock("first", Listener.class);
        final Listener second = EasyMock.createMock("second", Listener.class);
        EasyMock.replay(first, second);

        unit.register(Listener.class, first);
        Assert.assertSame(first, Iterables.getOnlyElement(unit.getListeners(Listener.class)));

        unit.register(Listener.class, second);
        Assert.assertEquals(2, Iterables.size(unit.getListeners(Listener.class)));
        Assert.assertEquals(Sets.newHashSet(first, second), Sets.newHashSet(unit.getListeners(Listener.class)));

        unit.remove(Listener.class, first);
        Assert.assertEquals(1, Iterables.size(unit.getListeners(Listener.class)));
        Assert.assertSame(second, Iterables.getOnlyElement(unit.getListeners(Listener.class)));
    }
    
    /**
     * Tests {@link Registry#getListeners(Key)}.
     */
    @Test
    public void getListenersKey() {
        final Registry unit = unit();
        final Listener first = EasyMock.createMock("first", Listener.class);
        final Listener second = EasyMock.createMock("second", Listener.class);
        EasyMock.replay(first, second);
        
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        
        unit.register(key, first);
        Assert.assertSame(first, Iterables.getOnlyElement(unit.getListeners(key)));

        unit.register(key, second);
        Assert.assertEquals(2, Iterables.size(unit.getListeners(key)));
        Assert.assertEquals(Sets.newHashSet(first, second), Sets.newHashSet(unit.getListeners(key)));

        unit.remove(key, first);
        Assert.assertEquals(1, Iterables.size(unit.getListeners(key)));
        Assert.assertSame(second, Iterables.getOnlyElement(unit.getListeners(key)));
    }
    
    /**
     * Tests {@link Registry#getListeners(Class)} using live view, if supported.
     */
    @Test
    public void getListenersTypeLiveView() {
        final Listener first = EasyMock.createMock("first", Listener.class);
        final Listener second = EasyMock.createMock("second", Listener.class);
        EasyMock.replay(first, second);
        
        if (supportsLiveView()) {
            final Registry unit = unit();
            unit.register(Listener.class, first);
            final Iterable<Listener> listeners = unit.getListeners(Listener.class);
            Assert.assertSame(first, Iterables.getOnlyElement(listeners));

            unit.register(Listener.class, second);
            Assert.assertEquals(2, Iterables.size(listeners));
            Assert.assertEquals(Sets.newHashSet(first, second), Sets.newHashSet(listeners));

            unit.remove(Listener.class, first);
            Assert.assertEquals(1, Iterables.size(listeners));
            Assert.assertSame(second, Iterables.getOnlyElement(listeners));
        } else {
            final Registry unit = unit();
            unit.register(Listener.class, first);
            final Iterable<Listener> listeners = unit.getListeners(Listener.class);
            Assert.assertSame(first, Iterables.getOnlyElement(listeners));

            unit.register(Listener.class, second);
            Assert.assertEquals(1, Iterables.size(listeners));
            Assert.assertEquals(Sets.newHashSet(first), Sets.newHashSet(listeners));

            unit.remove(Listener.class, first);
            Assert.assertTrue(Iterables.isEmpty(listeners));
        }
    }
    
    /**
     * Tests {@link Registry#getListeners(Key)} using live view, if supported.
     */
    @Test
    public void getListenersKeyLiveView() {
        final Listener first = EasyMock.createMock("first", Listener.class);
        final Listener second = EasyMock.createMock("second", Listener.class);
        EasyMock.replay(first, second);
        
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        
        if (supportsLiveView()) {
            final Registry unit = unit();
            unit.register(key, first);
            final Iterable<Listener> listeners = unit.getListeners(key);
            Assert.assertSame(first, Iterables.getOnlyElement(listeners));

            unit.register(key, second);
            Assert.assertEquals(2, Iterables.size(listeners));
            Assert.assertEquals(Sets.newHashSet(first, second), Sets.newHashSet(listeners));

            unit.remove(key, first);
            Assert.assertEquals(1, Iterables.size(listeners));
            Assert.assertSame(second, Iterables.getOnlyElement(listeners));
        } else {
            final Registry unit = unit();
            unit.register(key, first);
            final Iterable<Listener> listeners = unit.getListeners(key);
            Assert.assertSame(first, Iterables.getOnlyElement(listeners));

            unit.register(key, second);
            Assert.assertEquals(1, Iterables.size(listeners));
            Assert.assertEquals(Sets.newHashSet(first), Sets.newHashSet(listeners));

            unit.remove(key, first);
            Assert.assertTrue(Iterables.isEmpty(listeners));
        }
    }
    
    /**
     * Tests {@link Registry#getListeners(Class)} with a null type.
     */
    @Test(expected = NullPointerException.class)
    public void getListenersTypeNullType() {
        final Class<Object> nullType = null;
        unit().getListeners(nullType);
    }
    
    /**
     * Tests {@link Registry#getListeners(Key)} with a null key.
     */
    @Test(expected = NullPointerException.class)
    public void getListenersKeyNullKey() {
        final Key<Object> nullKey = null;
        unit().getListeners(nullKey);
    }

    /**
     * Tests {@link Registry#proxy(Class)} with no listeners
     * being registered.
     */
    @Test
    public void proxyTypeEmpty() {
        unit().proxy(Listener.class).doAnything();
    }
    
    /**
     * Tests {@link Registry#proxy(Key)} with no listeners
     * being registered.
     */
    @Test
    public void proxyKeyEmpty() {
        unit().proxy(Key.get(Listener.class, Deprecated.class)).doAnything();
    }
    
    /**
     * Tests {@link Registry#proxy(Class)} with a single listener.
     */
    @Test
    public void proxyTypeSingle() {
        final Registry unit = unit();
        
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        listener.doAnything();
        EasyMock.expectLastCall();
        EasyMock.replay(listener);
        
        unit.register(Listener.class, listener);

        unit.proxy(Listener.class).doAnything();
    }

    /**
     * Tests {@link Registry#proxy(Key)} with a single listener.
     */
    @Test
    public void proxyKeySingle() {
        final Registry unit = unit();
        
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        listener.doAnything();
        EasyMock.expectLastCall();
        EasyMock.replay(listener);
        
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        
        unit.register(key, listener);
        unit.proxy(key).doAnything();
    }
    
    /**
     * Tests {@link Registry#proxy(Class)} with multiple listeners.
     */
    @Test
    public void proxyTypeMultiple() {
        final Registry unit = unit();
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        first.doAnything();
        EasyMock.expectLastCall();
        
        final Listener second = EasyMock.createMock("second", Listener.class);
        second.doAnything();
        EasyMock.expectLastCall();
        
        EasyMock.replay(first, second);
        
        unit.register(Listener.class, first);
        unit.register(Listener.class, second);
        
        unit.proxy(Listener.class).doAnything();        
    }
    
    /**
     * Tests {@link Registry#proxy(Key)} with multiple listeners.
     */
    @Test
    public void proxyKeyMultiple() {
        final Registry unit = unit();
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        first.doAnything();
        EasyMock.expectLastCall();
        
        final Listener second = EasyMock.createMock("second", Listener.class);
        second.doAnything();
        EasyMock.expectLastCall();
        
        EasyMock.replay(first, second);
        
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        
        unit.register(key, first);
        unit.register(key, second);
        
        unit.proxy(key).doAnything();   
    }
    
    /**
     * Tests {@link Registry#proxy(Class)} with hot un/loading listeners.
     */
    @Test
    public void proxyTypeConcurrent() {
        final Registry unit = unit();
        
        final Listener proxy = unit.proxy(Listener.class);
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        first.doAnything();
        EasyMock.expectLastCall();
        first.doAnything();
        EasyMock.expectLastCall();
        EasyMock.replay(first);
        
        unit.register(Listener.class, first);
        
        proxy.doAnything();
        
        final Listener second = EasyMock.createMock("second", Listener.class);
        second.doAnything();
        EasyMock.expectLastCall();
        second.doAnything();
        EasyMock.expectLastCall();
        EasyMock.replay(second);
        
        unit.register(Listener.class, second);
        
        proxy.doAnything();
        
        unit.remove(Listener.class, first);
        
        proxy.doAnything();
    }
    
    /**
     * Tests {@link Registry#proxy(Key)} with hot un/loading listeners.
     */
    @Test
    public void proxyKeyConcurrent() {
        final Registry unit = unit();
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        
        final Listener proxy = unit.proxy(key);
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        first.doAnything();
        EasyMock.expectLastCall();
        first.doAnything();
        EasyMock.expectLastCall();
        EasyMock.replay(first);
        
        unit.register(key, first);
        
        proxy.doAnything();
        
        final Listener second = EasyMock.createMock("second", Listener.class);
        second.doAnything();
        EasyMock.expectLastCall();
        second.doAnything();
        EasyMock.expectLastCall();
        EasyMock.replay(second);
        
        unit.register(key, second);
        
        proxy.doAnything();
        
        unit.remove(key, first);
        
        proxy.doAnything();
    }
    
    /**
     * Tests that {@link Registry#proxy(Class)} does not register
     * the proxy itself.
     */
    @Test
    public void proxyTypeNotRegistered() {
        final Registry unit = unit();
        unit.proxy(Listener.class);
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Listener.class)));
    }
    
    /**
     * Tests that {@link Registry#proxy(Key)} does not register
     * the proxy itself.
     */
    @Test
    public void proxyKeyNotRegistered() {
        final Registry unit = unit();
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        unit.proxy(key);
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(key)));
    }
    
    /**
     * Tests {@link Registry#proxy(Class)} using an interface
     * providing a method which does not return void.
     */
    @Test(expected = IllegalStateException.class)
    public void proxyTypeNoVoid() {
        final Registry unit = unit();
        final Predicate<?> proxy = unit.proxy(Predicate.class);
        proxy.apply(null);
    }

    /**
     * Tests {@link Registry#proxy(Key)} using an interface
     * providing a method which does not return void.
     */
    @Test(expected = IllegalStateException.class)
    public void proxyKeyNoVoid() {
        final Registry unit = unit();
        final Predicate<?> proxy = unit.proxy(Key.get(Predicate.class, Deprecated.class));
        proxy.apply(null);
    }
    
    /**
     * Tests {@link Registry#proxy(Class)} with a null type.
     */
    @Test(expected = NullPointerException.class)
    public void proxyTypeNullType() {
        final Class<Object> nullType = null;
        unit().proxy(nullType);
    }
    
    /**
     * Tests {@link Registry#proxy(Key)} with a null key.
     */
    @Test(expected = NullPointerException.class)
    public void proxyKeyNullKey() {
        final Key<Object> nullKey = null;
        unit().proxy(nullKey);
    }
    
    /**
     * Tests {@link Registry#proxy(Class)} with a class type.
     */
    @Test(expected = IllegalArgumentException.class)
    public void proxyTypeClass() {
        unit().proxy(Object.class);
    }
    
    /**
     * Tests {@link Registry#proxy(Key)} with a class key.
     */
    @Test(expected = IllegalArgumentException.class)
    public void proxyKeyClass() {
        unit().proxy(Key.get(Object.class, Deprecated.class));
    }
    
    /**
     * Tests {@link Registry#proxy(Class)} with an enum type.
     */
    @Test(expected = IllegalArgumentException.class)
    public void proxyTypeEnum() {
        unit().proxy(TimeUnit.class);
    }
    
    /**
     * Tests {@link Registry#proxy(Key)} with an enum key.
     */
    @Test(expected = IllegalArgumentException.class)
    public void proxyKeyEnum() {
        unit().proxy(Key.get(TimeUnit.class, Deprecated.class));
    }
    
    /**
     * Tests {@link Registry#proxy(Class)} with an annotation type. 
     */
    @Test(expected = IllegalArgumentException.class)
    public void proxyTypeAnnotation() {
        unit().proxy(Deprecated.class);
    }
    
    /**
     * Tests {@link Registry#proxy(Key)} with an annotation key. 
     */
    @Test(expected = IllegalArgumentException.class)
    public void proxyKeyAnnotation() {
        unit().proxy(Key.get(Deprecated.class, Deprecated.class));
    }
    
    /**
     * Tests {@link Registry#notify(Class, Procedure)}.
     */
    @Test
    public void notifyType() {
        final Registry unit = unit();
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        first.doAnything();
        EasyMock.expectLastCall();

        final Listener second = EasyMock.createMock("second", Listener.class);
        second.doAnything();
        EasyMock.expectLastCall();
        
        EasyMock.replay(first, second);
        
        unit.register(Listener.class, first);
        unit.register(Listener.class, second);
        
        final Procedure<? super Listener> procedure = new Procedure<Listener>() {
            
            @Override
            public void apply(Listener input) {
                input.doAnything();
            }
            
        };
        
        unit.notify(Listener.class, procedure);
    }
    
    /**
     * Tests {@link Registry#notify(Key, Procedure)}.
     */
    @Test
    public void notifyKey() {
        final Registry unit = unit();
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        first.doAnything();
        EasyMock.expectLastCall();

        final Listener second = EasyMock.createMock("second", Listener.class);
        second.doAnything();
        EasyMock.expectLastCall();
        
        EasyMock.replay(first, second);
        
        unit.register(key, first);
        unit.register(key, second);
        
        final Procedure<? super Listener> procedure = new Procedure<Listener>() {
            
            @Override
            public void apply(Listener input) {
                input.doAnything();
            }
            
        };
        
        unit.notify(key, procedure);
    }
    
    /**
     * Tests {@link Registry#notify(Class, Procedure)} with a null type.
     */
    @Test(expected = NullPointerException.class)
    public void notifyTypeNullType() {
        @SuppressWarnings("unchecked")
        final Procedure<? super Object> procedure = EasyMock.createMock("procedure", Procedure.class);
        EasyMock.replay(procedure);
        final Class<Object> nullType = null;
        unit().notify(nullType, procedure);
    }
    
    /**
     * Tests {@link Registry#notify(Key, Procedure)} with a null key.
     */
    @Test(expected = NullPointerException.class)
    public void notifyKeyNullKey() {
        @SuppressWarnings("unchecked")
        final Procedure<? super Object> procedure = EasyMock.createMock("procedure", Procedure.class);
        EasyMock.replay(procedure);
        final Key<Object> nullKey = null;
        unit().notify(nullKey, procedure);
    }
    
    /**
     * Tests {@link Registry#notify(Class, Procedure)} with a null procedure.
     */
    @Test(expected = NullPointerException.class)
    public void notifyTypeNullProcedure() {
        unit().notify(Object.class, null);
    }

    /**
     * Tests {@link Registry#notify(Key, Procedure)} with a null procedure.
     */
    @Test(expected = NullPointerException.class)
    public void notifyKeyNullProcedure() {
        unit().notify(Key.get(Listener.class, Deprecated.class), null);
    }
    
    /**
     * Tests {@link Registry#notify(Class, Procedure)} with a null type and 
     * a null procedure.
     */
    @Test(expected = NullPointerException.class)
    public void notifyTypeNulls() {
        final Class<Object> nullType = null;
        unit().notify(nullType, null);
    }
    
    /**
     * Tests {@link Registry#notify(Key, Procedure)} with a null key and
     * a null procedure.
     */
    @Test(expected = NullPointerException.class)
    public void notifyKeyNulls() {
        final Key<Object> nullKey = null;
        unit().notify(nullKey, null);
    }
    
    /**
     * A custom runtime exception to check exception propagation in notify-method.
     *
     * @author Willi Schoenborn
     */
    private static final class CustomRuntimeException extends RuntimeException {

        private static final long serialVersionUID = -6586412563906113367L;
        
    }
    
    /**
     * Tests {@link Registry#notify(Class, Procedure)} with a failing procedure.
     */
    @Test(expected = CustomRuntimeException.class)
    public void notifyTypeRuntime() {
        final Registry unit = unit();
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        first.doAnything();
        EasyMock.expectLastCall().andThrow(new CustomRuntimeException());

        final Listener second = EasyMock.createMock("second", Listener.class);
        
        EasyMock.replay(first, second);
        unit.register(Listener.class, first);
        unit.register(Listener.class, second);
        
        final Procedure<? super Listener> procedure = new Procedure<Listener>() {
            
            @Override
            public void apply(Listener input) {
                input.doAnything();
            }
            
        };
        
        unit.notify(Listener.class, procedure);
    }
    
    /**
     * Tests {@link Registry#notify(Key, Procedure)} with a failing procedure.
     */
    @Test(expected = CustomRuntimeException.class)
    public void notifyKeyRuntime() {
        final Registry unit = unit();
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        first.doAnything();
        EasyMock.expectLastCall().andThrow(new CustomRuntimeException());

        final Listener second = EasyMock.createMock("second", Listener.class);
        
        EasyMock.replay(first, second);
        unit.register(key, first);
        unit.register(key, second);
        
        final Procedure<? super Listener> procedure = new Procedure<Listener>() {
            
            @Override
            public void apply(Listener input) {
                input.doAnything();
            }
            
        };
        
        unit.notify(key, procedure);
    }
    
    /**
     * Tests {@link Registry#notifySilent(Class, Procedure)}.
     */
    @Test
    public void notifySilentType() {
        final Registry unit = unit();
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        first.doAnything();
        EasyMock.expectLastCall();

        final Listener second = EasyMock.createMock("second", Listener.class);
        second.doAnything();
        EasyMock.expectLastCall();
        
        EasyMock.replay(first, second);
        unit.register(Listener.class, first);
        unit.register(Listener.class, second);
        
        final Procedure<? super Listener> procedure = new Procedure<Listener>() {
            
            @Override
            public void apply(Listener input) {
                input.doAnything();
            }
            
        };
        
        unit.notifySilent(Listener.class, procedure);
    }
    
    /**
     * Tests {@link Registry#notifySilent(Key, Procedure)}.
     */
    @Test
    public void notifySilentKey() {
        final Registry unit = unit();
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        first.doAnything();
        EasyMock.expectLastCall();

        final Listener second = EasyMock.createMock("second", Listener.class);
        second.doAnything();
        EasyMock.expectLastCall();
        
        EasyMock.replay(first, second);
        unit.register(key, first);
        unit.register(key, second);
        
        final Procedure<? super Listener> procedure = new Procedure<Listener>() {
            
            @Override
            public void apply(Listener input) {
                input.doAnything();
            }
            
        };
        
        unit.notifySilent(key, procedure);
    }

    /**
     * Tests {@link Registry#notifySilent(Class, Procedure)} with a null type.
     */
    @Test(expected = NullPointerException.class)
    public void notifySilentTypeNullType() {
        @SuppressWarnings("unchecked")
        final Procedure<? super Object> procedure = EasyMock.createMock("procedure", Procedure.class);
        EasyMock.replay(procedure);
        final Class<Object> nullType = null;
        unit().notifySilent(nullType, procedure);
    }
    
    /**
     * Tests {@link Registry#notifySilent(Key, Procedure)} with a null key.
     */
    @Test(expected = NullPointerException.class)
    public void notifySilentKeyNullKey() {
        @SuppressWarnings("unchecked")
        final Procedure<? super Object> procedure = EasyMock.createMock("procedure", Procedure.class);
        EasyMock.replay(procedure);
        final Key<Object> nullKey = null;
        unit().notifySilent(nullKey, procedure);
    }

    /**
     * Tests {@link Registry#notifySilent(Class, Procedure)} with a null procedure.
     */
    @Test(expected = NullPointerException.class)
    public void notifySilentTypeNullProcedure() {
        unit().notifySilent(Object.class, null);
    }
    
    /**
     * Tests {@link Registry#notifySilent(Key, Procedure)} with a null procedure.
     */
    @Test(expected = NullPointerException.class)
    public void notifySilentKeyNullProcedure() {
        unit().notifySilent(Key.get(Listener.class, Deprecated.class), null); 
    }
    
    /**
     * Tests {@link Registry#notifySilent(Class, Procedure)} with a null type
     * and a null procedure.
     */
    @Test(expected = NullPointerException.class)
    public void notifyTypeSilentNulls() {
        final Class<Object> nullType = null;
        unit().notifySilent(nullType, null);
    }
    
    /**
     * Tests {@link Registry#notifySilent(Key, Procedure)} with a null key
     * and a null procedure.
     */
    @Test(expected = NullPointerException.class)
    public void notifyKeySilentNulls() {
        final Key<Object> nullKey = null;
        unit().notifySilent(nullKey, null);
    }
    
    /**
     * Tests {@link Registry#notifySilent(Class, Procedure)} with failing procedure.
     */
    @Test
    public void notifySilentTypeRuntime() {
        final Registry unit = unit();
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        first.doAnything();
        EasyMock.expectLastCall().andThrow(new CustomRuntimeException());

        final Listener second = EasyMock.createMock("second", Listener.class);
        second.doAnything();
        EasyMock.expectLastCall().andThrow(new CustomRuntimeException());
        
        EasyMock.replay(first, second);
        unit.register(Listener.class, first);
        unit.register(Listener.class, second);
        
        final Procedure<? super Listener> procedure = new Procedure<Listener>() {
            
            @Override
            public void apply(Listener input) {
                input.doAnything();
            }
            
        };
        
        unit.notifySilent(Listener.class, procedure);
    }
    
    /**
     * Tests {@link Registry#notifySilent(Key, Procedure)} with a failing procedure.
     */
    @Test
    public void notifySilentKeyRuntime() {
        final Registry unit = unit();
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        first.doAnything();
        EasyMock.expectLastCall().andThrow(new CustomRuntimeException());

        final Listener second = EasyMock.createMock("second", Listener.class);
        second.doAnything();
        EasyMock.expectLastCall().andThrow(new CustomRuntimeException());
        
        EasyMock.replay(first, second);
        unit.register(key, first);
        unit.register(key, second);
        
        final Procedure<? super Listener> procedure = new Procedure<Listener>() {
            
            @Override
            public void apply(Listener input) {
                input.doAnything();
            }
            
        };
        
        unit.notifySilent(key, procedure);
    }
    
    /**
     * Tests the return value of {@link Registry#remove(Class, Object)}.
     */
    @Test
    public void removeTypeListener() {
        final Registry unit = unit();
        
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        EasyMock.replay(listener);
        unit.register(Listener.class, listener);
        
        Assert.assertTrue(unit.remove(Listener.class, listener));
        Assert.assertFalse(unit.remove(Listener.class, listener));
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Listener.class)));
    }
    
    /**
     * Tests the return value of {@link Registry#remove(Key, Object)}.
     */
    @Test
    public void removeKeyListener() {
        final Registry unit = unit();
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        EasyMock.replay(listener);
        unit.register(key, listener);
        
        Assert.assertTrue(unit.remove(key, listener));
        Assert.assertFalse(unit.remove(key, listener));
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(key)));
    }
    
    /**
     * Tests {@link Registry#remove(Class, Object)} with a listener which
     * is registered for multiple types.
     */
    @Test
    public void removeTypeListenerDifferentTypes() {
        final Registry unit = unit();
        
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        EasyMock.replay(listener);
        
        unit.register(Listener.class, listener);
        unit.register(Object.class, listener);
        
        Assert.assertTrue(unit.remove(Listener.class, listener));
        Assert.assertFalse(unit.remove(Listener.class, listener));
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Listener.class)));
        
        Assert.assertTrue(Iterables.contains(unit.getListeners(Object.class), listener));

        Assert.assertTrue(unit.remove(Object.class, listener));
        Assert.assertFalse(unit.remove(Object.class, listener));
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Object.class)));
    }

    /**
     * Tests {@link Registry#remove(Key, Object)} with a listener which
     * is registered for multiple keys/types.
     */
    @Test
    public void remoteKeyListenerDifferentTypes() {
        final Registry unit = unit();
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        EasyMock.replay(listener);
        
        unit.register(key, listener);
        unit.register(Object.class, listener);
        
        Assert.assertTrue(unit.remove(key, listener));
        Assert.assertFalse(unit.remove(key, listener));
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(key)));
        
        Assert.assertTrue(Iterables.contains(unit.getListeners(Object.class), listener));

        Assert.assertTrue(unit.remove(Object.class, listener));
        Assert.assertFalse(unit.remove(Object.class, listener));
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Object.class)));
    }
    
    /**
     * Tests {@link Registry#remove(Class, Object)} with a null type.
     */
    @Test(expected = NullPointerException.class)
    public void removeTypeListenerNullType() {
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        EasyMock.replay(listener);
        final Class<Object> nullType = null;
        unit().remove(nullType, listener);
    }
    
    /**
     * Tests {@link Registry#remove(Key, Object)} with a null key.
     */
    @Test(expected = NullPointerException.class)
    public void remoteKeyListenerNullKey() {
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        EasyMock.replay(listener);
        final Key<Object> nullKey = null;
        unit().remove(nullKey, listener);
    }
    
    /**
     * Tests {@link Registry#remove(Class, Object)} with a null listener.
     */
    @Test(expected = NullPointerException.class)
    public void removeTypeListenerNullListener() {
        unit().remove(Object.class, null);
    }
    
    /**
     * Tests {@link Registry#remove(Key, Object)} with a null listener.
     */
    @Test(expected = NullPointerException.class)
    public void removeKeyListenerNullListener() {
        unit().remove(Key.get(Listener.class, Deprecated.class), null);
    }
    
    /**
     * Tests {@link Registry#remove(Class, Object)} with a null type and
     * a null listener.
     */
    @Test(expected = NullPointerException.class)
    public void removeTypeListenerNulls() {
        final Class<Object> nullType = null;
        unit().remove(nullType, null);
    }
    
    /**
     * Tests {@link Registry#remove(Key, Object)} with a null key and
     * a null listener.
     */
    @Test(expected = NullPointerException.class)
    public void removeKeyListenerNulls() {
        final Key<Object> nullKey = null;
        unit().remove(nullKey, null);
    }

    /**
     * Tests the return value of {@link Registry#remove(Object)}.
     */
    @Test
    public void removeListener() {
        final Registry unit = unit();
        
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        EasyMock.replay(listener);
        
        unit.register(Listener.class, listener);

        Assert.assertSame(listener, Iterables.getOnlyElement(unit.getListeners(Listener.class)));
        Assert.assertTrue(unit.remove(listener));
        Assert.assertFalse(unit.remove(listener));
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Listener.class)));
    }
    
    /**
     * Tests {@link Registry#remove(Object)} for a listener which is 
     * registered for multiple types.
     */
    @Test
    public void removeListenerDifferentTypes() {
        final Registry unit = unit();

        final Listener listener = EasyMock.createMock("listener", Listener.class);
        EasyMock.replay(listener);
        
        unit.register(Listener.class, listener);
        unit.register(Object.class, listener);

        Assert.assertSame(listener, Iterables.getOnlyElement(unit.getListeners(Listener.class)));
        Assert.assertSame(listener, Iterables.getOnlyElement(unit.getListeners(Object.class)));
        Assert.assertTrue(unit.remove(listener));
        Assert.assertFalse(unit.remove(listener));
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Listener.class)));
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Object.class)));
    }
    
    /**
     * Tests {@link Registry#remove(Object)} with a null listener.
     */
    @Test(expected = NullPointerException.class)
    public void removeListenerNulls() {
        unit().remove(null);
    }
    
    /**
     * Tests the return value of {@link Registry#removeAll(Class)}.
     */
    @Test
    public void removeAllType() {
        final Registry unit = unit();
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        final Listener second = EasyMock.createMock("second", Listener.class);
        
        EasyMock.replay(first, second);
        unit.register(Listener.class, first);
        unit.register(Listener.class, second);
        
        Assert.assertEquals(Sets.newHashSet(first, second), Sets.newHashSet(unit.getListeners(Listener.class)));
        Assert.assertEquals(Sets.newHashSet(first, second), Sets.newHashSet(unit.removeAll(Listener.class)));
        Assert.assertTrue(Iterables.isEmpty(unit.removeAll(Listener.class)));
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Listener.class)));
    }
    
    /**
     * Tests the return value of {@link Registry#removeAll(Key)}.
     */
    @Test
    public void removeAllKey() {
        final Registry unit = unit();
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        final Listener second = EasyMock.createMock("second", Listener.class);
        
        EasyMock.replay(first, second);
        unit.register(key, first);
        unit.register(key, second);
        
        Assert.assertEquals(Sets.newHashSet(first, second), Sets.newHashSet(unit.getListeners(key)));
        Assert.assertEquals(Sets.newHashSet(first, second), Sets.newHashSet(unit.removeAll(key)));
        Assert.assertTrue(Iterables.isEmpty(unit.removeAll(key)));
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(key)));
    }
    
    /**
     * Tests {@link Registry#removeAll(Class)} with a listener which
     * is registered for multiple types.
     */
    @Test
    public void removeAllTypeDifferentTypes() {
        final Registry unit = unit();
        
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        EasyMock.replay(listener);
        
        unit.register(Listener.class, listener);
        unit.register(Object.class, listener);

        Assert.assertSame(listener, Iterables.getOnlyElement(unit.getListeners(Listener.class)));
        Assert.assertSame(listener, Iterables.getOnlyElement(unit.getListeners(Object.class)));
        Assert.assertEquals(Sets.newHashSet(listener), Sets.newHashSet(unit.removeAll(Listener.class)));
        Assert.assertTrue(Iterables.isEmpty(unit.removeAll(Listener.class)));
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Listener.class)));
        Assert.assertFalse(Iterables.isEmpty(unit.getListeners(Object.class)));
    }

    /**
     * Tests {@link Registry#removeAll(Key)} with a listener which
     * is registered for multiple keys.
     */
    @Test
    public void removeAllKeyDifferentTypes() {
        final Registry unit = unit();
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        EasyMock.replay(listener);
        
        unit.register(key, listener);
        unit.register(Object.class, listener);

        Assert.assertSame(listener, Iterables.getOnlyElement(unit.getListeners(key)));
        Assert.assertSame(listener, Iterables.getOnlyElement(unit.getListeners(Object.class)));
        Assert.assertEquals(Sets.newHashSet(listener), Sets.newHashSet(unit.removeAll(key)));
        Assert.assertTrue(Iterables.isEmpty(unit.removeAll(key)));
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(key)));
        Assert.assertFalse(Iterables.isEmpty(unit.getListeners(Object.class)));
    }
    
    /**
     * Tests {@link Registry#removeAll(Class)} with a null type.
     */
    @Test(expected = NullPointerException.class)
    public void removeAllTypeNullType() {
        final Class<Object> nullType = null;
        unit().removeAll(nullType);
    }
    
    /**
     * Tests {@link Registry#removeAll(Key)} with a null key.
     */
    @Test(expected = NullPointerException.class)
    public void removeAllKeyNullKey() {
        final Key<Object> nullKey = null;
        unit().removeAll(nullKey);
    }
    
}