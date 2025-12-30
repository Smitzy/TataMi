package at.tatami.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * Platform-specific factory for creating DataStore instances
 */
expect fun createDataStore(): DataStore<Preferences>