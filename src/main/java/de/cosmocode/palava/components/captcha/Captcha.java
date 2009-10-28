package de.cosmocode.palava.components.captcha;

import de.cosmocode.palava.Component;

public interface Captcha extends  Component {

	public static String SESSION_QUESTIONKEY = "Captcha_question";

	public byte [] getJpegCapchta( String token ) ;

    public boolean validate( String sessionID, String userInput );

}