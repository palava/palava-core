package de.cosmocode.palava.core.command;

import com.google.common.base.Function;

import de.cosmocode.palava.core.call.filter.Filterable;

/**
 * Static utility class for working with {@link Command}s.
 *
 * @author Willi Schoenborn
 */
public final class Commands {
    
    private static final Function<Command, Class<?>> GET_CLASS = new Function<Command, Class<?>>() {
        
        @Override
        public Class<?> apply(Command command) {
            if (command instanceof Filterable) {
                return Filterable.class.cast(command).getConcreteClass();
            } else {
                return command.getClass();
            }
        }
        
    };

    public static Class<?> getConcreteClass(Command command) {
        return GET_CLASS.apply(command);
    }

}
