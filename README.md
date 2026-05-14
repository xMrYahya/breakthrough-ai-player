# Breakthrough AI Player

A Java AI player for the board game Breakthrough using minimax search, alpha-beta pruning, iterative deepening, and deterministic move ordering.

## Overview

This repository contains a complete AI player implementation for the board game Breakthrough. The AI connects to an external game server via TCP sockets, receives board states, computes optimal moves using adversarial search algorithms, and communicates decisions back through a well-defined protocol.

The project demonstrates core game-AI and search algorithm engineering: board representation, legal move generation, minimax search with pruning, heuristic evaluation, time-budgeted iterative deepening, and deterministic move ordering for improved search efficiency.

**Note:** This is an AI engine/client implementation. The visual game executable and server are external dependencies, not included in this repository.

## Features

- **TCP Socket Client**: Connects to external Breakthrough game server over TCP (localhost:8888 by default)
- **Protocol-Driven Gameplay**: Implements text-based server protocol with command parsing and move encoding
- **Board Representation**: 8×8 board state with piece tracking and legal move generation
- **Adversarial Search**: Full minimax implementation with alpha-beta pruning
- **Iterative Deepening**: Time-budgeted progressive search (≈4.5 seconds per move) that maximizes search depth within time constraints
- **Deterministic Move Ordering**: Prioritizes winning moves, captures, and forward advancement to improve pruning efficiency
- **Heuristic Evaluation**: Material and advancement-based position scoring
- **Robust Error Handling**: Invalid-move recovery and graceful timeout management
- **Deterministic Behavior**: No randomness; identical positions always produce identical decisions

## Tech Stack

- **Language**: Java
- **Runtime**: Java Standard Library only
- **Networking**: `java.net.Socket` (TCP)
- **Algorithms**: 
  - Minimax
  - Alpha-Beta Pruning
  - Iterative Deepening
  - Deterministic Move Ordering
  - Heuristic Evaluation

## Game Rules (Breakthrough)

Breakthrough is a two-player board game:
- 8×8 board with pieces starting on opposite sides
- Pieces move one square forward into an empty square
- Pieces capture one square diagonally forward
- Pieces cannot move backward
- First player to reach the opposite side of the board wins

## AI Architecture

### Component Overview

```
Client.java
  └─> Socket communication & protocol handling
      └─> AiPlayer.java
          └─> Move selection & validation
              └─> MinimaxAlphaBeta.java
                  ├─> Iterative deepening search
                  ├─> Alpha-beta pruning
                  ├─> Move ordering
                  └─> Timeout management
                      ├─> MoveGenerator.java
                      ├─> EvaluationFunction.java
                      └─> Board.java
```

### Key Classes

| Class | Responsibility |
|-------|-----------------|
| `Client.java` | Socket lifecycle, protocol command parsing, game loop |
| `AiPlayer.java` | AI orchestration, move selection, legal move validation |
| `MinimaxAlphaBeta.java` | Search algorithm (minimax, alpha-beta, iterative deepening, move ordering) |
| `MoveGenerator.java` | Legal move enumeration for a given board state and player |
| `EvaluationFunction.java` | Heuristic board scoring (material + advancement) |
| `Board.java` | Board state representation, move application, win detection |
| `Move.java` | Move representation and server protocol encoding/decoding |
| `PlayerColor.java` | Player side constants and metadata |

## Search Strategy

### Minimax with Alpha-Beta Pruning

The AI uses minimax to explore the game tree, assuming both players play optimally:
- **Maximizing player**: scores each move, retains the highest
- **Minimizing player**: scores each move, retains the lowest
- **Alpha-beta pruning**: eliminates branches that cannot improve the outcome

### Iterative Deepening

Rather than searching to a fixed depth, the AI progressively deepens:

1. Search depth 1 → store best move
2. Search depth 2 → store best move
3. Search depth 3, 4, 5, ... (progressively deeper)
4. Stop when time budget expires (~4500 ms)
5. Return the best move from the deepest **fully completed** depth

**Advantage**: The AI uses the full available time budget instead of finishing instantly at a shallow depth. If the time budget expires during depth N, depth N-1's best move is returned, guaranteeing a safe, completed search result.

