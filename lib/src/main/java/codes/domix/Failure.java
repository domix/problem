package codes.domix;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a structured failure descriptor for modeling and propagating failure-related information
 * across different layers of an application. This record contains multiple attributes to detail
 * the nature, context, and cause of a failure.
 *
 * @param kind     The type of failure, categorized using the {@link Kind} enumeration. Must not be null.
 * @param message  A human-readable message describing the failure. Must not be null.
 * @param reason   An optional string to provide additional reasoning or explanation for the failure.
 * @param code     An optional application-specific error code for the failure.
 * @param i18nKey  An optional internationalization key for the failure message.
 * @param i18nArgs A map of arguments to support internationalization message formatting. Defaults to an empty map if null.
 * @param details  A list of subordinate or nested failure details. Defaults to an empty list if null.
 * @param data     Optional application-specific data related to the failure.
 * @param cause    An optional throwable that represents the root cause of the failure.
 */
public record Failure<T>(
    Kind kind,
    String message,
    String reason,
    String code,
    String i18nKey,
    Map<String, Object> i18nArgs,
    List<Failure<?>> details,
    T data,
    Throwable cause
) {

    public enum Kind {
        BUSINESS,
        VALIDATION,
        TECHNICAL,
        AUTHORIZATION,
        NOT_FOUND,
        CONFLICT
    }

    public Failure {
        kind = Objects.requireNonNull(kind, "kind must not be null");
        message = Objects.requireNonNull(message, "message must not be null");
        i18nArgs = (i18nArgs == null) ? Map.of() : Map.copyOf(i18nArgs);
        details = (details == null) ? List.of() : List.copyOf(details);
        // cause is allowed to be null
    }

    // ----------------------------
    // Factories
    // ----------------------------

    public static Failure<Void> business(String message) {
        return base(Kind.BUSINESS, message);
    }

    public static <T> Failure<T> business(String message, T data) {
        return base(Kind.BUSINESS, message, data);
    }

    public static Failure<Void> validation(String message) {
        return base(Kind.VALIDATION, message);
    }

    public static <T> Failure<T> validation(String message, T data) {
        return base(Kind.VALIDATION, message, data);
    }

    public static Failure<Void> technical(String message) {
        return base(Kind.TECHNICAL, message);
    }

    public static <T> Failure<T> technical(String message, T data) {
        return base(Kind.TECHNICAL, message, data);
    }

    public static Failure<Void> businessCausedBy(String message, Throwable cause) {
        return base(Kind.BUSINESS, message).withCause(cause);
    }

    public static Failure<Void> validationCausedBy(String message, Throwable cause) {
        return base(Kind.VALIDATION, message).withCause(cause);
    }

    public static Failure<Void> technicalCausedBy(String message, Throwable cause) {
        return base(Kind.TECHNICAL, message).withCause(cause);
    }

    private static Failure<Void> base(Kind kind, String message) {
        return new Failure<>(
            kind,
            message,
            null,
            null,
            null,
            Map.of(),
            List.of(),
            null,
            null
        );
    }

    private static <T> Failure<T> base(Kind kind, String message, T data) {
        return new Failure<>(
            kind,
            message,
            null,
            null,
            null,
            Map.of(),
            List.of(),
            data,
            null
        );
    }

    // ----------------------------
    // Withers
    // ----------------------------

    public Failure<T> withReason(String reason) {
        return new Failure<>(kind, message, reason, code, i18nKey, i18nArgs, details, data, cause);
    }

    public Failure<T> withCode(String code) {
        return new Failure<>(kind, message, reason, code, i18nKey, i18nArgs, details, data, cause);
    }

    public Failure<T> withI18n(String i18nKey, Map<String, Object> args) {
        return new Failure<>(kind, message, reason, code, i18nKey, args, details, data, cause);
    }

    public Failure<T> withDetails(List<Failure<?>> details) {
        return new Failure<>(kind, message, reason, code, i18nKey, i18nArgs, details, data, cause);
    }

    public <NewType> Failure<NewType> withData(NewType data) {
        return new Failure<>(kind, message, reason, code, i18nKey, i18nArgs, details, data, cause);
    }

    public Failure<T> withCause(Throwable cause) {
        return new Failure<>(kind, message, reason, code, i18nKey, i18nArgs, details, data, cause);
    }

    public boolean hasCause() {
        return cause != null;
    }

    // ----------------------------
    // Protection against accidental leakage
    // (record default toString/equals/hashCode include all components!)
    // ----------------------------

    @Override
    public String toString() {
        // Do NOT print cause stacktrace / message here.
        return
            "Failure[kind=%s, message=%s, reason=%s, code=%s, i18nKey=%s, i18nArgs=%s, details=%s, data=%s, cause=%s]"
                .formatted(
                    kind,
                    message,
                    reason,
                    code,
                    i18nKey,
                    i18nArgs,
                    details,
                    data,
                    cause == null ? "null" : cause.getClass().getName()
                );
    }

    @Override
    public boolean equals(Object o) {
        // Exclude cause from equality semantics
        if (this == o) {
            return true;
        }
        if (!(o instanceof Failure<?> other)) {
            return false;
        }
        return kind == other.kind
            && Objects.equals(message, other.message)
            && Objects.equals(reason, other.reason)
            && Objects.equals(code, other.code)
            && Objects.equals(i18nKey, other.i18nKey)
            && Objects.equals(i18nArgs, other.i18nArgs)
            && Objects.equals(details, other.details)
            && Objects.equals(data, other.data);
    }

    @Override
    public int hashCode() {
        // Exclude cause from hash semantics
        return Objects.hash(
            kind,
            message,
            reason,
            code,
            i18nKey,
            i18nArgs,
            details,
            data
        );
    }
}

