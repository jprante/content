package org.xbib.content.xml.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

/**
 *
 */
public class ToQName {

    public static QName toQName(QName root, NamespaceContext context, String string) {
        String name = string;
        String nsPrefix = root.getPrefix();
        String nsURI = root.getNamespaceURI();
        if (name.startsWith("_") || name.startsWith("@")) {
            name = name.substring(1);
        }
        name = ISO9075.encode(name);
        int pos = name.indexOf(':');
        if (pos > 0) {
            nsPrefix = name.substring(0, pos);
            nsURI = context.getNamespaceURI(nsPrefix);
            if (nsURI == null) {
                throw new IllegalArgumentException("unknown namespace prefix: " + nsPrefix);
            }
            name = name.substring(pos + 1);
        }
        return new QName(nsURI, name, nsPrefix);
    }
}
