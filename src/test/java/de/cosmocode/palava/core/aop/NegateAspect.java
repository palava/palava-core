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

package de.cosmocode.palava.core.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cosmocode.commons.Throwables;

/**
 * An aspect testing {@link PalavaAspect}.
 *
 * @since 2.9
 * @author Willi Schoenborn
 */
@Aspect("pertypewithin(de.cosmocode.palava.core.aop.PalavaAspectTest)")
public final class NegateAspect extends PalavaAspect {

    private static final Logger LOG = LoggerFactory.getLogger(NegateAspect.class);
    
    /**
     * An advice which blocks methods annotated with {@link Negate}.
     *
     * @since 2.9
     * @param point the proceeding join point
     * @return the new result
     */
    @Around("call(@de.cosmocode.palava.core.aop.Negate * *.*())")
    public Boolean block(ProceedingJoinPoint point) {
        checkState();
        LOG.trace("Negating {}", point.getStaticPart());
        
        try {
            return invert(point.proceed());
        /* CHECKSTYLE:OFF */
        } catch (Throwable e) {
        /* CHECKSTYLE:ON */
            throw Throwables.sneakyThrow(e);
        }
    }
    
    private Boolean invert(Object result) {
        if (result == Boolean.TRUE) {
            return Boolean.FALSE;
        } else if (result == Boolean.FALSE) {
            return Boolean.TRUE;
        } else {
            throw new IllegalArgumentException(result + " is no Boolean");
        }
    }
    
}
