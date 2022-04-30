package co.rh.id.lib.rx3_utils.subject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * This class assist Serializable implementation for PublishSubject
 */
public class SerialPublishSubject<E> implements Serializable {
    private transient PublishSubject<E> mSubject;

    public SerialPublishSubject() {
        mSubject = PublishSubject.create();
    }

    public PublishSubject<E> getSubject() {
        return mSubject;
    }

    public void onNext(E element) {
        mSubject.onNext(element);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        // Leave blank nothing to write
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        mSubject = PublishSubject.create();
    }
}
