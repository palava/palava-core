package de.cosmocode.palava.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides utility methods that determine the approximate size of an Object in the RAM.
 * NOTE: This whole class is work in progress. Bugs or total non-functioning can occur ...
 * @author olorenz
 *
 */
public final class ObjectSizeCalculator {
    
    private static final Logger log = LoggerFactory.getLogger(ObjectSizeCalculator.class);
    
    private static final int defaultMaximumRecursions = 8;
    
    private final Set<Object> objCache;
    
    private ObjectSizeCalculator() {
        this.objCache = new HashSet<Object>();
    }
    
    
    public static int sizeOf (String s) {
        // approximate size
        return s == null ? 0 : 8 * (int) (((s.length() * 2) + 45) / 8);
    }
    
    
    public static long sizeOf (byte[] b) {
        return b==null?0:b.length+12;
    }
    public static long sizeOf (boolean[] b) {
        return b==null?0:b.length+12;
    }
    public static long sizeOf (short[] b) {
        return b==null?0:2*b.length+12;
    }
    public static long sizeOf (char[] b) {
        return b==null?0:2*b.length+12;
    }
    public static long sizeOf (float[] b) {
        return b==null?0:4*b.length+12;
    }
    public static long sizeOf (int[] b) {
        return b==null?0:4*b.length+12;
    }
    public static long sizeOf (long[] b) {
        return b==null?0:8*b.length+12;
    }
    public static long sizeOf (double[] b) {
        return b==null?0:8*b.length+12;
    }
    public static long sizeOf (Object[] b) {
        return new ObjectSizeCalculator().sizeOfArrayIntern(b);
    }
    public static long sizeOf (Map<?, ?> m) {
        return new ObjectSizeCalculator().sizeOfMapIntern(m);
    }
    public static long sizeOf (Collection<?> c) {
        return new ObjectSizeCalculator().sizeOfCollectionIntern(c);
    }
    

    public static long sizeOf (Object o) {
        return sizeOf(o, defaultMaximumRecursions);
    }
    
    public static long sizeOf (Object o, int maximumrecursion) {
        return new ObjectSizeCalculator().sizeOfIntern(o, maximumrecursion);
    }
    
    
    
    
    private long sizeOfArrayIntern (Object[] objs) {
        if (objs == null) return 0;
        
        int size = 12;
        for (Object entry : objs) {
            size += 4 + sizeOfIntern(entry);
        }
        
        return size;
    }
    
    private long sizeOfMapIntern (Map<?, ?> m) {
        if (m == null) return 0;
        
        int size = 8;
        for (Map.Entry<?, ?> entry : m.entrySet()) {
            size += 4 + sizeOfIntern(entry.getKey()) + sizeOfIntern(entry.getValue());
        }

        return size;
    }
    
    private long sizeOfCollectionIntern (Collection<?> c) {
        if (c == null) return 0;
        
        int size = 8;
        for (Object entry : c) {
            size += 4 + sizeOfIntern(entry);
        }

        return size;
    }
    
    private long specialSizeIntern(Object o) {
        if (o==null) {
            return 0;
        } else if (o instanceof Map<?, ?>) {
            return sizeOfMapIntern(Map.class.cast(o));
        } else if (o instanceof Collection<?>) {
            return sizeOfCollectionIntern(Collection.class.cast(o));
        } else if (o instanceof String) {
            return sizeOf((String)o);
        } else if (o.getClass().isArray()) {
            return arraySizeOf(o);
        } else {
            return -1;
        }
    }
    
    private long arraySizeOf(Object o) {
        Class<?> c = o.getClass();
        if (c.toString().equals("class [B")) return sizeOf((byte[])o);
        if (c.toString().equals("class [Z")) return sizeOf((boolean[])o);
        if (c.toString().equals("class [S")) return sizeOf((short[])o);
        if (c.toString().equals("class [C")) return sizeOf((char[])o);
        if (c.toString().equals("class [I")) return sizeOf((int[])o);
        if (c.toString().equals("class [F")) return sizeOf((float[])o);
        if (c.toString().equals("class [J")) return sizeOf((long[])o);
        if (c.toString().equals("class [D")) return sizeOf((double[])o);
        
        return sizeOfArrayIntern((Object[])o);
    }
    
    private long sizeOfIntern (Object o) {
        return sizeOfIntern(o, defaultMaximumRecursions);
    }
    
    private long sizeOfIntern (Object o, int maximumrecursion) {
        if (o == null) return 0;
        
        if (objCache.contains(o)) return 0;   // already counted

        Class<?> c = o.getClass();

        
        // primitives first
               if (c.equals(Byte.TYPE) || c.equals(Boolean.TYPE)
                || c.equals(Byte.class) || c.equals(Boolean.class)) {
            return 1;
        } else if (c.equals(Character.TYPE) || c.equals(Short.TYPE)
                || c.equals(Character.class) || c.equals(Short.class)) {
            return 2;
        } else if (c.equals(Integer.TYPE) || c.equals(Float.TYPE) 
                || c.equals(Integer.class) || c.equals(Float.class)
                ) {
            return 4;
        } else if (c.equals(Long.TYPE) || c.equals(Double.TYPE)
                || c.equals(Long.class) || c.equals(Double.class)) {
            return 8;
        } else {
            // check special handling (known functions and arrays)
            long tmp = specialSizeIntern(o);
            if (tmp >= 0) {
                objCache.add(o);
                return tmp;
            }
            
            
            // complex object
            long size = 8;
            
            Field lastField = null;
            for (Field f : c.getFields()) {
                // no static fields
                if (Modifier.isStatic(f.getModifiers())) continue;
                // no duplicate fields
                if (f.equals(lastField)) continue;
                lastField = f;
                
                // no static fields
                if (Modifier.isStatic(f.getModifiers())) continue;
                
                if (maximumrecursion > 0) {
                    try {
                        size += 4 + sizeOfIntern(f.get(o), maximumrecursion-1);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else log.debug("maximum recursion reached");
            }
            for (Method m : c.getMethods()) {
                if (maximumrecursion > 0) {
                    if (m.getName().startsWith("get") && !m.getName().equals("getClass") && m.getParameterTypes().length==0) {
                        try {
                            Object other = m.invoke(o, new Object[] {});
                            if (!o.equals(other)) size += 4 + sizeOfIntern(other, maximumrecursion-1);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                } else log.debug("maximum recursion reached");
            }

            objCache.add(o);
            return (long)Math.ceil(size/8)*8;
        }
    }

}
