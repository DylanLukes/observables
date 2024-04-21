package edu.ucsd.cse110.observables;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * A factory class for creating {@link Subject}s, hiding the underlying
 * implementations.
 */
public class Observables {
    // region Factory Methods

    /**
     * Creates a new {@link Subject}. For example:
     * <pre>{@code
     * MutableSubject<Integer> ints = Observables.createSubject();
     * @param <T> The type of the subject's values.
     * @return A new subject.
     */
    public static <T> MutableSubject<T> createSubject() {
        return new SubjectImpl<>();
    }

    /**
     * Convenience factory method for when using var-syntax. For example:
     * <pre>{@code
     * var ints = Observables.createSubject(Integer.class);
     * }</pre>
     * @param klass The class of the subject's values (used for type inference).
     * @return A new subject.
     * @param <T> The type of the subject's values.
     */
    public static <T> MutableSubject<T> createSubject(Class<T> klass) {
        return createSubject();
    }

    /**
     * Creates a new {@link MediatorSubject}. This is a more advanced tool, very
     * useful in some cases but not necessary for most simple use cases. See the
     * tests for examples.
     * @return A new mediator subject.
     * @param <T> The type of the mediator subject's values.
     */
    public static <T> MediatorSubject<T> createMediatorSubject() {
        return new MediatorSubjectImpl<>();
    }

    /**
     * Convenience factory method for when using var-syntax. For example:
     * <pre>{@code
     * var ints = Observables.createMediatorSubject(Integer.class);
     * }</pre>
     * @param klass The class of the mediator subject's values (used for type inference).
     * @return A new mediator subject.
     * @param <T> The type of the mediator subject's values.
     */
    public static <T> MediatorSubject<T> createMediatorSubject(Class<T> klass) {
        return createMediatorSubject();
    }
    // endregion

    // region Transformation Methods

    /**
     * Applies the given function on the main thread to each value emitted by
     * the source Subject and returns a Subject that emits resulting values.
     * <p>
     * This allows for "chaining" of Subjects, where the output of one Subject
     * is used as the input to the new returned Subject.
     * <p>
     * Example:
     * <pre>{@code
     * Subject<Integer> ints = Observables.createSubject();
     * Subject<Integer> doublesInts = Observables.map(source, x -> x * 2);
     *
     * doublesInts.registerObserver(value -> System.out.println("Doubled: " + value));
     * ints.setValue(21);
     *
     * // Outputs "Doubled: 42"
     * }</pre>
     *
     *
     * @param subject The subject to map.
     * @param transformation The function to map the subject with.
     * @return A new subject which has
     * @param <T> The type of the source subject.
     * @param <R> The type of the resulting subject.
     */
    public static <T, R> Subject<R> map(Subject<T> subject, Function<T, R> transformation) {
        MediatorSubject<R> mapped = createMediatorSubject();
        mapped.registerSource(subject, value -> {
            R mappedValue = transformation.apply(value);
            mapped.setValue(mappedValue);
        });
        return mapped;
    }

    /**
     * Advanced fancy transformation. Returns a new {@link Subject} that emits values
     * from a delegate {@link Subject} based on the most recent value emitted by the trigger.
     * <p>
     * This allows for dynamic switching of the source Subject based on the values
     * emitted by the trigger Subject.
     *
     * @apiNote If you are into functional programming, this is vaguely similar to a flatMap.
     *
     * @param trigger The subject that triggers the switch.
     * @param transformation A function to apply to each value set on source to create a new delegate LiveData for the returned one.
     * @return A new subject that emits values from a source subject based on the trigger subject.
     * @param <T> The type of the trigger subject.
     * @param <R> The type of the resulting subject.
     */
    public static <T, R> Subject<R> switchMap(Subject<T> trigger, Function<T, Subject<R>> transformation) {
        MediatorSubject<R> result = createMediatorSubject();

        result.registerSource(trigger, new Observer<T>() {
            @Nullable Subject<R> source = null;

            @Override
            public void update(T value) {
                var newSource = transformation.apply(value);
                if (source == newSource) {
                    return;
                }
                if (source != null) {
                    result.unregisterSource(source);
                }
                source = newSource;
                if (source != null) {
                    result.registerSource(source, result::setValue);
                }
            }
        });

        return result;
    }
}
