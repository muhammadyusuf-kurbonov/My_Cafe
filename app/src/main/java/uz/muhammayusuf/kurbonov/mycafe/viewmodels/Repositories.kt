package uz.muhammayusuf.kurbonov.mycafe.viewmodels

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import uz.muhammayusuf.kurbonov.mycafe.base.viewmodels.repository.BaseFirestoreRepository
import uz.muhammayusuf.kurbonov.mycafe.models.OrderModel
import uz.muhammayusuf.kurbonov.mycafe.models.UserModel
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

sealed class Repositories {

    object OrderedFirestoreDataRepository : BaseFirestoreRepository() {
        override val collectionName: String
            get() = "ordered"

        suspend fun getItem(id: String): OrderModel? = suspendCoroutine<DocumentSnapshot> {
            dataStore.document(id).get().withCoroutine(it)
        }.toObject(OrderModel::class.java)
    }

    object CompletedFirestoreDataRepository : BaseFirestoreRepository() {
        override val collectionName: String
            get() = "completed"
    }

    object NotificationsDataStore {

        fun sendNotification(author: String, table: Int) {

//            fun addDocument(notificationMessage: NotificationMessage) {
//                val document = dataStore.document()
//                notificationMessage.id = document.id
//                document.set(notificationMessage)
//            }
//
//            val notificationMessage = NotificationMessage(
//                table = table.toString(),
//                author = author
//            )

//            Timber.d("Observing notification $notificationMessage")
            val response = OkHttpClient.Builder()
                .addInterceptor {
                    Timber.d("Requesting ...")
                    Timber.d(it.request().toString())
                    it.proceed(it.request())
                }
                .build().newCall(
                    Request.Builder()
                        .url("https://server-qm.vercel.app/api/notify?author=$author&table=$table")
                        .get()
                        .build()
                )
                .execute()

            Timber.d("${response.body}")

//            addDocument(notificationMessage)
        }
    }

    @Suppress("unused")
    object UserDataRepository {
        private val dataStore = FirebaseFirestore.getInstance().collection("users")
        private var currentUser: UserModel? = null

        fun addUser(user: UserModel) {
            val document = dataStore.document()
            user.id = document.id
            document.set(user)
        }

        suspend fun getUser(uid: String) = suspendCoroutine<UserModel?> { continuation ->
            dataStore.whereEqualTo("userId", uid).get().addOnCanceledListener {
                throw CancellationException()
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }.addOnSuccessListener {
                val list = it.toObjects(UserModel::class.java)
                if (list.size > 1)
                    throw IllegalArgumentException(" Why there are 2 same uids?")
                if (list.isEmpty())
                    continuation.resume(null)
                continuation.resume(list[0])
            }
        }

        fun updateUser(user: UserModel) {
            val id = user.id
            dataStore.document(id).set(user)
        }

        @ExperimentalCoroutinesApi
        fun listenData() = callbackFlow<List<UserModel>> {
            val addSnapshotListener = dataStore.addSnapshotListener { value, error ->
                if (error != null) {
                    Timber.e(error)
                }
                offer(value?.toObjects(UserModel::class.java) ?: emptyList())
            }

            awaitClose {
                addSnapshotListener.remove()
            }
        }

        suspend fun getCurrentUser(): UserModel {
            if (currentUser == null) {
                changeCurrentUser(
                    FirebaseAuth.getInstance().currentUser?.uid
                        ?: throw IllegalStateException("Not authorized!")
                )
            }
            return currentUser!!
        }

        suspend fun changeCurrentUser(uid: String): Boolean {
            currentUser = getUser(uid)
            LocalSettingsStorage.setPref("uid", uid)
            return currentUser != null
        }

    }

    object LocalSettingsStorage {
        private lateinit var sharedPreferences: SharedPreferences

        fun init(context: Context, listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            sharedPreferences = context.getSharedPreferences("prefs.dat", 0)
            sharedPreferences.registerOnSharedPreferenceChangeListener(
                listener
            )
        }

        fun setPref(key: String, value: String) {
            sharedPreferences.edit().putString(key, value).apply()
        }

        fun getPref(key: String, default: String? = null) =
            sharedPreferences.getString(key, default)
    }

    object SharedSettings {
        private val dataStore = FirebaseFirestore.getInstance()
            .collection("settings")
            .document("storage")

        suspend fun setPref(key: String, value: String) {
            val data = dataStore.getData<HashMap<String, Any>>() ?: mutableMapOf()
            data[key] = value
        }

        suspend fun getPref(key: String): String? = suspendCoroutine<DocumentSnapshot> {
            dataStore.get().withCoroutine(it)
        }.getString(key)
    }
}