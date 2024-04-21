package io.github.dylanlukes.observables;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.List;

/**
 * A subject that can be observed by observers.
 * @param <T> The type of the value that the subject holds.
 */
public interface Subject<T> {
    /**
     * Register an observer to be notified when the value changes.
     *
     * @param observer The observer to registerObserver.
     * @return The observer that was registered, so that it can be unregistered later.
     */
    Observer<? super T> registerObserver(@NotNull Observer<? super T> observer);

    /**
     * Unregister an observer so that it will no longer be notified when the value changes.
     *
     * @param observer The observer to unregister.
     */
    void unregisterObserver(@NotNull Observer<? super T> observer);

    /**
     * Get the current value.
     *
     * @return The current value.
     * @throws IllegalStateException if no value has been set yet.
     */
    T getValue();

    /**
     * Get the list of observers. This method is for testing purposes only.
     * Do NOT use this method in production code.
     *
     * @return The list of observers.
     */
    @TestOnly
    List<Observer<? super T>> getObservers();
}