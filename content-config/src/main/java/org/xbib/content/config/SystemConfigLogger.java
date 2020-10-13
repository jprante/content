package org.xbib.content.config;

public class SystemConfigLogger implements ConfigLogger {

    @Override
    public void info(String string) {
        System.out.println("info: " + string);
    }

    @Override
    public void warn(String message) {
        System.out.println("warning: " + message);
    }

    @Override
    public void error(String message) {
        System.out.println("error: " + message);
    }
}
