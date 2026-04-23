# Optimal Truck Load Planner API 🚚

A production-quality REST API to solve the complex combinatorial optimization of truck route loading. Utilizing a Dynamic Programming algorithm paired with Bitwise masks, this engine evaluates massive subsets of shipping combinations and instantly caches results to maximize payout while strictly respecting capacity constraints.

---

## 🛠️ Tech Stack 
- **Java 21**: High-performance backend engine utilizing modern language features.
- **Spring Boot 3.x**: Primary application framework handling DI, routing, and environment configurations.
- **Spring Cache + Redis**: Used for lightning-fast identical-request memoization, completely bypassing re-computation.
- **Jackson**: Payload JSON serialization to and from Java DTO entities.
- **Docker & Docker Compose**: Unified local containerized deployment (Multi-stage maven builder targeting Alpine JRE).
- **JUnit 5**: Comprehensive test-cases verifying speed, edge cases, and DP validation.

---

## 🚀 Features
- **In-Memory Core Calculation**: Deep optimizations removing the need for relational DB lookups.
- **Robust Rule Validations**: Prevents mixing Hazmat and Non-Hazmat statuses, demands identical Origins and Destinations, and validates payload caps returning `HTTP 413` safely on overflow checks.
- **Return Identical-Payout Solutions**: Evaluates secondary subsets capturing _all mathematically equivalent ways_ to pack a truck for maximum identical dollar payouts.
- **Detailed Utilization Metrics**: Percentage capacities computed for both cubic volume boundaries and precise weight tolerances.

---

## 🧠 Algorithm & Approach
The backend effectively avoids exponential disaster `O(2^n)` computational freezes by strictly utilizing:
1. **Iterative Dynamic Programming (1D Array Tracking)**: Instead of exploring a DFS tree of nodes, we use primitive mapped arrays scaling from $1$ to $(2^n) - 1$. Lookups fetch payload totals in instant `O(1)` memory access.
2. **Bitmasking Subsets**: Every subset configuration of truck orders mapping precisely to an integer bit layout (e.g. `0101` means picking order 1 and 3). 
3. **Precomputed Compatibility Matrices**: We pre-bake heavy time-overlap logic testing into bit arrays *prior* to iteration. Finding if an order conflicts takes just 1 hardware cycle: `(prevMask & ~compatMask[lastBit]) != 0`.
4. **Instant Early-Pruning**: If an order blows out the truck's weight or volume constraint, the tree is mathematically pruned skipping expensive checks for heavier paths organically down the iteration.

---

## ⚡ Redis Caching Implementation
Identical inputs should never be recalculated natively.
- **Strategic Hashing**: A custom `OptimizationKeyGenerator` digests incoming JSON payloads regardless of list-ordering.
- **Memoization**: Redis caches the `OptimizationResponse` payload locally on port `6379`. Subsequent API hits with identical truck limits, order variables, and subsets yield `0ms` calculations. 

---

## 💻 How To Run

### 1. Boot up the Service
Requirements: **Docker & Docker Compose** installed.

Clone the repository and launch the container suite:
```bash
git clone <your-repo>
cd <folder>
docker compose up --build
```
> Wait until you see `Started TruckPlannerApplication in X seconds`.

### 2. Make a Test Request
You can target `POST http://localhost:8080/api/v1/load-optimizer/optimize`. 

Here is an example `cURL` request querying three orders (Make sure to run this in Git Bash/Mac/Linux, or replace `\` with `^` on Windows `cmd`):

```bash
curl -X POST http://localhost:8080/api/v1/load-optimizer/optimize \
-H "Content-Type: application/json" \
-d @sample-request.json
```

*(Note: API validates simple local dates strictly via `"2025-12-05"` layouts without requiring heavy timestamp assumptions!)*