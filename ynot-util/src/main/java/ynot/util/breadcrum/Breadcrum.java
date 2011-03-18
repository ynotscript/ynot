package ynot.util.breadcrum;

import java.util.Iterator;
import java.util.Stack;

/**
 * This class represents a breadcrum.
 * 
 * @author ERIC.QUESADA
 * 
 */
public class Breadcrum implements Iterable<String>, Comparable<Breadcrum> {

    /**
     * Separator to use to separate namespaces.
     */
    private static final String NAMESPACE_SEPARATOR = "_";

    /**
     * The full breadcrum data.
     */
    private Stack<String> breadcrum;

    /**
     * The previous sublevel (set when goOut).
     */
    private String previous;

    /**
     * Default constructor.
     */
    public Breadcrum() {
        breadcrum = new Stack<String>();
    }

    /**
     * Constructor with an initial breadcrum.
     * 
     * @param defaultNamespace
     *            the initial breadcrum.
     */
    public Breadcrum(final String defaultNamespace) {
        this();
        for (String ns : defaultNamespace.split(NAMESPACE_SEPARATOR)) {
            goIn(ns);
        }
    }

    /**
     * To enter in a sub level.
     * 
     * @param namespace
     *            the sub namespace.
     */
    public final void goIn(final String namespace) {
        breadcrum.push(namespace);
    }

    /**
     * To exit from the current sub level.
     * 
     * @return the concerned sub level.
     */
    public final String goOut() {
        previous = breadcrum.pop();
        return previous();
    }

    /**
     * Return the last existing sub level.
     * 
     * @return the last existing sub level.
     */
    public final String previous() {
        return previous;
    }

    /**
     * To get the deeper level.
     * 
     * @return the deeper level.
     */
    public final String last() {
        return breadcrum.peek();
    }

    /**
     * To get the higher level.
     * 
     * @return the higher level.
     */
    public final String first() {
        return breadcrum.get(0);
    }

    @Override
    public final String toString() {
        StringBuilder ns = new StringBuilder();
        for (String oneNamespace : breadcrum) {
            if (ns.length() > 0) {
                ns.append(NAMESPACE_SEPARATOR);
            }
            ns.append(oneNamespace);
        }
        return ns.toString();
    }

    @Override
    public final Iterator<String> iterator() {
        return breadcrum.iterator();
    }

    /**
     * To get a reversed breadcrum.
     * 
     * @return the reversed breadcrum..
     */
    public final Breadcrum reverse() {
        Breadcrum reversedBreadcrum = new Breadcrum();
        for (int i = breadcrum.size() - 1; i >= 0; i--) {
            reversedBreadcrum.goIn(breadcrum.get(i));
        }
        return reversedBreadcrum;
    }

    @Override
    public final int compareTo(final Breadcrum otherBreacrum) {
        if (getSize() < otherBreacrum.getSize()) {
            return -1;
        } else if (getSize() > otherBreacrum.getSize()) {
            return 1;
        } else {
            return toString().compareToIgnoreCase(otherBreacrum.toString());
        }
    }

    /**
     * To get the size of the current breadcrum.
     * 
     * @return the concerned size.
     */
    private int getSize() {
        return breadcrum.size();
    }

}