package com.example.ui.teacher

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SampleData
import com.example.domain.model.EducationClass
import com.example.domain.model.Question
import com.example.domain.model.QuestionType
import com.example.domain.model.SubjectType
import com.example.ui.components.AppHeader
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.SampleDataNotice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionBankScreen(
    onBackClick: () -> Unit
) {
    var selectedClass by remember { mutableStateOf(EducationClass.CLASS_9) }
    var selectedSubject by remember { mutableStateOf(SubjectType.COMPUTER_SCIENCE) }
    var selectedType by remember { mutableStateOf<QuestionType?>(null) } // null = All

    val filteredQuestions = SampleData.questions.filter {
        it.educationClass == selectedClass &&
        it.subject == selectedSubject &&
        (selectedType == null || it.questionType == selectedType)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppHeader(
            title = "Academic Question Bank",
            subtitle = "${filteredQuestions.size} Questions Available",
            onBackClick = onBackClick
        )

        SampleDataNotice()

        // Filter Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Class selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EducationClass.entries.forEach { eduClass ->
                    val isSelected = selectedClass == eduClass
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedClass = eduClass },
                        label = { Text(eduClass.shortName) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Subject selector
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SubjectType.entries) { sub ->
                    val isSelected = selectedSubject == sub
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSubject = sub },
                        label = { Text(sub.title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Question Type Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedType == null,
                    onClick = { selectedType = null },
                    label = { Text("All Types") }
                )
                FilterChip(
                    selected = selectedType == QuestionType.MCQ,
                    onClick = { selectedType = QuestionType.MCQ },
                    label = { Text("MCQs") }
                )
                FilterChip(
                    selected = selectedType == QuestionType.SHORT,
                    onClick = { selectedType = QuestionType.SHORT },
                    label = { Text("Short Qs") }
                )
                FilterChip(
                    selected = selectedType == QuestionType.LONG,
                    onClick = { selectedType = QuestionType.LONG },
                    label = { Text("Long Qs") }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredQuestions.isEmpty()) {
                item {
                    Text(
                        text = "No questions match the selected filter criteria.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(filteredQuestions) { q ->
                    QuestionBankItem(question = q)
                }
            }
        }
    }
}

@Composable
fun QuestionBankItem(question: Question) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${question.questionType.name} • ${question.marks} ${if (question.marks == 1) "Mark" else "Marks"}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    DifficultyBadge(difficulty = question.difficulty)
                }

                Text(
                    text = question.chapter,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (question.questionType == QuestionType.MCQ && question.options.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                question.options.forEachIndexed { idx, opt ->
                    val isCorrect = idx == question.correctOptionIndex
                    Text(
                        text = "${('A' + idx)}. $opt ${if (isCorrect) "✓ (Correct)" else ""}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isCorrect) Color(0xFF059669) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }
            }

            if (question.modelSolution.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Marking Scheme: ${question.modelSolution}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
