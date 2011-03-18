package ynot.util.security;

/**
 * A simple implementation of a lock. It can work with an unique key (if you
 * gave it on the constructor). Or with any key. The only constraint will be to
 * use the same key (used when you locked) when you want to unlock.
 * 
 * @author ERIC.QUESADA
 * 
 */
public class Lock implements Cloneable {

    /**
     * the used key when locked (else null).
     */
    private String usedKey;

    /**
     * if it's set so the lock is only for this key.
     */
    private final String uniqueKey;

    /**
     * To create a lock usable with any key.
     */
    public Lock() {
        usedKey = null;
        uniqueKey = null;
    }

    /**
     * To create a lock for an uniq key.
     * 
     * @param key
     *            the concerned key.
     */
    public Lock(final String key) {
        usedKey = null;
        uniqueKey = key;
    }

    /**
     * try to lock with a key.
     * 
     * @param key
     *            the concerned key.
     * @return true if succeed to lock else false.
     */
    public final boolean tryToLock(final String key) {
        if (isLocked() || !isAllowedKey(key)) {
            return false;
        }
        lock(key);
        return true;
    }

    /**
     * try to unlock with a key.
     * 
     * @param key
     *            the concerned key.
     * @return true if succeed to unlock else false.
     */
    public final boolean tryToUnlock(final String key) {
        if (!isLocked() || !isSameKeyWhenLocking(key)) {
            return false;
        }
        unlock();
        return true;
    }

    /**
     * To know if it's already locked.
     * 
     * @return true if it's the case else false.
     */
    public final boolean isLocked() {
        return usedKey != null;
    }

    /**
     * To know if it's an allowed key.
     * 
     * @param key
     *            the concerned key.
     * @return true if it's the case else false.
     */
    private boolean isAllowedKey(final String key) {
        if (null == key) {
            return false;
        }
        if (isUniqueKey() && !isSameKeyWhenLocking(key)) {
            return false;
        }
        return true;
    }

    /**
     * To know if the lock if for an uniq key.
     * 
     * @return true if it's the case else false.
     */
    private boolean isUniqueKey() {
        return uniqueKey != null;
    }

    /**
     * To lock with a key.
     * 
     * @param key
     *            the concerned key.
     */
    private void lock(final String key) {
        usedKey = key;
    }

    /**
     * To check if it's the same key used when locked.
     * 
     * @param key
     *            the concerned key.
     * @return true if it's the case else false.
     */
    private boolean isSameKeyWhenLocking(final String key) {
        if (isUniqueKey()) {
            return uniqueKey.equals(key);
        } else {
            return usedKey.equals(key);
        }
    }

    /**
     * To unlock.
     */
    private void unlock() {
        this.usedKey = null;
    }

    @Override
    public final Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
