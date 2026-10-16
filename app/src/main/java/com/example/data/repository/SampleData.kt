package com.example.data.repository

import com.example.domain.model.ChapterInfo
import com.example.domain.model.Difficulty
import com.example.domain.model.EducationClass
import com.example.domain.model.Question
import com.example.domain.model.QuestionType
import com.example.domain.model.StudyNote
import com.example.domain.model.SubjectType

object SampleData {

    val chapters: List<ChapterInfo> = listOf(
        // Class 9 Computer Science
        ChapterInfo(1, "Fundamentals of Computer", SubjectType.COMPUTER_SCIENCE, EducationClass.CLASS_9, 15, 6, 3, "Von Neumann architecture, Generations of computers, Hardware vs Software"),
        ChapterInfo(2, "Fundamentals of Operating System", SubjectType.COMPUTER_SCIENCE, EducationClass.CLASS_9, 12, 5, 2, "Functions of OS, CLI vs GUI, Process management, File systems"),
        ChapterInfo(3, "Office Automation", SubjectType.COMPUTER_SCIENCE, EducationClass.CLASS_9, 10, 4, 2, "Word processing, Spreadsheets, Formula evaluation, Presentation tools"),
        ChapterInfo(4, "Data Communication & Computer Networks", SubjectType.COMPUTER_SCIENCE, EducationClass.CLASS_9, 14, 6, 3, "Topologies (Star, Ring, Bus), Transmission media, LAN/WAN"),

        // Class 9 Physics
        ChapterInfo(1, "Physical Quantities & Measurement", SubjectType.PHYSICS, EducationClass.CLASS_9, 14, 5, 2, "Base & Derived SI units, Vernier Calliper, Screw Gauge, Significant figures"),
        ChapterInfo(2, "Kinematics", SubjectType.PHYSICS, EducationClass.CLASS_9, 16, 6, 3, "Scalars & Vectors, Speed vs Velocity, Equations of uniform motion, Free fall"),
        ChapterInfo(3, "Dynamics", SubjectType.PHYSICS, EducationClass.CLASS_9, 15, 6, 3, "Newton's Laws of Motion, Momentum, Friction, Centripetal acceleration"),
        ChapterInfo(4, "Turning Effect of Forces", SubjectType.PHYSICS, EducationClass.CLASS_9, 12, 5, 2, "Torque, Principle of Moments, Center of Mass, States of Equilibrium"),

        // Class 9 Chemistry
        ChapterInfo(1, "Fundamentals of Chemistry", SubjectType.CHEMISTRY, EducationClass.CLASS_9, 15, 6, 3, "Mole concept, Avogadro's number, Empirical & Molecular formula"),
        ChapterInfo(2, "Structure of Atoms", SubjectType.CHEMISTRY, EducationClass.CLASS_9, 14, 5, 2, "Rutherford atomic model, Bohr atomic theory, Isotopes and uses"),
        ChapterInfo(3, "Periodic Table & Periodicity", SubjectType.CHEMISTRY, EducationClass.CLASS_9, 13, 5, 3, "Modern periodic law, Atomic radius, Ionization energy, Electronegativity"),
        ChapterInfo(4, "Structure of Molecules", SubjectType.CHEMISTRY, EducationClass.CLASS_9, 12, 5, 2, "Ionic bonding, Covalent bonding, Intermolecular forces, Hydrogen bond"),

        // Class 9 Biology
        ChapterInfo(1, "Introduction to Biology", SubjectType.BIOLOGY, EducationClass.CLASS_9, 14, 5, 2, "Branches of biology, Scientific method, Levels of biological organization"),
        ChapterInfo(2, "Solving a Biological Problem", SubjectType.BIOLOGY, EducationClass.CLASS_9, 10, 4, 2, "Hypothesis, Deduction, Controlled experimentation in Malaria study"),
        ChapterInfo(3, "Biodiversity", SubjectType.BIOLOGY, EducationClass.CLASS_9, 12, 5, 2, "Five kingdom classification, Binomial nomenclature, Conservation"),
        ChapterInfo(4, "Cells and Tissues", SubjectType.BIOLOGY, EducationClass.CLASS_9, 15, 6, 3, "Prokaryotic vs Eukaryotic cells, Cell organelles, Plant & Animal tissues"),

        // Class 11 (1st Year) Computer Science
        ChapterInfo(1, "Overview of Computer System", SubjectType.COMPUTER_SCIENCE, EducationClass.CLASS_11, 15, 6, 3, "CPU registers, System bus architecture, Cache and RAM hierarchy"),
        ChapterInfo(2, "Information Networks", SubjectType.COMPUTER_SCIENCE, EducationClass.CLASS_11, 16, 6, 3, "OSI 7-layer reference model, TCP/IP protocol suite, Network devices"),
        ChapterInfo(3, "Data Communications", SubjectType.COMPUTER_SCIENCE, EducationClass.CLASS_11, 14, 5, 2, "Analog vs Digital signals, Modulation techniques, Transmission modes"),
        ChapterInfo(4, "Applications and Uses of Computers", SubjectType.COMPUTER_SCIENCE, EducationClass.CLASS_11, 12, 4, 2, "CAD/CAM in engineering, E-commerce, Artificial intelligence fundamentals"),

        // Class 11 Physics
        ChapterInfo(1, "Measurements", SubjectType.PHYSICS, EducationClass.CLASS_11, 14, 5, 2, "Errors & uncertainties, Precision vs Accuracy, Dimensional analysis"),
        ChapterInfo(2, "Vectors and Equilibrium", SubjectType.PHYSICS, EducationClass.CLASS_11, 16, 6, 3, "Scalar product, Vector cross product, Torque, First & Second conditions"),
        ChapterInfo(3, "Motion and Force", SubjectType.PHYSICS, EducationClass.CLASS_11, 15, 6, 3, "Newton's laws in momentum form, Elastic collision, Projectile trajectory"),
        ChapterInfo(4, "Work and Energy", SubjectType.PHYSICS, EducationClass.CLASS_11, 14, 5, 2, "Work-energy theorem, Conservative field, Escape velocity calculation"),

        // Class 11 Chemistry
        ChapterInfo(1, "Basic Concepts", SubjectType.CHEMISTRY, EducationClass.CLASS_11, 15, 6, 3, "Relative atomic mass, Stoichiometry, Limiting reactant, Percentage yield"),
        ChapterInfo(2, "Experimental Techniques", SubjectType.CHEMISTRY, EducationClass.CLASS_11, 11, 4, 2, "Filtration, Solvent extraction, Crystallization, Paper chromatography"),
        ChapterInfo(3, "Gases", SubjectType.CHEMISTRY, EducationClass.CLASS_11, 16, 6, 3, "Boyle's & Charles's law, Ideal gas equation, Graham's law of diffusion"),
        ChapterInfo(4, "Atomic Structure", SubjectType.CHEMISTRY, EducationClass.CLASS_11, 14, 5, 3, "Planck's Quantum theory, Quantum numbers (n, l, m, s), Electronic configurations"),

        // Class 11 Biology
        ChapterInfo(1, "Biological Molecules", SubjectType.BIOLOGY, EducationClass.CLASS_11, 16, 6, 3, "Structure of Carbohydrates, Fatty acids, Proteins & Nucleic acids (DNA/RNA)"),
        ChapterInfo(2, "Enzymes", SubjectType.BIOLOGY, EducationClass.CLASS_11, 12, 5, 2, "Lock & key vs Induced fit model, Factors affecting enzymatic rate"),
        ChapterInfo(3, "The Cell", SubjectType.BIOLOGY, EducationClass.CLASS_11, 15, 6, 3, "Fluid mosaic model of membrane, Endoplasmic reticulum, Mitochondria"),
        ChapterInfo(4, "Variety of Life", SubjectType.BIOLOGY, EducationClass.CLASS_11, 13, 5, 2, "Acellular structures, Structure of Bacteriophage, Viral lytic/lysogenic cycles")
    )

