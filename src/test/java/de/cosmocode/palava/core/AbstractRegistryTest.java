package de.cosmocode.palava.core;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Tests {@link Registry} implementations.
 * 
 * TODO add tests for Key-methods
 *
 * @author Willi Schoenborn
 */
public abstract class AbstractRegistryTest {

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
    public void register() {
        final Registry unit = unit();
        final Object listener = new Object();
        unit.register(Object.class, listener);
        Assert.assertSame(listener, Iterables.getOnlyElement(unit.getListeners(Object.class)));
    }
    
    /**
     * Tests {@link Registry#register(Class, Object)} using the same pair of
     * type and listener twice.
     */
    @Test
    public void registerSame() {
        final Registry unit = unit();
        final Object listener = new Object();
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Object.class)));
        unit.register(Object.class, listener);
        Assert.assertFalse(Iterables.isEmpty(unit.getListeners(Object.class)));
        unit.register(Object.class, listener);
        Assert.assertFalse(Iterables.isEmpty(unit.getListeners(Object.class)));
        Assert.assertSame(listener, Iterables.getOnlyElement(unit.getListeners(Object.class)));
    }
    
    /**
     * Tests {@link Registry#register(Class, Object)} using the same listener 
     * for different types.
     */
    @Test
    public void registerDifferentTypes() {
        final Registry unit = unit();
        final Listener listener = EasyMock.createMock("listener", Listener.class);
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
     * Tests {@link Registry#register(Class, Object)} with a null type.
     */
    @Test(expected = NullPointerException.class)
    public void registerNullType() {
        final Class<Object> nullType = null;
        unit().register(nullType, new Object());
    }
    
    /**
     * Tests {@link Registry#register(Class, Object)} with a null listener.
     */
    @Test(expected = NullPointerException.class)
    public void registerNullListener() {
        unit().register(Charset.class, null);
    }
    
    /**
     * Tests {@link Registry#register(Class, Object)} with a null type and
     * a null listener.
     */
    @Test(expected = NullPointerException.class)
    public void registerNulls() {
        final Class<Object> nullType = null;
        unit().register(nullType, null);
    }
    
    /**
     * Tests {@link Registry#getListeners(Class)}.
     */
    @Test
    public void getListeners() {
        final Registry unit = unit();
        final Object first = new Object();
        final Object second = new Object();
        
        unit.register(Object.class, first);
        Assert.assertSame(first, Iterables.getOnlyElement(unit.getListeners(Object.class)));

        unit.register(Object.class, second);
        Assert.assertEquals(2, Iterables.size(unit.getListeners(Object.class)));
        Assert.assertEquals(Sets.newHashSet(first, second), Sets.newHashSet(unit.getListeners(Object.class)));

        unit.remove(Object.class, first);
        Assert.assertEquals(1, Iterables.size(unit.getListeners(Object.class)));
        Assert.assertSame(second, Iterables.getOnlyElement(unit.getListeners(Object.class)));
    }
    
    /**
     * Tests {@link Registry#getListeners(Class)} using live view, if supported.
     */
    @Test
    public void getListenersLiveView() {
        if (supportsLiveView()) {
            final Registry unit = unit();
            final Object first = new Object();
            final Object second = new Object();
            
            unit.register(Object.class, first);
            final Iterable<Object> listeners = unit.getListeners(Object.class);
            Assert.assertSame(first, Iterables.getOnlyElement(listeners));

            unit.register(Object.class, second);
            Assert.assertEquals(2, Iterables.size(listeners));
            Assert.assertEquals(Sets.newHashSet(first, second), Sets.newHashSet(listeners));

            unit.remove(Object.class, first);
            Assert.assertEquals(1, Iterables.size(listeners));
            Assert.assertSame(second, Iterables.getOnlyElement(listeners));
        } else {
            return;
        }
    }
    
    /**
     * Tests {@link Registry#getListeners(Class)} with a null type.
     */
    @Test(expected = NullPointerException.class)
    public void getListenersNullType() {
        final Class<Object> nullType = null;
        unit().getListeners(nullType);
    }
    
    /**
     * Hidden interface to easily mock listeners.
     *
     * @author Willi Schoenborn
     */
    private static interface Listener {

        void doAnything();
        
    }

    /**
     * Tests {@link Registry#proxy(Class)} with no listeners
     * being registered.
     */
    @Test
    public void proxyEmpty() {
        unit().proxy(Listener.class).doAnything();
    }
    
    /**
     * Tests {@link Registry#proxy(Class)} with a single listener.
     */
    @Test
    public void proxySingle() {
        final Registry unit = unit();
        
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        listener.doAnything();
        EasyMock.expectLastCall();
        EasyMock.replay(listener);
        
        unit.register(Listener.class, listener);

        unit.proxy(Listener.class).doAnything();
    }
    
    /**
     * Tests {@link Registry#proxy(Class)} with multiple listeners.
     */
    @Test
    public void proxyMultiple() {
        final Registry unit = unit();
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        first.doAnything();
        EasyMock.expectLastCall();
        EasyMock.replay(first);
        
        final Listener second = EasyMock.createMock("second", Listener.class);
        second.doAnything();
        EasyMock.expectLastCall();
        EasyMock.replay(second);
        
        unit.register(Listener.class, first);
        unit.register(Listener.class, second);
        
        unit.proxy(Listener.class).doAnything();        
    }
    
    /**
     * Tests {@link Registry#proxy(Class)} with hot un/loading listeners.
     */
    @Test
    public void proxyConcurrent() {
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
     * Tests that {@link Registry#proxy(Class)} does not register
     * the proxy itself.
     */
    @Test
    public void proxyNotRegistered() {
        final Registry unit = unit();
        unit.proxy(Listener.class);
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Listener.class)));
    }
    
    /**
     * Tests {@link Registry#proxy(Class)} using an interface
     * providing a method which does not return void.
     */
    @Test(expected = IllegalStateException.class)
    public void proxyNoVoid() {
        final Registry unit = unit();
        final Predicate<?> proxy = unit.proxy(Predicate.class);
        proxy.apply(null);
    }
    
    /**
     * Tests {@link Registry#proxy(Class)} with a null type.
     */
    @Test(expected = NullPointerException.class)
    public void proxyNullType() {
        final Class<Object> nullType = null;
        unit().proxy(nullType);
    }
    
    /**
     * Tests {@link Registry#proxy(Class)} with a class type.
     */
    @Test(expected = IllegalArgumentException.class)
    public void proxyClass() {
        unit().proxy(Object.class);
    }
    
    /**
     * Tests {@link Registry#proxy(Class)} with an enum type.
     */
    @Test(expected = IllegalArgumentException.class)
    public void proxyEnum() {
        unit().proxy(TimeUnit.class);
    }
    
    /**
     * Tests {@link Registry#proxy(Class)} with an annotation type. 
     */
    @Test(expected = IllegalArgumentException.class)
    public void proxyAnnotation() {
        unit().proxy(Deprecated.class);
    }
    
    /**
     * Tests {@link Registry#notify(Class, Procedure)}.
     */
    @Test
    public void notifyTest() {
        final Registry unit = unit();
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        first.doAnything();
        EasyMock.expectLastCall();
        EasyMock.replay(first);
        unit.register(Listener.class, first);

        final Listener second = EasyMock.createMock("second", Listener.class);
        second.doAnything();
        EasyMock.expectLastCall();
        EasyMock.replay(second);
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
     * Tests {@link Registry#notify(Class, Procedure)} with a null type.
     */
    @Test(expected = NullPointerException.class)
    public void notifyNullType() {
        @SuppressWarnings("unchecked")
        final Procedure<? super Object> procedure = EasyMock.createMock("procedure", Procedure.class);
        EasyMock.replay(procedure);
        final Class<Object> nullType = null;
        unit().notify(nullType, procedure);
    }
    
    /**
     * Tests {@link Registry#notify(Class, Procedure)} with a null procedure.
     */
    @Test(expected = NullPointerException.class)
    public void notifyNullProcedure() {
        unit().notify(Object.class, null);
    }
    
    /**
     * Tests {@link Registry#notify(Class, Procedure)} with a null type and 
     * a null procedure.
     */
    @Test(expected = NullPointerException.class)
    public void notifyNulls() {
        final Class<Object> nullType = null;
        unit().notify(nullType, null);
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
    public void notifyRuntime() {
        final Registry unit = unit();
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        first.doAnything();
        EasyMock.expectLastCall().andThrow(new CustomRuntimeException());
        EasyMock.replay(first);
        unit.register(Listener.class, first);

        final Listener second = EasyMock.createMock("second", Listener.class);
        EasyMock.replay(second);
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
     * Tests {@link Registry#notifySilent(Class, Procedure)}.
     */
    @Test
    public void notifySilent() {
        final Registry unit = unit();
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        first.doAnything();
        EasyMock.expectLastCall();
        EasyMock.replay(first);
        unit.register(Listener.class, first);

        final Listener second = EasyMock.createMock("second", Listener.class);
        second.doAnything();
        EasyMock.expectLastCall();
        EasyMock.replay(second);
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
     * Tests {@link Registry#notifySilent(Class, Procedure)} with a null type.
     */
    @Test(expected = NullPointerException.class)
    public void notifySilentNullType() {
        @SuppressWarnings("unchecked")
        final Procedure<? super Object> procedure = EasyMock.createMock("procedure", Procedure.class);
        EasyMock.replay(procedure);
        unit().notifySilent(null, procedure);
    }

    /**
     * Tests {@link Registry#notifySilent(Class, Procedure)} with a null procedure.
     */
    @Test(expected = NullPointerException.class)
    public void notifySilentNullProcedure() {
        unit().notifySilent(Object.class, null);
    }
    
    /**
     * Tests {@link Registry#notifySilent(Class, Procedure)} with a null type
     * and a null procedure.
     */
    @Test(expected = NullPointerException.class)
    public void notifySilentNulls() {
        unit().notifySilent(null, null);
    }
    
    /**
     * Tests {@link Registry#notifySilent(Class, Procedure)} with failing procedure.
     */
    @Test
    public void notifySilentRuntime() {
        final Registry unit = unit();
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        first.doAnything();
        EasyMock.expectLastCall().andThrow(new CustomRuntimeException());
        EasyMock.replay(first);
        unit.register(Listener.class, first);

        final Listener second = EasyMock.createMock("second", Listener.class);
        second.doAnything();
        EasyMock.expectLastCall().andThrow(new CustomRuntimeException());
        EasyMock.replay(second);
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
     * Tests the return value of {@link Registry#remove(Class, Object)}.
     */
    @Test
    public void removeTypeListener() {
        final Registry unit = unit();
        
        final Listener listener = EasyMock.createMock("listener", Listener.class);
        unit.register(Listener.class, listener);
        
        Assert.assertTrue(unit.remove(Listener.class, listener));
        Assert.assertFalse(unit.remove(Listener.class, listener));
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Listener.class)));
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
     * Tests {@link Registry#remove(Class, Object)} with a null listener.
     */
    @Test(expected = NullPointerException.class)
    public void removeTypeListenerNullListener() {
        unit().remove(Object.class, null);
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
    public void removeAll() {
        final Registry unit = unit();
        
        final Listener first = EasyMock.createMock("first", Listener.class);
        EasyMock.replay(first);
        
        final Listener second = EasyMock.createMock("second", Listener.class);
        EasyMock.replay(second);

        unit.register(Listener.class, first);
        unit.register(Listener.class, second);
        
        Assert.assertEquals(Sets.newHashSet(first, second), Sets.newHashSet(unit.getListeners(Listener.class)));
        Assert.assertEquals(Sets.newHashSet(first, second), Sets.newHashSet(unit.removeAll(Listener.class)));
        Assert.assertTrue(Iterables.isEmpty(unit.removeAll(Listener.class)));
        Assert.assertTrue(Iterables.isEmpty(unit.getListeners(Listener.class)));
    }
    
    /**
     * Tests {@link Registry#removeAll(Class)} with a listener which
     * is registered for multiple types.
     */
    @Test
    public void removeAllDifferentTypes() {
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
     * Tests {@link Registry#removeAll(Class)} with a null type.
     */
    @Test(expected = NullPointerException.class)
    public void removeAllNullType() {
        final Class<Object> nullType = null;
        unit().removeAll(nullType);
    }
    
}
