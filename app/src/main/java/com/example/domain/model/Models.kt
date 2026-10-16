package com.example.domain.model

enum class EducationClass(val id: String, val displayName: String, val shortName: String) {
    CLASS_9("class_9", "Class 9 (Matric Part-I)", "Class 9"),
    CLASS_11("class_11", "1st Year (HSSC Part-I)", "Class 11")
}

enum class SubjectType(val id: String, val title: String, val code: String) {
    COMPUTER_SCIENCE("cs", "Computer Science", "CS-101"),
    PHYSICS("phy", "Physics", "PHY-102"),
    CHEMISTRY("chem", "Chemistry", "CHM-103"),
    BIOLOGY("bio", "Biology", "BIO-104")
}

enum class BoardType(val id: String, val boardName: String, val region: String) {
    FEDERAL_BOARD("fbise", "Federal Board (FBISE)", "Islamabad & Overseas"),
    PUNJAB_BOARD("bise_punjab", "Punjab Board (BISE Lahore / RWP / GUJ)", "Punjab Province"),
    SINDH_BOARD("bise_sindh", "Sindh Board (BISE Karachi / HYD)", "Sindh Province"),
    KPK_BOARD("bise_kpk", "KPK Board (BISE Peshawar / ABB)", "Khyber Pakhtunkhwa")
}

enum class QuestionType {
    MCQ,
    SHORT,
    LONG
}

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD
}

data class Question(
    val id: String,
    val educationClass: EducationClass,
    val subject: SubjectType,
    val chapter: String,
    val chapterNumber: Int,
    val questionType: QuestionType,
    val questionText: String,
    val options: List<String> = emptyList(), // For MCQs (4 choices)
    val correctOptionIndex: Int = 0, // 0..3 for MCQs
    val explanation: String = "",
    val marks: Int = 1,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val boardYearMention: String = "Federal / Lahore Board past model",
    val modelSolution: String = "" // For Short/Long questions
)

data class ChapterInfo(
    val number: Int,
    val title: String,
    val subject: SubjectType,
    val educationClass: EducationClass,
    val totalMcqs: Int,
    val shortQuestionsCount: Int,
    val longQuestionsCount: Int,
    val highYieldSummary: String
)

data class StudyNote(
    val id: String,
    val title: String,
    val content: String,
    val keyPoints: List<String> = emptyList(),
    val formulasOrDefinitions: List<String> = emptyList()
)

data class PaperSection(
    val id: String,
    val sectionName: String, // e.g. "Section A: Multiple Choice Questions (Compulsory)"
    val instructions: String, // e.g. "Answer all questions. Each question carries 1 mark."
    val questionType: QuestionType,
    val marksPerQuestion: Int,
    val requiredQuestionsCount: Int, // e.g. "Attempt any 5" -> 5
    val questions: List<Question> = emptyList()
) {
    val totalSectionMarks: Int
        get() = requiredQuestionsCount * marksPerQuestion
}

data class PaperDraft(
    val id: String,
    val schoolName: String,
    val examTitle: String,
    val educationClass: EducationClass,
    val subject: SubjectType,
    val examType: String, // e.g., "First Term Exam", "Send-up Exam", "Mock Board Exam"
    val examDate: String,
    val durationMinutes: Int,
    val totalMarksTarget: Int,
    val sections: List<PaperSection>,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
) {
    val calculatedTotalMarks: Int
        get() = sections.sumOf { it.totalSectionMarks }

    val isValidMarks: Boolean
        get() = calculatedTotalMarks == totalMarksTarget
}

data class PaperValidationResult(
    val isValid: Boolean,
    val calculatedMarks: Int,
    val targetMarks: Int,
    val errors: List<String>,
    val warnings: List<String>
)

data class QuizSubmission(
    val questionId: String,
    val selectedOptionIndex: Int? // null if skipped
)

data class QuizResult(
    val attemptId: String,
    val educationClass: EducationClass,
    val subject: SubjectType,
    val chapter: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val skippedCount: Int,
    val scorePercentage: Int,
    val timeSpentSeconds: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val questions: List<Question>,
    val userAnswers: Map<String, Int?>
)
