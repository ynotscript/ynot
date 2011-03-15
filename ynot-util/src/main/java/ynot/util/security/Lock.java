package ynot.util.security;

public class Lock implements Cloneable {

    private String usedKey;
    private final String uniqueKey;

    public Lock() {
        usedKey = null;
        uniqueKey = null;
    }

    public Lock(String key) {
        usedKey = null;
        uniqueKey = key;
    }

    public final boolean tryToLock(String key) {
        if (isLocked() || !isAllowedKey(key)) {
            return false;
        }
        lock(key);
        return true;
    }

    public final boolean tryToUnlock(String key) {
        if (!isLocked() || !isSameKeyWhenLocking(key)) {
            return false;
        }
        unlock();
        return true;
    }

    public final boolean isLocked() {
        return usedKey != null;
    }

    private boolean isAllowedKey(String key) {
        if (null == key) {
            return false;
        }
        if (isUniqueKey() && !isSameKeyWhenLocking(key)) {
            return false;
        }
        return true;
    }

    private boolean isUniqueKey() {
        return uniqueKey != null;
    }

    private void lock(String key) {
        usedKey = key;
    }

    private boolean isSameKeyWhenLocking(String key) {
        if (isUniqueKey()) {
            return uniqueKey.equals(key);
        } else {
            return usedKey.equals(key);
        }
    }

    private void unlock() {
        this.usedKey = null;
    }

    @Override
    public final Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
