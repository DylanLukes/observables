package edu.ucsd.cse218.observables;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

class MediatorSubjectTests {
    @Test
    void merge() {
        var source1 = Observables.createSubject();
        var source2 = Observables.createSubject();
        var merged = Observables.createMediatorSubject();

        merged.registerSource(source1, merged::setValue);
        merged.registerSource(source2, merged::setValue);
        assertThat(merged.getSources(), hasSize(2));

        var expected = 42;
        source1.setValue(expected);
        assertThat(merged.getValue(), is(expected));

        expected = 43;
        source2.setValue(expected);
        assertThat(merged.getValue(), is(expected));
    }

    @Test
    void registerObserverSource() {
        var upstream = Observables.createSubject(Integer.class);
        var downstream = Observables.createMediatorSubject(Integer.class);
        var expected = 42;
        var observed = new AtomicInteger(0);

        downstream.registerSource(upstream, downstream::setValue);
        downstream.registerObserver(observed::set);
        assertThat(downstream.getSources(), hasSize(1));
        assertThat(downstream.getObservers(), hasSize(1));

        upstream.setValue(expected);
        assertThat(observed.get(), is(expected));
    }

    @Test
    void unregisterObserverSource() {
        var upstream = Observables.createSubject(String.class);
        var downstream = Observables.createMediatorSubject(String.class);
        var downstreamNotified = new AtomicBoolean(false);

        downstream.registerSource(upstream, downstream::setValue);
        downstream.registerObserver(str -> {
            downstreamNotified.set(true);
        });
        assertThat(downstream.getSources(), hasSize(1));
        assertThat(downstream.getObservers(), hasSize(1));

        downstream.unregisterSource(upstream);
        assertThat(downstream.getSources(), hasSize(0));

        upstream.setValue("Hello, World!");
        assertThat(downstreamNotified.get(), is(false));
    }
}
