package edu.ucsd.cse110.observables;

public interface Observer<T> {
    void update(T value);
}
