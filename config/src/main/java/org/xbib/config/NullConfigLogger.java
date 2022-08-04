package org.xbib.config;

public class NullConfigLogger implements ConfigLogger {

    public NullConfigLogger() {
    }

    @Override
    public void info(String string) {
    }

    @Override
    public void warn(String message) {
    }

    @Override
    public void error(String message) {
    }
}
