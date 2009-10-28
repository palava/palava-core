package de.cosmocode.palava.components.mail;

import java.util.Locale;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import de.cosmocode.palava.Component;

public interface Mailer extends Component {

	@Deprecated
	public MimeMessage sendMessage(String template, String lang, Map<String, ?> params,  String... to) throws Exception;
	
	public MimeMessage send(TemplateDescriptor descriptor, String lang, Map<String, ?> params) throws Exception;
	
	public MimeMessage send(TemplateDescriptor descriptor, Locale locale, Map<String, ?> params) throws Exception;
	
	public MimeMessage send(TemplateDescriptor descriptor, Map<String, ?> params) throws Exception;
	
}