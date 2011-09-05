/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.core;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import de.cosmocode.collections.Procedure;
import de.cosmocode.commons.Throwables;
import de.cosmocode.junit.UnitProvider;
import de.cosmocode.palava.core.Registry.Key;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Tests {@link Registry} implementations.
 * 
 * @author Willi Schoenborn
 */
public abstract class AbstractRegistryTest implements UnitProvider<Registry> {

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
        EasyMock.verify(listener);
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
        EasyMock.verify(listener);
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
        EasyMock.verify(listener);
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
        EasyMock.verify(listener);
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
        EasyMock.verify(listener);
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
        EasyMock.verify(listener);
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
        EasyMock.verify(first, second);
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
        EasyMock.verify(first, second);
    }
    
    /**
     * Tests {@link Registry#getListeners(Class)} using live view, if supported.
     */
    @Test
    public void getListenersTypeLiveView() {
        final Listener first = EasyMock.createMock("first", Listener.class);
        final Listener second = EasyMock.createMock("second", Listener.class);
        EasyMock.replay(first, second);
        
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
        
        EasyMock.verify(first, second);
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
        
        EasyMock.verify(first, second);
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
     * Tests {@link Registry#find(Class, Predicate)} without expected matches.
     */
    @Test
    public void findEmpty() {
        Assert.assertTrue(Iterables.isEmpty(unit().find(Object.class, Predicates.alwaysFalse())));
    }
    
    /**
     * Tests {@link Registry#find(Class, Predicate)} with matches using a always-true predicate.
     */
    @Test
    public void findMatches() {
        final Listener a = EasyMock.createMock("a", Listener.class);
        final Listener b = EasyMock.createMock("b", Listener.class);
        EasyMock.replay(a, b);
        final Registry unit = unit();
        unit.register(Listener.class, a);
        unit.register(Listener.class, b);
        
        final Predicate<Object> predicate = new Predicate<Object>() {
            
            @Override
            public boolean apply(Object input) {
                return input == null;
            }
            
        };
        
        final Iterable<Listener> listeners = unit.find(Listener.class, predicate);
        Assert.assertTrue(Iterables.size(listeners) == 2);
        Assert.assertTrue(Iterables.contains(listeners, a));
        Assert.assertTrue(Iterables.contains(listeners, b));
        EasyMock.verify(a, b);
    }

    /**
     * Tests {@link Registry#find(Class, Predicate)} with matches using a selective predicate.
     */
    @Test
    public void findSelectedMatches() {
        final Listener a = EasyMock.createMock("a", Listener.class);
        final Listener b = EasyMock.createMock("b", Listener.class);
        final Listener c = EasyMock.createMock("c", Listener.class);
        EasyMock.replay(a, b, c);
        final Registry unit = unit();
        final Object meta = new Object();
        unit.register(Listener.class, a);
        unit.register(Key.get(Listener.class, meta), b);
        unit.register(Key.get(Listener.class, meta), c);
        
        final Predicate<Object> predicate = new Predicate<Object>() {
            
            @Override
            public boolean apply(Object input) {
                return input == meta;
            }
            
        };
        
        final Iterable<Listener> listeners = unit.find(Listener.class, predicate);
        Assert.assertTrue(Iterables.size(listeners) == 2);
        Assert.assertTrue(Iterables.contains(listeners, b));
        Assert.assertTrue(Iterables.contains(listeners, c));
        EasyMock.verify(a, b);
    }
    
    /**
     * Tests whether {@link Registry#find(Class, Predicate)} supports live view.
     */
    @Test
    public void findLiveView() {
        final Listener a = EasyMock.createMock("a", Listener.class);
        final Listener b = EasyMock.createMock("b", Listener.class);
        final Listener c = EasyMock.createMock("c", Listener.class);
        EasyMock.replay(a, b, c);
        final Registry unit = unit();
        final Object meta = new Object();
        unit.register(Key.get(Listener.class, meta), a);
        unit.register(Key.get(Listener.class, meta), b);
        
        final Predicate<Object> predicate = new Predicate<Object>() {
            
            @Override
            public boolean apply(Object input) {
                return input == meta;
            }
            
        };
        
        final Iterable<Listener> listeners = unit.find(Listener.class, predicate);
        Assert.assertTrue(Iterables.size(listeners) == 2);
        Assert.assertTrue(Iterables.contains(listeners, a));
        Assert.assertTrue(Iterables.contains(listeners, b));
        
        unit.register(Key.get(Listener.class, meta), c);
        
        Assert.assertTrue(Iterables.size(listeners) == 3);
        Assert.assertTrue(Iterables.contains(listeners, a));
        Assert.assertTrue(Iterables.contains(listeners, b));
        Assert.assertTrue(Iterables.contains(listeners, c));
        
        EasyMock.verify(a, b);
    }
    
    /**
     * Tests {@link Registry#find(Class, Predicate)} whether it returns
     * an immutable iterable.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void findImmutable() {
        final Registry unit = unit();
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        EasyMock.replay(listener);
        unit.register(Listener.class, listener);
        final Iterable<Listener> listeners = unit.find(Listener.class, Predicates.alwaysTrue());
        EasyMock.verify(listener);
        final Iterator<Listener> iterator = listeners.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertSame(listener, iterator.next());
        iterator.remove();
    }
    
    /**
     * Tests {@link Registry#find(Class, Predicate)} with a null type.
     */
    @Test(expected = NullPointerException.class)
    public void findNullType() {
        unit().find(null, Predicates.alwaysTrue());
    }
    
    /**
     * Tests {@link Registry#find(Class, Predicate)} with a null predicate.
     */
    @Test(expected = NullPointerException.class)
    public void findNullPredicate() {
        unit().find(Object.class, null);
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
        EasyMock.verify(listener);
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
        EasyMock.verify(listener);
    }

    @Test(expected = CustomRuntimeException.class)
    public void proxyTypeException() throws IOException {
        final Registry unit = unit();

        final Listener listener = EasyMock.createMock("listener", Listener.class);
        listener.doAnything();
        EasyMock.expectLastCall().andThrow(new CustomRuntimeException());
        EasyMock.replay(listener);

        unit.register(Listener.class, listener);
        unit.proxy(Listener.class).doAnything();
    }

    @Test(expected = CustomRuntimeException.class)
    public void proxyKeyException() throws IOException {
        final Registry unit = unit();

        final Listener listener = EasyMock.createMock("listener", Listener.class);
        listener.doAnything();
        EasyMock.expectLastCall().andThrow(new CustomRuntimeException());
        EasyMock.replay(listener);

        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);

        unit.register(key, listener);
        unit.proxy(key).doAnything();
    }

    @Test(expected = IOException.class)
    public void proxyTypeCheckedException() throws IOException {
        final Registry unit = unit();

        final FailingListener listener = EasyMock.createMock("listener", FailingListener.class);
        listener.doAnything();
        EasyMock.expectLastCall().andThrow(new IOException());
        EasyMock.replay(listener);

        unit.register(FailingListener.class, listener);
        unit.proxy(FailingListener.class).doAnything();
    }

    @Test(expected = IOException.class)
    public void proxyKeyCheckedException() throws IOException {
        final Registry unit = unit();

        final FailingListener listener = EasyMock.createMock("listener", FailingListener.class);
        listener.doAnything();
        EasyMock.expectLastCall().andThrow(new IOException());
        EasyMock.replay(listener);

        final Key<FailingListener> key = Key.get(FailingListener.class, Deprecated.class);

        unit.register(key, listener);
        unit.proxy(key).doAnything();
    }

    @Test(expected = RuntimeException.class)
    public void proxyTypeUndeclaredException() throws RuntimeException {
        final Registry unit = unit();

        final Listener listener = new Listener() {

            @Override
            public void doAnything() {
                throw Throwables.sneakyThrow(new IOException());
            }

        };

        unit.register(Listener.class, listener);

        try {
            unit.proxy(Listener.class).doAnything();
        } catch (UndeclaredThrowableException e) {
            throw new AssertionError(e);
        }
    }

    @Test(expected = RuntimeException.class)
    public void proxyKeyUndeclaredException() throws RuntimeException {
        final Registry unit = unit();

        final Listener listener = new Listener() {

            @Override
            public void doAnything() {
                throw Throwables.sneakyThrow(new IOException());
            }

        };

        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);

        unit.register(key, listener);

        try {
            unit.proxy(key).doAnything();
        } catch (UndeclaredThrowableException e) {
            throw new AssertionError(e);
        }
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
        EasyMock.verify(first, second);     
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
        EasyMock.verify(first, second);  
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
        EasyMock.verify(first, second);  
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
        EasyMock.verify(first, second);  
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
     * Tests {@link Object#toString()} of {@link Registry#proxy(Key)}.
     */
    @Test
    public void proxyKeyToString() {
        final Registry unit = unit();
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        unit.proxy(key).toString();
    }
    
    /**
     * Tests {@link Object#hashCode()} of {@link Registry#proxy(Key)}.
     */
    @Test
    public void proxyKeyHashCode() {
        final Registry unit = unit();
        final Key<Listener> key = Key.get(Listener.class, Deprecated.class);
        unit.proxy(key).hashCode();
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
        EasyMock.verify(first, second);  
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
        EasyMock.verify(first, second);  
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
        
        try {
            unit.notify(Listener.class, procedure);
        } finally {
            EasyMock.verify(first, second);  
        }
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
        
        try {
            unit.notify(key, procedure);
        } finally {
            EasyMock.verify(first, second);  
        }
    }
    
    /**
     * Tests {@link Registry#notifySilent(Class, Procedure)}.
     */
    @Test
    public void notifySilentlyType() {
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
        
        unit.notifySilently(Listener.class, procedure);
        EasyMock.verify(first, second);  
    }
    
    /**
     * Tests {@link Registry#notifySilent(Key, Procedure)}.
     */
    @Test
    public void notifySilentlyKey() {
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
        
        unit.notifySilently(key, procedure);
        EasyMock.verify(first, second);  
    }

    /**
     * Tests {@link Registry#notifySilent(Class, Procedure)} with a null type.
     */
    @Test(expected = NullPointerException.class)
    public void notifySilentlyTypeNullType() {
        @SuppressWarnings("unchecked")
        final Procedure<? super Object> procedure = EasyMock.createMock("procedure", Procedure.class);
        EasyMock.replay(procedure);
        final Class<Object> nullType = null;
        unit().notifySilently(nullType, procedure);
    }
    
    /**
     * Tests {@link Registry#notifySilent(Key, Procedure)} with a null key.
     */
    @Test(expected = NullPointerException.class)
    public void notifySilentlyKeyNullKey() {
        @SuppressWarnings("unchecked")
        final Procedure<? super Object> procedure = EasyMock.createMock("procedure", Procedure.class);
        EasyMock.replay(procedure);
        final Key<Object> nullKey = null;
        unit().notifySilently(nullKey, procedure);
    }

    /**
     * Tests {@link Registry#notifySilent(Class, Procedure)} with a null procedure.
     */
    @Test(expected = NullPointerException.class)
    public void notifySilentlyTypeNullProcedure() {
        unit().notifySilently(Object.class, null);
    }
    
    /**
     * Tests {@link Registry#notifySilent(Key, Procedure)} with a null procedure.
     */
    @Test(expected = NullPointerException.class)
    public void notifySilentlyKeyNullProcedure() {
        unit().notifySilently(Key.get(Listener.class, Deprecated.class), null); 
    }
    
    /**
     * Tests {@link Registry#notifySilent(Class, Procedure)} with a null type
     * and a null procedure.
     */
    @Test(expected = NullPointerException.class)
    public void notifySilentlyTypeNulls() {
        final Class<Object> nullType = null;
        unit().notifySilently(nullType, null);
    }
    
    /**
     * Tests {@link Registry#notifySilent(Key, Procedure)} with a null key
     * and a null procedure.
     */
    @Test(expected = NullPointerException.class)
    public void notifySilentlyKeyNulls() {
        final Key<Object> nullKey = null;
        unit().notifySilently(nullKey, null);
    }
    
    /**
     * Tests {@link Registry#notifySilent(Class, Procedure)} with failing procedure.
     */
    @Test
    public void notifySilentlyTypeRuntime() {
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
        
        unit.notifySilently(Listener.class, procedure);
        EasyMock.verify(first, second);  
    }
    
    /**
     * Tests {@link Registry#notifySilent(Key, Procedure)} with a failing procedure.
     */
    @Test
    public void notifySilentlyKeyRuntime() {
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
        
        unit.notifySilently(key, procedure);
        EasyMock.verify(first, second);  
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
        EasyMock.verify(listener);  
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
        EasyMock.verify(listener);
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
        EasyMock.verify(listener);
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
        EasyMock.verify(listener);
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
        EasyMock.verify(listener);
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
        EasyMock.verify(listener);
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
        EasyMock.verify(first, second);
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
        EasyMock.verify(first, second);
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
        EasyMock.verify(listener);
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
        EasyMock.verify(listener);
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
