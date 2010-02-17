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

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.inject.internal.Lists;
import com.google.inject.internal.Maps;
import com.google.inject.internal.Sets;

import de.cosmocode.junit.Asserts;
import de.cosmocode.palava.core.Registry.Key;

/**
 * Tests {@link Registry.Key}.
 *
 * @author Willi Schoenborn
 */
public final class RegistryKeyTest {

    private static final Object ANY = new Object() {

        @Override
        public int hashCode() {
            return super.hashCode();
        }
        
        @Override
        public boolean equals(Object that) {
            return true;
        };
        
    };
    
    private static final Set<String> ELEMENT_SET = ImmutableSet.of("element");
    
    private static final Object ANY_SET = new Object() {
        
        @Override
        public int hashCode() {
            return super.hashCode();
        };
        
        @Override
        public boolean equals(Object that) {
            return that instanceof Set<?>;
        };
        
    };
    
    private static final Predicate<Object> SET_PREDICATE = new Predicate<Object>() {
        
        @Override
        public boolean apply(Object input) {
            return input instanceof Set<?>;
        }
        
    };
    
    private static final Object ANY_ELEMENT_SET = new Object() {
        
        @Override
        public int hashCode() {
            return super.hashCode();
        };
        
        @Override
        public boolean equals(Object that) {
            return that instanceof Set<?> && Set.class.cast(that).contains("element");
        };
        
    };
    
    private static final Predicate<Object> ELEMENT_SET_PREDICATE = new Predicate<Object>() {
        
        @Override
        public boolean apply(Object input) {
            return input instanceof Set<?> && Set.class.cast(input).contains("element");
        }
        
    };
    
    /**
     * Tests {@link Registry.Key#equals(Object)} with the same instance.
     */
    @Test
    public void same() {
        final Key<Object> key = Key.get(Object.class);
        Assert.assertEquals(key, key);
    }
    
    /**
     * Tests {@link Registry.Key#equals(Object)} with similiar type keys.
     */
    @Test
    public void equalsType() {
        Assert.assertEquals(Key.get(Object.class), Key.get(Object.class));
    }
    
    /**
     * Tests {@link Registry.Key#equals(Object)} with similiar type and meta keys.
     */
    @Test
    public void equalsTypeMeta() {
        Assert.assertEquals(Key.get(Object.class, "meta"), Key.get(Object.class, "meta"));
    }
    
    /**
     * Tests {@link Registry.Key#equals(Object)} with different type keys.
     */
    @Test
    public void differentType() {
        Asserts.assertNotEquals(Key.get(Integer.class), Key.get(Long.class));
    }
    
    /**
     * Tests {@link Registry.Key#equals(Object)} with different meta information.
     */
    @Test
    public void differentMeta() {
        Asserts.assertNotEquals(Key.get(Object.class, "a"), Key.get(Object.class, "b"));
    }
    
    /**
     * Tests {@link Registry.Key#equals(Object)} with different types and different
     * meta information.
     */
    @Test
    public void different() {
        Asserts.assertNotEquals(Key.get(Integer.class, "a"), Key.get(Long.class, "b"));
    }
    
    /**
     * Tests {@link Registry.Key#equals(Object)} with an "any matching" meta information
     * on the right.
     */
    @Test
    public void any() {
        Assert.assertEquals(Key.get(Object.class), Key.get(Object.class, ANY));
        Assert.assertEquals(Key.get(Object.class, "meta"), Key.get(Object.class, ANY));
    }
    
    /**
     * Tests {@link Registry.Key#equals(Object)} with an "any matching" meta information
     * on the left.
     */
    @Test
    public void anyInverse() {
        Assert.assertEquals(Key.get(Object.class, ANY), Key.get(Object.class));
        Assert.assertEquals(Key.get(Object.class, ANY), Key.get(Object.class, "meta"));
    }
    
