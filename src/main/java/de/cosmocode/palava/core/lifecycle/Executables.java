package de.cosmocode.palava.core.lifecycle;


/**
 * Static utility class for {@link Executable}s.
 *
 * @author Willi Schoenborn
 */
public final class Executables {

    private Executables() {
        
    }
    
    /**
     * Adapts an {@link Executable} to the {@link Runnable} interface.
     * 
     * @param executable the backing executable
     * @return a runnable backed by the specified executable
     */
    public static Runnable asRunnable(final Executable executable) {
        return new Runnable() {
            
            @Override
            public void run() {
                try {
                    executable.execute();
                } catch (LifecycleException e) {
                    throw new IllegalStateException(e);
                }
            }
            
        };
    }
    
    /**
     * Adapts a {@link Runnable} to the {@link Executable} interface.
     * 
     * @param runnable the backing runnable
     * @return an executable backed by the specified executable
     */
    public static Executable asExecutable(final Runnable runnable) {
        return new Executable() {
            
            @Override
            public void execute() throws LifecycleException {
                try {
                    runnable.run();
                /* CHECKSTYLE:OFF */
                } catch (RuntimeException e) {
                /* CHECKSTYLE:ON */
                    throw new LifecycleException(e);
                }
            }
            
        };
    }

}
