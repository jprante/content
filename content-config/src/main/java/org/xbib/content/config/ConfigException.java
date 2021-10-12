package org.xbib.content.config;

@SuppressWarnings("serial")
public class ConfigException extends RuntimeException {

    public ConfigException(Exception e) {
        super(e);
    }

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
