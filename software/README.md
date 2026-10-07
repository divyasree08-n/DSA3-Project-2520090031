<!-- markdownlint-disable MD033 MD060 -->
# Resume-Job Matching & Talent Marketplace Engine

<div align="center">

![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9.12-C71A36?logo=apachemaven&logoColor=white)
![Build](https://img.shields.io/badge/Build-passing-brightgreen)

</div>

> Academic DSA-3 project for resume search, candidate ranking, and recruitment workflow demonstrations.

## Table of Contents

- [Project Overview](#project-overview)
- [Project Details](#project-details)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Two Entry Points](#two-entry-points)
- [Project Structure](#project-structure)
- [How to Run](#how-to-run)
- [Interactive Demo Menu](#interactive-demo-menu)
- [Course Outcome Mapping](#course-outcome-mapping)
- [Sample Run Output](#sample-run-output)
- [Algorithm Complexity](#algorithm-complexity)
- [Design Decisions](#design-decisions)
- [Sample Data](#sample-data)
- [Known Limitations](#known-limitations)
- [Future Enhancements](#future-enhancements)
- [References](#references)
- [Team & Guide](#team--guide)
- [License](#license)

## Project Overview

The **Resume-Job Matching & Talent Marketplace Engine** is a Java-based project that demonstrates how core data structures and algorithms can solve practical recruitment problems. The application performs:

- exact keyword search on resumes
- multi-pattern document matching
- fuzzy text matching and similarity scoring
- assignment optimization for candidate-job pairing
- greedy scheduling of interviews
- prime checking for hashing-based operations
- randomized and parallel algorithm demonstrations

This project is designed for the **Data Structures and Algorithms - 3** course and is intended as an academic prototype for learning, grading, and viva/demo purposes—not as a production hiring system.

## Project Details

| Item | Details |
|---|---|
| Project Title | Resume-Job Matching & Talent Marketplace Engine |
| Course | Data Structures and Algorithms - 3 (25CS2103E) |
| Academic Year | 2026-2027, Odd Semester |
| Institution | KLH (Deemed to be University), Bachupally, Hyderabad |
| Team Number | 1 |
| Section | 12 |
| Guide | Ms. Chandusha Kanda, Assistant Professor, CS&IT |

### Team Members

| Name | Student ID |
|---|---|
| N. Divya Sree | 2520090031 |
| S. Vennela | 2520080018 |

## Tech Stack

- **Language:** Java (compiled with `--release 25`, and also compatible with JDK 17+)
- **Build Tool:** Apache Maven 3.9.12
- **Maven Dependencies (only 3 allowed by syllabus):**
  - `org.apache.commons:commons-text:1.11.0`
  - `org.apache.commons:commons-lang3:3.14.0`
  - `org.jgrapht:jgrapht-core:1.5.2`
- **No Spring Boot**
- **No Lucene**
- **No external DSA library**
- **All DSA algorithms are hand-coded in Java**
- **IDE:** VS Code / IntelliJ IDEA
- **OS:** Windows 11 (also works on Linux and macOS)

## Features

- Exact keyword matching using **KMP**, **Rabin-Karp**, and **Z-function**
- Multi-pattern matching using **Aho-Corasick**
- Suffix-based document similarity and substring analysis using **Suffix Array** and **Kasai LCP**
- Fuzzy text comparisons using **Edit Distance**, **Needleman-Wunsch**, and **Smith-Waterman**
- Candidate-to-job assignment using **Hungarian Algorithm** and **Max Flow**
- Interview scheduling using **Greedy Interval Scheduling**
- Prime checking using **Miller-Rabin**
- Randomized sampling using **Reservoir Sampling**
- Parallel prefix sum using **Hillis-Steele Prefix Sum**
- Problem classification and algorithm recommendation through `ProblemClassifier`
- Weighted resume-job ranking through `MatchingEngine`
- Console-based demo for academic evaluation, viva, and classroom presentation

## Two Entry Points

This project provides **two runnable entry points**, both of which are important for different demo scenarios.

### 1. `Main.java` — Automated Demo

- Runs all **6 course outcomes (CO1–CO6)** sequentially and the final matching demo
- Best for: **grading, screenshots, automated demonstrations, project submission**
- Command:

```bash
mvn exec:java -Dexec.mainClass="com.klh.dsa.Main"
```

### 2. `Demo.java` — Interactive CLI Demo

- Provides a **menu-driven interface** with 8 options
- Allows user input for queries, strings, matrices, and candidate/job examples
- Best for: **live presentation, viva, classroom discussion, interactive testing**
- Command:

```bash
mvn exec:java -Dexec.mainClass="com.klh.dsa.Demo"
```

Both entry points are documented in the **How to Run** section below and can be used depending on the purpose of the demonstration.

## Project Structure

```text
software/
├── pom.xml
├── README.md
├── run-demo.bat                          # optional one-click launcher for Windows
└── src/
    ├── main/
    │   ├── java/com/klh/dsa/
    │   │   ├── Main.java                   # automated demo for all 6 COs
    │   │   ├── Demo.java                   # interactive CLI demo for presentation
    │   │   ├── algorithms/
    │   │   │   ├── KMP.java                 # Knuth-Morris-Pratt pattern matching
    │   │   │   ├── ZFunction.java           # Z-function matching
    │   │   │   ├── RabinKarp.java           # Rabin-Karp substring search
    │   │   │   ├── AhoCorasick.java         # multi-pattern matching
    │   │   │   ├── Trie.java                # prefix tree and term search
    │   │   │   ├── SuffixArray.java         # suffix-array + substring logic
    │   │   │   ├── EditDistance.java        # Levenshtein distance
    │   │   │   ├── SequenceAlignment.java   # Needleman-Wunsch and Smith-Waterman
    │   │   │   ├── IntervalDP.java          # matrix-chain and interval DP examples
    │   │   │   ├── BitmaskTSP.java          # bitmask TSP optimization
    │   │   │   ├── MaxFlow.java             # max-flow / Ford-Fulkerson demo
    │   │   │   ├── Dinic.java               # Dinic max-flow algorithm
    │   │   │   ├── HungarianAlgorithm.java  # assignment optimization
    │   │   │   ├── VertexCoverApprox.java   # 2-approximation for vertex cover
    │   │   │   ├── KnapsackDP.java          # 0/1 knapsack optimization
    │   │   │   ├── SchedulingApprox.java    # greedy interval scheduling
    │   │   │   ├── MillerRabin.java         # probabilistic primality testing
    │   │   │   ├── ReservoirSampling.java   # randomized sampling
    │   │   │   └── ParallelPrefixSum.java   # Hillis-Steele prefix sum
    │   │   ├── core/
    │   │   │   └── ProblemClassifier.java   # query classification and algorithm selection
    │   │   ├── model/
    │   │   │   ├── Candidate.java            # candidate data type
    │   │   │   ├── Job.java                  # job data type
    │   │   │   └── MatchResult.java          # scoring result for candidate-job pair
    │   │   ├── nlp/
    │   │   │   └── TextProcessor.java        # text normalization and skill extraction
    │   │   └── matcher/
    │   │       └── MatchingEngine.java        # ranking and score computation
    │   └── resources/
    │       └── sample-data/                  # sample input data and demo assets
    └── test/java/com/klh/dsa/
        └── (for tests and validation if added later)
```

## How to Run

### Prerequisites

- **JDK 17 or higher** (tested on JDK 25)
- **Apache Maven 3.8+**
- Confirm your environment with the following commands:

```bash
java -version
javac -version
mvn -version
```

All three commands should show compatible Java versions.

### Clone and Build

```bash
git clone <repository-url>
cd software
mvn clean compile
```

### Run Option A — Automated Demo

```bash
mvn exec:java -Dexec.mainClass="com.klh.dsa.Main"
```

This runs all 6 curriculum outcomes in sequence and prints the final candidate-job matching summary.

### Run Option B — Interactive Presentation Demo

```bash
mvn exec:java -Dexec.mainClass="com.klh.dsa.Demo"
```

This opens the interactive CLI menu. The user can choose from options **1–7**, or press **0** to exit.

### Run Option C — One-click on Windows

Double-click `run-demo.bat` in the `software/` folder.

### Additional Useful Commands

```bash
mvn test
mvn package
java -jar target/resume-job-matcher-1.0.0.jar
```

> For this project, `mvn exec:java` is the simplest and most direct method to run the demos.

## Interactive Demo Menu

The interactive CLI is menu-driven and supports the following options:

| Option | Purpose | Course Outcome | Sample Input |
|--------|---------|----------------|--------------|
| 1 | Classify a query and choose the most suitable algorithm | CO1 | "Find Java resumes" |
| 2 | Search a pattern in text using KMP / Rabin-Karp / Z-function | CO2 | text = "hello world", pattern = "world" |
| 3 | Compute edit distance and string similarity | CO3 | "python" vs "pyhton" |
| 4 | Solve optimal candidate-job assignment | CO4 | 3×3 compatibility matrix |
| 5 | Solve 0/1 knapsack for budget-aware hiring | CO5 | 4 items, capacity 5 |
| 6 | Test primality with Miller-Rabin | CO6 | 104729, k = 5 |
| 7 | Run the complete resume-to-job matching demo | All COs | 3 candidates, 3 jobs |
| 0 | Exit the application | — | — |

### Sample Menu Output

```text
============================================================
  Resume-Job Matching — Interactive Demo
  DSA-3 | 25CS2103E | Team 1, Section 12
============================================================

1  ->  CO1: Classify a query (which algorithm?)
2  ->  CO2: Search a pattern in text (KMP/RK/Z)
3  ->  CO3: Edit distance between two strings
4  ->  CO4: Optimal candidate-job assignment
5  ->  CO5: 0/1 Knapsack (budget-constrained hiring)
6  ->  CO6: Miller-Rabin primality test
7  ->  FULL: Match resumes to jobs
0  ->  Exit
```

### Example Run: Option 1 (CO1 Classification)

```text
Select an option: 1
Enter your query: Find Java resumes
Query           : Find Java resumes
Classified Type : EXACT_KEYWORD_SEARCH
Best Algorithm  : KMP
```

### Example Run: Option 2 (CO2 String Search)

```text
Select an option: 2
Enter text: hello world from java developers
Enter pattern: java
KMP first match       : 17
KMP time              : 13451 ns
Z-Function first match: 17
Z-Function time       : 11102 ns
Rabin-Karp matches    : [17]
Rabin-Karp time       : 21433 ns
Verdict: Pattern FOUND
```

### Example Run: Option 7 (Full Match Summary)

```text
Select an option: 7
CANDIDATE                JOB                              SCORE
────────────────────────────────────────────────────────────────────────────
Alice                    Java Backend Engineer            0.8750
Bob                      Data Scientist                   1.0000
Carol                    Frontend Developer               1.0000

Best match per candidate:
Alice -> Java Backend Engineer (87.50%)
Bob -> Data Scientist (100.00%)
Carol -> Frontend Developer (100.00%)
```

## Course Outcome Mapping

| CO | Module | Syllabus Topic | Algorithm(s) | Java File(s) | Demo Option |
|---|---|---|---|---|---|
| CO1 | Module 1: TextHack as a System | Problem classification and algorithm selection | Query classification via keyword matching | `core/ProblemClassifier.java` | 1 |
| CO2 | Module 2: String Algorithms | Linear-time string algorithms + suffix structures | Naive, KMP, Z-function, Rabin-Karp, Aho-Corasick, Suffix Array, Kasai LCP | `algorithms/KMP.java`, `algorithms/ZFunction.java`, `algorithms/RabinKarp.java`, `algorithms/AhoCorasick.java`, `algorithms/Trie.java`, `algorithms/SuffixArray.java` | 2 |
| CO3 | Module 3: Advanced Dynamic Programming | Advanced dynamic programming patterns | Edit Distance, Needleman-Wunsch, Smith-Waterman, Matrix Chain, Bitmask TSP | `algorithms/EditDistance.java`, `algorithms/SequenceAlignment.java`, `algorithms/IntervalDP.java`, `algorithms/BitmaskTSP.java` | 3 |
| CO4 | Module 4: Network Flow | Network flow and duality | Ford-Fulkerson, Edmonds-Karp, Dinic, Hungarian, Max-Flow Min-Cut | `algorithms/MaxFlow.java`, `algorithms/Dinic.java`, `algorithms/HungarianAlgorithm.java` | 4 |
| CO5 | Module 5: NP-Completeness and Approximation | NP-completeness and approximation | Vertex Cover 2-approximation, 0/1 Knapsack DP, Greedy Interval Scheduling | `algorithms/VertexCoverApprox.java`, `algorithms/KnapsackDP.java`, `algorithms/SchedulingApprox.java` | 5 |
| CO6 | Module 6: Randomized and Parallel Algorithms | Randomized and parallel computations | Miller-Rabin, Reservoir Sampling, Hillis-Steele Parallel Prefix Sum | `algorithms/MillerRabin.java`, `algorithms/ReservoirSampling.java`, `algorithms/ParallelPrefixSum.java` | 6 |
| Full Matching | Merged application demo | Resume-to-job scoring and matching | Weighted skill + keyword + similarity ranking | `matcher/MatchingEngine.java` | 7 |

## Sample Run Output

The following is a representative sample of the console output generated by `Main.java`:

```text
==========================================================
 Resume-Job Matching & Talent Marketplace Engine
DSA-3 | 25CS2103E | Java 25
==========================================================

--- CO1 - Problem Classification ---
Find exact Java keyword matches    -> EXACT_KEYWORD_SEARCH         : KMP
Fuzzy Python skill match           -> FUZZY_SKILL_MATCH             : Levenshtein Edit Distance
Search multiple resume patterns    -> MULTI_PATTERN_SEARCH         : Aho-Corasick
Compare document similarity        -> DOCUMENT_SIMILARITY          : Suffix Array + LCP
Assign candidates to jobs          -> CANDIDATE_JOB_ASSIGNMENT     : Hungarian Algorithm
Schedule interviews                -> INTERVIEW_SCHEDULING         : Earliest-Finish-Time Greedy
Test prime hash values             -> FAST_PRIME_HASH              : Miller-Rabin

--- CO2 - String Algorithms ---
KMP matches: [0, 31]
Z-function matches: [0, 31]
Rabin-Karp matches: [0, 31]
Aho-Corasick matches: {java=[0, 31], python=[9], developer=[16]}
Suffix array of "banana": [5, 3, 1, 0, 4, 2]
Longest common substring (banana/ananas): anana

--- CO3 - Dynamic Programming ---
Edit distance (kitten/sitting): 3
Edit similarity (Java/Java): 1.0
Needleman-Wunsch (ACGT/AGT): ...
Smith-Waterman (GGACGT/ACG): ...
Matrix-chain minimum cost: 26000
Bitmask TSP minimum cycle: 80

--- CO4 - Network Flow and Assignment ---
Edmonds-Karp max flow: 23
Dinic max flow: 23
Hungarian assignment (column per row): [1, 0, 2]

--- CO5 - NP Approximation and Greedy Algorithms ---
2-approximate vertex cover: ...
0/1 knapsack maximum value: 7
Maximum non-overlapping interviews: ...

--- CO6 - Randomized and Parallel Algorithms ---
Miller-Rabin: 104729 prime = true, 104730 prime = false
Reservoir sample of 3: [ ... ]
Sequential prefix sum: [1, 3, 6, 10, 15]
Hillis-Steele prefix sum: [1, 3, 6, 10, 15]
Prefix-sum benchmark (1000 elements): ... ns

--- Full Resume-to-Job Matching Demo ---
Bob -> Data Scientist (100.0%)
Carol -> Frontend Developer (100.0%)
Alice -> Java Backend Engineer (87.5%)
...

ALL 6 COURSE OUTCOMES DEMONSTRATED
```

> The exact values may vary by runtime and machine, but the structure and demonstration flow remain consistent with the source code.

## Algorithm Complexity

Let `n` and `m` be input lengths or item counts as appropriate; let `z` be the number of pattern occurrences, `σ` the alphabet size, `V` the number of vertices, `E` the number of edges, `W` the knapsack capacity, and `k` the number of Miller-Rabin rounds or sample choices.

| Algorithm | Time | Space |
|---|---:|---:|
| KMP | O(n + m) | O(m) |
| Z-Function | O(n + m) | O(n) |
| Rabin-Karp | O(n + m) average, O(nm) worst | O(1) |
| Aho-Corasick | O(n + m + z) | O(m·σ) |
| Trie | O(L) per operation | O(m·σ) |
| Suffix Array | O(n log²n) | O(n) |
| Kasai LCP | O(n) | O(n) |
| Edit Distance | O(m·n) | O(min(m,n)) |
| Needleman-Wunsch | O(m·n) | O(m·n) |
| Smith-Waterman | O(m·n) | O(m·n) |
| Matrix Chain | O(n³) | O(n²) |
| Bitmask TSP | O(2ⁿ·n²) | O(2ⁿ·n) |
| Edmonds-Karp | O(V·E²) | O(V²) |
| Dinic | O(V²·E) | O(V + E) |
| Hungarian | O(n³) | O(n²) |
| Vertex Cover 2-approx | O(V + E) | O(V) |
| 0/1 Knapsack | O(n·W) | O(W) |
| Interval Scheduling | O(n log n) | O(n) |
| Miller-Rabin | O(k log³n) | O(1) |
| Reservoir Sampling | O(n) | O(k) |
| Hillis-Steele Prefix Sum | O(n log n) | O(n) |

## Design Decisions

### Hand-coded Algorithms

The project implements the main algorithms directly in Java rather than relying on third-party data structure libraries. This helps students understand algorithm structure, state transitions, memory behavior, and runtime complexity in a way that aligns with course learning outcomes.

### Minimal Dependency Set

Only three Maven dependencies are used:

- `commons-text`
- `commons-lang3`
- `jgrapht-core`

This keeps the project lightweight, easy to compile, and consistent with academic requirements.

### Separation of Concerns

The project is divided into clear modules:

- `algorithms/` for reusable algorithm implementations
- `core/` for classification and strategy selection
- `model/` for candidate, job, and result objects
- `nlp/` for text normalization and extraction
- `matcher/` for ranking and matching logic
- `Main.java` for standard CO demonstrations
- `Demo.java` for interactive demonstration and viva support

### Weighted Matching Strategy

The matching engine computes a score using weighted similarity-based ranking. This is intended for educational demonstration and not as a final recruitment decision metric.

## Sample Data

The project uses small, fixed examples to make demonstrations easy to understand and repeat.

### Sample Candidate

```java
Candidate candidate = new Candidate("C1", "Alice",
        "Java, Spring Boot, SQL, REST APIs, Microservices");
```

### Sample Job

```java
Job job = new Job("J1", "Java Backend Engineer",
        "Java, Spring Boot, REST APIs, SQL, cloud deployment");
```

### Sample Match Output

```text
Alice -> Java Backend Engineer (87.50%)
Bob -> Data Scientist (100.00%)
Carol -> Frontend Developer (100.00%)
```

## Known Limitations

- No frontend or web interface is included yet
- Resume parsing is text-based and does not yet support PDF extraction
- Matching is based on heuristics and keyword similarity rather than trained ML/NLP models
- The app is best suited for academic demonstration and course evaluation
- It is not designed as a production hiring or screening platform

## Future Enhancements

- Add a REST API for candidate-job matching
- Build a recruiter dashboard and job seeker portal
- Parse PDF and DOCX resumes automatically
- Add embedding-based semantic matching
- Integrate database-backed job and candidate storage
- Support recruiter workflow features such as sorting, filters, and analytics
- Implement benchmarking tools for comparing algorithm performance

## References

1. Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, and Clifford Stein, *Introduction to Algorithms*, 4th Edition, MIT Press.
2. Jon Kleinberg and Éva Tardos, *Algorithm Design*, 1st Edition, Pearson.
3. Robert Sedgewick and Kevin Wayne, *Algorithms*, 4th Edition, Addison-Wesley.
4. R. K. Ahuja, T. L. Magnanti, and J. B. Orlin, *Network Flows: Theory, Algorithms, and Applications*, Prentice Hall.
5. Course lecture notes and lab material for **Data Structures and Algorithms - 3 (25CS2103E)**.

## Team & Guide

| Name | Role | Details |
|---|---|---|
| N. Divya Sree | Team Member | Student ID: 2520090031 |
| S. Vennela | Team Member | Student ID: 2520080018 |
| Ms. Chandusha Kanda | Guide | Assistant Professor, CS&IT |

## License

This repository is developed for academic learning, demonstration, and evaluation under **KLH (Deemed to be University)** for the **2026-2027 Odd Semester**. Unless otherwise stated, the project is intended for coursework and academic use only. Redistribution or reuse beyond academic purposes requires prior permission from the project team and guide.

---

<p align="center">
  <strong>Resume-Job Matching & Talent Marketplace Engine</strong><br>
  <em>Academic Project • DSA-3 • KLH • Team 1 • Section 12</em>
</p>
