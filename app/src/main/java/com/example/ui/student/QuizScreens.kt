package com.example.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SampleData
import com.example.domain.model.Difficulty
import com.example.domain.model.EducationClass
import com.example.domain.model.Question
import com.example.domain.model.QuestionType
import com.example.domain.model.QuizResult
import com.example.domain.model.SubjectType
import com.example.ui.components.AppHeader
import com.example.ui.components.SampleDataNotice
import com.example.viewmodel.ActiveQuizState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizConfigScreen(
    currentClass: EducationClass,
    preselectedSubject: SubjectType? = null,
    preselectedChapter: String? = null,
    onBackClick: () -> Unit,
    onStartQuiz: (EducationClass, SubjectType, String?, Int) -> Unit
) {
    var selectedClass by remember { mutableStateOf(currentClass) }
    var selectedSubject by remember { mutableStateOf(preselectedSubject ?: SubjectType.COMPUTER_SCIENCE) }
    var selectedChapter by remember { mutableStateOf(preselectedChapter ?: "All Chapters") }
    var requestedCount by remember { mutableIntStateOf(10) }

    val chapters = listOf("All Chapters") + SampleData.chapters.filter {
        it.educationClass == selectedClass && it.subject == selectedSubject
    }.map { it.title }

    // Count available MCQs in sample bank for this selection
    val availableQuestions = SampleData.questions.filter {
        it.educationClass == selectedClass &&
        it.subject == selectedSubject &&
        it.questionType == QuestionType.MCQ &&
        (selectedChapter == "All Chapters" || it.chapter.equals(selectedChapter, ignoreCase = true))
    }
    val effectiveCount = minOf(requestedCount, availableQuestions.size)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppHeader(
            title = "Board MCQ Quiz Setup",
            subtitle = "Custom Exam Practice Configuration",
            onBackClick = onBackClick
        )

        SampleDataNotice()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Select Class
            item {
                Text(
                    text = "1. Select Class",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    EducationClass.entries.forEach { eduClass ->
                        val isSelected = selectedClass == eduClass
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedClass = eduClass
                                selectedChapter = "All Chapters"
                            },
                            label = { Text(eduClass.displayName) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Select Subject
            item {
                Text(
                    text = "2. Select Subject",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SubjectType.entries.chunked(2).forEach { rowSubjects ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowSubjects.forEach { sub ->
                                val isSelected = selectedSubject == sub
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedSubject = sub
                                        selectedChapter = "All Chapters"
                                    },
                                    label = { Text(sub.title) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Select Chapter
            item {
                Text(
                    text = "3. Select Chapter / Scope",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(chapters) { ch ->
                        val isSelected = selectedChapter == ch
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedChapter = ch },
                            label = { Text(ch) }
                        )
                    }
                }
            }

            // Target Question Count
            item {
                Text(
                    text = "4. Number of Questions",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(10, 25, 50, 100).forEach { count ->
                        val isSelected = requestedCount == count
                        FilterChip(
                            selected = isSelected,
                            onClick = { requestedCount = count },
                            label = { Text("$count Qs") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Available Count Notice Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Question Bank Availability",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (availableQuestions.isEmpty()) {
                                "Currently no MCQs loaded for this specific selection. Try 'All Chapters' or switch subject."
                            } else if (availableQuestions.size < requestedCount) {
                                "Sample bank currently contains ${availableQuestions.size} MCQs for this scope (fewer than $requestedCount). The quiz will proceed with all ${availableQuestions.size} available questions."
                            } else {
                                "Full test ready: $requestedCount high-yield board exam MCQs will be selected."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Start Quiz Button
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val finalChapter = if (selectedChapter == "All Chapters") null else selectedChapter
                        onStartQuiz(selectedClass, selectedSubject, finalChapter, effectiveCount)
                    },
                    enabled = availableQuestions.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_quiz_confirm_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (availableQuestions.isNotEmpty()) "Start Quiz (${effectiveCount} Questions)" else "No Questions Available",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveQuizScreen(
    state: ActiveQuizState,
    onSelectAnswer: (String, Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onJumpTo: (Int) -> Unit,
    onSubmit: () -> Unit,
    onQuit: () -> Unit
) {
    var showConfirmSubmitDialog by remember { mutableStateOf(false) }
    var showQuitDialog by remember { mutableStateOf(false) }

    val currentQuestion = state.questions.getOrNull(state.currentIndex)
    val totalCount = state.questions.size
    val answeredCount = state.userAnswers.values.count { it != null }
    val progress = if (totalCount > 0) (state.currentIndex + 1).toFloat() / totalCount else 0f

    if (currentQuestion == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active questions.")
        }
        return
    }

    val selectedOption = state.userAnswers[currentQuestion.id]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Active Quiz Top Bar
        AppHeader(
            title = "${state.subject.title} Quiz",
            subtitle = "Question ${state.currentIndex + 1} of $totalCount • ${state.educationClass.shortName}",
            onBackClick = { showQuitDialog = true },
            actions = {
                TextButton(
                    onClick = { showConfirmSubmitDialog = true },
                    modifier = Modifier.testTag("active_quiz_submit_action")
                ) {
                    Text(
                        text = "Submit",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        )

        // Progress bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Question Palette Navigation Strip
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question Palette ($answeredCount/$totalCount Answered)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(state.questions) { index, q ->
                        val isAnswered = state.userAnswers[q.id] != null
                        val isCurrent = state.currentIndex == index

                        val bgColor = when {
                            isCurrent -> MaterialTheme.colorScheme.primary
                            isAnswered -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }

                        val textColor = when {
                            isCurrent || isAnswered -> Color.White
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(bgColor)
                                .clickable { onJumpTo(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = textColor
                            )
                        }
                    }
                }
            }

            // Question Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Q${state.currentIndex + 1}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = currentQuestion.boardYearMention,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currentQuestion.questionText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                lineHeight = 24.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Four Selectable Answer Options
            item {
                Text(
                    text = "Select one answer option:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            itemsIndexed(currentQuestion.options) { index, optionText ->
                val isSelected = selectedOption == index
                val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface

                Card(
                    onClick = { onSelectAnswer(currentQuestion.id, index) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    elevation = CardDefaults.cardElevation(if (isSelected) 3.dp else 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .testTag("quiz_option_${index}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${('A' + index)}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Navigation Buttons (Previous, Next, Finish)
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onPrevious,
                        enabled = state.currentIndex > 0,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("quiz_previous_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Previous")
                    }

                    if (state.currentIndex < totalCount - 1) {
                        Button(
                            onClick = onNext,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("quiz_next_button")
                        ) {
                            Text("Next")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Button(
                            onClick = { showConfirmSubmitDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("quiz_finish_button")
                        ) {
                            Text("Finish Test")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

    // Confirmation dialog before final submission
    if (showConfirmSubmitDialog) {
        val skippedCount = totalCount - answeredCount
        AlertDialog(
            onDismissRequest = { showConfirmSubmitDialog = false },
            icon = {
                Icon(
                    imageVector = if (skippedCount > 0) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (skippedCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Submit Quiz Attempt?") },
            text = {
                Column {
                    Text("Total Questions: $totalCount")
                    Text("Answered: $answeredCount")
                    if (skippedCount > 0) {
                        Text(
                            text = "Unanswered / Skipped: $skippedCount",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Are you ready to calculate your final score?")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmSubmitDialog = false
                        onSubmit()
                    },
                    modifier = Modifier.testTag("confirm_submit_quiz_button")
                ) {
                    Text("Yes, Calculate Score")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmSubmitDialog = false }) {
                    Text("Review Questions")
                }
            }
        )
    }

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = { Text("Quit Quiz?") },
            text = { Text("Your current quiz progress for this session will be discarded.") },
            confirmButton = {
                Button(
                    onClick = {
                        showQuitDialog = false
                        onQuit()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Quit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) {
                    Text("Continue Quiz")
                }
            }
        )
    }
}

@Composable
fun QuizResultScreen(
    result: QuizResult,
    onTryAgain: () -> Unit,
    onBackToDashboard: () -> Unit
) {
    var filterIncorrectOnly by remember { mutableStateOf(false) }

    val displayedQuestions = if (filterIncorrectOnly) {
        result.questions.filter {
            result.userAnswers[it.id] != it.correctOptionIndex
        }
    } else {
        result.questions
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppHeader(
            title = "Quiz Results & Analysis",
            subtitle = "${result.subject.title} • ${result.educationClass.shortName}",
            onBackClick = onBackToDashboard
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Score Summary Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = if (result.scorePercentage >= 70) Color(0xFFD1FAE5) else Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = if (result.scorePercentage >= 80) "Outstanding Performance!"
                                else if (result.scorePercentage >= 60) "Good Effort - Ready for Revision"
                                else "Needs More Preparation",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (result.scorePercentage >= 70) Color(0xFF065F46) else Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "${result.scorePercentage}%",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )

                        Text(
                            text = "${result.correctCount} out of ${result.totalQuestions} questions correct",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Breakdown Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatBox(label = "Correct", count = "${result.correctCount}", color = Color(0xFF059669))
                            StatBox(label = "Incorrect", count = "${result.incorrectCount}", color = Color(0xFFDC2626))
                            StatBox(label = "Skipped", count = "${result.skippedCount}", color = Color(0xFFD97706))
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onTryAgain,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("quiz_try_again_button")
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Try Again")
                            }

                            OutlinedButton(
                                onClick = onBackToDashboard,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Dashboard")
                            }
                        }
                    }
                }
            }

            // Question Review Filter
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question Review",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    FilterChip(
                        selected = filterIncorrectOnly,
                        onClick = { filterIncorrectOnly = !filterIncorrectOnly },
                        label = { Text("Only Mistakes") },
                        modifier = Modifier.testTag("filter_mistakes_chip")
                    )
                }
            }

            itemsIndexed(displayedQuestions) { idx, q ->
                val userChoice = result.userAnswers[q.id]
                val isCorrect = userChoice == q.correctOptionIndex
                val isSkipped = userChoice == null

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Question ${idx + 1}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Surface(
                                color = when {
                                    isCorrect -> Color(0xFFD1FAE5)
                                    isSkipped -> Color(0xFFFEF3C7)
                                    else -> Color(0xFFFEE2E2)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = when {
                                        isCorrect -> "Correct"
                                        isSkipped -> "Skipped"
                                        else -> "Incorrect"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = when {
                                        isCorrect -> Color(0xFF065F46)
                                        isSkipped -> Color(0xFF92400E)
                                        else -> Color(0xFF991B1B)
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = q.questionText,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        q.options.forEachIndexed { optIdx, optText ->
                            val isThisCorrect = optIdx == q.correctOptionIndex
                            val isThisUserChoice = userChoice == optIdx

                            val optBg = when {
                                isThisCorrect -> Color(0xFFD1FAE5)
                                isThisUserChoice && !isCorrect -> Color(0xFFFEE2E2)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            }

                            val optTextCol = when {
                                isThisCorrect -> Color(0xFF065F46)
                                isThisUserChoice && !isCorrect -> Color(0xFF991B1B)
                                else -> MaterialTheme.colorScheme.onSurface
                            }

                            Surface(
                                color = optBg,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${('A' + optIdx)}. $optText",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (isThisCorrect || isThisUserChoice) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = optTextCol,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isThisUserChoice && !isCorrect) {
                                        Text(
                                            text = "(Your choice)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF991B1B)
                                        )
                                    } else if (isThisCorrect) {
                                        Text(
                                            text = "(Correct)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF065F46)
                                        )
                                    }
                                }
                            }
                        }

                        if (q.explanation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Explanation: ${q.explanation}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(label: String, count: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
