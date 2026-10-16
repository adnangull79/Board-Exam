package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.PaperDraftEntity
import com.example.data.local.QuestionEntity
import com.example.data.local.QuizAttemptEntity
import com.example.domain.model.Difficulty
import com.example.domain.model.EducationClass
import com.example.domain.model.PaperDraft
import com.example.domain.model.PaperSection
import com.example.domain.model.Question
import com.example.domain.model.QuestionType
import com.example.domain.model.QuizResult
import com.example.domain.model.SubjectType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class ExamRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val questionDao = db.questionDao()
    private val quizAttemptDao = db.quizAttemptDao()
    private val paperDraftDao = db.paperDraftDao()

    suspend fun initializeDatabaseIfEmpty() {
        withContext(Dispatchers.IO) {
            val count = questionDao.getQuestionCount()
            if (count == 0) {
                val entities = SampleData.questions.map { q ->
                    val optionsJson = JSONArray(q.options).toString()
                    QuestionEntity(
                        id = q.id,
                        classId = q.educationClass.id,
                        subjectId = q.subject.id,
                        chapter = q.chapter,
                        chapterNumber = q.chapterNumber,
                        questionType = q.questionType.name,
                        questionText = q.questionText,
                        optionsJson = optionsJson,
                        correctOptionIndex = q.correctOptionIndex,
                        explanation = q.explanation,
                        marks = q.marks,
                        difficulty = q.difficulty.name,
                        boardYearMention = q.boardYearMention,
                        modelSolution = q.modelSolution
                    )
                }
                questionDao.insertAll(entities)

                // Also insert a default sample teacher paper draft for immediate demonstration
                val samplePaper = createDefaultSamplePaperDraft()
                savePaperDraft(samplePaper)
            }
        }
    }

    fun getAllQuestions(): List<Question> {
        // Returns the comprehensive sample questions bank
        return SampleData.questions
    }

    fun getQuestions(
        educationClass: EducationClass,
        subject: SubjectType,
        chapter: String? = null,
        type: QuestionType? = null
    ): List<Question> {
        return SampleData.questions.filter {
            it.educationClass == educationClass &&
            it.subject == subject &&
            (chapter == null || chapter == "All Chapters" || it.chapter.equals(chapter, ignoreCase = true)) &&
            (type == null || it.questionType == type)
        }
    }

    // === Quiz Attempts Flow & Persistence ===
    val recentQuizAttempts: Flow<List<QuizResult>> = quizAttemptDao.getAllAttempts().map { list ->
        list.map { entity ->
            val eduClass = EducationClass.entries.find { it.id == entity.classId } ?: EducationClass.CLASS_9
            val sub = SubjectType.entries.find { it.id == entity.subjectId } ?: SubjectType.COMPUTER_SCIENCE
            QuizResult(
                attemptId = entity.id,
                educationClass = eduClass,
                subject = sub,
                chapter = entity.chapter,
                totalQuestions = entity.totalQuestions,
                correctCount = entity.correctCount,
                incorrectCount = entity.incorrectCount,
                skippedCount = entity.skippedCount,
                scorePercentage = entity.scorePercentage,
                timeSpentSeconds = entity.timeSpentSeconds,
                timestamp = entity.timestamp,
                questions = emptyList(),
                userAnswers = emptyMap()
            )
        }
    }.flowOn(Dispatchers.IO)

    suspend fun saveQuizResult(result: QuizResult) {
        withContext(Dispatchers.IO) {
            val entity = QuizAttemptEntity(
                id = result.attemptId,
                classId = result.educationClass.id,
                subjectId = result.subject.id,
                chapter = result.chapter,
                totalQuestions = result.totalQuestions,
                correctCount = result.correctCount,
                incorrectCount = result.incorrectCount,
                skippedCount = result.skippedCount,
                scorePercentage = result.scorePercentage,
                timeSpentSeconds = result.timeSpentSeconds,
                timestamp = result.timestamp
            )
            quizAttemptDao.insertAttempt(entity)
        }
    }

    // === Paper Drafts Flow & Persistence ===
    val allPaperDrafts: Flow<List<PaperDraft>> = paperDraftDao.getAllDrafts().map { list ->
        list.map { entityToPaperDraft(it) }
    }.flowOn(Dispatchers.IO)

    suspend fun savePaperDraft(draft: PaperDraft) {
        withContext(Dispatchers.IO) {
            val entity = paperDraftToEntity(draft)
            paperDraftDao.insertDraft(entity)
        }
    }

    suspend fun getPaperDraftById(id: String): PaperDraft? {
        return withContext(Dispatchers.IO) {
            val entity = paperDraftDao.getDraftById(id)
            entity?.let { entityToPaperDraft(it) }
        }
    }

    suspend fun deletePaperDraft(id: String) {
        withContext(Dispatchers.IO) {
            paperDraftDao.deleteDraftById(id)
        }
    }

    suspend fun duplicatePaperDraft(source: PaperDraft): PaperDraft {
        val newDraft = source.copy(
            id = UUID.randomUUID().toString(),
            examTitle = "${source.examTitle} (Copy)",
            createdAtMillis = System.currentTimeMillis(),
            updatedAtMillis = System.currentTimeMillis()
        )
        savePaperDraft(newDraft)
        return newDraft
    }

    private fun paperDraftToEntity(draft: PaperDraft): PaperDraftEntity {
        val sectionsArray = JSONArray()
        for (section in draft.sections) {
            val secObj = JSONObject()
            secObj.put("id", section.id)
            secObj.put("sectionName", section.sectionName)
            secObj.put("instructions", section.instructions)
            secObj.put("questionType", section.questionType.name)
            secObj.put("marksPerQuestion", section.marksPerQuestion)
            secObj.put("requiredQuestionsCount", section.requiredQuestionsCount)

            val questionsArray = JSONArray()
            for (q in section.questions) {
                val qObj = JSONObject()
                qObj.put("id", q.id)
                qObj.put("classId", q.educationClass.id)
                qObj.put("subjectId", q.subject.id)
                qObj.put("chapter", q.chapter)
                qObj.put("chapterNumber", q.chapterNumber)
                qObj.put("questionType", q.questionType.name)
                qObj.put("questionText", q.questionText)
                qObj.put("options", JSONArray(q.options))
                qObj.put("correctOptionIndex", q.correctOptionIndex)
                qObj.put("explanation", q.explanation)
                qObj.put("marks", q.marks)
                qObj.put("difficulty", q.difficulty.name)
                qObj.put("boardYearMention", q.boardYearMention)
                qObj.put("modelSolution", q.modelSolution)
                questionsArray.put(qObj)
            }
            secObj.put("questions", questionsArray)
            sectionsArray.put(secObj)
        }

        return PaperDraftEntity(
            id = draft.id,
            schoolName = draft.schoolName,
            examTitle = draft.examTitle,
            classId = draft.educationClass.id,
            subjectId = draft.subject.id,
            examType = draft.examType,
            examDate = draft.examDate,
            durationMinutes = draft.durationMinutes,
            totalMarksTarget = draft.totalMarksTarget,
            sectionsJson = sectionsArray.toString(),
            createdAtMillis = draft.createdAtMillis,
            updatedAtMillis = draft.updatedAtMillis
        )
    }

    private fun entityToPaperDraft(entity: PaperDraftEntity): PaperDraft {
        val sections = mutableListOf<PaperSection>()
        try {
            val sectionsArray = JSONArray(entity.sectionsJson)
            for (i in 0 until sectionsArray.length()) {
                val secObj = sectionsArray.getJSONObject(i)
                val secId = secObj.optString("id", UUID.randomUUID().toString())
                val secName = secObj.optString("sectionName", "Section")
                val instructions = secObj.optString("instructions", "")
                val qTypeName = secObj.optString("questionType", "MCQ")
                val qType = try { QuestionType.valueOf(qTypeName) } catch (_: Exception) { QuestionType.MCQ }
                val marksPerQ = secObj.optInt("marksPerQuestion", 1)
                val requiredCount = secObj.optInt("requiredQuestionsCount", 1)

                val questionsList = mutableListOf<Question>()
                val questionsArray = secObj.optJSONArray("questions")
                if (questionsArray != null) {
                    for (j in 0 until questionsArray.length()) {
                        val qObj = questionsArray.getJSONObject(j)
                        val optionsList = mutableListOf<String>()
                        val optsArray = qObj.optJSONArray("options")
                        if (optsArray != null) {
                            for (k in 0 until optsArray.length()) {
                                optionsList.add(optsArray.getString(k))
                            }
                        }
                        val classEnum = EducationClass.entries.find { it.id == qObj.optString("classId") } ?: EducationClass.CLASS_9
                        val subEnum = SubjectType.entries.find { it.id == qObj.optString("subjectId") } ?: SubjectType.COMPUTER_SCIENCE
                        val qTypeEnum = try { QuestionType.valueOf(qObj.optString("questionType", "MCQ")) } catch (_: Exception) { QuestionType.MCQ }
                        val diffEnum = try { Difficulty.valueOf(qObj.optString("difficulty", "MEDIUM")) } catch (_: Exception) { Difficulty.MEDIUM }

                        questionsList.add(
                            Question(
                                id = qObj.optString("id", UUID.randomUUID().toString()),
                                educationClass = classEnum,
                                subject = subEnum,
                                chapter = qObj.optString("chapter", "Chapter 1"),
                                chapterNumber = qObj.optInt("chapterNumber", 1),
                                questionType = qTypeEnum,
                                questionText = qObj.optString("questionText", ""),
                                options = optionsList,
                                correctOptionIndex = qObj.optInt("correctOptionIndex", 0),
                                explanation = qObj.optString("explanation", ""),
                                marks = qObj.optInt("marks", marksPerQ),
                                difficulty = diffEnum,
                                boardYearMention = qObj.optString("boardYearMention", "Past Board Exam"),
                                modelSolution = qObj.optString("modelSolution", "")
                            )
                        )
                    }
                }

                sections.add(
                    PaperSection(
                        id = secId,
                        sectionName = secName,
                        instructions = instructions,
                        questionType = qType,
                        marksPerQuestion = marksPerQ,
                        requiredQuestionsCount = requiredCount,
                        questions = questionsList
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val eduClass = EducationClass.entries.find { it.id == entity.classId } ?: EducationClass.CLASS_9
        val subject = SubjectType.entries.find { it.id == entity.subjectId } ?: SubjectType.COMPUTER_SCIENCE

        return PaperDraft(
            id = entity.id,
            schoolName = entity.schoolName,
            examTitle = entity.examTitle,
            educationClass = eduClass,
            subject = subject,
            examType = entity.examType,
            examDate = entity.examDate,
            durationMinutes = entity.durationMinutes,
            totalMarksTarget = entity.totalMarksTarget,
            sections = sections,
            createdAtMillis = entity.createdAtMillis,
            updatedAtMillis = entity.updatedAtMillis
        )
    }

    private fun createDefaultSamplePaperDraft(): PaperDraft {
        val cs9Mcqs = SampleData.questions.filter { it.educationClass == EducationClass.CLASS_9 && it.subject == SubjectType.COMPUTER_SCIENCE && it.questionType == QuestionType.MCQ }.take(3)
        val cs9Short = SampleData.questions.filter { it.educationClass == EducationClass.CLASS_9 && it.subject == SubjectType.COMPUTER_SCIENCE && it.questionType == QuestionType.SHORT }.take(1)
        val cs9Long = SampleData.questions.filter { it.educationClass == EducationClass.CLASS_9 && it.subject == SubjectType.COMPUTER_SCIENCE && it.questionType == QuestionType.LONG }.take(1)

        val secA = PaperSection(
            id = "sec_a_sample",
            sectionName = "Section A: Multiple Choice Questions",
            instructions = "Note: Attempt all questions. Each question carries 1 mark.",
            questionType = QuestionType.MCQ,
            marksPerQuestion = 1,
            requiredQuestionsCount = 3,
            questions = cs9Mcqs
        )
        val secB = PaperSection(
            id = "sec_b_sample",
            sectionName = "Section B: Short Answer Questions",
            instructions = "Note: Attempt the following short question. Carries 3 marks.",
            questionType = QuestionType.SHORT,
            marksPerQuestion = 3,
            requiredQuestionsCount = 1,
            questions = cs9Short
        )
        val secC = PaperSection(
            id = "sec_c_sample",
            sectionName = "Section C: Long Descriptive Question",
            instructions = "Note: Attempt the following comprehensive question in detail. Carries 8 marks.",
            questionType = QuestionType.LONG,
            marksPerQuestion = 8,
            requiredQuestionsCount = 1,
            questions = cs9Long
        )

        return PaperDraft(
            id = "sample_draft_1",
            schoolName = "Islamabad Model College for Boys (G-10/4)",
            examTitle = "Mid-Term Send-Up Examination 2026",
            educationClass = EducationClass.CLASS_9,
            subject = SubjectType.COMPUTER_SCIENCE,
            examType = "Mid-Term Examination",
            examDate = "15-March-2026",
            durationMinutes = 60,
            totalMarksTarget = 14, // 3 + 3 + 8 = 14
            sections = listOf(secA, secB, secC)
        )
    }
}
