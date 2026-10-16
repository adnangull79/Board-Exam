package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.ExamRepository
import com.example.data.repository.SampleData
import com.example.domain.model.BoardType
import com.example.domain.model.ChapterInfo
import com.example.domain.model.Difficulty
import com.example.domain.model.EducationClass
import com.example.domain.model.PaperDraft
import com.example.domain.model.PaperSection
import com.example.domain.model.PaperValidationResult
import com.example.domain.model.Question
import com.example.domain.model.QuestionType
import com.example.domain.model.QuizResult
import com.example.domain.model.SubjectType
import com.example.domain.paper.PaperValidator
import com.example.domain.quiz.QuizScorer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class UserRole {
    ROLE_SELECTOR,
    STUDENT,
    TEACHER
}

enum class StudentTab {
    HOME,
    SUBJECTS,
    PRACTICE,
    PROGRESS,
    SETTINGS
}

enum class TeacherTab {
    HOME,
    QUESTION_BANK,
    MY_PAPERS,
    SETTINGS
}

sealed class AppScreen {
    object MainDashboard : AppScreen()
    data class SubjectDetail(val subject: SubjectType) : AppScreen()
    data class ChapterDetail(val chapter: ChapterInfo, val initialTab: Int = 0) : AppScreen()
    data class QuizConfig(val preselectedSubject: SubjectType? = null, val preselectedChapter: String? = null) : AppScreen()
    object ActiveQuiz : AppScreen()
    data class QuizResultView(val result: QuizResult) : AppScreen()
    data class PaperBuilder(val step: Int = 1) : AppScreen()
    data class PaperPreview(val draft: PaperDraft) : AppScreen()
    data class AnswerKeyPreview(val draft: PaperDraft) : AppScreen()
}

