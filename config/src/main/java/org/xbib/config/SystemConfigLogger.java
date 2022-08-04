package org.xbib.config;

public class SystemConfigLogger implements ConfigLogger {

    public SystemConfigLogger() {
    }

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
