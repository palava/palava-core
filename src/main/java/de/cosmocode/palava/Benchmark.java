package de.cosmocode.palava;

public class Benchmark {

    private static final ThreadLocal<Benchmark> benchmark;
    private static final boolean active;
    
    static {
    	benchmark = new ThreadLocal<Benchmark>();
    	String property = System.getProperty("palava.benchmark");
    	active = Boolean.parseBoolean(property);
    }
    
    private final long start = System.currentTimeMillis();
    private long end = -1;
    
    private String url = "unknown";
    
    private int hits = 0;
    
    private Benchmark() {
        
    }
    
    public static void start() {
        if (!active) return;
        if (benchmark.get() == null) {
            benchmark.set(new Benchmark());
        } else {
            throw new IllegalStateException("Benchmark already started");
        }
    }
    
    public static void setURL(String url) {
        if (!active) return;
        getCurrent().url = url;
    }
    
    public static void hit() {
        if (!active) return;
        getCurrent().hits++;
    }
    
    public static void stop() {
        if (!active) return;
        getCurrent().end = System.currentTimeMillis();
    }
    
    public static Benchmark getCurrent() {
    	if (benchmark.get() == null) {
    		throw new IllegalStateException("No Benchmark started.");
    	} else {
    		return benchmark.get();
    	}
    }
    
    public static boolean isActive() {
    	return active;
    }
    
    private long calcDuration() {
        return end == -1 ? System.currentTimeMillis() - start : end - start;
    }
    
    @Override
    public String toString() {
        return "Sum: " + url + " " + calcDuration() + " ms " + Thread.currentThread().getName() + " " + hits + " hits";
    }
    
}