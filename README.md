# Thupakki
### Educational Microcontroller Simulator with Process Scheduling

**Microcontroller:** Nuvoton MS51FB9AE  
**Programming Language:** Java  
**Project Type:** Problem-Based Learning (PBL)

---

## Team Details

**Team Name:** Thupakki

| Member | Role |
|---|---|
| Aaron Kenneth Dsouza | Team Lead |
| Aman Dsilva | Team Member |
| Alwisha Sweedal Tauro | Team Member |
| Georgie Shibu | Team Member |

## Project Objective

Build a simplified educational simulator for the Nuvoton MS51FB9AE microcontroller. The simulator demonstrates instruction execution, memory and peripheral operations, and process management, bringing together concepts from Microprocessor Architecture, Data Structures, and Operating Systems. It is designed to help users visualize how programs execute and how CPU scheduling manages multiple programs.

## Project Statement

Design and implement a software-based simulator for the assigned 8-bit microcontroller, the Nuvoton MS51FB9AE. The simulator models essential processor components and executes a defined subset of processor instructions. The planned system will manage program memory, data memory, and the stack; provide simplified GPIO, timer, and interrupt functionality; and support multiple programs as processes.

Process management will use data structures such as a Process Control Block (PCB) and a ready queue, along with context switching and the FCFS, Round Robin, and Priority scheduling algorithms.

## Project Scope

The project is being developed incrementally, with the following areas in scope:

- **CPU simulation:** Model the required CPU state, registers, program counter, flags, and instruction execution.
- **Instruction execution:** Support a defined subset of MS51 instructions using the FETCH → DECODE → EXECUTE sequence.
- **Memory:** Implement memory read/write operations and display relevant memory contents.
- **Stack:** Implement the Stack Pointer (SP), PUSH and POP operations, and stack visualization.
- **FIFO Queue:** Implement Enqueue and Dequeue operations, FIFO ordering, and full/empty conditions.
- **User interface:** Provide controls to load, reset, step through, and run a program, with visible execution state and trace.
- **Process management and scheduling:** Model processes using PCBs and a ready queue; implement context switching and FCFS, Round Robin, and Priority scheduling.
- **Peripherals:** Add simplified GPIO, timer, and interrupt functionality.
- **Multi-process architecture:** Separate the simulator into UI, Core, and Logging processes and enable inter-process communication (IPC).
- **Testing and documentation:** Validate components with test cases, processor-specific assembly programs, flowcharts, and documented results.

## Team Responsibilities

The team will divide implementation, testing, and integration responsibilities so that every member makes a meaningful technical contribution.

| Responsibility Area | Main Tasks |
|---|---|
| CPU and instruction execution | CPU state, registers, instruction representation, and execution cycle |
| Memory and stack | Memory read/write, Stack Pointer, PUSH/POP, and memory/stack display |
| Data structures and process management | FIFO queue, PCB, ready queue, and process-related functionality |
| Scheduling, UI, and integration | Scheduling algorithms, context switching, UI integration, and system-level testing |

Specific task ownership may be refined as the project progresses. Individual contributions and important decisions should be documented in the project repository.

## Selected Programming Language

**Java** has been selected for the project.

Reasons for selection:
- Java is part of the current semester's syllabus.
- Java supports the object-oriented design and data structures needed for the simulator.
- Its classes and standard libraries are suitable for modelling CPU components, memory, stacks, queues, and process-management components.

## System Architecture


## Development Plan

### Week 1 — Planning and Setup
- Form the team and assign responsibilities.
- Study the assigned microcontroller.
- Establish the repository and initial architecture.
- Select Java as the implementation language.

### Week 2 — First Working CPU Simulator
- Implement the basic CPU state, registers, program memory, and instruction representation.
- Develop the FETCH → DECODE → EXECUTE cycle.
- Add initial instruction support, UI controls, execution tracing, and a demonstration program.

### Week 3 — Memory, Stack, and FIFO Queue
- Integrate memory read/write functionality.
- Implement the Stack Pointer and PUSH/POP operations.
- Implement FIFO Queue Enqueue/Dequeue operations.
- Prepare a queue flowchart, assembly validation program, test cases, and updated documentation.

### Week 4 — Multi-Process Simulator and IPC
- Separate the simulator into UI, Core, and Logging processes.
- Select and implement a suitable POSIX IPC mechanism.
- Test inter-process communication.
- Compare standalone and multi-process execution and document performance results.


## Current Project Status

### Week 2 — CPU Simulator Foundation
The initial simulator foundation was planned around CPU state, registers, program memory, instruction representation, the FETCH → DECODE → EXECUTE cycle, UI controls, and execution tracing.

### Week 3 — Enhancing Simulator Functionality
The planned focus is memory read/write, stack operations, and a FIFO queue. The following items still require implementation or verification unless confirmed by the team:

- Memory integration and UI display
- Stack Pointer, PUSH, and POP
- FIFO Queue operations and status display
- Queue flowchart and assembly validation
- Memory, stack, and queue tests
- Updated design documentation and test results


## Testing and Validation

Testing will cover:
- Individual supported instructions and the demonstration program
- Memory read/write operations
- Stack PUSH/POP and Stack Pointer updates
- Queue Enqueue/Dequeue, FIFO ordering, and full/empty conditions
- Inter-process communication and component integration
- Performance comparison between standalone and multi-process versions

Expected results, actual results, and pass/fail status should be recorded for each test.

## Repository Documentation

Project documentation should include:
- Weekly status reports
- Meeting minutes and technical decisions
- Queue flowchart and assembly validation program
- Test cases and execution results
- IPC architecture and mechanism justification
- Benchmark results and performance analysis

---

## How to Run the Project

### Prerequisites

Ensure the following are installed on your system:

- **Java Development Kit (JDK):** Install a JDK compatible with the project.
- **Git:** Required to clone the repository.
- **IDE (Recommended):** IntelliJ IDEA, Eclipse, or Visual Studio Code with Java extensions.

Verify your Java installation:

```bash
java -version
javac -version
```

### 1. Clone the Repository

Open a terminal and run:

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd <YOUR_PROJECT_DIRECTORY>
```

Replace the placeholders with your repository URL and directory name.

### 2. Open the Project

Open the cloned project directory in your preferred Java IDE. Allow the IDE to detect and configure the Java project.

### 3. Build and Run

**If the project uses a standard Java source structure**, compile the source files and run the class containing the `main()` method.

For example:

```bash
javac -d out src/*.java
java -cp out Main
```

Replace `Main` with the actual entry-point class name. If the project uses packages, multiple source directories, JavaFX, or a build tool such as Maven or Gradle, use the corresponding build and run configuration instead.

Alternatively, open the entry-point class in your IDE and select **Run**.

### 4. Using the Simulator

Once the application starts:

1. Load a demonstration program.
2. Reset the simulator to initialize its state.
3. Use **Step** to execute one instruction at a time.
4. Observe the FETCH → DECODE → EXECUTE sequence and CPU state changes.
5. Use **Run** to execute the program until it terminates.
6. Inspect the execution trace and other available simulator displays.

Memory, Stack, and FIFO Queue controls will be available as those features are implemented and integrated.

### Troubleshooting

- **`java` or `javac` not found:** Install the JDK and configure your system's PATH.
- **Class not found:** Check the entry-point class name, package declaration, and classpath.
- **Compilation errors:** Verify the source paths and ensure all required dependencies are configured.
- **UI does not launch:** Check whether the project requires additional UI libraries or IDE configuration.

