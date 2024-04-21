package edu.ucsd.cse110.observables;

import org.jetbrains.annotations.TestOnly;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A special {@link Subject} that can itself observe other subjects.
 * <p>
 * This class is used primarily to implement some transformations, but
 * can also be used for merging multiple subjects into one. See the tests
 * for examples.
 *
 * @param <T> The type of the value.
 */
class MediatorSubjectImpl<T> extends SubjectImpl<T> implements MediatorSubject<T> {
    record SourceWrapper(Subject<?> source, Observer<?> observer) {
    }

    protected ConcurrentLinkedQueue<SourceWrapper> sources = new ConcurrentLinkedQueue<>();

    protected MediatorSubjectImpl() {
        super();
    }

    @Override
    public <S> Observer<? super S> registerSource(Subject<S> source, Observer<? super S> observer) {
        var wrapper = new SourceWrapper(source, observer);
        if (!sources.contains(wrapper)) {
            sources.add(wrapper);
        }
        source.registerObserver(observer);
        return observer;
    }

    @Override
    public <S> void unregisterSource(Subject<S> source) {
        var wrapper = sources.stream().filter(w -> w.source == source).findFirst().orElseThrow();
        sources.remove(wrapper);
        //noinspection unchecked
        source.unregisterObserver((Observer<? super S>) wrapper.observer);
    }

    @TestOnly
    @Override
    public List<? extends Observer<?>> getSources() {
        return sources.stream().map(SourceWrapper::observer).toList();
    }
}
