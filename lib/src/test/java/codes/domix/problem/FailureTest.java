package codes.domix.problem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FailureTest {

    @Test
    void constructor_shouldRejectNullKind() {
        var ex = assertThrows(NullPointerException.class, () ->
            new Failure<>(
                null,
                "msg",
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
        );

        assertTrue(
            ex.getMessage().contains("kind"),
            "Exception message should mention kind"
        );
    }

    @Test
    void constructor_shouldRejectNullMessage() {
        var ex = assertThrows(NullPointerException.class, () ->
            new Failure<>(
                Failure.Kind.BUSINESS,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
        );

        assertTrue(ex.getMessage().contains("message"), "Exception message should mention message");
    }

    @Test
    @SuppressWarnings("rawtypes")
    void constructor_shouldDefaultNullCollectionsToEmptyAndCopyThem() {
        var originalArgs = new HashMap<String, Object>();
        originalArgs.put("a", 1);

        var originalDetails = new ArrayList<Failure<?>>();
        originalDetails.add(Failure.validation("v1"));

        var f = new Failure<>(
            Failure.Kind.VALIDATION,
            "msg",
            null,
            null,
            null,
            originalArgs,
            originalDetails,
            null,
            null
        );

        // Should be non-null
        assertNotNull(f.i18nArgs());
        assertNotNull(f.details());

        // Should be copies (immutable snapshots)
        originalArgs.put("b", 2);
        originalDetails.add(Failure.validation("v2"));

        assertEquals(1, f.i18nArgs().size(), "i18nArgs should be an immutable copy");
        assertEquals(1, f.details().size(), "details should be an immutable copy");

        // And should be unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> f.i18nArgs().put("x", 9));
        assertThrows(UnsupportedOperationException.class, () -> ((List) f.details()).add(Failure.business("nope")));
    }

    @Test
    void constructor_shouldTurnNullCollectionsIntoEmpty() {
        var f = new Failure<>(
            Failure.Kind.BUSINESS,
            "msg",
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );

        assertNotNull(f.i18nArgs());
        assertNotNull(f.details());
        assertTrue(f.i18nArgs().isEmpty());
        assertTrue(f.details().isEmpty());
    }

    @Test
    void factories_shouldCreateBaseFailures() {
        var b = Failure.business("b");
        assertEquals(Failure.Kind.BUSINESS, b.kind());
        assertEquals("b", b.message());
        assertNull(b.code());
        assertTrue(b.details().isEmpty());
        assertTrue(b.i18nArgs().isEmpty());
        assertNull(b.cause());

        var v = Failure.validation("v");
        assertEquals(Failure.Kind.VALIDATION, v.kind());

        var t = Failure.technical("t");
        assertEquals(Failure.Kind.TECHNICAL, t.kind());
    }

    @Test
    void causedByFactories_shouldAttachCause() {
        var cause = new IllegalStateException("boom");

        var b = Failure.businessCausedBy("msg", cause);
        assertEquals(Failure.Kind.BUSINESS, b.kind());
        assertSame(cause, b.cause());
        assertTrue(b.hasCause());

        var t = Failure.technicalCausedBy("msg", cause);
        assertEquals(Failure.Kind.TECHNICAL, t.kind());
        assertSame(cause, t.cause());
        assertTrue(t.hasCause());
    }

    @Test
    void withers_shouldProduceNewInstancesAndPreserveExistingFields() {
        var base = Failure.business("m");

        var f = base
            .withReason("r")
            .withCode("C1")
            .withI18n("I18N_KEY", Map.of("name", "Domingo"))
            .withDetails(List.of(Failure.validation("v1")))
            .withData(Map.of("k", "v"))
            .withCause(new RuntimeException("x"));

        // base unchanged
        assertNull(base.reason());
        assertNull(base.code());
        assertTrue(base.i18nArgs().isEmpty());
        assertTrue(base.details().isEmpty());
        assertNull(base.data());
        assertNull(base.cause());

        // new has all
        assertEquals("r", f.reason());
        assertEquals("C1", f.code());
        assertEquals("I18N_KEY", f.i18nKey());
        assertEquals("Domingo", f.i18nArgs().get("name"));
        assertEquals(1, f.details().size());
        assertTrue(((Map<?, ?>) f.data()).containsKey("k"));
        assertNotNull(f.cause());
    }

    @Test
    void equalsAndHashCode_shouldIgnoreCause() {
        var f1 = Failure.business("m", Map.of())
            .withCode("X")
            .withDetails(List.of(Failure.validation("v1")))
            .withData(Map.of("k", "v"))
            .withCause(new RuntimeException("cause-1"));

        var f2 = Failure.business("m", Map.of())
            .withCode("X")
            .withDetails(List.of(Failure.validation("v1")))
            .withData(Map.of("k", "v"))
            .withCause(new IllegalArgumentException("cause-2"));

        assertEquals(f1, f2, "Failures should be equal even if cause differs");
        assertEquals(f1.hashCode(), f2.hashCode(), "HashCode should ignore cause");
    }

    @Test
    void toString_shouldNotLeakCauseMessageOrStackTrace() {
        var cause = new RuntimeException("SENSITIVE_MESSAGE");
        var f = Failure.business("m").withCause(cause);

        var s = f.toString();

        // It should show at most the class name (as implemented), not the message or stacktrace
        assertTrue(s.contains("cause=" + RuntimeException.class.getName()),
            "toString should include only the cause class name");
        assertFalse(s.contains("SENSITIVE_MESSAGE"),
            "toString must not include the cause message");
        assertFalse(s.contains("at "),
            "toString must not include stack trace lines");
    }

    @Test
    void hasCause_shouldWork() {
        assertFalse(Failure.business("m").hasCause());
        assertTrue(Failure.business("m").withCause(new RuntimeException()).hasCause());
    }

    @Test
    void validateEquals() {
        var one = Failure.business("");
        var two = Failure.business("");
        assertEquals(one, two);
    }

    @Test
    void testI18nArgs() {
        var f = Failure.business("m")
            .withI18n("key");
        assertEquals("key", f.i18nKey());
    }
}