    /**
     * Tests {@link Registry.Key#equals(Object)} with a meta information
     * on the right matching only sets.
     */
    @Test
    public void set() {
        Assert.assertEquals(Key.get(Object.class, Sets.newHashSet()), Key.get(Object.class, ANY_SET));
        Assert.assertEquals(Key.get(Object.class, ELEMENT_SET), Key.get(Object.class, ANY_SET));
        Asserts.assertNotEquals(Key.get(Object.class, Lists.newArrayList()), Key.get(Object.class, ANY_SET));
        Asserts.assertNotEquals(Key.get(Object.class, Maps.newHashMap()), Key.get(Object.class, ANY_SET));
    }
    
    /**
     * Tests {@link Registry.Key#equals(Object)} with a meta information
     * on the left matching only sets.
     */
    @Test
    public void setInverse() {
        Assert.assertEquals(Key.get(Object.class, ANY_SET), Key.get(Object.class, Sets.newHashSet()));
        Assert.assertEquals(Key.get(Object.class, ANY_SET), Key.get(Object.class, ELEMENT_SET));
        Asserts.assertNotEquals(Key.get(Object.class, ANY_SET), Key.get(Object.class, Lists.newArrayList()));
        Asserts.assertNotEquals(Key.get(Object.class, ANY_SET), Key.get(Object.class, Maps.newHashMap()));
    }
    
    /**
     * Tests {@link Registry.Key#equals(Object)} with a meta information
     * on the right matching only sets with the element "element".
     */
    @Test
    public void setWithElement() {
        Assert.assertEquals(Key.get(Object.class, ELEMENT_SET), Key.get(Object.class, ANY_ELEMENT_SET));
        final Set<String> set = Sets.newHashSet();
        set.add("element");
        Assert.assertEquals(Key.get(Object.class, set), Key.get(Object.class, ANY_ELEMENT_SET));
        Asserts.assertNotEquals(Key.get(Object.class, Sets.newHashSet()), Key.get(Object.class, ANY_ELEMENT_SET));
        Asserts.assertNotEquals(Key.get(Object.class, Lists.newArrayList()), Key.get(Object.class, ANY_ELEMENT_SET));
        Asserts.assertNotEquals(Key.get(Object.class, Maps.newHashMap()), Key.get(Object.class, ANY_ELEMENT_SET));
    }

    /**
     * Tests {@link Registry.Key#equals(Object)} with a meta information
     * on the left matching only sets with the element "element".
     */
    @Test
    public void setWithElementInverse() {
        Assert.assertEquals(Key.get(Object.class, ANY_ELEMENT_SET), Key.get(Object.class, ELEMENT_SET));
        final Set<String> set = Sets.newHashSet();
        set.add("element");
        Assert.assertEquals(Key.get(Object.class, ANY_ELEMENT_SET), Key.get(Object.class, set));
        Asserts.assertNotEquals(Key.get(Object.class, ANY_ELEMENT_SET), Key.get(Object.class, Sets.newHashSet()));
        Asserts.assertNotEquals(Key.get(Object.class, ANY_ELEMENT_SET), Key.get(Object.class, Lists.newArrayList()));
        Asserts.assertNotEquals(Key.get(Object.class, ANY_ELEMENT_SET), Key.get(Object.class, Maps.newHashMap()));
    }
    
    /**
     * Tests {@link Registry.Key#matcher(Predicate)} with a set matcher
     * on the right.
     */
    @Test
    public void matcherSet() {
        Assert.assertEquals(Key.get(Object.class, Sets.newHashSet()), Key.matcher(Object.class, SET_PREDICATE));
        Assert.assertEquals(Key.get(Object.class, ELEMENT_SET), Key.matcher(Object.class, SET_PREDICATE));
        Asserts.assertNotEquals(Key.get(Object.class, Lists.newArrayList()), Key.matcher(Object.class, SET_PREDICATE));
        Asserts.assertNotEquals(Key.get(Object.class, Maps.newHashMap()), Key.matcher(Object.class, SET_PREDICATE));
        
    }

