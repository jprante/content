package org.xbib.content;

import java.io.IOException;

/**
 * An interface allowing to transfer an object to content using an {@link XContentBuilder}.
 */
@FunctionalInterface
public interface ToXContent {

    XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException;


    Params EMPTY_PARAMS = new Params() {

        @Override
        public String param(String key) {
            return null;
        }

        @Override
        public String param(String key, String defaultValue) {
            return defaultValue;
        }

    };

    /**
     *
     */
    interface Params {
        String param(String key);

        String param(String key, String defaultValue);

    }
}
