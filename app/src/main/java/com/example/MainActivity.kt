package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.role.RoleSelectionScreen
import com.example.ui.student.ActiveQuizScreen
import com.example.ui.student.ChapterDetailScreen
import com.example.ui.student.QuizConfigScreen
import com.example.ui.student.QuizResultScreen
import com.example.ui.student.StudentHomeScreen
import com.example.ui.student.StudentProgressScreen
import com.example.ui.student.StudentSettingsScreen
import com.example.ui.student.SubjectDetailScreen
import com.example.ui.teacher.AnswerKeyPreviewScreen
import com.example.ui.teacher.PaperBuilderScreen
import com.example.ui.teacher.PaperPrintPreviewScreen
import com.example.ui.teacher.QuestionBankScreen
import com.example.ui.teacher.TeacherHomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.StudentTab
import com.example.viewmodel.TeacherTab
import com.example.viewmodel.UserRole

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentRole by viewModel.currentRole.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    val selectedBoard by viewModel.selectedBoard.collectAsState()
    val studentTab by viewModel.studentTab.collectAsState()
    val teacherTab by viewModel.teacherTab.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val activeQuizState by viewModel.activeQuizState.collectAsState()
    val recentQuizzes by viewModel.recentQuizAttempts.collectAsState()
    val allDrafts by viewModel.allPaperDrafts.collectAsState()
    val activeDraft by viewModel.activePaperDraft.collectAsState()
    val paperValidation by viewModel.paperValidation.collectAsState()

    // Handle Android system back button
    BackHandler(enabled = true) {
        viewModel.navigateBack()
    }

    if (currentRole == UserRole.ROLE_SELECTOR) {
        RoleSelectionScreen(
            selectedClass = selectedClass,
            onClassSelected = { viewModel.setEducationClass(it) },
            selectedBoard = selectedBoard,
            onBoardSelected = { viewModel.setBoard(it) },
            onSelectRole = { viewModel.selectRole(it) }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Only show bottom navigation on main dashboard screens, not inside active quiz or paper builder
            if (currentScreen is AppScreen.MainDashboard) {
                if (currentRole == UserRole.STUDENT) {
                    StudentBottomBar(
                        selectedTab = studentTab,
                        onTabSelected = { viewModel.selectStudentTab(it) }
                    )
                } else if (currentRole == UserRole.TEACHER) {
                    TeacherBottomBar(
                        selectedTab = teacherTab,
                        onTabSelected = { viewModel.selectTeacherTab(it) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is AppScreen.MainDashboard -> {
                    if (currentRole == UserRole.STUDENT) {
                        when (studentTab) {
                            StudentTab.HOME -> StudentHomeScreen(
                                selectedClass = selectedClass,
                                onClassSelected = { viewModel.setEducationClass(it) },
                                selectedBoard = selectedBoard,
                                onBoardSelected = { viewModel.setBoard(it) },
                                recentQuizzes = recentQuizzes,
                                onSubjectClick = { sub -> viewModel.navigateTo(AppScreen.SubjectDetail(sub)) },
                                onQuickStartQuiz = { viewModel.navigateTo(AppScreen.QuizConfig()) },
                                onSwitchRole = { viewModel.selectRole(UserRole.ROLE_SELECTOR) },
                                onViewAllProgress = { viewModel.selectStudentTab(StudentTab.PROGRESS) }
                            )
                            StudentTab.SUBJECTS -> SubjectDetailScreen(
                                subject = com.example.domain.model.SubjectType.COMPUTER_SCIENCE,
                                educationClass = selectedClass,
                                onBackClick = { viewModel.selectStudentTab(StudentTab.HOME) },
                                onChapterClick = { ch -> viewModel.navigateTo(AppScreen.ChapterDetail(ch)) },
                                onStartSubjectQuiz = { sub -> viewModel.navigateTo(AppScreen.QuizConfig(preselectedSubject = sub)) }
                            )
                            StudentTab.PRACTICE -> QuizConfigScreen(
                                currentClass = selectedClass,
                                onBackClick = { viewModel.selectStudentTab(StudentTab.HOME) },
                                onStartQuiz = { cls, sub, ch, count ->
                                    viewModel.startQuiz(cls, sub, ch, count)
                                }
                            )
                            StudentTab.PROGRESS -> StudentProgressScreen(
                                quizAttempts = recentQuizzes,
                                onBackClick = { viewModel.selectStudentTab(StudentTab.HOME) }
                            )
                            StudentTab.SETTINGS -> StudentSettingsScreen(
                                selectedClass = selectedClass,
                                onClassSelected = { viewModel.setEducationClass(it) },
                                selectedBoard = selectedBoard,
                                onBoardSelected = { viewModel.setBoard(it) },
                                onBackClick = { viewModel.selectStudentTab(StudentTab.HOME) },
                                onSwitchRole = { viewModel.selectRole(UserRole.ROLE_SELECTOR) }
                            )
                        }
                    } else {
                        // Teacher Dashboard
                        when (teacherTab) {
                            TeacherTab.HOME, TeacherTab.MY_PAPERS -> TeacherHomeScreen(
                                selectedClass = selectedClass,
                                savedDrafts = allDrafts,
                                onCreateNewPaper = {
                                    viewModel.startNewPaper(selectedClass, com.example.domain.model.SubjectType.COMPUTER_SCIENCE)
                                },
                                onOpenDraft = { draft -> viewModel.loadDraft(draft) },
                                onDuplicateDraft = { draft -> viewModel.duplicateDraft(draft) },
                                onDeleteDraft = { draftId -> viewModel.deleteDraft(draftId) },
                                onBrowseQuestionBank = { viewModel.navigateTo(AppScreen.PaperBuilder(step = 2)) },
                                onPreviewPaper = { draft -> viewModel.navigateTo(AppScreen.PaperPreview(draft)) },
                                onSwitchRole = { viewModel.selectRole(UserRole.ROLE_SELECTOR) }
                            )
                            TeacherTab.QUESTION_BANK -> QuestionBankScreen(
                                onBackClick = { viewModel.selectTeacherTab(TeacherTab.HOME) }
                            )
                            TeacherTab.SETTINGS -> StudentSettingsScreen(
                                selectedClass = selectedClass,
                                onClassSelected = { viewModel.setEducationClass(it) },
                                selectedBoard = selectedBoard,
                                onBoardSelected = { viewModel.setBoard(it) },
                                onBackClick = { viewModel.selectTeacherTab(TeacherTab.HOME) },
                                onSwitchRole = { viewModel.selectRole(UserRole.ROLE_SELECTOR) }
                            )
                        }
                    }
                }

                is AppScreen.SubjectDetail -> SubjectDetailScreen(
                    subject = screen.subject,
                    educationClass = selectedClass,
                    onBackClick = { viewModel.navigateBack() },
                    onChapterClick = { ch -> viewModel.navigateTo(AppScreen.ChapterDetail(ch)) },
                    onStartSubjectQuiz = { sub -> viewModel.navigateTo(AppScreen.QuizConfig(preselectedSubject = sub)) }
                )

                is AppScreen.ChapterDetail -> ChapterDetailScreen(
                    chapter = screen.chapter,
                    initialTab = screen.initialTab,
                    onBackClick = { viewModel.navigateBack() },
                    onStartChapterQuiz = { ch ->
                        viewModel.navigateTo(AppScreen.QuizConfig(preselectedSubject = ch.subject, preselectedChapter = ch.title))
                    }
                )

                is AppScreen.QuizConfig -> QuizConfigScreen(
                    currentClass = selectedClass,
                    preselectedSubject = screen.preselectedSubject,
                    preselectedChapter = screen.preselectedChapter,
                    onBackClick = { viewModel.navigateBack() },
                    onStartQuiz = { cls, sub, ch, count ->
                        viewModel.startQuiz(cls, sub, ch, count)
                    }
                )

                is AppScreen.ActiveQuiz -> {
                    activeQuizState?.let { state ->
                        ActiveQuizScreen(
                            state = state,
                            onSelectAnswer = { qId, opt -> viewModel.selectQuizAnswer(qId, opt) },
                            onNext = { viewModel.goToNextQuestion() },
                            onPrevious = { viewModel.goToPreviousQuestion() },
                            onJumpTo = { idx -> viewModel.jumpToQuestion(idx) },
                            onSubmit = { viewModel.submitQuiz() },
                            onQuit = { viewModel.navigateBack() }
                        )
                    }
                }

                is AppScreen.QuizResultView -> QuizResultScreen(
                    result = screen.result,
                    onTryAgain = {
                        viewModel.startQuiz(
                            educationClass = screen.result.educationClass,
                            subject = screen.result.subject,
                            chapter = screen.result.chapter,
                            requestedCount = screen.result.totalQuestions
                        )
                    },
                    onBackToDashboard = {
                        viewModel.selectStudentTab(StudentTab.HOME)
                    }
                )

                is AppScreen.PaperBuilder -> PaperBuilderScreen(
                    draft = activeDraft,
                    validationResult = paperValidation,
                    initialStep = screen.step,
                    onBackClick = { viewModel.navigateBack() },
                    onUpdateExamDetails = { school, title, type, date, duration, targetMarks, eduClass, sub ->
                        viewModel.updatePaperExamDetails(school, title, type, date, duration, targetMarks, eduClass, sub)
                    },
                    onAddQuestionToSection = { secIdx, q ->
                        viewModel.addQuestionToSection(secIdx, q)
                    },
                    onRemoveQuestionFromSection = { secIdx, qId ->
                        viewModel.removeQuestionFromSection(secIdx, qId)
                    },
                    onAddCustomQuestion = { secIdx, text, type, opts, correctIdx, marks, solution ->
                        viewModel.addCustomQuestion(secIdx, text, type, opts, correctIdx, marks, solution)
                    },
                    onUpdateSectionConfig = { secIdx, req, marksPerQ ->
                        viewModel.updateSectionConfig(secIdx, req, marksPerQ)
                    },
                    onSaveDraft = { viewModel.saveDraftToDatabase() },
                    onPreviewPaper = { draft -> viewModel.navigateTo(AppScreen.PaperPreview(draft)) },
                    onPreviewAnswerKey = { draft -> viewModel.navigateTo(AppScreen.AnswerKeyPreview(draft)) }
                )

                is AppScreen.PaperPreview -> PaperPrintPreviewScreen(
                    draft = screen.draft,
                    onBackClick = { viewModel.navigateBack() }
                )

                is AppScreen.AnswerKeyPreview -> AnswerKeyPreviewScreen(
                    draft = screen.draft,
                    onBackClick = { viewModel.navigateBack() }
                )
            }
        }
    }
}

@Composable
fun StudentBottomBar(
    selectedTab: StudentTab,
    onTabSelected: (StudentTab) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == StudentTab.HOME,
            onClick = { onTabSelected(StudentTab.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            modifier = Modifier.testTag("student_tab_home")
        )
        NavigationBarItem(
            selected = selectedTab == StudentTab.SUBJECTS,
            onClick = { onTabSelected(StudentTab.SUBJECTS) },
            icon = { Icon(Icons.Default.MenuBook, contentDescription = "Subjects") },
            label = { Text("Subjects") },
            modifier = Modifier.testTag("student_tab_subjects")
        )
        NavigationBarItem(
            selected = selectedTab == StudentTab.PRACTICE,
            onClick = { onTabSelected(StudentTab.PRACTICE) },
            icon = { Icon(Icons.Default.Quiz, contentDescription = "Practice") },
            label = { Text("Practice") },
            modifier = Modifier.testTag("student_tab_practice")
        )
        NavigationBarItem(
            selected = selectedTab == StudentTab.PROGRESS,
            onClick = { onTabSelected(StudentTab.PROGRESS) },
            icon = { Icon(Icons.Default.TrendingUp, contentDescription = "Progress") },
            label = { Text("Progress") },
            modifier = Modifier.testTag("student_tab_progress")
        )
        NavigationBarItem(
            selected = selectedTab == StudentTab.SETTINGS,
            onClick = { onTabSelected(StudentTab.SETTINGS) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            modifier = Modifier.testTag("student_tab_settings")
        )
    }
}

@Composable
fun TeacherBottomBar(
    selectedTab: TeacherTab,
    onTabSelected: (TeacherTab) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == TeacherTab.HOME,
            onClick = { onTabSelected(TeacherTab.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            modifier = Modifier.testTag("teacher_tab_home")
        )
        NavigationBarItem(
            selected = selectedTab == TeacherTab.QUESTION_BANK,
            onClick = { onTabSelected(TeacherTab.QUESTION_BANK) },
            icon = { Icon(Icons.Default.MenuBook, contentDescription = "Bank") },
            label = { Text("Bank") },
            modifier = Modifier.testTag("teacher_tab_bank")
        )
        NavigationBarItem(
            selected = selectedTab == TeacherTab.MY_PAPERS,
            onClick = { onTabSelected(TeacherTab.MY_PAPERS) },
            icon = { Icon(Icons.Default.Assignment, contentDescription = "My Papers") },
            label = { Text("My Papers") },
            modifier = Modifier.testTag("teacher_tab_papers")
        )
        NavigationBarItem(
            selected = selectedTab == TeacherTab.SETTINGS,
            onClick = { onTabSelected(TeacherTab.SETTINGS) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            modifier = Modifier.testTag("teacher_tab_settings")
        )
    }
}
