package at.tatami.core

import dev.gitlive.firebase.storage.FirebaseStorage
import dev.gitlive.firebase.storage.FirebaseStorageMetadata
import dev.gitlive.firebase.storage.StorageReference

enum class ImageType(val contentType: String) {
    JPEG("image/jpeg"),
    PNG("image/png"),
    WEBP("image/webp")
}

class StorageService(
    private val storage: FirebaseStorage
) {
    /**
     * Uploads an image to Firebase Storage with appropriate metadata.
     *
     * @param path The storage path where the image will be uploaded (e.g., "images/profile/user123.jpg")
     * @param imageData The raw image data as a byte array
     * @param imageType The type of image being uploaded (JPEG, PNG, or WEBP)
     * @return The download URL of the uploaded image
     * @throws FirebaseStorageException if the upload fails
     */
    suspend fun uploadImage(path: String, imageData: ByteArray, imageType: ImageType): String {
        val ref = storage.reference(path)
        val data = createDataFromBytes(imageData)
        ref.putData(data, FirebaseStorageMetadata(contentType = imageType.contentType))
        return ref.getDownloadUrl()
    }
    
    suspend fun deleteImage(path: String) {
        val ref = storage.reference(path)
        ref.delete()
    }
    
    suspend fun deleteFolder(path: String) {
        val ref = storage.reference(path)
        val items = ref.listAll()
        items.items.forEach { item ->
            item.delete()
        }
    }
}