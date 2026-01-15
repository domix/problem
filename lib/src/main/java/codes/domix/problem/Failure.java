package codes.domix.problem;

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
    List<? extends Failure<?>> details,
    T data,
    Throwable cause
) {

    /**
     * Represents different types of classifications for an operation or error context.
     * This enum categorizes various functional and technical states that can occur
     * in the system or application. Each constant denotes a specific kind of category.
     */
    public enum Kind {
        /**
         * Denotes a classification related to business logic or operations.
         * This category is typically used to signify issues or states that
         * pertain to domain-specific rules or processes within the system.
         */
        BUSINESS,
        /**
         * Represents a classification related to data or input validation processes.
         * This category is typically used to signify issues, errors, or states that
         * involve validation checks, such as ensuring that required input values
         * conform to specified constraints or rules within the system.
         */
        VALIDATION,
        /**
         * Represents a classification related to technical issues or states within the system.
         * This category typically signifies errors, malfunctions, or other system-level
         * conditions that occur due to infrastructure, integration, configuration, or
         * runtime dependencies.
         */
        TECHNICAL,
        /**
         * Represents a classification related to authorization contexts.
         * This category is used to denote states, issues, or errors involving access control
         * and permissions within the system. It is typically associated with operations
         * or conditions where the system evaluates whether an entity (e.g., user, process)
         * has the necessary permissions to perform a specific action or access a resource.
         */
        AUTHORIZATION,
        /**
         * Represents a classification used to indicate that a particular resource,
         * entity, or expected result was not found within the system. This category
         * is typically utilized in contexts where an operation or request fails due
         * to the absence of the item being queried or referenced.
         */
        NOT_FOUND,
        /**
         * Represents a classification used to indicate a conflict state within the system.
         * This category is typically utilized in contexts where a requested operation or
         * action cannot be completed due to a conflict with the current state of a resource,
         * entity, or process. Conflicts commonly arise in scenarios such as version mismatches,
         * duplicate entries, or state transitions that violate predefined rules or constraints.
         */
        CONFLICT
    }

    /**
     * Constructs a new {@code Failure} instance with the specified parameters.
     *
     * @param kind     the type of the failure. Must not be {@code null}.
     * @param message  the failure message. Must not be {@code null}.
     * @param reason   the reason for the failure. Can be {@code null}.
     * @param code     an optional code representing the failure. Can be {@code null}.
     * @param i18nKey  an optional internationalization key for the failure. Can be {@code null}.
     * @param i18nArgs optional arguments for internationalization. If {@code null}, defaults to an empty map.
     * @param details  optional list of related failures. If {@code null}, defaults to an empty list.
     * @param data     optional data related to the failure. Can be {@code null}.
     * @param cause    the cause of the failure. Can be {@code null}.
     * @throws NullPointerException if {@code kind} or {@code message} is {@code null}.
     */
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

    /**
     * Creates a new {@code Failure} of kind {@code BUSINESS} with the specified message.
     *
     * @param message the failure message. Must not be {@code null}.
     * @return a new {@code Failure} instance of type {@code BUSINESS} with no associated data.
     * @throws NullPointerException if the {@code message} is {@code null}.
     */
    public static Failure<Void> business(String message) {
        return base(Kind.BUSINESS, message);
    }

    /**
     * Creates a new {@code Failure} of kind {@code BUSINESS} with the specified message and associated data.
     *
     * @param <T>     the type of the data associated with the failure.
     * @param message the failure message. Must not be {@code null}.
     * @param data    the data associated with the failure. Can be {@code null}.
     * @return a new {@code Failure} instance of type {@code BUSINESS} with the specified message and data.
     * @throws NullPointerException if {@code message} is {@code null}.
     */
    public static <T> Failure<T> business(String message, T data) {
        return base(Kind.BUSINESS, message)
            .withData(data);
    }

    /**
     * Creates a new {@code Failure} of kind {@code VALIDATION} with the specified message.
     *
     * @param message the failure message. Must not be {@code null}.
     * @return a new {@code Failure} instance of type {@code VALIDATION} with no associated data.
     * @throws NullPointerException if the {@code message} is {@code null}.
     */
    public static Failure<Void> validation(String message) {
        return base(Kind.VALIDATION, message);
    }

    /**
     * Creates a new {@code Failure} of kind {@code TECHNICAL} with the specified message.
     *
     * @param message the failure message. Must not be {@code null}.
     * @return a new {@code Failure} instance of type {@code TECHNICAL} with no associated data.
     * @throws NullPointerException if the {@code message} is {@code null}.
     */
    public static Failure<Void> technical(String message) {
        return base(Kind.TECHNICAL, message);
    }

    /**
     * Creates a new {@code Failure} of kind {@code BUSINESS} with the specified message and cause.
     *
     * @param message the failure message. Must not be {@code null}.
     * @param cause   the cause of the failure. Can be {@code null}.
     * @return a new {@code Failure} instance of type {@code BUSINESS} with the specified message and cause.
     * @throws NullPointerException if the {@code message} is {@code null}.
     */
    public static Failure<Void> businessCausedBy(String message, Throwable cause) {
        return base(Kind.BUSINESS, message)
            .withCause(cause);
    }

    /**
     * Creates a new {@code Failure} of kind {@code TECHNICAL} with the specified message and cause.
     *
     * @param message the failure message. Must not be {@code null}.
     * @param cause   the cause of the failure. Can be {@code null}.
     * @return a new {@code Failure} instance of type {@code TECHNICAL} with the specified message and cause.
     * @throws NullPointerException if the {@code message} is {@code null}.
     */
    public static Failure<Void> technicalCausedBy(String message, Throwable cause) {
        return base(Kind.TECHNICAL, message)
            .withCause(cause);
    }

    /**
     * Creates a new {@code Failure} with the specified kind and message, and no additional details or data.
     *
     * @param kind    the type of failure. Must not be {@code null}.
     * @param message the failure message. Must not be {@code null}.
     * @return a new {@code Failure} instance of the specified kind with the provided message.
     * @throws NullPointerException if {@code kind} or {@code message} is {@code null}.
     */
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

    // ----------------------------
    // Withers
    // ----------------------------

    /**
     * Associates a specified reason with the current {@code Failure} instance and returns a new instance
     * containing the updated reason.
     *
     * @param reason the reason for the failure. Can be {@code null}.
     * @return a new {@code Failure} instance with the specified reason.
     */
    public Failure<T> withReason(String reason) {
        return new Failure<>(kind, message, reason, code, i18nKey, i18nArgs, details, data, cause);
    }

    /**
     * Creates a new {@code Failure} instance with the specified error code.
     *
     * @param code the error code to associate with the failure
     * @return a new {@code Failure} instance with the provided code
     */
    public Failure<T> withCode(String code) {
        return new Failure<>(kind, message, reason, code, i18nKey, i18nArgs, details, data, cause);
    }

    /**
     * Adds internationalization support to the failure instance by setting an i18n key
     * and its associated arguments.
     *
     * @param i18nKey the key representing the internationalized message
     * @param args    a map of arguments to be used for localization placeholders in the message
     * @return a new Failure instance with the provided i18n key and arguments
     */
    public Failure<T> withI18n(String i18nKey, Map<String, Object> args) {
        return new Failure<>(kind, message, reason, code, i18nKey, args, details, data, cause);
    }

    /**
     * Associates an internationalization (i18n) key with the current Failure instance
     * to support localized messaging or error representation.
     *
     * @param i18nKey the internationalization key to be associated with this Failure instance
     * @return a new Failure instance with the specified i18n key
     */
    public Failure<T> withI18n(String i18nKey) {
        return new Failure<>(kind, message, reason, code, i18nKey, Map.of(), details, data, cause);
    }

    /**
     * Adds additional failure details to the current failure instance and returns a new failure object.
     *
     * @param details a list of additional failure details to associate with the current failure instance
     * @return a new Failure object populated with the provided details and existing failure information
     */
    public Failure<T> withDetails(List<? extends Failure<?>> details) {
        return new Failure<>(kind, message, reason, code, i18nKey, i18nArgs, details, data, cause);
    }

    /**
     * Creates a new Failure instance with the specified data while retaining all other properties.
     *
     * @param data      the new data to associate with the Failure instance
     * @param <NewType> the type of the new data
     * @return a new Failure instance with the updated data
     */
    public <NewType> Failure<NewType> withData(NewType data) {
        return new Failure<>(kind, message, reason, code, i18nKey, i18nArgs, details, data, cause);
    }

    /**
     * Associates a cause with this failure instance and returns a new Failure object
     * containing the specified cause alongside the existing failure details.
     *
     * @param cause the throwable representing the cause of the failure
     * @return a new Failure instance with the specified cause
     */
    public Failure<T> withCause(Throwable cause) {
        return new Failure<>(kind, message, reason, code, i18nKey, i18nArgs, details, data, cause);
    }

    /**
     * Checks whether this instance has an associated cause.
     *
     * @return true if a cause is present, otherwise false.
     */
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

