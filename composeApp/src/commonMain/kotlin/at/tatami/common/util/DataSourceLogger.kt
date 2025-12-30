package at.tatami.common.util

/**
 * Centralized logging utility for data source tracking.
 * Provides consistent logging format similar to Coil's cache status logging.
 */
object DataSourceLogger {
    
    private const val TAG = "DataSource"
    
    /**
     * Log when data is retrieved from memory cache (StateFlow cached value)
     */
    fun logCacheHit(entityType: String, entityId: String) {
        println("[$TAG] 🧠 $entityType retrieved from cache: $entityId")
    }
    
    /**
     * Log when data is fetched from Firestore
     */
    fun logFirestoreFetch(entityType: String, entityId: String) {
        println("[$TAG] 🔥 $entityType fetched from Firestore: $entityId")
    }
    
    /**
     * Log when ID is loaded from DataStore
     */
    fun logDataStoreAccess(entityType: String, entityId: String?) {
        if (entityId != null) {
            println("[$TAG] 📀 $entityType ID loaded from DataStore: $entityId")
        } else {
            println("[$TAG] 📀 No $entityType ID found in DataStore")
        }
    }
    
    /**
     * Log when waiting for data from Firestore
     */
    fun logAwaitingFirestore(entityType: String, entityId: String? = null) {
        val idSuffix = if (entityId != null) ": $entityId" else ""
        println("[$TAG] ⏳ Waiting for $entityType from Firestore$idSuffix")
    }
    
    /**
     * Log when Firestore listener starts
     */
    fun logFirestoreListenerStart(entityType: String, entityId: String) {
        println("[$TAG] 👂 Started Firestore listener for $entityType: $entityId")
    }
    
    /**
     * Log when no data is available
     */
    fun logNoData(entityType: String, reason: String) {
        println("[$TAG] ❌ No $entityType data: $reason")
    }
    
    /**
     * Log cache miss (data not available in cache, needs Firestore fetch)
     */
    fun logCacheMiss(entityType: String, entityId: String) {
        println("[$TAG] 💨 $entityType cache miss: $entityId")
    }
}