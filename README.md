# Connect4game
Connect 4 game developed for DAA course using Dynamic Programming and Backtracking. Includes Hint and Undo features for better gameplay. AI evaluates board states to suggest optimal moves and improve decision making.
# Connect 4 Game (DAA Project)

##  Overview
This project is an implementation of the classic Connect 4 game developed as part of the Design and Analysis of Algorithms (DAA) course.  
The project focuses on applying algorithmic techniques to improve decision-making and game performance.



##  Objectives
- To implement a functional Connect 4 game
- To apply DAA concepts in a real-world problem
- To improve AI decision-making using algorithms
- To enhance user experience with additional features



## Features
-  Hint Button: Suggests the best possible move
-  Undo Button: Reverts the last move
-  Intelligent AI Player
-  Efficient board evaluation
-  Interactive gameplay



##  Algorithms Used

### 1. Backtracking
- Used to explore all possible moves
- Simulates future game states
- Helps in selecting the optimal move

### 2. Dynamic Programming
- Stores previously evaluated board states
- Avoids repeated computations
- Improves performance and efficiency



##  How the System Works

1. Player makes a move
2. Board updates accordingly
3. AI analyzes possible moves using backtracking
4. Dynamic programming avoids recalculating states
5. Best move is selected
6. Hint button suggests optimal move (optional)
7. Undo button allows reversing last move



## Time Complexity
- Worst Case: O(b^d)
  - b = branching factor (possible moves)
  - d = depth of search



##  Space Complexity
- O(n), where n is number of stored board states

---

##  Technologies Used
- Programming Language: (Python / Java)
- Data Structures: Arrays, Stack
- Concepts: DAA (Backtracking, Dynamic Programming)



##  Improvements from Previous Version

| Feature | Previous Version | Current Version |
|--------|----------------|----------------|
| AI Logic | Basic | Intelligent |
| Hint | Not Available | Added |
| Undo | Not Available | Added |
| Optimization | None | Dynamic Programming |
| Move Selection | Simple | Backtracking |



##  How to Run

1. Download or clone the repository
2. Open the project in your IDE
3. Run the main file
4. Start playing the game



##  Future Enhancements
- Minimax Algorithm
- Alpha-Beta Pruning
- GUI Improvements
- Multiplayer Mode



##  Author
Pradeep

##  Conclusion
This project demonstrates how algorithmic techniques like Dynamic Programming and Backtracking can be applied to real-world problems like games to improve efficiency and decision-making.
