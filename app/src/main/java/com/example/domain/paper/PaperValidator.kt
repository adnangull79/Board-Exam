package com.example.domain.paper

import com.example.domain.model.PaperDraft
import com.example.domain.model.PaperValidationResult

object PaperValidator {

    /**
     * Validates paper configuration, marks alignment, and question counts according to Pakistani board formats.
     */
    fun validatePaper(draft: PaperDraft): PaperValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        if (draft.schoolName.isBlank()) {
            errors.add("Institution / School name is required.")
        }
        if (draft.examTitle.isBlank()) {
            errors.add("Examination title (e.g., Send-up Exam) is required.")
        }
        if (draft.durationMinutes <= 0) {
            errors.add("Duration must be greater than 0 minutes.")
        }
        if (draft.totalMarksTarget <= 0) {
            errors.add("Total target marks must be greater than 0.")
        }

        var totalCalculatedMarks = 0

        if (draft.sections.isEmpty()) {
            errors.add("At least one exam section (e.g., Section A MCQs) must be included.")
        } else {
            draft.sections.forEachIndexed { index, section ->
                val sectionNum = index + 1
                if (section.questions.isEmpty()) {
                    errors.add("Section $sectionNum (${section.sectionName}) has no questions added.")
                } else if (section.questions.size < section.requiredQuestionsCount) {
                    errors.add(
                        "Section $sectionNum requires students to attempt ${section.requiredQuestionsCount} questions, but only ${section.questions.size} questions are present in paper."
                    )
                }

                if (section.requiredQuestionsCount <= 0) {
                    errors.add("Section $sectionNum required attempt count must be at least 1.")
                }

                if (section.marksPerQuestion <= 0) {
                    errors.add("Section $sectionNum marks per question must be greater than 0.")
                }

                totalCalculatedMarks += section.totalSectionMarks

                if (section.questions.size > section.requiredQuestionsCount) {
                    val choiceBonus = section.questions.size - section.requiredQuestionsCount
                    warnings.add("Section $sectionNum offers $choiceBonus choice questions (${section.requiredQuestionsCount} required of ${section.questions.size}).")
                }
            }
        }

        if (draft.totalMarksTarget > 0 && totalCalculatedMarks != draft.totalMarksTarget) {
            val diff = draft.totalMarksTarget - totalCalculatedMarks
            if (diff > 0) {
                errors.add("Marks mismatch: Total allocated is $totalCalculatedMarks marks. Need $diff more marks to reach target of ${draft.totalMarksTarget}.")
            } else {
                errors.add("Marks mismatch: Total allocated is $totalCalculatedMarks marks. Exceeds target of ${draft.totalMarksTarget} by ${-diff} marks.")
            }
        }

        return PaperValidationResult(
            isValid = errors.isEmpty(),
            calculatedMarks = totalCalculatedMarks,
            targetMarks = draft.totalMarksTarget,
            errors = errors,
            warnings = warnings
        )
    }
}