data class ActiveQuizState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val userAnswers: Map<String, Int?> = emptyMap(),
    val educationClass: EducationClass = EducationClass.CLASS_9,
    val subject: SubjectType = SubjectType.COMPUTER_SCIENCE,
    val chapter: String = "All Chapters",
    val startTimestamp: Long = System.currentTimeMillis()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExamRepository(application)

    // User Role & Preferences
    private val _currentRole = MutableStateFlow(UserRole.ROLE_SELECTOR)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _selectedClass = MutableStateFlow(EducationClass.CLASS_9)
    val selectedClass: StateFlow<EducationClass> = _selectedClass.asStateFlow()

    private val _selectedBoard = MutableStateFlow(BoardType.FEDERAL_BOARD)
    val selectedBoard: StateFlow<BoardType> = _selectedBoard.asStateFlow()

    // Navigation state
    private val _studentTab = MutableStateFlow(StudentTab.HOME)
    val studentTab: StateFlow<StudentTab> = _studentTab.asStateFlow()

    private val _teacherTab = MutableStateFlow(TeacherTab.HOME)
    val teacherTab: StateFlow<TeacherTab> = _teacherTab.asStateFlow()

    private val _screenStack = MutableStateFlow<List<AppScreen>>(listOf(AppScreen.MainDashboard))
    val currentScreen: StateFlow<AppScreen> = MutableStateFlow<AppScreen>(AppScreen.MainDashboard).apply {
        viewModelScope.launch {
            _screenStack.collect { stack ->
                value = stack.lastOrNull() ?: AppScreen.MainDashboard
            }
        }
    }

    // Active Quiz State
    private val _activeQuizState = MutableStateFlow<ActiveQuizState?>(null)
    val activeQuizState: StateFlow<ActiveQuizState?> = _activeQuizState.asStateFlow()

    private val _lastQuizResult = MutableStateFlow<QuizResult?>(null)
    val lastQuizResult: StateFlow<QuizResult?> = _lastQuizResult.asStateFlow()

    // Teacher Paper Drafting State
    private val _activePaperDraft = MutableStateFlow<PaperDraft>(createInitialPaperDraft())
    val activePaperDraft: StateFlow<PaperDraft> = _activePaperDraft.asStateFlow()

    private val _paperValidation = MutableStateFlow(PaperValidator.validatePaper(_activePaperDraft.value))
    val paperValidation: StateFlow<PaperValidationResult> = _paperValidation.asStateFlow()

    // Room Persistent Flows
    val recentQuizAttempts: StateFlow<List<QuizResult>> = repository.recentQuizAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPaperDrafts: StateFlow<List<PaperDraft>> = repository.allPaperDrafts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
        }
    }

    fun selectRole(role: UserRole) {
        _currentRole.value = role
        _screenStack.value = listOf(AppScreen.MainDashboard)
    }

    fun setEducationClass(educationClass: EducationClass) {
        _selectedClass.value = educationClass
    }

    fun setBoard(board: BoardType) {
        _selectedBoard.value = board
    }

    fun selectStudentTab(tab: StudentTab) {
        _studentTab.value = tab
        _screenStack.value = listOf(AppScreen.MainDashboard)
    }

    fun selectTeacherTab(tab: TeacherTab) {
        _teacherTab.value = tab
        _screenStack.value = listOf(AppScreen.MainDashboard)
    }

    fun navigateTo(screen: AppScreen) {
        val current = _screenStack.value.toMutableList()
        current.add(screen)
        _screenStack.value = current
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value.toMutableList()
        if (current.size > 1) {
            current.removeAt(current.size - 1)
            _screenStack.value = current
            return true
        } else if (_currentRole.value != UserRole.ROLE_SELECTOR) {
            // Return to role selector
            _currentRole.value = UserRole.ROLE_SELECTOR
            return true
        }
        return false
    }

    // === Quiz Management ===
    fun startQuiz(
        educationClass: EducationClass,
        subject: SubjectType,
        chapter: String?,
        requestedCount: Int
    ) {
        val allQuestions = repository.getAllQuestions()
        val mcqQuestions = allQuestions.filter { it.questionType == QuestionType.MCQ }
        val selectedQuestions = QuizScorer.prepareQuizQuestions(
            allQuestions = mcqQuestions,
            educationClass = educationClass,
            subject = subject,
            chapter = chapter,
            requestedCount = requestedCount
        )

        if (selectedQuestions.isNotEmpty()) {
            _activeQuizState.value = ActiveQuizState(
                questions = selectedQuestions,
                currentIndex = 0,
                userAnswers = emptyMap(),
                educationClass = educationClass,
                subject = subject,
                chapter = chapter ?: "All Chapters",
                startTimestamp = System.currentTimeMillis()
            )
            navigateTo(AppScreen.ActiveQuiz)
        }
    }

    fun selectQuizAnswer(questionId: String, optionIndex: Int) {
        val current = _activeQuizState.value ?: return
        val updatedAnswers = current.userAnswers.toMutableMap()
        // If clicking already selected, keep it selected (single-answer MCQ standard)
        updatedAnswers[questionId] = optionIndex
        _activeQuizState.value = current.copy(userAnswers = updatedAnswers)
    }

    fun goToNextQuestion() {
        val current = _activeQuizState.value ?: return
        if (current.currentIndex < current.questions.size - 1) {
            _activeQuizState.value = current.copy(currentIndex = current.currentIndex + 1)
        }
    }

    fun goToPreviousQuestion() {
        val current = _activeQuizState.value ?: return
        if (current.currentIndex > 0) {
            _activeQuizState.value = current.copy(currentIndex = current.currentIndex - 1)
        }
    }

    fun jumpToQuestion(index: Int) {
        val current = _activeQuizState.value ?: return
        if (index in current.questions.indices) {
            _activeQuizState.value = current.copy(currentIndex = index)
        }
    }

    fun submitQuiz() {
        val current = _activeQuizState.value ?: return
        val durationSeconds = ((System.currentTimeMillis() - current.startTimestamp) / 1000).toInt()
        val result = QuizScorer.evaluateQuiz(
            questions = current.questions,
            answers = current.userAnswers,
            educationClass = current.educationClass,
            subject = current.subject,
            chapter = current.chapter,
            durationSeconds = durationSeconds
        )

        _lastQuizResult.value = result
        _activeQuizState.value = null

        // Save to Room persistent database
        viewModelScope.launch {
            repository.saveQuizResult(result)
        }

        // Navigate to result screen
        navigateTo(AppScreen.QuizResultView(result))
    }

    // === Teacher Paper Maker Management ===
    fun startNewPaper(educationClass: EducationClass, subject: SubjectType) {
        val newDraft = PaperDraft(
            id = UUID.randomUUID().toString(),
            schoolName = "Government Higher Secondary School",
            examTitle = "Terminal Examination 2026",
            educationClass = educationClass,
            subject = subject,
            examType = "Send-Up Examination",
            examDate = "25-April-2026",
            durationMinutes = 90,
            totalMarksTarget = 50,
            sections = listOf(
                PaperSection(
                    id = UUID.randomUUID().toString(),
                    sectionName = "Section A: Multiple Choice Questions",
                    instructions = "Note: Attempt all questions. Each question carries 1 mark.",
                    questionType = QuestionType.MCQ,
                    marksPerQuestion = 1,
                    requiredQuestionsCount = 10,
                    questions = emptyList()
                ),
                PaperSection(
                    id = UUID.randomUUID().toString(),
                    sectionName = "Section B: Short Answer Questions",
                    instructions = "Note: Attempt any five (5) questions. Each carries 4 marks.",
                    questionType = QuestionType.SHORT,
                    marksPerQuestion = 4,
                    requiredQuestionsCount = 5,
                    questions = emptyList()
                ),
                PaperSection(
                    id = UUID.randomUUID().toString(),
                    sectionName = "Section C: Long Descriptive Questions",
                    instructions = "Note: Attempt any two (2) questions. Each carries 10 marks.",
                    questionType = QuestionType.LONG,
                    marksPerQuestion = 10,
                    requiredQuestionsCount = 2,
                    questions = emptyList()
                )
            )
        )
        _activePaperDraft.value = newDraft
        updateValidation()
        navigateTo(AppScreen.PaperBuilder(step = 1))
    }

    fun updatePaperExamDetails(
        schoolName: String,
        examTitle: String,
        examType: String,
        examDate: String,
        durationMinutes: Int,
        totalMarksTarget: Int,
        educationClass: EducationClass,
        subject: SubjectType
    ) {
        val current = _activePaperDraft.value
        _activePaperDraft.value = current.copy(
            schoolName = schoolName,
            examTitle = examTitle,
            examType = examType,
            examDate = examDate,
            durationMinutes = durationMinutes,
            totalMarksTarget = totalMarksTarget,
            educationClass = educationClass,
            subject = subject,
            updatedAtMillis = System.currentTimeMillis()
        )
        updateValidation()
    }

    fun addQuestionToSection(sectionIndex: Int, question: Question) {
        val current = _activePaperDraft.value
        if (sectionIndex in current.sections.indices) {
            val sections = current.sections.toMutableList()
            val targetSection = sections[sectionIndex]
            if (targetSection.questions.none { it.id == question.id }) {
                val updatedQuestions = targetSection.questions + question
                sections[sectionIndex] = targetSection.copy(questions = updatedQuestions)
                _activePaperDraft.value = current.copy(
                    sections = sections,
                    updatedAtMillis = System.currentTimeMillis()
                )
                updateValidation()
            }
        }
    }

    fun removeQuestionFromSection(sectionIndex: Int, questionId: String) {
        val current = _activePaperDraft.value
        if (sectionIndex in current.sections.indices) {
            val sections = current.sections.toMutableList()
            val targetSection = sections[sectionIndex]
            val updatedQuestions = targetSection.questions.filterNot { it.id == questionId }
            sections[sectionIndex] = targetSection.copy(questions = updatedQuestions)
            _activePaperDraft.value = current.copy(
                sections = sections,
                updatedAtMillis = System.currentTimeMillis()
            )
            updateValidation()
        }
    }

    fun addCustomQuestion(
        sectionIndex: Int,
        text: String,
        type: QuestionType,
        options: List<String> = emptyList(),
        correctOptionIndex: Int = 0,
        marks: Int = 1,
        modelSolution: String = ""
    ) {
        val current = _activePaperDraft.value
        val newQ = Question(
            id = "custom_${UUID.randomUUID()}",
            educationClass = current.educationClass,
            subject = current.subject,
            chapter = "Teacher Custom Questions",
            chapterNumber = 0,
            questionType = type,
            questionText = text,
            options = options,
            correctOptionIndex = correctOptionIndex,
            explanation = "Custom created by teacher for ${current.examTitle}",
            marks = marks,
            difficulty = Difficulty.MEDIUM,
            boardYearMention = "Custom Institution Item",
            modelSolution = modelSolution
        )
        addQuestionToSection(sectionIndex, newQ)
    }

    fun updateSectionConfig(sectionIndex: Int, requiredCount: Int, marksPerQuestion: Int) {
        val current = _activePaperDraft.value
        if (sectionIndex in current.sections.indices) {
            val sections = current.sections.toMutableList()
            val targetSection = sections[sectionIndex]
            sections[sectionIndex] = targetSection.copy(
                requiredQuestionsCount = requiredCount,
                marksPerQuestion = marksPerQuestion
            )
            _activePaperDraft.value = current.copy(
                sections = sections,
                updatedAtMillis = System.currentTimeMillis()
            )
            updateValidation()
        }
    }

    fun saveDraftToDatabase() {
        viewModelScope.launch {
            repository.savePaperDraft(_activePaperDraft.value)
        }
    }

    fun loadDraft(draft: PaperDraft) {
        _activePaperDraft.value = draft
        updateValidation()
        navigateTo(AppScreen.PaperBuilder(step = 4))
    }

    fun duplicateDraft(draft: PaperDraft) {
        viewModelScope.launch {
            val newDraft = repository.duplicatePaperDraft(draft)
            _activePaperDraft.value = newDraft
            updateValidation()
            navigateTo(AppScreen.PaperBuilder(step = 1))
        }
    }

    fun deleteDraft(draftId: String) {
        viewModelScope.launch {
            repository.deletePaperDraft(draftId)
        }
    }

    private fun updateValidation() {
        _paperValidation.value = PaperValidator.validatePaper(_activePaperDraft.value)
    }

    private fun createInitialPaperDraft(): PaperDraft {
        return PaperDraft(
            id = UUID.randomUUID().toString(),
            schoolName = "Federal Model College, Islamabad",
            examTitle = "Mid-Term Examination 2026",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.COMPUTER_SCIENCE,
            examType = "First Term Examination",
            examDate = "20-April-2026",
            durationMinutes = 90,
            totalMarksTarget = 50,
            sections = emptyList()
        )
    }
}
