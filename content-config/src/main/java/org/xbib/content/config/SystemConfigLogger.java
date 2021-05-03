package org.xbib.content.config;

public class SystemConfigLogger implements ConfigLogger {

    @Override
    public void info(String string) {
        System.err.println("info: " + string);
    }

    @Override
    public void warn(String message) {
        System.err.println("warning: " + message);
    }

    @Override
    public void error(String message) {
        System.err.println("error: " + message);
    }
}
