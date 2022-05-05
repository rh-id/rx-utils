package co.rh.id.lib.rx3_utils.subject;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.Subject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Subject with a Queue to back the values. This subject will flush all the queue values when subscribed.
 * If onNext is invoked when there are no observers, the value will be added to queue waiting to be flushed.
 *
 * @param <T>
 */
public class QueueSubject<T> extends Subject<T> {

    private final LinkedList<T> mQueue;
    private final List<Observer<? super T>> mObservers;
    private boolean mDone;
    private Throwable mThrowable;

    public QueueSubject() {
        mQueue = new LinkedList<>();
        mObservers = new ArrayList<>();
    }

    public synchronized List<T> getValues() {
        return new ArrayList<>(mQueue);
    }

    @Override
    public boolean hasObservers() {
        return !mObservers.isEmpty();
    }

    @Override
    public boolean hasThrowable() {
        return mThrowable != null;
    }

    @Override
    public boolean hasComplete() {
        return mDone;
    }

    @Override
    public @Nullable Throwable getThrowable() {
        return mThrowable;
    }

    @Override
    public synchronized void onSubscribe(@NonNull Disposable d) {
        if (mDone) {
            d.dispose();
        }
    }

    @Override
    public synchronized void onNext(@NonNull T t) {
        if (mDone) return;
        mQueue.add(t);
        flushQueue();
    }

    @Override
    public synchronized void onError(@NonNull Throwable e) {
        if (mDone) return;
        if (!mObservers.isEmpty()) {
            for (Observer<? super T> observer : mObservers) {
                observer.onError(e);
            }
        }
        mThrowable = e;
        mDone = true;
    }

    @Override
    public synchronized void onComplete() {
        if (mDone) return;
        flushQueue();
        mQueue.clear();
        mDone = true;
    }

    private synchronized void flushQueue() {
        if (!mObservers.isEmpty()) {
            while (!mQueue.isEmpty()) {
                T value = mQueue.remove();
                for (Observer<? super T> observer : mObservers) {
                    observer.onNext(value);
                }
            }
        }
    }

    @Override
    protected synchronized void subscribeActual(@NonNull Observer<? super T> observer) {
        mObservers.add(observer);
        flushQueue();
    }
}
