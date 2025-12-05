package ipca.example.carlist.ui.profile

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ipca.example.carlist.models.User
import androidx.compose.runtime.mutableStateOf

data class ProfileState(
    val user: User = User(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null
)

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var uiState = mutableStateOf(ProfileState())
        private set

    init {
        loadProfile()
    }

    fun loadProfile() {
        uiState.value = uiState.value.copy(isLoading = true, error = null)

        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = e.localizedMessage
                    )
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    uiState.value = uiState.value.copy(
                        user = user!!,
                        isLoading = false,
                        error = null
                    )
                } else {
                    Log.d(TAG, "User not found")
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = "User not found"
                    )
                }
            }
    }

    fun logout() {
        auth.signOut()
    }
}