# problem

**problem** is a lightweight, opinionated Java library for treating failures as first-class problems.

Instead of relying solely on exceptions, it provides an immutable model to represent business, validation, and technical problems explicitly — while still preserving exception causes internally for diagnostics and observability, without leaking them outside the process.

---

## Why problem?

In many Java applications, failures are either:
- represented only as exceptions, or
- flattened into strings or error codes at system boundaries.

This makes it difficult to:
- reason about failures as domain concepts
- compose errors functionally
- preserve context and intent
- safely retain technical causes for debugging

**problem** addresses this by modeling failures as explicit, immutable objects.

> Failures are not exceptional — they are part of the domain.

---

## Core Concepts

### Problem
A `Problem` (or `Failure`) represents something that went wrong, with:
- an explicit **kind** (business, validation, technical, etc.)
- a human-readable message
- optional error codes and i18n metadata
- optional contextual data
- optional nested problems (details)
- an optional **Throwable cause**, preserved internally

### Problem Kind
Problems are explicitly classified:

```java
BUSINESS
VALIDATION
TECHNICAL
AUTHORIZATION
NOT_FOUND
CONFLICT
````

The kind expresses *intent*, not transport concerns (HTTP, messaging, etc.).

---

## Example: Business Problem

```java
var problem = Failure.business("Insufficient funds")
    .withCode("INSUFFICIENT_FUNDS")
    .withData(Map.of(
        "available", 50,
        "requested", 100
    ));
```

This represents a **business problem**, not an exception.

---

## Example: Validation Problems with Details

```java
var problem = Failure.validation("Invalid request")
    .withCode("VALIDATION_ERROR")
    .withDetails(List.of(
        Failure.validation("name is required")
            .withCode("FIELD_REQUIRED")
            .withData(Map.of("field", "name")),
        Failure.validation("amount must be positive")
            .withCode("INVALID_AMOUNT")
            .withData(Map.of("field", "amount"))
    ));
```

Validation errors are naturally modeled as **a problem with nested problems**.

---

## Example: Technical Problem Caused by an Exception

```java
try {
    repository.save(entity);
} catch (SQLException ex) {
    return Failure.technical("Database error")
        .withCode("DB_ERROR")
        .withCause(ex);
}
```

* The original `Throwable` is preserved
* It is **not serialized**
* It is **excluded from equals/hashCode**
* It is **not leaked in toString()**

This allows full diagnostics *inside the process* while keeping error models safe.

---

## Business Problems Caused by Exceptions

Sometimes a business rule is enforced imperatively and throws an exception.
`problem` allows preserving the cause **even for business problems**:

```java
try {
    domainService.withdraw(account, amount);
} catch (IllegalStateException ex) {
    return Failure.business("Withdrawal not allowed")
        .withCode("WITHDRAWAL_NOT_ALLOWED")
        .withCause(ex);
}
```

The problem remains a **business problem**, but the technical cause is still available for logging and tracing.

---

## Functional and Imperative Friendly

`problem` works equally well with:

* functional styles (`Result`, `Either`, railway-oriented programming)
* imperative styles (exceptions as control flow)

It does not force a single error-handling paradigm.

---

## Serialization Safety

* `Throwable` causes are never serialized
* Collections are immutable snapshots
* The model is safe to expose via APIs using dedicated DTOs

This prevents accidental leakage of stack traces or internal details.

---

## Design Principles

* Immutable by default
* Explicit over implicit
* Domain-driven
* Framework-agnostic
* Safe by construction

---

## What problem is NOT

* ❌ A replacement for exceptions
* ❌ A logging framework
* ❌ An HTTP-only error model

It is a **problem modeling library**, not a transport or infrastructure abstraction.

---

## Requirements

* Java 17+
* No framework dependencies

---

## License

Apache 2 License

