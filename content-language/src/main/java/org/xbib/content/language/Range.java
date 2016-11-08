package org.xbib.content.language;

import org.xbib.content.language.Subtag.Type;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A language range used for matching language tags.
 */
public class Range extends SubtagSet {

    private static final String RANGE_PATTERN = "((?:[a-zA-Z]{1,8}|\\*))((?:[-_](?:[a-zA-Z0-9]{1,8}|\\*))*)";
    private static final String RANGE_COMPONENT = "[-_]((?:[a-zA-Z0-9]{1,8}|\\*))";
    private static final Pattern P_RANGE = Pattern.compile(RANGE_PATTERN);
    private static final Pattern P_RANGE_COMPONENT = Pattern.compile(RANGE_COMPONENT);
    private static final String LANGUAGE =
            "((?:[a-zA-Z]{2,3}(?:[-_](?:[a-zA-Z]{3}|\\*)){0,3})|[a-zA-Z]{4}|[a-zA-Z]{5,8}|\\*)";
    private static final String SCRIPT = "((?:[-_](?:[a-zA-Z]{4}|\\*))?)";
    private static final String REGION = "((?:[-_](?:(?:[a-zA-Z]{2})|(?:[0-9]{3})|\\*))?)";
    private static final String VARIANT = "((?:[-_](?:(?:[a-zA-Z0-9]{5,8})|(?:[0-9][a-zA-Z0-9]{3})|\\*))*)";
    private static final String EXTENSION = "((?:[-_](?:(?:[a-wy-zA-WY-Z0-9](?:[-_][a-zA-Z0-9]{2,8})+)|\\*))*)";
    private static final String PRIVATEUSE = "[xX](?:[-_][a-zA-Z0-9]{2,8})+";
    private static final String PRIVATEUSE_2 = "((?:[-_](?:" + PRIVATEUSE + ")+|\\*)?)";
    private static final String LANGTAG = "^" + LANGUAGE + SCRIPT + REGION + VARIANT + EXTENSION + PRIVATEUSE_2 + "$";
    private static final String GRANDFATHERED =
            "^(?:art[-_]lojban|cel[-_]gaulish|en[-_]GB[-_]oed|i[-_]ami|i[-_]bnn|i[-_]default|i[-_]enochian|i[-_]"
                    + "hak|i[-_]klingon|i[-_]lux|i[-_]mingo|i[-_]navajo|i[-_]pwn|i[-_]tao||i[-_]tay|i[-_]tsu|no[-_]"
                    + "bok|no[-_]nyn|sgn[-_]BE[-_]fr|sgn[-_]BE[-_]nl|sgn[-_]CH[-_]de|zh[-_]cmn|zh[-_]cmn[-_]"
                    + "Hans|zh[-_]cmn[-_]Hant|zh[-_]gan|zh[-_]guoyu|zh[-_]hakka|zh[-_]min|zh[-_]min[-_]nan|zh[-_]"
                    + "wuu|zh[-_]xiang|zh[-_]yue)$";
    private static final Pattern P_PRIVATEUSE = Pattern.compile("^" + PRIVATEUSE + "$");
    private static final Pattern P_GRANDFATHERED = Pattern.compile(GRANDFATHERED);
    private static final Pattern P_EXTENDED_RANGE = Pattern.compile(LANGTAG);
    private final boolean extended;

    /**
     * Create a language range.
     *
     * @param range    The language-range
     * @param extended true if this is an extended language range
     */
    public Range(String range, boolean extended) {
        super(parse(range, extended).primary);
        this.extended = extended;
    }

    /**
     * Create a language range.
     * @param range range
     */
    public Range(String range) {
        this(parse(range).primary);
    }

    /**
     * Create a Language-Range from a Lang tag.
     * @param lang lang
     */
    public Range(Lang lang) {
        this(lang.toString());
    }

    /**
     * Create a language range from a Lang tag.
     *
     * @param lang     The language tag
     * @param extended true if this is an extended language-range.
     */
    public Range(Lang lang, boolean extended) {
        this(lang.toString(), extended);
    }

    Range(Subtag primary) {
        super(primary);
        this.extended = !checkBasic();
    }

    /**
     * Filter the given set of lang tags. Return an array of matching tags.
     * @param lang lang
     * @param range range
     * @return lang array
     */
    public static Lang[] filter(String range, Lang... lang) {
        return new Range(range).filter(lang);
    }

    /**
     * Filter the given set of lang tags. Return an array of matching tags.
     * @param range range
     * @param lang lang
     * @return string array
     */
    public static String[] filter(String range, String... lang) {
        return new Range(range).filter(lang);
    }

    /**
     * True if the lang tag matches the range.
     *
     * @param range    The language-range
     * @param lang     The language tag
     * @param extended true to use extended match rules
     * @return  True if the lang tag matches the range.
     */
    public static boolean matches(String range, Lang lang, boolean extended) {
        return new Range(range, extended).matches(lang);
    }

