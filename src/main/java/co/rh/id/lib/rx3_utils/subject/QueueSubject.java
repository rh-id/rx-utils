package co.rh.id.lib.rx3_utils.subject;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
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
    private final List<QueueDisposable<T>> mSubscribers;
    private boolean mDone;
    private Throwable mThrowable;

    public QueueSubject() {
        mQueue = new LinkedList<>();
        mSubscribers = new ArrayList<>();
    }

    public synchronized List<T> getValues() {
        return new ArrayList<>(mQueue);
    }

    @Override
    public synchronized boolean hasObservers() {
        return !mSubscribers.isEmpty();
    }

    @Override
    public synchronized boolean hasThrowable() {
        return mThrowable != null;
    }

    @Override
    public synchronized boolean hasComplete() {
        return mDone;
    }

    @Override
    public synchronized @Nullable Throwable getThrowable() {
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
        if (!mSubscribers.isEmpty()) {
            for (QueueDisposable<T> queueDisposable : mSubscribers) {
                queueDisposable.onError(e);
            }
        }
        mThrowable = e;
        mQueue.clear();
        mDone = true;
    }

    @Override
    public synchronized void onComplete() {
        if (mDone) return;
        flushQueue();
        mQueue.clear();
        if (!mSubscribers.isEmpty()) {
            for (QueueDisposable<T> queueDisposable : mSubscribers) {
                queueDisposable.onComplete();
            }
        }
        mDone = true;
    }

    private synchronized void flushQueue() {
        if (!mSubscribers.isEmpty()) {
            while (!mQueue.isEmpty()) {
                T value = mQueue.remove();
                for (QueueDisposable<T> queueDisposable : mSubscribers) {
                    queueDisposable.onNext(value);
                }
            }
        }
    }

    private synchronized void remove(QueueDisposable<T> queueDisposable) {
        mSubscribers.remove(queueDisposable);
    }

    private synchronized void add(QueueDisposable<T> queueDisposable) {
        mSubscribers.add(queueDisposable);
    }

    @Override
    protected synchronized void subscribeActual(@NonNull Observer<? super T> observer) {
        QueueDisposable<T> queueDisposable = new QueueDisposable<>(this, observer);
        /* onSubscribe seemed to be mandatory call in this method?
         it will break Observable.observeOn(differentThreadScheduler) if this is not called
         */
        observer.onSubscribe(queueDisposable);
        add(queueDisposable);
        flushQueue();
    }

    static final class QueueDisposable<T> implements Disposable {
        private final QueueSubject<T> mParent;
        private final Observer<? super T> mDownstream;

        private volatile boolean mDisposed;

        QueueDisposable(QueueSubject<T> parent, Observer<? super T> actual) {
            mParent = parent;
            mDownstream = actual;
        }

        public void onNext(T t) {
            if (!mDisposed) {
                mDownstream.onNext(t);
            }
        }

        public void onError(Throwable t) {
            if (mDisposed) {
                RxJavaPlugins.onError(t);
            } else {
                mDownstream.onError(t);
            }
        }

        public void onComplete() {
            if (!mDisposed) {
                mDownstream.onComplete();
            }
        }

        @Override
        public void dispose() {
            if (!mDisposed) {
                mParent.remove(this);
            }
            mDisposed = true;
        }

        @Override
        public boolean isDisposed() {
            return mDisposed;
        }
    }
}
