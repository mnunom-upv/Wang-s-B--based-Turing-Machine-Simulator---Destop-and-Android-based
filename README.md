

---

TURING MACHINE VISUAL SIMULATOR 

This project contains both desktop application developed in Python using PyQt6 and a Android-based mobile app that allows users to design, validate, and execute Turing Machine programs in an interactive and visual way.

---

FEATURES

* Write Turing Machine programs using a simple custom language
* Automatic syntax and semantic validation
* Execute programs step-by-step or automatically
* Pause execution at any time
* Visual representation of tape cells and head position
* Highlighting of the currently executed line
* Import and export programs (.txt)
* Optional screenshots during execution

---

USER INTERFACE OVERVIEW

The application is divided into the following sections:

* Code Editor: Write your Turing Machine program
* Line Numbers Panel: Displays line numbers
* Control Buttons:
  Start: Load and validate program
  Next: Execute one step
  Continue: Resume execution
  Pause: Stop execution temporarily
* Input Field: Defines the initial tape content
* Visualization Area: Displays tape and head movement

---

HOW TO USE

1. Write a Program

Programs must follow a structured format using labels and instructions.

Example:

Start:
If Blank Write '1'
Move Right
Goto Start

---

2. Provide Input

Enter the initial tape content in the Input field (for example: 1011).

---

3. Run the Program

* Click Start to validate and begin execution
* Use Next for step-by-step execution
* Use Continue for automatic execution
* Use Pause to stop execution

---

SUPPORTED INSTRUCTIONS

Actions:

Write 'x'      -> Writes character x in the current cell
Write Blank    -> Writes an empty symbol
Move Right     -> Moves the head to the right
Move Left      -> Moves the head to the left
Goto Label     -> Jumps to a label
Return True    -> Ends execution successfully
Return False   -> Ends execution with failure

---

Conditionals:

If 'x' Write 'y'
If Not Blank Move Right

Supported:

* If
* If Not
* Conditions using characters or Blank

---

Labels:

Labels define positions in the program.

Example:

Start:
Loop:

Rules:

* Must start with uppercase
* Must end with colon (:)
* Must be unique

---

VALIDATION RULES

The system checks:

* Program must start with "Start:"
* Program must end with Return or Goto
* No unknown keywords allowed
* Labels must be declared before use
* No duplicate labels
* Correct instruction syntax required

Errors are:

* Highlighted in the editor
* Displayed in the error panel

---

VISUALIZATION

* Each tape cell is displayed as a box
* The current head position is indicated with an arrow
* Tape expands dynamically as needed
* Execution updates in real time

---

FILE OPERATIONS

* Open: Load a .txt program
* Export: Save current program
* Screenshots: Capture execution frames (optional)

---

REQUIREMENTS

* Python 3.x
* PyQt6

Install dependencies:

pip install PyQt6

---

RUN THE APPLICATION

python DesktopApp.py

---

EDUCATIONAL PURPOSE

This tool is useful for:

* Learning Turing Machines
* Understanding automata theory
* Visualizing algorithm execution
* Teaching formal languages

---

CREDITS

Marco Aurelio Nuño-Maganda


NOTES

* The simulator uses a custom instruction language
* Execution is deterministic and sequential
* Tape grows dynamically during execution

---

