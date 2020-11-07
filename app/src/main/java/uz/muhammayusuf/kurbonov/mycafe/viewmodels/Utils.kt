package uz.muhammayusuf.kurbonov.mycafe.viewmodels


import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import timber.log.Timber
import uz.muhammayusuf.kurbonov.mycafe.R
import uz.muhammayusuf.kurbonov.mycafe.models.UserModel
import uz.muhammayusuf.kurbonov.mycafe.models.UserRoles
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun AppCompatActivity.registerSignIn(): ActivityResultLauncher<Intent> {
    return registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            lifecycleScope.launch {
                val user = FirebaseAuth.getInstance().currentUser!!
                val fromResultIntent = IdpResponse.fromResultIntent(it.data)
                    ?: throw IllegalStateException("Result data is empty ${it.data}")
                if (fromResultIntent.isNewUser) {
                    Repositories.UserDataRepository.addUser(
                        UserModel(
                            user.uid,
                            name = user.displayName ?: "User",
                            email = user.email ?: "none",
                            role = UserRoles.GUEST
                        )
                    )
                }
                Repositories.UserDataRepository.changeCurrentUser(user.uid)
                findNavController(R.id.fragment).navigate(R.id.mainFragment)
            }
        } else {
            Timber.d("User not authorized. Why? $it")
            finish()
        }
    }
}

fun getSignInIntent() = AuthUI.getInstance()
    .createSignInIntentBuilder()
    .setAvailableProviders(
        listOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )
    )
    .build()

fun subscribeToTopics(key: String) {
    FirebaseMessaging.getInstance()
        .subscribeToTopic(key)
}

suspend inline fun <reified T> Query.getData() = suspendCoroutine<List<T>> { cont ->
    get().addOnSuccessListener {
        cont.resume(it.toObjects(T::class.java))
    }.addOnCanceledListener {
        cont.resumeWithException(CancellationException())
    }.addOnFailureListener {
        cont.resumeWithException(it)
    }
}

suspend inline fun <reified T> DocumentReference.getData() = suspendCoroutine<T?> { cont ->
    get().addOnSuccessListener {
        cont.resume(it.toObject(T::class.java))
    }.addOnCanceledListener {
        cont.resumeWithException(CancellationException())
    }.addOnFailureListener {
        cont.resumeWithException(it)
    }
}

fun <T : Any?> Task<T>.withCoroutine(continuation: Continuation<T>) {
    addOnSuccessListener {
        continuation.resume(it)
    }.addOnFailureListener {
        continuation.resumeWithException(it)
    }.addOnCanceledListener {
        continuation.resumeWithException(CancellationException())
    }
}