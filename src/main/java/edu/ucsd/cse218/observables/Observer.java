package edu.ucsd.cse218.observables;

public interface Observer<T> {
    void update(T value);
}
