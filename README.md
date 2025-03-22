# **MRTASim: Multi-Robot Task Allocation Simulation**

MRTASim is an agent-based multi-robot task allocation simulation software designed to experiment with different MRTA methodologies.

It supports various task allocation strategies, path planning techniques, and auction-based bidding mechanisms for robots.

---

## **Features**

- Multi-robot coordination with centralized, distributed, and hybrid task allocation.
- Path planning methods including D*-Lite, RRT, and upcoming reinforcement learning techniques.
- Market-based bidding mechanisms using Euclidean distance, TSP-based cost evaluation, and fuzzy logic.
- Graphical User Interface for visualization and manual interaction.
- Automated Monte Carlo simulations for robust performance evaluation.

---

## **Installation**

### **Prerequisites**
Before installing MRTASim, ensure that you have the following dependencies installed:

- **Java Development Kit (JDK) 8 or later**  
  [Download](https://www.oracle.com/tr/java/technologies/downloads/)

- **Eclipse IDE (Recommended for development)**  
  [Download](https://eclipseide.org/)

- **JADE (Java Agent Development Environment)**  
  [Download](https://jade-project.gitlab.io/page/download/)

- **jFuzzyLogic library**  
  [Download](https://jfuzzylogic.sourceforge.net/html/index.html)

- **eJade plugin for Eclipse**  
  [Download](http://dit.unitn.it/~dnguyen/ejade/index.html)

### **Installation Steps**

1. Clone the repository.
2. Download dependencies.
3. Set CLASSPATH with the project and its dependencies.
4. *(Optional - Recommended)* Open the project in Eclipse and configure dependencies:
   - Add JADE, jFuzzyLogic, and eJade libraries to the Build Path.
   - Ensure all dependencies are correctly linked in the project settings.
5. Create a folder for log files.
6. Create a text file called **MRTAFolder.txt** and copy this file to `C:\`.
   - The content should be in this format:  
     `<Repository main folder>_<Project name>_<Log files folder>`

   ![image](https://github.com/user-attachments/assets/2a2f86e6-2d18-4c48-bd5c-24d0ecfae6c1)

Figure 1. Example content for "C:\MRTAFolder.txt"

8. Compile and run the software.

---

## **Usage**

### **Running the Simulation**

MRTASim can be executed via the command line or within Eclipse:

#### **a) Using Eclipse and eJADE (Recommended)**
1. Start Eclipse.
2. Start eJADE from the toolbar as shown below:

   ![image](https://github.com/user-attachments/assets/3d57fb2e-da2b-46ff-8ada-c0ffa18f878d)
   *Figure 2. Start an agent using eJADE.*

3. Deploy `SimManagerAgent`.
4. `SimManagerAgent` requires no parameters to be started. Simply click **“Finish”**.

   ![image](https://github.com/user-attachments/assets/0011e595-ab43-4bd8-9419-8a21a6e9bf0d)
   *Figure 3. SimManagerAgent setup.*

#### **b) From Command Line (Not recommended)**
Dealing with dependencies manually can be challenging. Here is an example execution:

```bash
javac -cp "D:\EWS2\MRTA45\src" "D:\EWS2\MRTA45\src\*.java"
java jade.Boot -gui -cp "D:\EWS2\MRTA45\bin" -cp "D:\JADE\lib" -host localhost -container -name "myFirstContainer" SimManager:SimManagerAgent
```

### **Graphical User Interface**
MRTASim provides a GUI where users can:

- Load and modify maps.
- Observe robot task assignments in real-time.
- Adjust simulation speed (1x to 40x).
- Add new tasks and robots dynamically.

   ![image](https://github.com/user-attachments/assets/1bfb01bf-6580-482c-a6f9-3a13ed210810)
   *Figure 4. Parameter Setup: Configure your simulation.*

Detailed information about MRTA taxonomy can be found at:
Öztürk, S., Kuzucuoğlu, A.E. (2020). Building a Generic Simulation Model for Analyzing the Feasibility of Multi-Robot Task Allocation (MRTA) Problems.
[Read more](https://doi.org/10.1007/978-3-030-43890-6_6)

   ![image](https://github.com/user-attachments/assets/355758e6-31b3-4372-abc9-e6aed2412081)
   *Figure 5: Execution of parametric setup: Best for basic experiments.*

   ![image](https://github.com/user-attachments/assets/5ce6103e-5281-4b4a-bdfa-eaf59497b881)
   *Figure 6: Execution of heatmap setup: Best for automated execution of complex scenarios, e.g., feasibility analysis for large models.*

---

# **Contribution Guide**

Thank you for your interest in contributing to **MRTASim**! 
Your contributions help improve the project and ensure its continued success. 
Please follow the guidelines below to make the process smooth and efficient.

##  How to Contribute

### 1 Fork the Repository
1. Go to the [MRTASim GitHub repository](https://github.com/timetraveler00/MRTASim).
2. Click the **Fork** button to create your own copy of the repository.
3. Clone your forked repository:
   ```bash
   git clone https://github.com/your-username/MRTASim.git
   cd MRTASim
   ```
4. Add the original repository as a remote source to stay updated:
   ```bash
   git remote add upstream https://github.com/timetraveler00/MRTASim.git
   ```

### 2 Create a New Branch
Before making changes, create a new branch:
```bash
git checkout -b feature-your-feature-name
```
Use a meaningful branch name that reflects your contribution (e.g., `fix-bug-x`, `add-feature-y`).

### 3️ Implement Changes
- Follow the coding standards (see **Coding Guidelines** below).
- Ensure that your code does not break existing functionality.
- Write clear commit messages (see **Commit Guidelines** below).

### 4️ Test Your Changes
Before submitting your changes, test them thoroughly:
```bash
# If tests are available, run them
mvn test
```

### 5️ Create a Pull Request (PR)
1. Push your branch to GitHub:
   ```bash
   git push origin feature-your-feature-name
   ```
2. Go to the original repository on GitHub.
3. Click **Compare & pull request**.
4. Provide a clear title and description for your PR.
5. Submit the pull request and wait for review.

---

##  Coding Guidelines

- Recommended coding guideline : [Google Java Coding Guideline](https://google.github.io/styleguide/javaguide.html)
- Follow Java best practices and clean coding principles. 
- Maintain consistency in indentation and formatting.
- Use meaningful variable and function names.
- Add comments where necessary for better code readability.
- If modifying existing code, ensure compatibility with previous implementations.

---

##  Commit Guidelines

Follow a structured commit message format:
```
[TYPE] Short description (max 50 chars)

Detailed explanation (if needed, max 72 chars per line)
```

### Common commit types:
- `feat`: Adding a new feature
- `fix`: Fixing a bug
- `docs`: Updating documentation
- `style`: Code style changes (formatting, whitespace, etc.)
- `refactor`: Code refactoring without changing functionality
- `test`: Adding or updating tests
- `chore`: Miscellaneous maintenance tasks

**Example:**
```
feat: Add new task allocation algorithm

Implemented a new bidding mechanism for dynamic task allocation.
```

---



## **License**

MRTASim is distributed under the MIT License. See the `LICENSE` file for details.

---

## **Contact & Support**

If you have any questions, issues, or feedback, feel free to reach out:

- **Email**: [savasozturk.academic@gmail.com](mailto:savasozturk.academic@gmail.com)
- **GitHub Issues**: [Submit an issue](https://github.com/timetraveler00/MRTASim/issues)
- **Discussions Forum**: [Start a discussion](https://github.com/timetraveler00/MRTASim/discussions)

Thank you for supporting MRTASim. 
