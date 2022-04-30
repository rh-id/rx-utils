package co.rh.id.lib.rx3_utils.subject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Optional;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

/**
 * This class assist Serializable implementation for BehaviorSubject with Optional as value
 */
public class SerialOptionalBehaviorSubject<E extends Serializable> implements Serializable {
    private transient BehaviorSubject<Optional<E>> mSubject;

    public SerialOptionalBehaviorSubject() {
        mSubject = BehaviorSubject.createDefault(Optional.empty());
    }

    public SerialOptionalBehaviorSubject(E element) {
        mSubject = BehaviorSubject.createDefault(Optional.ofNullable(element));
    }

    public BehaviorSubject<Optional<E>> getSubject() {
        return mSubject;
    }

    public Optional<E> getValue() {
        return mSubject.getValue();
    }

    public void onNext(E element) {
        mSubject.onNext(Optional.ofNullable(element));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(mSubject.getValue().orElse(null));
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        E element = (E) in.readObject();
        mSubject = BehaviorSubject.createDefault(Optional.ofNullable(element));
    }
}
