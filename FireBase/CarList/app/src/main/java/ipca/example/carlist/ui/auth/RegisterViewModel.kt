package ipca.example.carlist.ui.auth

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import ipca.example.carlist.models.User


data class RegisterState(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    var error: String? = null,
    var loading: Boolean = false,
    val isRegistered: Boolean = false
)

class RegisterViewModel : ViewModel() {

    var uiState = mutableStateOf(RegisterState())
        private set

    fun updateEmail(email: String) {
        uiState.value = uiState.value.copy(email = email.trim())
    }

    fun updatePassword(password: String) {
        uiState.value = uiState.value.copy(password = password)
    }

    fun updateName(name: String) {
        uiState.value = uiState.value.copy(name = name.trim())
    }


    fun registar(onRegisterSuccess: () -> Unit) {
        uiState.value = uiState.value.copy(loading = true, error = null)

        val auth = Firebase.auth

        val email = uiState.value.email
        val password = uiState.value.password

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            uiState.value = uiState.value.copy(error = "Email ou password vazios", loading = false)
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "createUserWithEmail:success")

                createUser().addOnCompleteListener { dbTask ->
                    if (dbTask.isSuccessful) {
                        Log.d(TAG, "Firestore: Sucesso na gravação do documento.")
                        auth.signOut()
                        uiState.value = uiState.value.copy(loading = false, error = null)
                        onRegisterSuccess()
                    } else {
                        Log.w(TAG, "Firestore: Falha ao adicionar documento", dbTask.exception)
                        uiState.value = uiState.value.copy(
                            error = "Erro ao guardar dados do utilizador: " + dbTask.exception?.localizedMessage,
                            loading = false
                        )
                    }
                }
            } else {
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                uiState.value = uiState.value.copy(
                    error = task.exception?.localizedMessage ?: "Erro ao autenticar",
                    loading = false
                )
            }
        }
    }

    fun createUser(): Task<Void> {
        val userID = Firebase.auth.currentUser?.uid
        val db = Firebase.firestore

        val user = User(
            id = userID,
            name = uiState.value.name,
            email = uiState.value.email
        )

        if (userID.isNullOrEmpty()) {
            return com.google.android.gms.tasks.Tasks.forException(Exception("User ID is null after creation."))
        }

        return db.collection("users")
            .document(userID)
            .set(user)
    }
}
