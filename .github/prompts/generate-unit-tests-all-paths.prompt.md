---
agent: 'Code generation mode'
---
Generate or update unit and slice tests so all code paths are covered for this Spring Boot project.

Requirements:
- Use JUnit 5, Mockito, and Spring Boot test support (`@WebMvcTest`, `@DataJpaTest`, and focused service tests) as appropriate.
- Cover every controller, service, and repository method for all meaningful branches:
  - happy path/success responses
  - validation failures and bad input
  - not-found and exception paths
  - edge cases (empty lists, null handling where applicable, boundary values)
- Mock dependencies at layer boundaries and verify key interactions when behavior depends on them.
- Keep tests deterministic, isolated, and readable; avoid brittle timing or environment assumptions.
- Add or adjust test fixtures/data setup only as needed to exercise branches.

Constraints:
- Do not change production behavior unless a testability fix is strictly required; if required, keep it minimal and explain it.
- Prefer extending existing test classes before creating new ones unless separation materially improves clarity.
- Follow existing project conventions and package structure.

Success criteria:
- `mvn test` passes.
- New tests assert expected status codes, payloads, and exception handling for each path.
- Coverage includes success, failure, and edge branches for all exposed endpoints and core service logic.

