package io.github.dylanlukes.observables;

import java.lang.Number;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SubjectTests {
    /** Registering an observer should add the observer to the list of observers. */
    @Test
    void registerObserver() {
        var subject = Observables.createSubject(Integer.class);
        var expected = 42;
        var observed = new AtomicInteger(0);

        subject.registerObserver(observed::set);
        assertThat(subject.getObservers(), hasSize(1));

        subject.setValue(expected);
        assertThat(observed.get(), is(expected));
    }

    /** Registering the same observer twice should not add the observer twice. */
    @Test
    void registerObserverIdempotency() {
        var subject = Observables.createSubject(Integer.class);
        var observer = subject.registerObserver(v -> {});

        subject.registerObserver(observer);
        subject.registerObserver(observer);
        assertThat(subject.getObservers(), hasSize(1));
    }

    /** Registering an observer for a broader type should add the observer to the list of observers. */
    @Test
    void registerObserverContravariance() {
        var subject = Observables.createSubject(Integer.class);
        var expected = 42;
        var observed = new AtomicReference<Number>(0);

        // Register accepts an Observer for a broader type (contravariant in T).
        subject.registerObserver((Number v) -> { observed.set(v); });
        assertThat(subject.getObservers(), hasSize(1));

        subject.setValue(expected);
        assertThat(observed.get(), is(expected));
    }

    /**
     * Unregistering an observer should remove the observer from the list of observers.
     */
    @Test
    void unregisterObserver() {
        var subject = Observables.createSubject(String.class);
        var notified = new AtomicBoolean(false);

        var observer = subject.registerObserver(str -> { notified.set(true); });
        assertThat(subject.getObservers(), hasSize(1));

        subject.unregisterObserver(observer);
        assertThat(subject.getObservers(), hasSize(0));

        subject.setValue("Hello, World!");
        assertThat(notified.get(), is(false));
    }
}