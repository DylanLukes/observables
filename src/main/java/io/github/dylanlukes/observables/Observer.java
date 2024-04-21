package io.github.dylanlukes.observables;

/**
 * An observer that can be notified when the value of a subject changes.
 * @param <T> The type of the value that the observer observes.
 */
public interface Observer<T> {
    /**
     * Called when the value of the subject changes.
     * @param value The new value.
     */
    void update(T value);
}
