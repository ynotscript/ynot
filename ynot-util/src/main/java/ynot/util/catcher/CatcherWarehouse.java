package ynot.util.catcher;

import java.util.HashMap;
import java.util.Map;

/**
 * To store and manage the CatcherStorage.
 * @author equesada
 */
public final class CatcherWarehouse {
	
	/**
	 * Private constructor to forbid the instantiation.
	 */
	private CatcherWarehouse() { }
	
	/**
	 * All the storages of Pattern.
	 */
	private static Map<String, CatcherStorage> storages 
		= new HashMap<String, CatcherStorage>();
	
	/**
	 * Default storage name.
	 */
	private static final String DEFAULT_STORAGE_NAME = "default";

	/**
	 * The default loading storage process.
	 */
	private static final Boolean LOAD_ON_DEFAULT = true;
	
	/**
	 * To get a CatcherStorage from his name.
	 * @param storageName the name of the storage to get.
	 * @param load to if true so it will automatically load the storage.
	 * @return the corresponding CatcherStorage.
	 */
	public static CatcherStorage getStorage(
			final String storageName, final Boolean load) {
		CatcherStorage ret = storages.get(storageName);	
		if (ret == null) {
			ret = new CatcherStorage(storageName);
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
	 * @return the asked CatcherStorage.
	 */
	public static CatcherStorage getStorage(final String storageName) {
		return CatcherWarehouse.getStorage(
				storageName, LOAD_ON_DEFAULT);
	}
	
	/**
	 * To get the default CatcherStorage.
	 * @param load if true so it will automatically load the storage.
	 * @return the default CatcherStorage.
	 */
	public static CatcherStorage getStorage(final Boolean load) {
		return CatcherWarehouse.getStorage(DEFAULT_STORAGE_NAME, load);
	}
	
	/**
	 * To get the default CatcherStorage.
	 * @return the defaultCatcherStorage.
	 */
	public static CatcherStorage getStorage() {
		return CatcherWarehouse.getStorage(
				DEFAULT_STORAGE_NAME, LOAD_ON_DEFAULT);
	}
}
