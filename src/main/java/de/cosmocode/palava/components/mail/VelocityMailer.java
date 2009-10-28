package de.cosmocode.palava.components.mail;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import de.cosmocode.palava.Component;
import de.cosmocode.palava.ComponentException;
import de.cosmocode.palava.ComponentManager;
import de.cosmocode.palava.Server;

public class VelocityMailer implements Mailer, Component {

    private static final Locale NO_LOCALE = null;
    
	private static final String CHARSET = "UTF-8";
	
	private String hostname;
	private final Properties properties = new Properties();
	private final VelocityEngine engine = new VelocityEngine();

	@Override
	public void configure(Element root, Server server) throws ComponentException {
	
		hostname = root.getChildText("hostname");
	
		@SuppressWarnings("unchecked")
		final List<Element> children = root.getChildren("property");
	
		for (Element child : children) {
			final String name = child.getAttributeValue("name");
			String value = child.getText();
	
			if (name.equals("file.resource.loader.path") && !value.startsWith(File.separator)) {
				value = server.getFilename(value);
			}
			
			properties.put(name, value);
		}
	}

	@Override
	public void compose(ComponentManager manager) throws ComponentException {
		
	}

	@Override
	public void initialize() throws Exception {
		engine.init(properties);
	}
	
	@Override
	public MimeMessage send(TemplateDescriptor descriptor, Map<String, ?> params) throws Exception {
	    return send(descriptor, NO_LOCALE, params);
	}
	
	@Override
	public MimeMessage send(TemplateDescriptor descriptor, Locale locale, Map<String, ?> params) throws Exception {
	    return send(descriptor, locale == null ? null : locale.toString(), params);
	}
	
	@Override
	public MimeMessage send(TemplateDescriptor descriptor, String lang, Map<String, ?> params) throws Exception {
		return sendMessage(descriptor.getName(), lang, params);
	}

	@Override
	public MimeMessage sendMessage(String templateName, String lang, Map<String, ?> params, String... to) throws Exception {
		if (templateName == null) throw new IllegalArgumentException("Template name is null");
		
		final VelocityContext ctx = new VelocityContext(params);
		
		final String prefix = StringUtils.isBlank(lang) ? "" : lang + "/";
		final Template template = engine.getTemplate(prefix + templateName, CHARSET);
		
	    final Embedder embed = new Embedder(engine);
		ctx.put("embed", embed);
		
		ctx.put("entity", EntityEncoder.getInstance());
		
		final StringWriter writer = new StringWriter();
		template.merge(ctx, writer);
		
		final EmailFactory factory = EmailFactory.getInstance();
		final SAXBuilder builder = new SAXBuilder();
		final Document document = builder.build(new StringReader(writer.toString()));
		
		final Email email = factory.build(document, embed);
		email.setHostName(hostname);
		
		for (String recipient : to) {
			email.addTo(recipient);
		}
		
		email.send();
		
		return email.getMimeMessage();
	}

}