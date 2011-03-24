package ynot.util.catcher;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ynot.util.pattern.PatternStorage;
import ynot.util.pattern.PatternWarehouse;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * To store and manage a pool of matchers.
 * 
 * @author equesada
 */
public class CatcherStorage {

	/**
	 * The default configuration file.
	 */
	private static final String DEFAULT_CONFIGURATION_FILE = "catchers.xml";

	/**
	 * The name of the storage.
	 */
	private String name;

	/**
	 * All the matchers of the storage: catcherId => Catcher.
	 */
	private Map<String, Catcher> catchers;

	/**
	 * The cached matchers to save time when matching (without any ##).
	 */
	private Map<String, Catcher> cachedCatchers;
	
	/**
	 * Default constructor.
	 * @param storageName the name of the storage.
	 */
	public CatcherStorage(final String storageName) {
		name = storageName;
		catchers = new LinkedHashMap<String, Catcher>();
		cachedCatchers = new LinkedHashMap<String, Catcher>();
	}

	/**
	 * To load the default configuration file.
	 */
	public final void load() {
		load(DEFAULT_CONFIGURATION_FILE);
	}

	/**
	 * To load a particular matcher file.
	 * @param matcherFile the matcher file.
	 */
	@SuppressWarnings("unchecked")
	public final void load(final String matcherFile) {
		catchers.clear();
		InputStream matcherIS = Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(matcherFile);
		if (matcherIS != null) {
			XStream xstream = new XStream(new DomDriver());
			xstream.alias("catcher", Catcher.class);
			xstream.alias("catchers", List.class);
			xstream.alias("match", String.class);
			xstream.alias("unmatch", String.class);
			List<Catcher> catchersList = (List<Catcher>) xstream
			    .fromXML(matcherIS);
			for (Catcher catcher : catchersList) {			
				catchers.put(catcher.getId(), catcher);
			}
		}
		generateCache();
	}
	
	/**
	 * To generate the cache of catchers.
	 */
	private void generateCache() {
		cachedCatchers.clear();
		for (Entry<String, Catcher> onEntry : catchers.entrySet()) {
			String catcherName = onEntry.getKey();
			Catcher catcherValue = onEntry.getValue();
			cachedCatchers.put(catcherName, cleanCatcher(catcherValue));
		}
	}
	
	/**
	 * Clean the matches of ##xxx##.
	 * @param catcher the catcher to clean.
	 * @return the cleaned catcher.
	 */
	private Catcher cleanCatcher(final Catcher catcher) {
		// Init
		Catcher ret;
		try {
			ret = (Catcher) catcher.clone();
		} catch (CloneNotSupportedException e) {
			return catcher;
		}
		List<String> currentMatches = ret.getMatches();
		List<String> currentUnmatches = ret.getUnmatches();
		List<String> newMatches = new ArrayList<String>();
		List<String> newUnmatches = new ArrayList<String>();
		PatternStorage storage = PatternWarehouse.getStorage();
		
		// For each match try to replace with real values 
		for (String onCurrentMatch : currentMatches) {
			newMatches.add(storage.parse(onCurrentMatch));
		}
		
		// For each match try to replace with real values 
		for (String onCurrentUnmatch : currentUnmatches) {
			newUnmatches.add(storage.parse(onCurrentUnmatch));
		}
		
		// Replace current by new
		ret.setMatches(newMatches);
		ret.setUnmatches(newUnmatches);
		
		// Return
		return ret;
	}
	
	/**
	 * Try to find a cacher matching with the line.
	 * @param line the line to test.
	 * @return the catcher id else null.
	 */
	public final String find(final String line) {
		String foundCatcherId = null;
		for (Entry<String, Catcher> entry : cachedCatchers.entrySet()) {
			Catcher testedCatcher = entry.getValue();
			if (testedCatcher.match(line)) {
				foundCatcherId = entry.getKey();
				break;
			}
		}
		return foundCatcherId;
	}

	/**
	 * To get a catcher from his id.
	 * @param catcherId the id of the catcher.
	 * @return the corresponding catcher.
	 */
	public final Catcher getCatcher(final String catcherId) {
		return cachedCatchers.get(catcherId);
	}
	
	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}
	
}
