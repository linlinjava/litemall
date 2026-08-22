# Test Pyramid

Use this reference when the planned evidence is concentrated in expensive or
brittle layers. It is a portfolio heuristic, not a mandatory quota.

| Layer group | Typical portfolio range | Best evidence |
| --- | ---: | --- |
| Unit | 60-75% | isolated rules, calculations, validation, state decisions |
| Integration and Contract | 20-30% | wiring, persistence, transactions, boundaries, compatibility |
| E2E | 5-10% | a small set of critical user journeys |

Prefer the lowest layer that can credibly observe the approved oracle. Add a
higher layer only when it proves a distinct property. Risk, architecture, and
failure cost override the illustrative percentages.
