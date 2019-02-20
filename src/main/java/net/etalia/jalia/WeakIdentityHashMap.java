package net.etalia.jalia;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WeakIdentityHashMap<K, V> {

    private final HashMap<WeakReference<K>, V> mMap = new HashMap<>();
    private final ReferenceQueue<Object> mRefQueue = new ReferenceQueue<>();

    private void cleanUp() {
        Reference<?> ref;
        while ((ref = mRefQueue.poll()) != null) {
            mMap.remove(ref);
        }
    }

    public void put(K key, V value) {
        cleanUp();
        mMap.put(new CmpWeakReference<>(key, mRefQueue), value);
    }

    public V get(K key) {
        cleanUp();
        return mMap.get(new CmpWeakReference<>(key));
    }

    public Collection<V> values() {
        cleanUp();
        return mMap.values();
    }

    public Set<Map.Entry<WeakReference<K>, V>> entrySet() {
        return mMap.entrySet();
    }

    public int size() {
        cleanUp();
        return mMap.size();
    }

    public boolean isEmpty() {
        cleanUp();
        return mMap.isEmpty();
    }

    private static class CmpWeakReference<K> extends WeakReference<K> {

        private final int mHashCode;

        public CmpWeakReference(K key) {
            super(key);
            mHashCode = System.identityHashCode(key);
        }

        public CmpWeakReference(K key, ReferenceQueue<Object> refQueue) {
            super(key, refQueue);
            mHashCode = System.identityHashCode(key);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            K k = get();
            if (k != null && o instanceof CmpWeakReference) {
                return ((CmpWeakReference) o).get() == k;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return mHashCode;
        }
    }
}