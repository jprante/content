package org.xbib.content.json.jackson;

/**
 * A strategy for determining whether two instances are considered equivalent.
 * @param <T> type parameter
 */
public interface Equivalence<T> {
    /**
     * Returns {@code true} if the given objects are considered equivalent.
     *
     * The <code>equivalent</code> method implements an equivalence relation on non-null object
     * references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value {@code x}, {@code x.equals(x)}
     * should return {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values {@code x} and {@code y}, {@code
     * x.equals(y)} should return {@code true} if and only if {@code y.equals(x)} returns {@code
     * true}.
     * <li>It is <i>transitive</i>: for any non-null reference values {@code x}, {@code y}, and {@code
     * z}, if {@code x.equals(y)} returns {@code true} and {@code y.equals(z)} returns {@code
     * true}, then {@code x.equals(z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values {@code x} and {@code y},
     * multiple invocations of {@code x.equals(y)} consistently return {@code true} or
     * consistently return {@code false}, provided no information used in {@code equals}
     * comparisons on the objects is modified.
     * <li>For any non-null reference value {@code x}, {@code x.equals(null)} should return {@code
     * false}.
     * </ul>
     * @param a a
     * @param b b
     * @return true if a and b are equivalent
     */
    boolean equivalent(T a, T b);

    /**
     * Returns a hash code for {@code object}. This function must return the same value for
     * any two instances which are {@link #equivalent}, and should as often as possible return a
     * distinct value for instances which are not equivalent.
     *
     * @param t the object of type t
     * @return hash code
     * @see Object#hashCode the same contractual obligations apply here
     */
    int hash(T t);
}
