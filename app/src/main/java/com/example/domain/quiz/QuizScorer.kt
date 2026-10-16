package com.example.domain.quiz

import com.example.domain.model.EducationClass
import com.example.domain.model.Question
import com.example.domain.model.QuizResult
import com.example.domain.model.SubjectType
import java.util.UUID

object QuizScorer {

    /**
     * Calculates the quiz score, correct, incorrect, and skipped counts accurately.
     */
    fun evaluateQuiz(
        questions: List<Question>,
        answers: Map<String, Int?>,
        educationClass: EducationClass,
        subject: SubjectType,
        chapter: String,
        durationSeconds: Int
    ): QuizResult {
        var correct = 0
        var incorrect = 0
        var skipped = 0

        for (question in questions) {
            val selected = answers[question.id]
            when {
                selected == null -> skipped++
                selected == question.correctOptionIndex -> correct++
                else -> incorrect++
            }
        }

        val total = questions.size
        val percentage = if (total > 0) ((correct.toDouble() / total.toDouble()) * 100).toInt() else 0

        return QuizResult(
            attemptId = UUID.randomUUID().toString(),
            educationClass = educationClass,
            subject = subject,
            chapter = chapter,
            totalQuestions = total,
            correctCount = correct,
            incorrectCount = incorrect,
            skippedCount = skipped,
            scorePercentage = percentage,
            timeSpentSeconds = durationSeconds,
            questions = questions,
            userAnswers = answers
        )
    }

    /**
     * Filters and safely shuffles questions for a quiz attempt without altering answer mapping.
     */
    fun prepareQuizQuestions(
        allQuestions: List<Question>,
        educationClass: EducationClass,
        subject: SubjectType,
        chapter: String?,
        requestedCount: Int
    ): List<Question> {
        val filtered = allQuestions.filter {
            it.educationClass == educationClass &&
            it.subject == subject &&
            (chapter == null || chapter == "All Chapters" || it.chapter.equals(chapter, ignoreCase = true))
        }

        // Return up to requestedCount without duplication
        val selected = filtered.shuffled().distinctBy { it.id }.take(requestedCount)
        return selected
    }
}
