package edu.ucsd.cse110.observables;

import org.jetbrains.annotations.TestOnly;

import java.util.List;

public interface MediatorSubject<T> extends MutableSubject<T> {
    /**
     * Register a source subject to be observed.
     * <p>
     * Example:
     * <pre>{@code
     * var source1 = new SubjectImpl<String>();
     * var source2 = new SubjectImpl<String>();
     *
     * var merged = Observables.createMediatorSubject();
     *
     * merged.registerSource(source1, v -> merged.setValue(v));
     * merged.registerSource(source2, merged::setValue); // shorthand syntax for same as above
     *
     * merged.registerObserver(v -> System.out.println("Merged: " + v));
     * source1.setValue("Hello");
     * source2.setValue("World");
     *
     * // Outputs:
     * // Merged: Hello
     * // Merged: World
     *
     * @param source The source subject to observe.
     * @param observer The observer to notify when the source subject changes.
     * @return The observer that was registered, so that it can be unregistered later.
     * @param <S> The type of the source subject.
     */
    <S> Observer<? super S> registerSource(Subject<S> source, Observer<? super S> observer);


    /**
     * Unregister a source subject that was previously registered.
     *
     * @param source The source subject to stop observing.
     * @param <S>    The type of the source subject.
     */
    <S> void unregisterSource(Subject<S> source);

    /**
     * Get the list of sources. This method is for testing purposes only.
     *
     * @return The list of sources.
     */
    @TestOnly
    List<? extends Observer<?>> getSources();
}
