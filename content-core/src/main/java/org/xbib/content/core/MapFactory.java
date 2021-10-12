package org.xbib.content.core;

import java.util.Map;

@FunctionalInterface
public interface MapFactory {
    Map<String, Object> newMap();
}