    /**
     * True if the lang tag matches the range.
     *
     * @param range The language-range
     * @param lang  The language tag
     * @return  True if the lang tag matches the range.
     */
    public static boolean matches(String range, Lang lang) {
        return new Range(range).matches(lang);
    }

    /**
     * True if the lang tag matches the range.
     *
     * @param range    The language-range
     * @param lang     The language tag
     * @param extended true to use extended match rules
     * @return  True if the lang tag matches the range.
     */
    public static boolean matches(String range, String lang, boolean extended) {
        return new Range(range, extended).matches(lang);
    }

    /**
     * True if the lang tag matches the range.
     *
     * @param range The language range
     * @param lang  The language tag
     * @return  True if the lang tag matches the range.
     */
    public static boolean matches(String range, String lang) {
        return new Range(range).matches(lang);
    }

    /**
     * Parse the language range.
     * @param range range
     * @return range
     */
    public static Range parse(String range) {
        return parse(range, false);
    }

    /**
     * Parse the language range.
     *
     * @param range    The language range
     * @param extended true to use extended language rules
     * @return range
     */
    public static Range parse(String range, boolean extended) {
        if (!extended) {
            Subtag primary = null;
            Subtag current;
            Matcher m = P_RANGE.matcher(range);
            if (m.find()) {
                String first = m.group(1);
                String therest = m.group(2);
                primary =
                        new Subtag("*".equals(first) ? Subtag.Type.WILDCARD : Subtag.Type.SIMPLE, first
                                .toLowerCase(Locale.US));
                current = primary;
                Matcher n = P_RANGE_COMPONENT.matcher(therest);
                while (n.find()) {
                    String name = n.group(1).toLowerCase(Locale.US);
                    current = new Subtag("*".equals(name) ? Subtag.Type.WILDCARD : Subtag.Type.SIMPLE, name, current);
                }
            }
            return new Range(primary);
        } else {
            Subtag primary = null;
            Matcher m = P_GRANDFATHERED.matcher(range);
            if (m.find()) {
                String[] tags = range.split("[-_]");
                Subtag current = null;
                for (String tag : tags) {
                    if (current == null) {
                        primary = new Subtag(Type.GRANDFATHERED, tag, null);
                        current = primary;
                    } else {
                        current = new Subtag(Type.GRANDFATHERED, tag, current);
                    }
                }
                return new Range(primary);
            }
            m = P_PRIVATEUSE.matcher(range);
            if (m.find()) {
                String[] tags = range.split("[-_]");
                Subtag current = null;
                for (String tag : tags) {
                    if (current == null) {
                        primary = new Subtag("*".equals(tag) ? Type.WILDCARD : Type.SINGLETON, tag, null);
                        current = primary;
                    } else {
                        current = new Subtag("*".equals(tag) ? Type.WILDCARD : Type.PRIVATEUSE, tag, current);
                    }
                }
                return new Range(primary);
            }
            m = P_EXTENDED_RANGE.matcher(range);
            if (m.find()) {
                String langtag = m.group(1);
                String script = m.group(2);
                String region = m.group(3);
                String variant = m.group(4);
                String extension = m.group(5);
                String privateuse = m.group(6);
                Subtag current = null;
                String[] tags = langtag.split("[-_]");
                for (String tag : tags) {
                    if (current == null) {
                        primary = new Subtag("*".equals(tag) ? Type.WILDCARD : Type.PRIMARY, tag);
                        current = primary;
                    } else {
                        current = new Subtag("*".equals(tag) ? Type.WILDCARD : Type.EXTLANG, tag, current);
                    }
                }
                if (script != null && script.length() > 0) {
                    current =
                            new Subtag("*".equals(script.substring(1)) ? Type.WILDCARD : Type.SCRIPT, script.substring(1),
                                    current);
                }
                if (region != null && region.length() > 0) {
                    current =
                            new Subtag("*".equals(region.substring(1)) ? Type.WILDCARD : Type.REGION, region.substring(1),
                                    current);
                }
                if (variant != null && variant.length() > 0) {
                    variant = variant.substring(1);
                    tags = variant.split("-");
                    for (String tag : tags) {
                        current = new Subtag("*".equals(tag) ? Type.WILDCARD : Type.VARIANT, tag, current);
                    }
                }
                if (extension != null && extension.length() > 0) {
                    extension = extension.substring(1);
                    tags = extension.split("-");
                    current = new Subtag("*".equals(tags[0]) ? Type.WILDCARD : Type.SINGLETON, tags[0], current);
                    for (int i = 1; i < tags.length; i++) {
                        String tag = tags[i];
                        current =
                                new Subtag("*".equals(tag) ? Type.WILDCARD : tag.length() == 1 ? Type.SINGLETON
                                        : Type.EXTENSION, tag, current);
                    }
                }
                if (privateuse != null && privateuse.length() > 0) {
                    privateuse = privateuse.substring(1);
                    tags = privateuse.split("-");
                    current = new Subtag("*".equals(tags[0]) ? Type.WILDCARD : Type.SINGLETON, tags[0], current);
                    for (int i = 1; i < tags.length; i++) {
                        current = new Subtag("*".equals(tags[i]) ? Type.WILDCARD : Type.PRIVATEUSE, tags[i], current);
                    }
                }
                return new Range(primary);
            }
        }
        throw new IllegalArgumentException("invalid range");
    }

