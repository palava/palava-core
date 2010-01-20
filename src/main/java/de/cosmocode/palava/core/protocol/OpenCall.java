package de.cosmocode.palava.core.protocol;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.command.Command;
import de.cosmocode.palava.core.request.HttpRequest;

final class OpenCall implements Call {

    private static final Logger log = LoggerFactory.getLogger(OpenCall.class);

    @Override
    public HttpRequest getHttpRequest() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Command getCommand() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getInputStream() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Header getHeader() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void discard() throws ConnectionLostException, IOException {
        // TODO Auto-generated method stub

    }

}
