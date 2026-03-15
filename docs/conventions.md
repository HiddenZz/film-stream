# Conventions

## Immutable Objects

Prefer immutable objects wherever possible.

- Use `record` for events, DTOs, and value objects passed between layers.
- Use Lombok `@Value` for immutable classes that need inheritance or custom logic incompatible with records.
- For database entities (MyBatis), use a mutable `@Data` class — MyBatis requires a no-arg constructor and setters for result mapping.

Keep database entities internal to the repository layer. Expose data to other layers via immutable records/DTOs.
Map between them with a dedicated mapper (MapStruct or manual) when needed.