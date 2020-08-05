package org.xbib.content.config;

import java.io.OutputStream;

public interface ConfigLogger {

    void setLevel(String level);

    void info(String string);

    void error(String message);

    void error(String message, Throwable throwable);

    OutputStream getStderrOutputStream();

    OutputStream getStdoutOutputStream();
}
