package io.github.dylanlukes.observables;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TransformationTests {
    @Test
    void map() {
        var source = Observables.createSubject(Integer.class);
        var mapped = Observables.map(source, Object::toString);

        var expected = "42";
        source.setValue(42);
        assertThat(mapped.getValue(), is(expected));
    }

    @Test
    void switchMap() {
        var source1 = Observables.createSubject(Integer.class);
        var source2 = Observables.createSubject(Integer.class);

        var trigger = Observables.createSubject(Boolean.class);

        var mapped = Observables.switchMap(trigger, b -> {
            if (b) return source1;
            else return source2;
        });

        var expected = 42;
        var observed = new AtomicInteger(0);
        mapped.registerObserver(observed::set);
        trigger.setValue(true); // switch to source1
        source1.setValue(expected);
        assertThat(observed.get(), is(expected));

        expected = 43;
        trigger.setValue(false); // switch to source2
        source2.setValue(expected);
        assertThat(observed.get(), is(expected));
    }
}
