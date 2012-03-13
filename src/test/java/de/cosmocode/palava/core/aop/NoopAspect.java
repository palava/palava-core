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

import de.cosmocode.commons.Throwables;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect("pertypewithin(de.cosmocode.palava.core.aop.SuppressInjectTest)")
public final class NoopAspect extends PalavaAspect {

    private static final Logger LOG = LoggerFactory.getLogger(NoopAspect.class);

    @Around("call(@de.cosmocode.palava.core.aop.Noop * *())")
    public Boolean block(ProceedingJoinPoint point) {
        checkNotInjected();
        LOG.trace("Noop-ing {}", point.getStaticPart());

        try {
            return null;
        /* CHECKSTYLE:OFF */
        } catch (Throwable e) {
        /* CHECKSTYLE:ON */
            throw Throwables.sneakyThrow(e);
        }
    }

}
