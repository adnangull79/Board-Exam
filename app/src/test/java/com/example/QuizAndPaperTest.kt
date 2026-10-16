package com.example

import com.example.data.repository.SampleData
import com.example.domain.model.Difficulty
import com.example.domain.model.EducationClass
import com.example.domain.model.PaperDraft
import com.example.domain.model.PaperSection
import com.example.domain.model.Question
import com.example.domain.model.QuestionType
import com.example.domain.model.SubjectType
import com.example.domain.paper.PaperValidator
import com.example.domain.quiz.QuizScorer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizAndPaperTest {

    private val sampleQuestions = listOf(
        Question(
            id = "q1",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.COMPUTER_SCIENCE,
            chapter = "Fundamentals of Computer",
            chapterNumber = 1,
            questionType = QuestionType.MCQ,
            questionText = "Question 1",
            options = listOf("A", "B", "C", "D"),
            correctOptionIndex = 1 // 'B'
        ),
        Question(
            id = "q2",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.COMPUTER_SCIENCE,
            chapter = "Fundamentals of Computer",
            chapterNumber = 1,
            questionType = QuestionType.MCQ,
            questionText = "Question 2",
            options = listOf("A", "B", "C", "D"),
            correctOptionIndex = 0 // 'A'
        ),
        Question(
            id = "q3",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.COMPUTER_SCIENCE,
            chapter = "Fundamentals of Computer",
            chapterNumber = 1,
            questionType = QuestionType.MCQ,
            questionText = "Question 3",
            options = listOf("A", "B", "C", "D"),
            correctOptionIndex = 2 // 'C'
        ),
        Question(
            id = "q4",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.COMPUTER_SCIENCE,
            chapter = "Fundamentals of Computer",
            chapterNumber = 1,
            questionType = QuestionType.MCQ,
            questionText = "Question 4",
            options = listOf("A", "B", "C", "D"),
            correctOptionIndex = 3 // 'D'
        )
    )

    @Test
    fun testQuizScoringCalculation() {
        val userAnswers = mapOf(
            "q1" to 1, // Correct
            "q2" to 3, // Incorrect (correct is 0)
            "q3" to 2  // Correct
            // q4 is omitted -> Skipped
        )

        val result = QuizScorer.evaluateQuiz(
            questions = sampleQuestions,
            answers = userAnswers,
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.COMPUTER_SCIENCE,
            chapter = "Fundamentals of Computer",
            durationSeconds = 45
        )

        assertEquals(4, result.totalQuestions)
        assertEquals(2, result.correctCount)
        assertEquals(1, result.incorrectCount)
        assertEquals(1, result.skippedCount)
        assertEquals(50, result.scorePercentage)
    }

    @Test
    fun testQuizQuestionSelectionAndFiltering() {
        val selected = QuizScorer.prepareQuizQuestions(
            allQuestions = sampleQuestions,
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.COMPUTER_SCIENCE,
            chapter = "Fundamentals of Computer",
            requestedCount = 2
        )

        assertEquals(2, selected.size)
        // Ensure no duplicate IDs
        assertEquals(2, selected.map { it.id }.distinct().size)
    }

    @Test
    fun testPaperValidation_SuccessWhenMarksMatch() {
        val secA = PaperSection(
            id = "secA",
            sectionName = "Section A: MCQs",
            instructions = "Attempt all",
            questionType = QuestionType.MCQ,
            marksPerQuestion = 1,
            requiredQuestionsCount = 2,
            questions = sampleQuestions.take(2)
        )
        val secB = PaperSection(
            id = "secB",
            sectionName = "Section B: Short",
            instructions = "Attempt all",
            questionType = QuestionType.SHORT,
            marksPerQuestion = 4,
            requiredQuestionsCount = 2,
            questions = listOf(
                Question("sq1", EducationClass.CLASS_9, SubjectType.COMPUTER_SCIENCE, "Ch 1", 1, QuestionType.SHORT, "Short 1", marks = 4),
                Question("sq2", EducationClass.CLASS_9, SubjectType.COMPUTER_SCIENCE, "Ch 1", 1, QuestionType.SHORT, "Short 2", marks = 4)
            )
        )

        val draft = PaperDraft(
            id = "draft_1",
            schoolName = "Federal Model School",
            examTitle = "Mid-Term Exam",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.COMPUTER_SCIENCE,
            examType = "Terminal",
            examDate = "15-May-2026",
            durationMinutes = 60,
            totalMarksTarget = 10, // SecA: 2*1=2, SecB: 2*4=8 => Total = 10
            sections = listOf(secA, secB)
        )

        val validation = PaperValidator.validatePaper(draft)
        assertTrue(validation.isValid)
        assertEquals(10, validation.calculatedMarks)
        assertEquals(0, validation.errors.size)
    }

    @Test
    fun testPaperValidation_DetectsMarksMismatch() {
        val secA = PaperSection(
            id = "secA",
            sectionName = "Section A: MCQs",
            instructions = "Attempt all",
            questionType = QuestionType.MCQ,
            marksPerQuestion = 1,
            requiredQuestionsCount = 2,
            questions = sampleQuestions.take(2)
        )

        val draft = PaperDraft(
            id = "draft_mismatch",
            schoolName = "Federal Model School",
            examTitle = "Mid-Term Exam",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.COMPUTER_SCIENCE,
            examType = "Terminal",
            examDate = "15-May-2026",
            durationMinutes = 60,
            totalMarksTarget = 50, // Only 2 allocated, target is 50
            sections = listOf(secA)
        )

        val validation = PaperValidator.validatePaper(draft)
        assertFalse(validation.isValid)
        assertTrue(validation.errors.any { it.contains("Marks mismatch") })
    }
}
