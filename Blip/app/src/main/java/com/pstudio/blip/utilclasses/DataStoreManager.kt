import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserPreferences(context: Context) {

    private val dataStore: DataStore<Preferences> = androidx.datastore.preferences.core.PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("user_prefs") }
    )

    companion object {
        private val USER_UID = stringPreferencesKey("user_uid")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_NAME = stringPreferencesKey("user_name")
    }

    val userUidFlow: Flow<String?> = dataStore.data.map { it[USER_UID] }

    suspend fun saveUser(uid: String, email: String, name: String) {
        dataStore.edit { prefs ->
            prefs[USER_UID] = uid
            prefs[USER_EMAIL] = email
            prefs[USER_NAME] = name
        }
    }

    suspend fun getUser(): Triple<String?, String?, String?> {
        val prefs = dataStore.data.first()
        return Triple(prefs[USER_UID], prefs[USER_EMAIL], prefs[USER_NAME])
    }

    suspend fun clearUser() {
        dataStore.edit { it.clear() }
    }
}