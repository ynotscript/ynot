package ynot.util.breadcrum;

import java.util.Iterator;
import java.util.Stack;

public class Breadcrum implements Iterable<String>, Comparable<Breadcrum> {

	private static final String NAMESPACE_SEPARATOR = "_";

	private Stack<String> breadcrum;
	private String previous;

	public Breadcrum() {
		breadcrum = new Stack<String>();
	}

	public Breadcrum(String defaultNamespace) {
		this();
		for (String ns : defaultNamespace.split(NAMESPACE_SEPARATOR)) {
			goIn(ns);
		}
	}

	public final void goIn(String namespace) {
		breadcrum.push(namespace);
	}

	public final String goOut() {
		previous = breadcrum.pop();
		return previous();
	}

	public final String previous() {
		return previous;
	}

	public final String last() {
		return breadcrum.peek();
	}

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

	private int getSize() {
		return breadcrum.size();
	}

}