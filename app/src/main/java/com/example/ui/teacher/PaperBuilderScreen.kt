package com.example.ui.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SampleData
import com.example.domain.model.Difficulty
import com.example.domain.model.EducationClass
import com.example.domain.model.PaperDraft
import com.example.domain.model.PaperSection
import com.example.domain.model.PaperValidationResult
import com.example.domain.model.Question
import com.example.domain.model.QuestionType
import com.example.domain.model.SubjectType
import com.example.ui.components.AppHeader
import com.example.ui.components.SampleDataNotice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaperBuilderScreen(
    draft: PaperDraft,
    validationResult: PaperValidationResult,
    initialStep: Int = 1,
    onBackClick: () -> Unit,
    onUpdateExamDetails: (String, String, String, String, Int, Int, EducationClass, SubjectType) -> Unit,
    onAddQuestionToSection: (Int, Question) -> Unit,
    onRemoveQuestionFromSection: (Int, String) -> Unit,
    onAddCustomQuestion: (Int, String, QuestionType, List<String>, Int, Int, String) -> Unit,
    onUpdateSectionConfig: (Int, Int, Int) -> Unit,
    onSaveDraft: () -> Unit,
    onPreviewPaper: (PaperDraft) -> Unit,
    onPreviewAnswerKey: (PaperDraft) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(initialStep) }
    val stepTitles = listOf("1. Details", "2. Section A (MCQs)", "3. Section B/C", "4. Review & Validate", "5. Save & Layout")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppHeader(
            title = "Exam Paper Builder",
            subtitle = "${draft.subject.title} • ${draft.educationClass.shortName} • Step $currentStep of 5",
            onBackClick = onBackClick
        )

        SampleDataNotice("Teacher Mode: Ensure total allocated marks match target board marks before printing.")

        // Steps Tab Bar
        ScrollableTabRow(
            selectedTabIndex = currentStep - 1,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            stepTitles.forEachIndexed { index, title ->
                Tab(
                    selected = currentStep == index + 1,
                    onClick = { currentStep = index + 1 },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (currentStep == index + 1) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    },
                    modifier = Modifier.testTag("paper_builder_tab_${index + 1}")
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (currentStep) {
                1 -> Step1ExamDetailsView(
                    draft = draft,
                    onSave = { school, title, type, date, duration, targetMarks, eduClass, sub ->
                        onUpdateExamDetails(school, title, type, date, duration, targetMarks, eduClass, sub)
                        currentStep = 2
                    }
                )
                2 -> Step2AddMcqsView(
                    draft = draft,
                    onAddQuestion = { q -> onAddQuestionToSection(0, q) },
                    onRemoveQuestion = { qId -> onRemoveQuestionFromSection(0, qId) },
                    onAddCustomMcq = { text, opts, correctIdx, marks ->
                        onAddCustomQuestion(0, text, QuestionType.MCQ, opts, correctIdx, marks, "")
                    },
                    onUpdateConfig = { req, marksPerQ -> onUpdateSectionConfig(0, req, marksPerQ) },
                    onNext = { currentStep = 3 }
                )
                3 -> Step3ShortAndLongQuestionsView(
                    draft = draft,
                    onAddQuestion = { secIdx, q -> onAddQuestionToSection(secIdx, q) },
                    onRemoveQuestion = { secIdx, qId -> onRemoveQuestionFromSection(secIdx, qId) },
                    onAddCustomQuestion = onAddCustomQuestion,
                    onUpdateConfig = onUpdateSectionConfig,
                    onNext = { currentStep = 4 }
                )
                4 -> Step4ReviewAndValidateView(
                    draft = draft,
                    validation = validationResult,
                    onRemoveQuestion = onRemoveQuestionFromSection,
                    onNext = { currentStep = 5 }
                )
                5 -> Step5SaveAndPreviewView(
                    draft = draft,
                    validation = validationResult,
                    onSave = onSaveDraft,
                    onPreviewPaper = { onPreviewPaper(draft) },
                    onPreviewAnswerKey = { onPreviewAnswerKey(draft) }
                )
            }
        }
    }
}