    val questions: List<Question> = listOf(
        // === Class 9 Computer Science ===
        Question(
            id = "cs9_mcq_1",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.COMPUTER_SCIENCE,
            chapter = "Fundamentals of Computer",
            chapterNumber = 1,
            questionType = QuestionType.MCQ,
            questionText = "Which electronic component was primarily used in the First Generation of computers (1940-1956)?",
            options = listOf("Transistors", "Vacuum Tubes", "Integrated Circuits (ICs)", "Microprocessors"),
            correctOptionIndex = 1,
            explanation = "First-generation computers (e.g. ENIAC, UNIVAC) used vacuum tubes for circuitry and magnetic drums for memory.",
            marks = 1,
            difficulty = Difficulty.EASY,
            boardYearMention = "FBISE 2023 / Lahore Board 2022"
        ),
        Question(
            id = "cs9_mcq_2",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.COMPUTER_SCIENCE,
            chapter = "Fundamentals of Computer",
            chapterNumber = 1,
            questionType = QuestionType.MCQ,
            questionText = "The computer architecture based on the stored-program concept was introduced by:",
            options = listOf("Charles Babbage", "John von Neumann", "Blaise Pascal", "Alan Turing"),
            correctOptionIndex = 1,
            explanation = "John von Neumann introduced the stored-program computer architecture in 1945 consisting of CPU, memory, and I/O interfaces.",
            marks = 1,
            difficulty = Difficulty.MEDIUM,
            boardYearMention = "FBISE 2024 / Gujranwala Board 2023"
        ),
        Question(
            id = "cs9_mcq_3",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.COMPUTER_SCIENCE,
            chapter = "Fundamentals of Operating System",
            chapterNumber = 2,
            questionType = QuestionType.MCQ,
            questionText = "Which type of user interface requires the user to memorize and type textual commands?",
            options = listOf("Graphical User Interface (GUI)", "Menu-Driven Interface", "Command Line Interface (CLI)", "Touch User Interface (TUI)"),
            correctOptionIndex = 2,
            explanation = "Command Line Interface (CLI) such as MS-DOS requires users to type explicit commands with exact syntax.",
            marks = 1,
            difficulty = Difficulty.EASY,
            boardYearMention = "Rawalpindi Board 2023"
        ),
        Question(
            id = "cs9_mcq_4",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.COMPUTER_SCIENCE,
            chapter = "Data Communication & Computer Networks",
            chapterNumber = 4,
            questionType = QuestionType.MCQ,
            questionText = "In which network topology are all client computers connected to a central device such as a Switch or Hub?",
            options = listOf("Bus Topology", "Ring Topology", "Star Topology", "Mesh Topology"),
            correctOptionIndex = 2,
            explanation = "In Star Topology, each host is connected to a central hub/switch via a dedicated point-to-point link.",
            marks = 1,
            difficulty = Difficulty.EASY,
            boardYearMention = "FBISE 2022 / Multan Board 2024"
        ),
        Question(
            id = "cs9_short_1",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.COMPUTER_SCIENCE,
            chapter = "Fundamentals of Computer",
            chapterNumber = 1,
            questionType = QuestionType.SHORT,
            questionText = "Differentiate between System Software and Application Software with two examples of each.",
            marks = 3,
            difficulty = Difficulty.MEDIUM,
            boardYearMention = "FBISE Model Paper 2024",
            modelSolution = "1. System Software controls and manages hardware operations (e.g., Windows 11, Linux, Device Drivers).\n2. Application Software performs specific user-oriented tasks (e.g., Microsoft Word, VLC Media Player)."
        ),
        Question(
            id = "cs9_long_1",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.COMPUTER_SCIENCE,
            chapter = "Data Communication & Computer Networks",
            chapterNumber = 4,
            questionType = QuestionType.LONG,
            questionText = "Explain Star Topology and Ring Topology in detail. Draw neat diagrams and list two advantages and disadvantages of each.",
            marks = 8,
            difficulty = Difficulty.HARD,
            boardYearMention = "Lahore Board 2023 Group 1",
            modelSolution = "Star Topology: Central device (switch), easy to install and expand, failure of one cable does not disrupt network. Disadvantage: Hub failure shuts down entire network.\nRing Topology: Unidirectional token passing, predictable latency. Disadvantage: Break in ring disables whole segment."
        ),

        // === Class 9 Physics ===
        Question(
            id = "phy9_mcq_1",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.PHYSICS,
            chapter = "Physical Quantities & Measurement",
            chapterNumber = 1,
            questionType = QuestionType.MCQ,
            questionText = "The least count of a standard metric Vernier Calliper with 0.1 mm precision is:",
            options = listOf("0.01 cm", "0.001 cm", "0.1 cm", "1 mm"),
            correctOptionIndex = 0,
            explanation = "Least count of Vernier Calipers = 0.1 mm = 0.01 cm (or 0.05 mm in high-precision workshop calipers).",
            marks = 1,
            difficulty = Difficulty.EASY,
            boardYearMention = "FBISE 2023 / Lahore Board 2021"
        ),
        Question(
            id = "phy9_mcq_2",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.PHYSICS,
            chapter = "Kinematics",
            chapterNumber = 2,
            questionType = QuestionType.MCQ,
            questionText = "A body moving with uniform velocity has an acceleration equal to:",
            options = listOf("Zero", "9.8 m/s²", "Constant positive value", "Infinity"),
            correctOptionIndex = 0,
            explanation = "Acceleration is the time rate of change of velocity (a = Δv/Δt). If velocity is constant, Δv = 0, hence acceleration is zero.",
            marks = 1,
            difficulty = Difficulty.EASY,
            boardYearMention = "Faisalabad Board 2023"
        ),
        Question(
            id = "phy9_mcq_3",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.PHYSICS,
            chapter = "Dynamics",
            chapterNumber = 3,
            questionType = QuestionType.MCQ,
            questionText = "The SI unit of linear momentum (p = mv) is:",
            options = listOf("N m", "kg m/s²", "N s (Newton-second)", "Joule"),
            correctOptionIndex = 2,
            explanation = "Momentum has unit kg m/s. Since 1 N = 1 kg m/s², 1 N·s = 1 kg·m/s.",
            marks = 1,
            difficulty = Difficulty.MEDIUM,
            boardYearMention = "FBISE 2024 / Rawalpindi Board 2022"
        ),
        Question(
            id = "phy9_short_1",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.PHYSICS,
            chapter = "Dynamics",
            chapterNumber = 3,
            questionType = QuestionType.SHORT,
            questionText = "State Newton's Second Law of Motion and derive the formula F = ma.",
            marks = 3,
            difficulty = Difficulty.MEDIUM,
            boardYearMention = "Lahore Board 2023 Group 2",
            modelSolution = "Statement: When a net force acts on a body, it produces an acceleration in the direction of the force that is directly proportional to the force and inversely proportional to its mass.\nDerivation: a ∝ F, a ∝ 1/m => a ∝ F/m => F = k·ma (with k=1 in SI units), F = ma."
        ),
        Question(
            id = "phy9_long_1",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.PHYSICS,
            chapter = "Kinematics",
            chapterNumber = 2,
            questionType = QuestionType.LONG,
            questionText = "Derive the third equation of motion (2as = v_f² - v_i²) with the help of a speed-time graph.",
            marks = 7,
            difficulty = Difficulty.HARD,
            boardYearMention = "FBISE 2023 Board Exam",
            modelSolution = "Area under speed-time graph = Trapezium OABD = Total distance S = (OA + BD) * OD / 2.\nSubstitute OD = (vf - vi)/a into formula to arrive at 2as = vf² - vi²."
        ),

        // === Class 9 Chemistry ===
        Question(
            id = "chem9_mcq_1",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.CHEMISTRY,
            chapter = "Fundamentals of Chemistry",
            chapterNumber = 1,
            questionType = QuestionType.MCQ,
            questionText = "The number of particles (atoms, molecules, or ions) present in one mole of any substance is equal to:",
            options = listOf("6.022 × 10²³", "3.011 × 10²³", "6.022 × 10²²", "1.66 × 10⁻²⁴"),
            correctOptionIndex = 0,
            explanation = "Avogadro's constant (NA) is 6.022 × 10²³ particles per mole.",
            marks = 1,
            difficulty = Difficulty.EASY,
            boardYearMention = "Gujranwala Board 2024 / FBISE 2023"
        ),
        Question(
            id = "chem9_mcq_2",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.CHEMISTRY,
            chapter = "Structure of Atoms",
            chapterNumber = 2,
            questionType = QuestionType.MCQ,
            questionText = "In Rutherford's alpha particle scattering gold foil experiment, most alpha particles passed through undeflected because:",
            options = listOf("The nucleus is negatively charged", "Most of the space inside an atom is empty", "Alpha particles are uncharged", "Electrons attract alpha particles"),
            correctOptionIndex = 1,
            explanation = "Since the nucleus occupies a very tiny volume at the center, the vast majority of the atom's volume is empty space.",
            marks = 1,
            difficulty = Difficulty.MEDIUM,
            boardYearMention = "FBISE 2022 / Sahiwal Board 2023"
        ),
        Question(
            id = "chem9_short_1",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.CHEMISTRY,
            chapter = "Structure of Molecules",
            chapterNumber = 4,
            questionType = QuestionType.SHORT,
            questionText = "Why does ice float on the surface of liquid water? Explain on the basis of hydrogen bonding.",
            marks = 3,
            difficulty = Difficulty.MEDIUM,
            boardYearMention = "Lahore Board 2024",
            modelSolution = "In liquid water, hydrogen bonds continuously break and reform. Upon freezing, water molecules arrange into a rigid, open hexagonal lattice structure with empty spaces. This causes the density of ice (0.917 g/cm³) to be lower than liquid water (1.00 g/cm³), allowing ice to float."
        ),

        // === Class 9 Biology ===
        Question(
            id = "bio9_mcq_1",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.BIOLOGY,
            chapter = "Cells and Tissues",
            chapterNumber = 4,
            questionType = QuestionType.MCQ,
            questionText = "Which cellular organelle is known as the 'Powerhouse of the Cell' due to ATP production?",
            options = listOf("Ribosome", "Golgi Apparatus", "Mitochondria", "Endoplasmic Reticulum"),
            correctOptionIndex = 2,
            explanation = "Mitochondria carry out cellular aerobic respiration and generate adenosine triphosphate (ATP).",
            marks = 1,
            difficulty = Difficulty.EASY,
            boardYearMention = "FBISE 2024 / Lahore Board 2023"
        ),
        Question(
            id = "bio9_mcq_2",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.BIOLOGY,
            chapter = "Biodiversity",
            chapterNumber = 3,
            questionType = QuestionType.MCQ,
            questionText = "The binomial system of nomenclature for naming organisms was introduced by:",
            options = listOf("Aristotle", "Carl Linnaeus", "Robert Whittaker", "Ernst Haeckel"),
            correctOptionIndex = 1,
            explanation = "Swedish botanist Carl Linnaeus introduced the binomial naming system (Genus + species epithet).",
            marks = 1,
            difficulty = Difficulty.EASY,
            boardYearMention = "Federal Board 2021"
        ),
        Question(
            id = "bio9_short_1",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.BIOLOGY,
            chapter = "Cells and Tissues",
            chapterNumber = 4,
            questionType = QuestionType.SHORT,
            questionText = "State three key structural differences between Plant Cells and Animal Cells.",
            marks = 3,
            difficulty = Difficulty.EASY,
            boardYearMention = "Rawalpindi Board 2023",
            modelSolution = "1. Plant cells possess a rigid cellulose cell wall outside the membrane; animal cells lack a cell wall.\n2. Plant cells contain chloroplasts for photosynthesis; animal cells do not.\n3. Plant cells have one large central vacuole; animal cells have multiple small vacuoles."
        ),

        // === Class 11 (1st Year) Computer Science ===
        Question(
            id = "cs11_mcq_1",
            educationClass = EducationClass.CLASS_11,
            subject = SubjectType.COMPUTER_SCIENCE,
            chapter = "Information Networks",
            chapterNumber = 2,
            questionType = QuestionType.MCQ,
            questionText = "Which layer of the OSI model is responsible for end-to-end reliable data delivery, error recovery, and flow control?",
            options = listOf("Network Layer", "Transport Layer", "Session Layer", "Data Link Layer"),
            correctOptionIndex = 1,
            explanation = "The Transport Layer (Layer 4) provides transparent transfer of data between end users using protocols like TCP.",
            marks = 1,
            difficulty = Difficulty.MEDIUM,
            boardYearMention = "FBISE HSSC-I 2024"
        ),
        Question(
            id = "cs11_mcq_2",
            educationClass = EducationClass.CLASS_11,
            subject = SubjectType.COMPUTER_SCIENCE,
            chapter = "Overview of Computer System",
            chapterNumber = 1,
            questionType = QuestionType.MCQ,
            questionText = "The CPU register that holds the memory address of the next instruction to be fetched and executed is:",
            options = listOf("Instruction Register (IR)", "Memory Data Register (MDR)", "Program Counter (PC)", "Accumulator (ACC)"),
            correctOptionIndex = 2,
            explanation = "The Program Counter (PC) automatically increments to hold the pointer to the next machine instruction in RAM.",
            marks = 1,
            difficulty = Difficulty.MEDIUM,
            boardYearMention = "Lahore Board 2023 Inter Part 1"
        ),
        Question(
            id = "cs11_short_1",
            educationClass = EducationClass.CLASS_11,
            subject = SubjectType.COMPUTER_SCIENCE,
            chapter = "Information Networks",
            chapterNumber = 2,
            questionType = QuestionType.SHORT,
            questionText = "Explain the function of Routers and Gateways in wide area network (WAN) communication.",
            marks = 4,
            difficulty = Difficulty.MEDIUM,
            boardYearMention = "FBISE 2023 HSSC-I",
            modelSolution = "1. Router: An intelligent Layer 3 network device that inspects IP packet headers to forward data across disparate network subnets using dynamic routing tables.\n2. Gateway: A protocol converter device operating at higher layers (Layers 4-7) enabling communication between networks with entirely incompatible architectures."
        ),

        // === Class 11 Physics ===
        Question(
            id = "phy11_mcq_1",
            educationClass = EducationClass.CLASS_11,
            subject = SubjectType.PHYSICS,
            chapter = "Measurements",
            chapterNumber = 1,
            questionType = QuestionType.MCQ,
            questionText = "The dimensional formula for Work and Torque is identical to that of:",
            options = listOf("[M L T⁻¹]", "[M L² T⁻²]", "[M L⁻¹ T⁻²]", "[M L² T⁻¹]"),
            correctOptionIndex = 1,
            explanation = "Work = Force × distance = [M L T⁻²] × [L] = [M L² T⁻²]. Both Work and Torque share this dimension.",
            marks = 1,
            difficulty = Difficulty.MEDIUM,
            boardYearMention = "FBISE HSSC-I 2024 / Lahore Board 2023"
        ),
        Question(
            id = "phy11_mcq_2",
            educationClass = EducationClass.CLASS_11,
            subject = SubjectType.PHYSICS,
            chapter = "Motion and Force",
            chapterNumber = 3,
            questionType = QuestionType.MCQ,
            questionText = "At what launch angle (θ) relative to the horizontal is the horizontal range of a projectile maximum?",
            options = listOf("30°", "45°", "60°", "90°"),
            correctOptionIndex = 1,
            explanation = "Range formula R = (v² sin 2θ)/g. sin(2θ) is maximum at 90°, which yields 2θ = 90° => θ = 45°.",
            marks = 1,
            difficulty = Difficulty.EASY,
            boardYearMention = "Gujranwala Board 2022"
        ),
        Question(
            id = "phy11_short_1",
            educationClass = EducationClass.CLASS_11,
            subject = SubjectType.PHYSICS,
            chapter = "Work and Energy",
            chapterNumber = 4,
            questionType = QuestionType.SHORT,
            questionText = "Show that the work done in a closed path within a gravitational field is equal to zero.",
            marks = 4,
            difficulty = Difficulty.HARD,
            boardYearMention = "FBISE Model Paper 2024",
            modelSolution = "In a conservative field like gravity, W_total = W_AB + W_BC + W_CD + W_DA = -mgh + 0 + mgh + 0 = 0. Therefore, the work done along any closed path is zero."
        ),

        // === Class 11 Chemistry ===
        Question(
            id = "chem11_mcq_1",
            educationClass = EducationClass.CLASS_11,
            subject = SubjectType.CHEMISTRY,
            chapter = "Basic Concepts",
            chapterNumber = 1,
            questionType = QuestionType.MCQ,
            questionText = "A reactant that controls the amount of product formed in a chemical reaction because it is consumed first is called:",
            options = listOf("Excess Reactant", "Catalyst", "Limiting Reactant", "Inhibitor"),
            correctOptionIndex = 2,
            explanation = "The limiting reactant gives the minimum amount of product according to stoichiometric calculations.",
            marks = 1,
            difficulty = Difficulty.EASY,
            boardYearMention = "Lahore Board 2023 / Rawalpindi 2022"
        ),
        Question(
            id = "chem11_mcq_2",
            educationClass = EducationClass.CLASS_11,
            subject = SubjectType.CHEMISTRY,
            chapter = "Atomic Structure",
            chapterNumber = 4,
            questionType = QuestionType.MCQ,
            questionText = "The Azimuthal Quantum Number (l) determines the:",
            options = listOf("Principal energy shell", "Shape of the subshell/orbital", "Spatial orientation of orbital", "Spin of the electron"),
            correctOptionIndex = 1,
            explanation = "Azimuthal quantum number l values (0, 1, 2, 3) designate s (spherical), p (dumbbell), d (double dumbbell), and f orbitals.",
            marks = 1,
            difficulty = Difficulty.MEDIUM,
            boardYearMention = "FBISE 2023 HSSC-I"
        ),

        // === Class 11 Biology ===
        Question(
            id = "bio11_mcq_1",
            educationClass = EducationClass.CLASS_11,
            subject = SubjectType.BIOLOGY,
            chapter = "Enzymes",
            chapterNumber = 2,
            questionType = QuestionType.MCQ,
            questionText = "The induced-fit model of enzyme action was proposed in 1958 by:",
            options = listOf("Emil Fischer", "Daniel Koshland", "Wilhelm Kühne", "Linus Pauling"),
            correctOptionIndex = 1,
            explanation = "Daniel Koshland proposed the Induced-Fit Model, showing that active sites undergo conformational adjustments when substrates bind.",
            marks = 1,
            difficulty = Difficulty.MEDIUM,
            boardYearMention = "Lahore Board 2024 Inter Part 1"
        ),
        Question(
            id = "bio11_mcq_2",
            educationClass = EducationClass.CLASS_11,
            subject = SubjectType.BIOLOGY,
            chapter = "Variety of Life",
            chapterNumber = 4,
            questionType = QuestionType.MCQ,
            questionText = "The protein coat enclosing the genetic material (DNA or RNA) of a virus is termed the:",
            options = listOf("Capsid", "Envelope", "Capsomere", "Pellicle"),
            correctOptionIndex = 0,
            explanation = "The protective protein shell is called the capsid, which is composed of structural protein subunits called capsomeres.",
            marks = 1,
            difficulty = Difficulty.EASY,
            boardYearMention = "Federal Board HSSC-I 2023"
        )
    )

