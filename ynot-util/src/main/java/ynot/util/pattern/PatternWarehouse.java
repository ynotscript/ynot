package ynot.util.pattern;

import java.util.HashMap;
import java.util.Map;

/**
 * To store and manage the PatternStorage.
 * @author equesada
 */
public final class PatternWarehouse {
	
	/**
	 * Private constructor to forbid the instantiation.
	 */
	private PatternWarehouse() { }
	
	/**
	 * All the storages of Pattern.
	 */
	private static Map<String, PatternStorage> storages 
		= new HashMap<String, PatternStorage>();
	
	/**
	 * Default storage name.
	 */
	private static final String DEFAULT_STORAGE_NAME = "default";

	/**
	 * The default loading storage process.
	 */
	private static final Boolean LOAD_ON_DEFAULT = true;
	
	/**
	 * To get a PatternStorage from his name.
	 * @param storageName the name of the storage to get.
	 * @param load to if true so it will automatically load the storage.
	 * @return the corresponding PatternStorage.
	 */
	public static PatternStorage getStorage(
			final String storageName, final Boolean load) {
		PatternStorage ret = storages.get(storageName);	
		if (ret == null) {
			ret = new PatternStorage(storageName);
			if (load) {
				ret.load();
			}
			storages.put(storageName, ret);
		}
		return ret;
	}
	
	/**
	 * To get a storage from his name.
	 * @param storageName the storage name
	 * @return the asked PatternStorage.
	 */
	public static PatternStorage getStorage(final String storageName) {
		return PatternWarehouse.getStorage(
				storageName, LOAD_ON_DEFAULT);
	}
	
	/**
	 * To get the default PatternStorage.
	 * @param load if true so it will automatically load the storage.
	 * @return the default PatternStorage.
	 */
	public static PatternStorage getStorage(final Boolean load) {
		return PatternWarehouse.getStorage(DEFAULT_STORAGE_NAME, load);
	}
	
	/**
	 * To get the default PatternStorage.
	 * @return the default PatternStorage.
	 */
	public static PatternStorage getStorage() {
		return PatternWarehouse.getStorage(
				DEFAULT_STORAGE_NAME, LOAD_ON_DEFAULT);
	}
}
