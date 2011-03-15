package ynot.util.pattern;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * To store and manage a pool of pattern.
 * 
 * @author equesada
 */
public class PatternStorage {

	/**
	 * The default configuration file.
	 */
	private static final String DEFAULT_CONFIGURATION_FILE = "patterns.xml";

	/**
	 * The name of the storage.
	 */
	private String name;

	/**
	 * All the patterns of the storage.
	 */
	private Map<String, Pattern> patterns;
	
	/**
	 * The cached patterns to save time when parsing (without any ##).
	 */
	private Map<String, Pattern> cachedPatterns;


	/**
	 * Default constructor.
	 * @param storageName the name of the storage.
	 */
	public PatternStorage(final String storageName) {
		name = storageName;
		patterns = new LinkedHashMap <String, Pattern>();
		cachedPatterns = new LinkedHashMap <String, Pattern>();
	}

	/**
	 * To load the default configuration file.
	 */
	public final void load() {
		load(DEFAULT_CONFIGURATION_FILE);
	}

	/**
	 * To load a particular pattern file.
	 * @param patternFile the pattern file.
	 */
	@SuppressWarnings("unchecked")
	public final void load(final String patternFile) {
		patterns.clear();
		InputStream patternIS = Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(patternFile);
		if (patternIS != null) {
			XStream xstream = new XStream(new DomDriver());
			xstream.alias("pattern", Pattern.class);
			xstream.alias("patterns", List.class);
			List<Pattern> patternsList = 
				(List<Pattern>) xstream.fromXML(patternIS);
			for (Pattern onePattern : patternsList) {
				patterns.put(onePattern.getId(), onePattern);
			}
		}
		generateCache();
	}
	
	/**
	 * To generate the cache of patterns.
	 */
	private void generateCache() {
		cachedPatterns.clear();
		for (Entry<String, Pattern> onEntry : patterns.entrySet()) {
			String patternName = onEntry.getKey();
			Pattern patternValue = onEntry.getValue();
			cachedPatterns.put(patternName, cleanPattern(patternValue));
		}
	}
	
	/**
	 * Clean the content of ##xxx##.
	 * @param pattern the pattern to clean.
	 * @return the pattern catcher.
	 */
	private Pattern cleanPattern(final Pattern pattern) {
		Pattern ret;
		try {
			ret = (Pattern) pattern.clone();
		} catch (CloneNotSupportedException e) {
			return pattern;
		}
		String patternValue = ret.getContent();
		if (patternValue.contains("##")) {
			patternValue = parse(patternValue);
		}
		ret.setContent(patternValue);
		return ret;
	}

	/**
	 * To replace the ## by the real value in a pattern.
	 * @param str the string to parse.
	 * @return the parsed string.
	 */
	public final String parse(final String str) {
		String ret = str;
		while (ret.contains("##")) {
			boolean findOne = false;
			for (Entry<String, Pattern> onEntry : cachedPatterns.entrySet()) {
				String patternName = onEntry.getKey();
				String patternValue = onEntry.getValue().getContent();
				String key = "##" + patternName + "##";
				if (ret.contains(key)) {
					findOne = true;
					ret = ret.replace(key, patternValue);
				}
			}
			if (!findOne) {
				break;
			}
		}
		return ret.trim();
	}
	
	/**
	 * To get a pattern from his id.
	 * @param patternId the id of the pattern.
	 * @return the corresponding pattern.
	 */
	public final Pattern getPattern(final String patternId) {
		return cachedPatterns.get(patternId);
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}
}
