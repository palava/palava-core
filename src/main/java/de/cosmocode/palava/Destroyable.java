package de.cosmocode.palava;


/**
 * component lifecycle class.
 * session data of type Destroyable are destroxed on session.invalidate
 * @author Detlef Hüttemann
 */
public interface Destroyable
{
    public void destroy() throws Exception ;
}
