package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val classId: String,
    val subjectId: String,
    val chapter: String,
    val chapterNumber: Int,
    val questionType: String, // MCQ, SHORT, LONG
    val questionText: String,
    val optionsJson: String, // JSON array of strings
    val correctOptionIndex: Int,
    val explanation: String,
    val marks: Int,
    val difficulty: String,
    val boardYearMention: String,
    val modelSolution: String
)

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey val id: String,
    val classId: String,
    val subjectId: String,
    val chapter: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val skippedCount: Int,
    val scorePercentage: Int,
    val timeSpentSeconds: Int,
    val timestamp: Long
)

@Entity(tableName = "paper_drafts")
data class PaperDraftEntity(
    @PrimaryKey val id: String,
    val schoolName: String,
    val examTitle: String,
    val classId: String,
    val subjectId: String,
    val examType: String,
    val examDate: String,
    val durationMinutes: Int,
    val totalMarksTarget: Int,
    val sectionsJson: String, // JSON serialized sections
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)
