package co.rh.id.lib.rx3_utils.disposable;


import io.reactivex.rxjava3.disposables.Disposable;

import java.util.HashMap;
import java.util.Map;

/**
 * Similar to CompositeDisposable, the difference is the unique key string to pass together with disposable object.
 * If same unique key is found when adding the disposable, the previous disposable is disposed and new one added to internal Map
 */
public class UniqueKeyDisposable implements Disposable {
    private boolean mIsDisposed;
    private Map<String, Disposable> mDisposableMap;

    public UniqueKeyDisposable() {
        mDisposableMap = new HashMap<>();
    }

    public void add(String uniqueKey, Disposable disposable) {
        Disposable existing = mDisposableMap.remove(uniqueKey);
        if (existing != null) {
            existing.dispose();
        }
        mDisposableMap.put(uniqueKey, disposable);
    }

    @Override
    public void dispose() {
        if (mIsDisposed) return;
        if (!mDisposableMap.isEmpty()) {
            for (Map.Entry<String, Disposable> entry : mDisposableMap.entrySet()) {
                entry.getValue().dispose();
            }
            mDisposableMap.clear();
        }
        mIsDisposed = true;
    }

    @Override
    public boolean isDisposed() {
        return mIsDisposed;
    }
}
