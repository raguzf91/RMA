package com.example.quizos.data

import android.content.ContentValues.TAG
import android.util.Log
import com.example.quizos.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.Query
import com.google.firebase.storage.ktx.storage


class AuthDataSource {
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    suspend fun registerUser(email: String, password: String): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid
            if (userId != null) {
                Result.success(userId)
            } else {
                Result.failure(Exception("Failed to get user ID after registration."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } as Result<String>
    }

    suspend fun loginUser(email: String, password: String): Result<AuthResult> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(authResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    fun logout() {
        auth.signOut()
    }
}

class UserDataSource {
    private val db: FirebaseFirestore by lazy {Firebase.firestore}
    private val storage by lazy { Firebase.storage }

    suspend fun saveUserDetails(user: User): Result<Unit> {
        return try {
            db.collection("users").document(user.uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserDetails(uid: String): Result<User> {
        return try {
            val document = db.collection("users").document(uid).get().await()
            val user = document.toObject(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User data not found."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserStats(uid: String, newTotalPoints: Int, newTotalCoins: Int): Result<Unit> {
        return try {
            val updates = mapOf(
                "points" to newTotalPoints,
                "coins" to newTotalCoins
            )
            db.collection("users").document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

       suspend fun updateUserCoins(uid: String, newTotalCoins: Int): Result<Unit> {
        return try {
            db.collection("users").document(uid).update("coins", newTotalCoins).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getTopUsers(limit: Int): Result<List<User>> {
        return try {
            val snapshot = db.collection("users")
                .orderBy("points", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            val users = snapshot.toObjects(User::class.java)
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserRank(user: User): Result<Int> {
        return try {
            val snapshot = db.collection("users")
                .whereGreaterThan("points", user.points)
                .get()
                .await()
            val rank = snapshot.size() + 1
            Result.success(rank)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isNewHighScore(score: Int): Result<Boolean> {
        return try {
            val higherScoresSnapshot = db.collection("users")
                .whereGreaterThan("points", score)
                .limit(1)
                .get()
                .await()
            Result.success(higherScoresSnapshot.isEmpty)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    
}

class AuthRepository(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource
) {
    suspend fun registerUserAndSaveDetails(username: String, email: String, password: String): Result<Unit> {
        val authResult = authDataSource.registerUser(email, password)

        return authResult.fold(
            onSuccess = { userId ->
                val newUser = User(uid = userId, username = username, email = email, points = 0)

                userDataSource.saveUserDetails(newUser)
            },
            onFailure = { exception ->
                Log.e(TAG, "User registration failed", exception)
                Result.failure(exception)

            }
        )
    }

    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val authResult = authDataSource.loginUser(email, password).getOrThrow()
            val uid = authResult.user?.uid ?: throw Exception("Login failed, user not found.")
            userDataSource.getUserDetails(uid)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login failed", e)
            Result.failure(e)
        }
    }



    suspend fun getCurrentUserDetails(): Result<User> {
        val uid = authDataSource.getCurrentUserId()
            ?: return Result.failure(Exception("No authenticated user found."))
        return userDataSource.getUserDetails(uid)
    }

     suspend fun updateUserStats(newTotalPoints: Int, newTotalCoins: Int): Result<Unit> {
        val uid = authDataSource.getCurrentUserId()
            ?: return Result.failure(Exception("No authenticated user found."))
        return userDataSource.updateUserStats(uid, newTotalPoints, newTotalCoins)
    }

      suspend fun updateUserCoins(newTotalCoins: Int): Result<Unit> {
        val uid = authDataSource.getCurrentUserId()
            ?: return Result.failure(Exception("No authenticated user found."))
        return userDataSource.updateUserCoins(uid, newTotalCoins)
    }

    suspend fun getTopUsers(limit: Int): Result<List<User>> {
        return userDataSource.getTopUsers(limit)
    }

    suspend fun getUserRank(user: User): Result<Int> {
        return userDataSource.getUserRank(user)
    }

    fun logout() {
        authDataSource.logout()
    }

    suspend fun isNewHighScore(score: Int): Result<Boolean> {
        return userDataSource.isNewHighScore(score)
    }

    

}