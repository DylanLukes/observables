package edu.ucsd.cse218.observables;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

class SubjectImpl<T> implements MutableSubject<T> {
    protected volatile AtomicReference<Optional<T>> value = new AtomicReference<>(Optional.empty());

    // CopyOnWriteArrayList to avoid ConcurrentModificationException when an observer is
    // registered or unregistered during notifying observers.
    protected final Queue<Observer<? super T>> observers = new ConcurrentLinkedQueue<>();

    protected SubjectImpl() {

    }

    protected SubjectImpl(T initialValue) {
        this.value.getAndSet(Optional.of(initialValue));
    }

    protected void notifyObservers() {
        observers.forEach(observer -> {
            value.get().ifPresent(observer::update);
        });
    }

    @Override
    public Observer<? super T> registerObserver(@NotNull Observer<? super T> observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
        return observer;
    }

    @Override
    public void unregisterObserver(@NotNull Observer<? super T> observer) {
        observers.remove(observer);
    }

    @Override
    public T getValue() {
        return value.get().orElseThrow();
    }

    public void setValue(T newValue) {
        value.set(Optional.of(newValue));
        notifyObservers();
    }

    @TestOnly
    public List<Observer<? super T>> getObservers() {
        return observers.stream().toList();
    }
}
