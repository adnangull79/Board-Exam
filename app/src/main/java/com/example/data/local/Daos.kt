package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE classId = :classId AND subjectId = :subjectId")
    fun getQuestionsByClassAndSubject(classId: String, subjectId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE classId = :classId AND subjectId = :subjectId AND chapter = :chapter")
    fun getQuestionsByChapter(classId: String, subjectId: String, chapter: String): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int
}

@Dao
interface QuizAttemptDao {
    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    fun getAllAttempts(): Flow<List<QuizAttemptEntity>>

    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC LIMIT 5")
    fun getRecentAttempts(): Flow<List<QuizAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: QuizAttemptEntity)
}

@Dao
interface PaperDraftDao {
    @Query("SELECT * FROM paper_drafts ORDER BY updatedAtMillis DESC")
    fun getAllDrafts(): Flow<List<PaperDraftEntity>>

    @Query("SELECT * FROM paper_drafts WHERE id = :id")
    suspend fun getDraftById(id: String): PaperDraftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: PaperDraftEntity)

    @Update
    suspend fun updateDraft(draft: PaperDraftEntity)

    @Query("DELETE FROM paper_drafts WHERE id = :id")
    suspend fun deleteDraftById(id: String)
}