### Move Ordering

To maximize alpha-beta pruning efficiency, moves are ordered heuristically before exploration:

1. **Winning moves** (reaches opposite side): priority 1,000,000
2. **Capturing moves** (opponent piece on destination): priority 10,000
3. **Forward advancement** (toward goal): priority 100 + (distance × 10)
4. **Other legal moves**: priority 100

Strong moves are explored first, allowing weak branches to be pruned earlier and increasing effective search depth.

### Heuristic Evaluation

At leaf nodes (depth limit or terminal position), the board is scored:

```
Score = Material + Advancement

Material       = (own pieces - opponent pieces) × 100
Advancement    = Σ(piece distance to goal row) × 10

Terminal nodes:
  Own win      = +1,000,000
  Opponent win = -1,000,000
```

## Project Structure

```
breakthrough/
├── README.md
├── src/
│   ├── AiPlayer.java
│   ├── Board.java
│   ├── Client.java
│   ├── EvaluationFunction.java
│   ├── MinimaxAlphaBeta.java
│   ├── Move.java
│   ├── MoveGenerator.java
│   └── PlayerColor.java
├── screenshots/
│   └── gameplay-demo.gif
```

## Setup and Execution

### Requirements

- Java 8 or later
- External Breakthrough game server/executable (not included in this repository)
- TCP connectivity to the server (default: `localhost:8888`)

### Build

Compile all Java sources:

```bash
javac -d bin src/*.java
```

### Run

The AI is a client; it requires an external game server to be running first.

```bash
java -cp bin Client [server_address]
```

**Examples:**

```bash
java -cp bin Client                    # Connect to localhost:8888
java -cp bin Client 192.168.1.100      # Connect to 192.168.1.100:8888
```

### Protocol

The AI communicates with the server using a text-based protocol:

- **Command `1`**: Start as RED (white), receive initial board
- **Command `2`**: Start as BLACK, receive initial board
- **Command `3`**: Server requests move, includes opponent's move
- **Command `4`**: Invalid move; retry with alternative
- **Command `5`**: Game over

Board states are transmitted as 64 space-separated integers (row-major, 8×8).

Moves are encoded as square notation: `A1-B2` (source square to destination square).

**Note:** The external server must implement this exact protocol. This client is designed to connect only to compatible servers.

## Demo

![Gameplay Demo](./screenshots/gameplay-demo.gif)

## Limitations

- **External dependency**: The game executable/server is not included; the AI is a client only
- **No standalone mode**: The AI requires an external server; it cannot play against itself
- **No GUI**: This is a headless AI engine; graphical output is not provided
- **No transposition table**: Repeated board positions are re-evaluated (higher CPU usage)
- **No opening book**: No pre-computed opening moves
- **No quiescence search**: Leaf evaluation at depth limit may miss hanging pieces or threats
- **Simple heuristic**: Evaluation considers only material and advancement; does not account for mobility, threats, or positional structure
- **No adaptive time control**: The time budget is fixed; no adjustment based on position complexity
- **Synchronous search**: The AI blocks until search completes; no interruptibility

## Future Improvements

### Search Enhancements
- **Transposition table**: Cache evaluated positions to avoid redundant computation
- **Zobrist hashing**: Fast, incremental position hashing for transposition table keys
- **Quiescence search**: Deeper evaluation for positions with pending captures
- **Killer heuristic**: Track moves that caused cutoffs at sibling nodes

### Evaluation Improvements
- **Positional features**: Mobility, control, threats, promotion proximity
- **Piece activity**: Value pieces based on movement options
- **Pawn structure**: Recognize formations and blocking patterns

### System Improvements
- **Automated testing**: Unit tests for move generation, evaluation, search correctness
- **Build tooling**: Gradle or Maven for reproducible builds
- **Search statistics**: Instrumentation to measure depth, nodes, pruning rate, time usage
- **Parallel search**: Multi-threaded search for deeper exploration
- **Protocol extension**: Optional server protocol for search statistics and diagnostics

## Contributions

This project was implemented independently, including the AI search architecture, minimax and alpha-beta logic, iterative deepening system, heuristic evaluation function, move ordering strategy, and TCP-based game-server integration.