    val sampleStudyNotes: Map<String, StudyNote> = mapOf(
        "cs9_ch1" to StudyNote(
            id = "cs9_ch1",
            title = "Fundamentals of Computer & Architecture",
            content = "The study of computers begins with the Von Neumann architecture (1945), which established the stored-program concept where instructions and data share unified memory space.",
            keyPoints = listOf(
                "First Generation (1940-1956): Vacuum tubes, machine language, high heat generation (ENIAC, EDVAC).",
                "Second Generation (1956-1963): Transistors, assembly language, smaller and faster (IBM 7094).",
                "Third Generation (1964-1971): Integrated Circuits (SSI & MSI), high-level languages like FORTRAN.",
                "Fourth Generation (1971-Present): Microprocessors (VLSI & ULSI), personal computing revolution.",
                "Fifth Generation (Present & Beyond): Artificial intelligence, parallel processing, quantum computing."
            ),
            formulasOrDefinitions = listOf(
                "CPU Components: ALU (Arithmetic Logic Unit) + CU (Control Unit) + Internal Registers.",
                "Bus System: Control Bus (command signals), Data Bus (bidirectional data), Address Bus (unidirectional memory pointers)."
            )
        ),
        "phy9_ch2" to StudyNote(
            id = "phy9_ch2",
            title = "Kinematics & Equations of Motion",
            content = "Kinematics is the branch of classical mechanics describing the motion of points, bodies, and systems of bodies without consideration of the forces causing the motion.",
            keyPoints = listOf(
                "Distance vs Displacement: Distance is scalar (total path length), while displacement is the shortest vector distance between initial and final points.",
                "Speed vs Velocity: Speed = distance/time (scalar); Velocity = displacement/time (vector).",
                "Uniform Acceleration: When equal changes in velocity occur in equal intervals of time.",
                "Gravitational Free Fall: Under gravity near Earth's surface, g ≈ 9.8 m/s² directed downwards."
            ),
            formulasOrDefinitions = listOf(
                "First Equation of Motion: v_f = v_i + a·t",
                "Second Equation of Motion: S = v_i·t + (1/2)·a·t²",
                "Third Equation of Motion: 2·a·S = v_f² - v_i²"
            )
        ),
        "chem9_ch1" to StudyNote(
            id = "chem9_ch1",
            title = "Fundamentals of Chemistry & Stoichiometry",
            content = "Chemistry is the science of matter, its properties, composition, structure, and the changes it undergoes during chemical reactions.",
            keyPoints = listOf(
                "Mole Concept: A mole is the amount of a substance that contains exactly 6.02214076 × 10²³ elementary entities.",
                "Atomic Mass Unit (amu): 1/12th the mass of one Carbon-12 atom (1 amu = 1.66 × 10⁻²⁴ g).",
                "Empirical vs Molecular Formula: Empirical shows simplest whole-number ratio; Molecular formula shows actual number of each atom in a molecule (Molecular Formula = n × Empirical Formula)."
            ),
            formulasOrDefinitions = listOf(
                "Number of moles (n) = Mass in grams (m) / Molar Mass (M)",
                "Number of particles = Number of moles × Avogadro's Number (N_A = 6.022 × 10²³)"
            )
        )
    )
}