// === STEP 1: EXAM DETAILS ===
@Composable
fun Step1ExamDetailsView(
    draft: PaperDraft,
    onSave: (String, String, String, String, Int, Int, EducationClass, SubjectType) -> Unit
) {
    var schoolName by remember { mutableStateOf(draft.schoolName) }
    var examTitle by remember { mutableStateOf(draft.examTitle) }
    var examType by remember { mutableStateOf(draft.examType) }
    var examDate by remember { mutableStateOf(draft.examDate) }
    var durationText by remember { mutableStateOf(draft.durationMinutes.toString()) }
    var totalMarksText by remember { mutableStateOf(draft.totalMarksTarget.toString()) }
    var selectedClass by remember { mutableStateOf(draft.educationClass) }
    var selectedSubject by remember { mutableStateOf(draft.subject) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Step 1: Examination Header & Configuration",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            OutlinedTextField(
                value = schoolName,
                onValueChange = { schoolName = it },
                label = { Text("Institution / School Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("school_name_input")
            )
        }

        item {
            OutlinedTextField(
                value = examTitle,
                onValueChange = { examTitle = it },
                label = { Text("Exam Title (e.g. Send-Up Exam 2026)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("exam_title_input")
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = examType,
                    onValueChange = { examType = it },
                    label = { Text("Exam Type") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = examDate,
                    onValueChange = { examDate = it },
                    label = { Text("Exam Date") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Duration (Mins)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = totalMarksText,
                    onValueChange = { totalMarksText = it },
                    label = { Text("Target Total Marks") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("total_marks_input")
                )
            }
        }

        item {
            Text(
                text = "Select Class",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EducationClass.entries.forEach { eduClass ->
                    FilterChip(
                        selected = selectedClass == eduClass,
                        onClick = { selectedClass = eduClass },
                        label = { Text(eduClass.displayName) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Text(
                text = "Select Subject",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SubjectType.entries.take(2).forEach { sub ->
                    FilterChip(
                        selected = selectedSubject == sub,
                        onClick = { selectedSubject = sub },
                        label = { Text(sub.title) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SubjectType.entries.drop(2).forEach { sub ->
                    FilterChip(
                        selected = selectedSubject == sub,
                        onClick = { selectedSubject = sub },
                        label = { Text(sub.title) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    val duration = durationText.toIntOrNull() ?: 90
                    val targetMarks = totalMarksText.toIntOrNull() ?: 50
                    onSave(schoolName, examTitle, examType, examDate, duration, targetMarks, selectedClass, selectedSubject)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("step1_save_next_button")
            ) {
                Text("Save Details & Proceed to Section A (MCQs)", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// === STEP 2: ADD MCQS (SECTION A) ===
@Composable
fun Step2AddMcqsView(
    draft: PaperDraft,
    onAddQuestion: (Question) -> Unit,
    onRemoveQuestion: (String) -> Unit,
    onAddCustomMcq: (String, List<String>, Int, Int) -> Unit,
    onUpdateConfig: (Int, Int) -> Unit,
    onNext: () -> Unit
) {
    var showCustomDialog by remember { mutableStateOf(false) }
    val sectionA = draft.sections.getOrNull(0)
    val selectedQuestions = sectionA?.questions ?: emptyList()

    // Available sample MCQs for this class and subject
    val availableBank = SampleData.questions.filter {
        it.educationClass == draft.educationClass &&
        it.subject == draft.subject &&
        it.questionType == QuestionType.MCQ
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Section A: Multiple Choice Questions",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Selected: ${selectedQuestions.size} / ${sectionA?.requiredQuestionsCount ?: 10} MCQs (${sectionA?.totalSectionMarks ?: 0} Marks)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Button(
                    onClick = { showCustomDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.testTag("write_custom_mcq_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Write Custom", fontSize = 12.sp)
                }
            }
        }

        // Section A Configuration
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Attempt Count: ${sectionA?.requiredQuestionsCount ?: 10} • 1 Mark per MCQ",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }

        item {
            Text(
                text = "Select MCQs from Question Bank:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(availableBank) { q ->
            val isAlreadyAdded = selectedQuestions.any { it.id == q.id }
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAlreadyAdded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = q.questionText,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${q.chapter} • ${q.boardYearMention}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (isAlreadyAdded) {
                        IconButton(
                            onClick = { onRemoveQuestion(q.id) },
                            modifier = Modifier.testTag("remove_mcq_${q.id}")
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        Button(
                            onClick = { onAddQuestion(q) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("add_mcq_${q.id}")
                        ) {
                            Text("Add", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onNext,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("step2_proceed_button")
            ) {
                Text("Proceed to Section B & C (Short/Long)", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }

    if (showCustomDialog) {
        AddCustomMcqDialog(
            onDismiss = { showCustomDialog = false },
            onAdd = { text, opts, correctIdx, marks ->
                onAddCustomMcq(text, opts, correctIdx, marks)
                showCustomDialog = false
            }
        )
    }
}

// === STEP 3: ADD SHORT & LONG QUESTIONS ===
@Composable
fun Step3ShortAndLongQuestionsView(
    draft: PaperDraft,
    onAddQuestion: (Int, Question) -> Unit,
    onRemoveQuestion: (Int, String) -> Unit,
    onAddCustomQuestion: (Int, String, QuestionType, List<String>, Int, Int, String) -> Unit,
    onUpdateConfig: (Int, Int, Int) -> Unit,
    onNext: () -> Unit
) {
    var showCustomShortDialog by remember { mutableStateOf(false) }
    var selectedSectionTarget by remember { mutableIntStateOf(1) } // 1 for Section B, 2 for Section C

    val sectionB = draft.sections.getOrNull(1)
    val sectionC = draft.sections.getOrNull(2)

    val availableShorts = SampleData.questions.filter {
        it.educationClass == draft.educationClass &&
        it.subject == draft.subject &&
        it.questionType == QuestionType.SHORT
    }

    val availableLongs = SampleData.questions.filter {
        it.educationClass == draft.educationClass &&
        it.subject == draft.subject &&
        it.questionType == QuestionType.LONG
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Step 3: Section B (Short Qs) & Section C (Long Qs)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Section B Status Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = sectionB?.sectionName ?: "Section B: Short Questions",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${sectionB?.totalSectionMarks ?: 0} Marks",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Questions added: ${sectionB?.questions?.size ?: 0} (Required to attempt: ${sectionB?.requiredQuestionsCount ?: 5})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                text = "Add Short Questions to Section B:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(availableShorts) { q ->
            val isAlreadyAdded = sectionB?.questions?.any { it.id == q.id } == true
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAlreadyAdded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = q.questionText,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "${q.chapter} • ${q.marks} Marks • ${q.boardYearMention}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (isAlreadyAdded) {
                        IconButton(onClick = { onRemoveQuestion(1, q.id) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        Button(
                            onClick = { onAddQuestion(1, q) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("add_short_q_${q.id}")
                        ) {
                            Text("Add", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Section C Long Questions
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = sectionC?.sectionName ?: "Section C: Long Questions",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${sectionC?.totalSectionMarks ?: 0} Marks",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Questions added: ${sectionC?.questions?.size ?: 0} (Required to attempt: ${sectionC?.requiredQuestionsCount ?: 2})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(availableLongs) { q ->
            val isAlreadyAdded = sectionC?.questions?.any { it.id == q.id } == true
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAlreadyAdded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = q.questionText,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "${q.chapter} • ${q.marks} Marks • ${q.boardYearMention}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (isAlreadyAdded) {
                        IconButton(onClick = { onRemoveQuestion(2, q.id) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        Button(
                            onClick = { onAddQuestion(2, q) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("add_long_q_${q.id}")
                        ) {
                            Text("Add", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onNext,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("step3_proceed_button")
            ) {
                Text("Proceed to Review & Marks Validation", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// === STEP 4: REVIEW & VALIDATE ===
@Composable
fun Step4ReviewAndValidateView(
    draft: PaperDraft,
    validation: PaperValidationResult,
    onRemoveQuestion: (Int, String) -> Unit,
    onNext: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Step 4: Paper Review & Board Marks Validation",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Live Validation Status Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (validation.isValid) Color(0xFFD1FAE5) else Color(0xFFFEF3C7)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("marks_validation_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (validation.isValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (validation.isValid) Color(0xFF065F46) else Color(0xFF92400E)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (validation.isValid) "Marks Validation Passed (✓)" else "Marks Validation Warning",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (validation.isValid) Color(0xFF065F46) else Color(0xFF92400E)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Target Total Marks: ${validation.targetMarks} • Calculated Allocated: ${validation.calculatedMarks} Marks",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = if (validation.isValid) Color(0xFF065F46) else Color(0xFF92400E)
                    )

                    if (validation.errors.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        validation.errors.forEach { err ->
                            Text(
                                text = "• $err",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF991B1B)
                            )
                        }
                    }

                    if (validation.warnings.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        validation.warnings.forEach { warn ->
                            Text(
                                text = "• $warn",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF92400E)
                            )
                        }
                    }
                }
            }
        }

        // Exam Header Summary
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = draft.schoolName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${draft.examTitle} • ${draft.educationClass.shortName} • ${draft.subject.title}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Time Allowed: ${draft.durationMinutes} Minutes • Max Marks: ${draft.totalMarksTarget}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section-by-Section Breakdown
        draft.sections.forEachIndexed { secIdx, section ->
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = section.sectionName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${section.totalSectionMarks} Marks",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            itemsIndexed(section.questions) { qIdx, q ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Q${qIdx + 1}.",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.width(32.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = q.questionText,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        IconButton(onClick = { onRemoveQuestion(secIdx, q.id) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onNext,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("step4_proceed_button")
            ) {
                Text("Proceed to Save & Paper Layouts", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// === STEP 5: SAVE & PREVIEW ===
@Composable
fun Step5SaveAndPreviewView(
    draft: PaperDraft,
    validation: PaperValidationResult,
    onSave: () -> Unit,
    onPreviewPaper: () -> Unit,
    onPreviewAnswerKey: () -> Unit
) {
    var savedMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Step 5: Finalize, Save & Generate Layouts",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Save Draft Action
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Save Paper Draft Locally",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Saves this exam configuration, sections, and questions to persistent local Room database so you can reopen, modify, or duplicate anytime.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            onSave()
                            savedMessage = "Paper draft saved successfully to database!"
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_draft_button")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Draft to Local Database")
                    }

                    if (savedMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = savedMessage!!,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF059669)
                        )
                    }
                }
            }
        }

        // Printable Paper Preview
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "2. Student Question Paper Layout",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Formatted for classroom distribution and print. Strictly excludes answers and marking keys.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onPreviewPaper,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("preview_student_paper_button")
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Preview Printable Question Paper")
                    }
                }
            }
        }

        // Separate Answer Key Preview
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "3. Confidential Teacher Answer Key",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Generates a separate marking sheet containing correct MCQ choices and rubric solutions for examiners.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onPreviewAnswerKey,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("preview_answer_key_button")
                    ) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Preview Teacher Answer Key")
                    }
                }
            }
        }
    }
}

@Composable
fun AddCustomMcqDialog(
    onDismiss: () -> Unit,
    onAdd: (String, List<String>, Int, Int) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var optA by remember { mutableStateOf("") }
    var optB by remember { mutableStateOf("") }
    var optC by remember { mutableStateOf("") }
    var optD by remember { mutableStateOf("") }
    var correctIndex by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Write Custom MCQ") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Question Statement") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = optA,
                        onValueChange = { optA = it },
                        label = { Text("Option A") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = optB,
                        onValueChange = { optB = it },
                        label = { Text("Option B") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = optC,
                        onValueChange = { optC = it },
                        label = { Text("Option C") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = optD,
                        onValueChange = { optD = it },
                        label = { Text("Option D") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text("Select Correct Option:", style = MaterialTheme.typography.labelSmall)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        listOf("A", "B", "C", "D").forEachIndexed { idx, letter ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = correctIndex == idx,
                                    onClick = { correctIndex = idx }
                                )
                                Text(letter)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank() && optA.isNotBlank() && optB.isNotBlank()) {
                        onAdd(text, listOf(optA, optB, optC, optD), correctIndex, 1)
                    }
                },
                enabled = text.isNotBlank() && optA.isNotBlank() && optB.isNotBlank()
            ) {
                Text("Add to Section A")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
