package ipca.example.carlist.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

data class LoginState(
    var email: String? = null,
    var password: String? = null,
    var error: String? = null,
    var loading: Boolean = false
)

class LoginViewModel : ViewModel() {

    var uiState = mutableStateOf(LoginState())
        private set

    private val auth: FirebaseAuth = Firebase.auth

    fun updateEmail(email: String) {
        uiState.value = uiState.value.copy(
            email = email.trim(),
            error = null
        )
    }

    fun updatePassword(password: String) {
        uiState.value = uiState.value.copy(
            password = password,
            error = null
        )
    }

    fun login(onLoginSuccess: () -> Unit) {
        val state = uiState.value

        if (state.email.isNullOrEmpty() || state.password.isNullOrEmpty()) {
            uiState.value = state.copy(
                error = "Email ou password vazios",
                loading = false
            )
            return
        }

        uiState.value = state.copy(loading = true, error = null)

        auth.signInWithEmailAndPassword(state.email!!, state.password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uiState.value = uiState.value.copy(loading = false)
                    onLoginSuccess()
                } else {
                    uiState.value = uiState.value.copy(
                        error = "Autenticação falhou.",
                        loading = false
                    )
                }
            }
    }

    fun logout() {
        auth.signOut()
    }
}
