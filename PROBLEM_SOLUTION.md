# Module Boundary Patterns — Problem & Solution

---

## Problem 1 — Other modules cannot access `set`

`UserContextImpl` lives in the App module and has a `setUserId()` method.
Other modules need to *get* the userId — but if they depend on the full
`UserContextImpl`, they also get access to `set`, which they should never call.

You might think: *"just make `set` private"* — but then the App module itself
can't call it either. And making it `public` exposes it to everyone.

> Access modifiers are binary. They cannot say "visible to App, hidden from
> consumers." That is an architectural problem, not a visibility problem.

### Solution — Dependency Inversion (SOLID)

Split the contract by role. Same object, two interfaces.

| Interface | Module | Exposes |
|---|---|---|
| `CurrentUserContext` | Shared module | `getId()` only |
| `UserContext` | App module | `set` + `get` |
| `UserContextImpl` | App module | implements **both** |

```java
public class UserContextImpl implements CurrentUserContext, UserContext {
    // ...
}
```

- **App module** injects `UserContext` → can read and write
- **Consumer modules** get injected with `CurrentUserContext` → can only read

`set` still exists and is still `public` — but consumers never see it because
the interface they depend on simply does not declare it.

> The real control comes from **which interface you expose to each module**,
> not from `public` or `private`.

### DDD Context Map

The supplier (App module) has the upper hand, so this is a **Conformist** relation
between App and other modules — consumers have no power to change the contract,
they simply conform to whatever the App module exposes.

---

## Problem 2 — A module cannot see the domain of another module

Modules are isolated — Feed cannot directly access the domain of userAdmin.
However Feed needs the post creator profile, and sometimes it does not have it
in its own store, so it must ask the userAdmin module.

The question is: who determines *when* this data is needed and *what format* it
should come in?

- **Who determines the time?** → Feed (the consumer)
- **Who provides it?** → userAdmin (the provider)

```
Feed (consumer) -D-------U- userAdmin (provider)
```

The consumer has the upper hand — so **Feed owns the DTO** and defines exactly
what it needs. It exposes that contract through a port interface so the provider
knows what it must deliver and in what format.

### Solution — Outbound Port & Adapter (Hexagonal Architecture)

| Role | Name | Responsibility |
|---|---|---|
| Consumer | `Feed` | owns the DTO, defines the need |
| Port | `FeedCreatorPort` | interface declaring `getCreatorProfile()` |
| Outbound Adapter | `FeedCreatorPortImpl` | lives in userAdmin, implements the port |
| Provider | `userAdmin` | fulfills the contract defined by Feed |

> Called **outbound adapter** because Feed is the one *asking* for a service —
> the flow originates from Feed going *out* toward userAdmin.

```java
// Feed module owns this — defines what it needs
public interface FeedCreatorPort {
    Optional<FeedCreatorDto> getCreatorProfile(UUID userId);
}

// userAdmin module implements it — conforms to Feed's contract
public class FeedCreatorPortImpl implements FeedCreatorPort {
    // ...
}
```

Feed depends only on its own port interface — it never imports anything from
userAdmin. userAdmin is the one that reaches across and implements Feed's port.

> The dependency arrow points toward the consumer, not the provider.
> This is Dependency Inversion applied at the module boundary level.

### DDD Context Map

The consumer (Feed) has the upper hand, so this is a **Customer–Supplier** relation
where the customer drives the contract — the opposite power dynamic from Problem 1.
Feed is the **Upstream** in terms of contract ownership, even though it is the
**Downstream** in terms of data flow.

---

## Problem 3 — A module cannot push async updates into another module's logic

Video and asset statuses for a post are updated asynchronously, each on its own
thread. MediaIngestion is the one that knows when an asset is ready — but it
cannot directly access the logic layer of Feed.

The key difference from Problem 2 is **who owns the flow**:

- **Problem 2** → Feed asks for data. Feed owns the flow. → Outbound adapter lives in the provider.
- **Problem 3** → MediaIngestion pushes data. MediaIngestion owns the flow. Feed does not ask, it just gets told.

So Feed does not determine the time — MediaIngestion does.

```
MediaIngestion (provider) -D-------U- Feed (consumer)
```

But Feed still cannot let MediaIngestion touch its logic layer directly —
so a port is created to protect that boundary.

### Solution — Inbound Port & Adapter (Hexagonal Architecture)

| Role | Name | Responsibility |
|---|---|---|
| Port | `FeedAssetUploadedPort` | interface owned by Feed, declares the update contract |
| Inbound Adapter | `FeedAssetUploadedPortImpl` | lives in Feed, implements the port |
| Provider | `MediaIngestion` | owns the flow, calls the port when asset is ready |

> Called **inbound adapter** because the flow comes *into* Feed from the outside —
> Feed is not asking, it is being told. The adapter sits at the entry point of Feed.

```java
// Feed module owns this — defines how it accepts incoming updates
public interface FeedAssetUploadedPort {
    void assetUploaded(UUID postId, String assetUrl, String assetType);
}

// Feed module implements it — the logic lives inside Feed
public class FeedAssetUploadedPortImpl implements FeedAssetUploadedPort {
    // ...
}
```

MediaIngestion depends only on the port interface — it never imports Feed's
internal logic. Feed controls how the update is handled; MediaIngestion only
triggers it.

> The implementation is in Feed because Feed was told, not because Feed asked.
> Whoever owns the flow determines where the adapter lives.

### DDD Context Map

MediaIngestion has the upper hand over the timing, so this is a **Conformist**
relation — Feed conforms to the event that MediaIngestion fires. However Feed
still protects its logic layer through the port, so the boundary is clean.

---

> ## Conclusion 1 — How to determine which design to use
>
> Ask one question:
>
> **Who owns the flow?**
>
> | Answer | Adapter lives in | Design |
> |---|---|---|
> | Consumer asks → consumer owns the flow | the **provider** module | Outbound Port & Adapter |
> | Provider pushes → provider owns the flow | the **consumer** module | Inbound Port & Adapter |
