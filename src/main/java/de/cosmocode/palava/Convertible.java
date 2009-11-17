package de.cosmocode.palava;


/**
 * marks an object to be usable with a converter
 * @author Detlef Hüttemann
 */
public interface Convertible
{
    public void convert( StringBuffer buf, ContentConverter converter ) throws ConversionException;
}