    /**
     * Tests {@link Registry.Key#matcher(Predicate)} with a set matcher
     * on the left.
     */
    @Test
    public void matchSetInverse() {
        Assert.assertEquals(Key.matcher(Object.class, SET_PREDICATE), Key.get(Object.class, Sets.newHashSet()));
        Assert.assertEquals(Key.matcher(Object.class, SET_PREDICATE), Key.get(Object.class, ELEMENT_SET));
        Asserts.assertNotEquals(Key.matcher(Object.class, SET_PREDICATE), Key.get(Object.class, Lists.newArrayList()));
        Asserts.assertNotEquals(Key.matcher(Object.class, SET_PREDICATE), Key.get(Object.class, Maps.newHashMap()));
    }

    /**
     * Tests {@link Registry.Key#matcher(Predicate)} with a matcher for
     * sets containing "elmenet" on the right.
     */
    @Test
    public void matcherSetWithElement() {
        Assert.assertEquals(Key.get(Object.class, ELEMENT_SET), Key.matcher(Object.class, ELEMENT_SET_PREDICATE));
        final Set<String> set = Sets.newHashSet();
        set.add("element");
        Assert.assertEquals(Key.get(Object.class, set), Key.matcher(Object.class, ELEMENT_SET_PREDICATE));
        Asserts.assertNotEquals(Key.get(Object.class, Sets.newHashSet()), 
            Key.matcher(Object.class, ELEMENT_SET_PREDICATE));
        Asserts.assertNotEquals(Key.get(Object.class, Lists.newArrayList()), 
            Key.matcher(Object.class, ELEMENT_SET_PREDICATE));
        Asserts.assertNotEquals(Key.get(Object.class, Maps.newHashMap()), 
            Key.matcher(Object.class, ELEMENT_SET_PREDICATE));
    }

    /**
     * Tests {@link Registry.Key#matcher(Predicate)} with a matcher for
     * sets containing "elmenet" on the left.
     */
    @Test
    public void machterSetWithElementInverse() {
        Assert.assertEquals(Key.get(Object.class, ELEMENT_SET), Key.matcher(Object.class, ELEMENT_SET_PREDICATE));
        final Set<String> set = Sets.newHashSet();
        set.add("element");
        Assert.assertEquals(Key.get(Object.class, set), Key.matcher(Object.class, ELEMENT_SET_PREDICATE));
        Asserts.assertNotEquals(Key.matcher(Object.class, ELEMENT_SET_PREDICATE),
            Key.get(Object.class, Sets.newHashSet()));
        Asserts.assertNotEquals(Key.matcher(Object.class, ELEMENT_SET_PREDICATE),
            Key.get(Object.class, Lists.newArrayList()));
        Asserts.assertNotEquals(Key.matcher(Object.class, ELEMENT_SET_PREDICATE),
            Key.get(Object.class, Maps.newHashMap()));
    }
    
    /**
     * Tests {@link Object#hashCode()} on a {@link Key} created by
     * {@link Registry.Key#matcher(Class, Predicate)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void matcherAdd() {
        Key.matcher(Object.class, SET_PREDICATE).hashCode();
    }
    
    /**
     * Tests {@link Registry.Key#matcher(Predicate)} with a null type.
     */
    @Test(expected = NullPointerException.class)
    public void matcherNullType() {
        final Class<Object> nullClass = null;
        Key.matcher(nullClass, SET_PREDICATE);
    }
    
    /**
     * Tests {@link Registry.Key#matcher(Predicate)} with a null predicate.
     */
    @Test(expected = NullPointerException.class)
    public void matcherNullPredicate() {
        Key.matcher(Object.class, null);
    }
    
    /**
     * Tests {@link Registry.Key#matcher(Predicate)} with a null type
     * and a null predicate.
     */
    @Test(expected = NullPointerException.class)
    public void matcherNulls() {
        final Class<Object> nullClass = null;
        Key.matcher(nullClass, null);
    }
    
}
