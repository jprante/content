package org.xbib.content.settings;

/**
 * A generic failure to handle settings.
 */
public class SettingsException extends RuntimeException {

    private static final long serialVersionUID = -1833327708622505101L;

    public SettingsException(String message) {
        super(message);
    }

    public SettingsException(String message, Throwable cause) {
        super(message, cause);
    }
}
