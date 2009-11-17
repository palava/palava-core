package de.cosmocode.palava;


/**
 * @author Detlef Hüttemann
 */
public class ComponentException extends Exception
{

    private static final long serialVersionUID = -5326161820444504422L;
    
    Component _component;

    public ComponentException( String msg, Component c )
	{
         super(msg);
         _component = c;
    }

    public Component getComponent ()
	{
        return _component;
    }

}