    /**
     * Append a subtag to the range.
     * @param subtag subtag
     * @return range
     */
    public Range append(Subtag subtag) {
        Subtag last = null;
        for (Subtag tag : this) {
            last = tag;
        }
        if (last != null) {
            last.setNext(subtag);
        }
        return this;
    }

    /**
     * Append a wildcard subtag to the range.
     * @return range
     */
    public Range appendWildcard() {
        return append(Subtag.newWildcard());
    }

    /**
     * Create a basic language-range from this range.
     * @return range
     */
    public Range toBasicRange() {
        if (primary.getType() == Subtag.Type.WILDCARD) {
            return new Range("*");
        } else {
            List<Subtag> list = new LinkedList<>();
            for (Subtag tag : this) {
                if (tag.getType() != Subtag.Type.WILDCARD) {
                    list.add(tag);
                }
            }
            Subtag primary = null;
            Subtag current = null;
            for (Subtag tag : list) {
                tag.setNext(null);
                tag.setPrevious(null);
                if (primary == null) {
                    primary = tag;
                    current = primary;
                } else {
                    current.setNext(tag);
                    current = tag;
                }
            }
            return new Range(primary);
        }
    }

    /**
     * True if this range is a basic range.
     * @return true if this range is a basic range
     */
    public boolean isBasic() {
        return !extended;
    }

    private boolean checkBasic() {
        Subtag current = primary.getNext();
        while (current != null) {
            if (current.getType() == Subtag.Type.WILDCARD) {
                return false;
            }
            current = current.getNext();
        }
        return true;
    }

    /**
     * True if the lang tag matches this range.
     * @param lang lang
     * @return true if the lang tag matches this range.
     */
    public boolean matches(String lang) {
        return matches(new Lang(lang), extended);
    }

    /**
     * True if the lang tag matches this range.
     *
     * @param lang     The language tage
     * @param extended True if extended matching rules should be used
     * @return true if the lang tag matches this range.
     */
    public boolean matches(String lang, boolean extended) {
        return matches(new Lang(lang), extended);
    }

    /**
     * True if the lang tag matches this range.
     * @param lang lang
     * @return true if the lang tag matches this range.
     */
    public boolean matches(Lang lang) {
        return matches(lang, false);
    }

    /**
     * True if the lang tag matches this range.
     *
     * @param lang     The language tage
     * @param extended True if extended matching rules should be used
     * @return true if the lang tag matches this range.
     */
    public boolean matches(Lang lang, boolean extended) {
        Iterator<Subtag> i = iterator();
        Iterator<Subtag> e = lang.iterator();
        if (isBasic() && !extended) {
            if (primary.getType() == Subtag.Type.WILDCARD) {
                return true;
            }
            while (i.hasNext() && e.hasNext()) {
                Subtag in = i.next();
                Subtag en = e.next();
                if (!in.equals(en)) {
                    return false;
                }
            }
            return true;
        } else {
            Subtag icurrent = i.next();
            Subtag ecurrent = e.next();
            if (!icurrent.equals(ecurrent)) {
                return false;
            }

            while (i.hasNext()) {
                icurrent = i.next();
                while (icurrent.getType() == Subtag.Type.WILDCARD && i.hasNext()) {
                    icurrent = i.next();
                }
                // the range ends in a wildcard so it will match everything beyond this point
                if (icurrent.getType() == Subtag.Type.WILDCARD) {
                    return true;
                }
                boolean matched = false;
                while (e.hasNext()) {
                    ecurrent = e.next();
                    if (extended && (ecurrent.getType().ordinal() < icurrent.getType().ordinal())) {
                        continue;
                    }
                    if (!ecurrent.equals(icurrent)) {
                        break;
                    } else {
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Filter the given set of lang tags.
     * @param lang lang
     * @return an array of matching tags.
     */
    public Lang[] filter(Lang... lang) {
        List<Lang> langs = new LinkedList<Lang>();
        for (Lang l : lang) {
            if (matches(l)) {
                langs.add(l);
            }
        }
        return langs.toArray(new Lang[langs.size()]);
    }

    /**
     * Filter the given set of lang tags.
     * @param lang lang tags
     * @return an array of matching tags.
     */
    public String[] filter(String... lang) {
        List<String> langs = new LinkedList<String>();
        for (String l : lang) {
            if (matches(l)) {
                langs.add(l);
            }
        }
        return langs.toArray(new String[langs.size()]);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass() && hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for (Subtag tag : this) {
            result = prime * result + tag.hashCode();
        }
        return result;
    }
}
