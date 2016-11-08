package org.xbib.content.resource.scheme;

import java.util.HashMap;
import java.util.Map;

/**
 * Static registry of custom IRI schemes.
 */
public final class SchemeRegistry {

    private static SchemeRegistry registry;
    private final Map<String, Scheme> schemes;

    SchemeRegistry() {
        schemes = new HashMap<>();
        schemes.put(HttpScheme.HTTP_SCHEME_NAME, new HttpScheme());
        schemes.put(HttpsScheme.HTTPS_SCHEME_NAME, new HttpsScheme());
        schemes.put(FtpScheme.FTP_SCHEME_NAME, new FtpScheme());
    }

    public static SchemeRegistry getInstance() {
        if (registry == null) {
            registry = new SchemeRegistry();
        }
        return registry;
    }

    @SuppressWarnings("unchecked")
    public boolean register(String schemeClass) throws ClassNotFoundException, IllegalAccessException,
            InstantiationException {
        Class<Scheme> klass = (Class<Scheme>) Thread.currentThread().getContextClassLoader().loadClass(schemeClass);
        return register(klass);
    }

    public boolean register(Class<Scheme> schemeClass) throws IllegalAccessException,
            InstantiationException {
        Scheme scheme = schemeClass.newInstance();
        return register(scheme);
    }

    public boolean register(Scheme scheme) {
        String name = scheme.getName();
        if (schemes.get(name) == null) {
            schemes.put(name.toLowerCase(), scheme);
            return true;
        } else {
            return false;
        }
    }

    public Scheme getScheme(String scheme) {
        if (scheme == null) {
            return null;
        }
        Scheme s = schemes.get(scheme.toLowerCase());
        return (s != null) ? s : new DefaultScheme(scheme);
    }

}
