package io.github.dylanlukes.observables;

public interface Observer<T> {
    void update(T value);
}
