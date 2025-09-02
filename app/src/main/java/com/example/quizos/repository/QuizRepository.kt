package com.example.quizos.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import com.example.quizos.model.QuestionCard
import com.google.firebase.firestore.Query

class QuizDataSource {
    private val db: FirebaseFirestore by lazy { Firebase.firestore }
    suspend fun getCategoryCounts(): Result<Map<String, Int>> {
        return try {
            val snapshot = db.collection("questions").get().await()
            val counts = snapshot.documents
                .mapNotNull { it.getString("topic") }
                .groupBy { it }
                .mapValues { it.value.size }
            Result.success(counts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

     suspend fun getQuestionsForCategory(categoryName: String, limit: Int = 10): Result<List<QuestionCard>> {
        return try {
            val query: Query = if (categoryName == "Mixed") {
                db.collection("questions").limit(limit.toLong())
            } else {
                db.collection("questions").whereEqualTo("topic", categoryName).limit(limit.toLong())
            }

            val snapshot = query.get().await()
            val questions = snapshot.toObjects(QuestionCard::class.java)
            val questionsWithShuffledOptions = questions.map { question ->
                question.copy(options = question.options.shuffled())
            }
            Result.success(questionsWithShuffledOptions.shuffled())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class QuizRepository(private val quizDataSource: QuizDataSource) {
    suspend fun getCategoryCounts(): Result<Map<String, Int>> {
        return quizDataSource.getCategoryCounts()
    }
    suspend fun getQuestionsForCategory(categoryName: String): Result<List<QuestionCard>> {
        return quizDataSource.getQuestionsForCategory(categoryName)
    }
}