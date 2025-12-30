package at.tatami.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val DATASTORE_NAME = "tatami_preferences"

// Android DataStore property delegate
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATASTORE_NAME
)

/**
 * Creates DataStore with Android context.
 *
 * Note: On Android, the expect/actual createDataStore() without parameters
 * is not used. Instead, this context-aware version is called from AndroidModule.
 */
fun createDataStore(context: Context): DataStore<Preferences> = context.dataStore

/**
 * Android actual implementation - throws because Android requires Context.
 * Use createDataStore(context) instead.
 */
actual fun createDataStore(): DataStore<Preferences> {
    throw IllegalStateException("DataStore must be provided through dependency injection on Android. Use createDataStore(context) instead.")
}