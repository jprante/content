package org.xbib.content.config;

import java.io.OutputStream;

public class SystemConfigLogger implements ConfigLogger {

    @Override
    public void setLevel(String level) {
        // d nothing
    }

    @Override
    public void info(String string) {
        System.out.println(string);
    }

    @Override
    public void error(String message) {
        System.err.println(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        System.err.println(message);
        System.err.println(ExceptionFormatter.format(throwable));
    }

    @Override
    public OutputStream getStderrOutputStream() {
        return System.err;
    }

    @Override
    public OutputStream getStdoutOutputStream() {
        return System.out;
    }
}
