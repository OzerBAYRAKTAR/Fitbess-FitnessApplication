# Architecture decisions

- Separate UI rendering, state coordination, business decisions and external data access. Make ownership and dependency direction visible.
- Use Clean Architecture dependency inversion where it protects business logic from infrastructure. Domain contracts must not depend on Android, UI or database implementations.
- Keep reusable domain logic platform-independent when practical. Do not invent a domain layer solely to satisfy a directory diagram.
- Add a use case when it encapsulates substantial business policy, coordinates operations, or removes meaningful duplication. A one-line forwarding class is not automatically useful.
- Add interfaces for a real boundary, substitution or test need. Do not create an interface and implementation for every class.
- Separate network/database models from domain/UI models when their contracts differ. Avoid mechanical copies and mappers when the same representation is sufficient.
- Prefer constructor dependency injection and explicit ownership. Follow the project's existing DI approach unless a change is justified.
- Organize by coherent responsibility; features may contain internal layers. Package boundaries can be enough before separate build modules are warranted.
- Add modules for concrete ownership, reuse, isolation or build benefits. Account for configuration and maintenance cost.
- Preserve a coherent existing architecture during a focused change. Propose broad restructuring separately when it is necessary.
- For a significant architecture decision, briefly record the problem, chosen approach, simpler alternative and tradeoff. Do not require a formal decision document for routine edits.

Google's Android UI/data/domain guidance and strict Clean Architecture are not identical dependency models. Choose and document the project's model; do not silently combine incompatible directions.
