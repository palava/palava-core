package de.cosmocode.palava.components.captcha;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jdom.Element;

import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import de.cosmocode.palava.ComponentException;
import de.cosmocode.palava.ComponentManager;
import de.cosmocode.palava.Server;

/**
 * capctha implementation based on jcaptcha.
 *
 */
public class JCaptchaImage implements Captcha {

	ImageCaptchaService service;
	private PalavaGimpyEngine captchaEngine;
	private static final Logger logger = Logger.getLogger(JCaptchaImage.class);

    /// TODO: make these fields configurable
    private int minGuarantedStorageDelayInSeconds = 180;
    private int maxCaptchaStoreSize = 100000;
    private int captchaStoreLoadBeforeGarbageCollection = 75000;

	@Override
	public byte[] getJpegCapchta(String token) {
		
		BufferedImage challenge = service.getImageChallengeForID(token);
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();

        JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(jpegOutputStream);

        try {
			jpegEncoder.encode(challenge);
		} catch (ImageFormatException e) {
			logger.error("cannot convert captcha to jpeg", e );
			return null;
		} catch (IOException e) {
			logger.error("cannot convert captcha to jpeg", e );
			return null;
		}

        return jpegOutputStream.toByteArray();	
	}
	

	@Override
	public void compose(ComponentManager manager) throws ComponentException {
		// TODO Auto-generated method stub

	}

	@Override
	public void configure(Element root, Server server)
			throws ComponentException {
		
		captchaEngine = new PalavaGimpyEngine();

        Element child = root.getChild("imagecaptcha");
        if ( child == null ) throw new ComponentException("missing config node 'imagecaptcha'", this);
		captchaEngine.configure( child,server);

	}

	@Override
	public void initialize() throws Exception {
		
        captchaEngine.initialize();
		service = new DefaultManageableImageCaptchaService(
            new FastHashMapCaptchaStore(),
            captchaEngine,
            minGuarantedStorageDelayInSeconds,
            maxCaptchaStoreSize,
            captchaStoreLoadBeforeGarbageCollection
        );
		

	}


	@Override
	public boolean validate( String id, String userInput) {
        try {
		    return service.validateResponseForID( id, userInput );
        } catch ( Exception e ) {
            logger.error("validation falied", e );
            return false;
        }

	}

}
