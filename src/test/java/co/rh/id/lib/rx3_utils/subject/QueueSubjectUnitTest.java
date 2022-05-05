package co.rh.id.lib.rx3_utils.subject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class QueueSubjectUnitTest {

    @Test
    public void cold_onNext() {
        String test = "this is a test";
        String test2 = "this is a test2";
        List<String> resultString = new ArrayList<>();
        QueueSubject<String> stringQueueSubject = new QueueSubject<>();
        stringQueueSubject.onNext(test);
        stringQueueSubject.onNext(test2);

        assertEquals(2, stringQueueSubject.getValues().size());

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(stringQueueSubject.subscribe(resultString::add));

        assertTrue(stringQueueSubject.getValues().isEmpty());

        assertEquals(2, resultString.size());
        assertEquals(test, resultString.get(0));
        assertEquals(test2, resultString.get(1));

        compositeDisposable.dispose();
    }

    @Test
    public void cold_onNext_observeOnDifferentThread() throws InterruptedException {
        String test = "this is a test";
        String test2 = "this is a test2";
        List<String> resultString = new ArrayList<>();
        QueueSubject<String> stringQueueSubject = new QueueSubject<>();
        stringQueueSubject.onNext(test);
        stringQueueSubject.onNext(test2);
        ExecutorService executorService = Executors.newCachedThreadPool();

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(stringQueueSubject.observeOn(Schedulers.from(executorService))
                .subscribe(resultString::add));

        Thread.sleep(10);
        assertTrue(stringQueueSubject.getValues().isEmpty());

        assertEquals(2, resultString.size());
        assertEquals(test, resultString.get(0));
        assertEquals(test2, resultString.get(1));

        compositeDisposable.dispose();
    }

    @Test
    public void cold_onNext_subscribeBeforeFirstValue() {
        String test = "this is a test";
        List<String> resultString = new ArrayList<>();
        QueueSubject<String> stringQueueSubject = new QueueSubject<>();
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(stringQueueSubject.subscribe(resultString::add));
        stringQueueSubject.onNext(test);

        assertEquals(0, stringQueueSubject.getValues().size());

        assertTrue(stringQueueSubject.getValues().isEmpty());

        assertEquals(1, resultString.size());
        assertEquals(test, resultString.get(0));

        compositeDisposable.dispose();
    }

    @Test
    public void hot_onNext() {
        String test = "this is a test";
        String test2 = "this is a test2";
        AtomicReference<String> resultString = new AtomicReference<>();
        AtomicReference<String> resultString2 = new AtomicReference<>();
        QueueSubject<String> stringQueueSubject = new QueueSubject<>();
        stringQueueSubject.onNext(test);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(stringQueueSubject.subscribe(resultString::set));
        compositeDisposable.add(stringQueueSubject.subscribe(resultString2::set));

        assertEquals(test, resultString.get());
        assertNull(resultString2.get());

        stringQueueSubject.onNext(test2);

        assertEquals(test2, resultString.get());
        assertEquals(test2, resultString2.get());

        assertTrue(stringQueueSubject.getValues().isEmpty());

        compositeDisposable.dispose();
    }

    @Test
    public void hot_onNext_observeOnDifferentThread() throws InterruptedException {
        String test = "this is a test";
        String test2 = "this is a test2";
        AtomicReference<String> resultString = new AtomicReference<>();
        AtomicReference<String> resultString2 = new AtomicReference<>();
        QueueSubject<String> stringQueueSubject = new QueueSubject<>();
        stringQueueSubject.onNext(test);
        ExecutorService executorService = Executors.newCachedThreadPool();
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(stringQueueSubject.observeOn(Schedulers.from(executorService))
                .subscribe(resultString::set));
        compositeDisposable.add(stringQueueSubject.observeOn(Schedulers.from(executorService))
                .subscribe(resultString2::set));

        Thread.sleep(10);
        assertEquals(test, resultString.get());
        assertNull(resultString2.get());

        stringQueueSubject.onNext(test2);

        Thread.sleep(10);
        assertEquals(test2, resultString.get());
        assertEquals(test2, resultString2.get());

        assertTrue(stringQueueSubject.getValues().isEmpty());

        compositeDisposable.dispose();
        executorService.shutdown();
    }
}