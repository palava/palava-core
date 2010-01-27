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

package de.cosmocode.palava.bridge.call;

import java.util.List;

/**
 * Indicates a missing argument when invoking a command.
 *
 * @author Willi Schoenborn
 */
public class MissingArgumentException extends RuntimeException {

    private static final long serialVersionUID = -4016565094430756701L;
    
    public MissingArgumentException(String argument) {
        super(argument);
    }
    
    public MissingArgumentException(Throwable cause) {
        super(cause);
    }
    
    public MissingArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingArgumentException(Object ignored, String missingArgument) {
        this(missingArgument);
    }
    
    public MissingArgumentException(Object ignored, String missingArgument, String type) {
        super(missingArgument + " (" + type + ")");
    }
    
    public MissingArgumentException(Object ignored, List<String> missingArguments) {
        this(missingArguments.toString());
    }
    
}
