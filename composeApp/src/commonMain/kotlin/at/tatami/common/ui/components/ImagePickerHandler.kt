package at.tatami.common.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.attafitamim.krop.core.crop.AspectRatio
import com.attafitamim.krop.core.crop.CropError
import com.attafitamim.krop.core.crop.CropResult
import com.attafitamim.krop.core.crop.CropState
import com.attafitamim.krop.core.crop.CropperStyleGuidelines
import com.attafitamim.krop.core.crop.crop
import com.attafitamim.krop.core.crop.cropperStyle
import com.attafitamim.krop.core.crop.rememberImageCropper
import com.attafitamim.krop.ui.ImageCropperDialog
import com.attafitamim.krop.filekit.toImageSrc
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import tatami.composeapp.generated.resources.*

/**
 * A headless component that handles image selection, cropping, and compression.
 * Shows a dialog with options to select or remove an image.
 * 
 * @param currentImage The current image data, used to show remove option
 * @param onImageSelected Callback when an image is selected (null to remove)
 * @param onDismiss Callback when the dialog is dismissed
 * @param aspectRatio The aspect ratio for cropping (default 1:1)
 * @param maxSizeKB Maximum size in KB before compression (default 500KB)
 * @param compressionQuality Quality for compression (default 80)
 * @param maxDimension Maximum width/height after compression (default 700)
 * @param showRemoveOption Whether to show the remove photo option
 */
@Composable
fun ImagePickerHandler(
    currentImage: ByteArray?,
    onImageSelected: (ByteArray?) -> Unit,
    onDismiss: () -> Unit,
    aspectRatio: AspectRatio = AspectRatio(1, 1),
    maxSizeKB: Int = 500,
    compressionQuality: Int = 80,
    maxDimension: Int = 700,
    showRemoveOption: Boolean = true
) {
    val scope = rememberCoroutineScope()
    val imageCropper = rememberImageCropper()
    
    // Configure cropper style
    val cropperStyle = cropperStyle(
        overlay = Color.Black.copy(alpha = 0.5f),
        autoZoom = true,
        guidelines = CropperStyleGuidelines(),
        aspects = listOf(aspectRatio),
        shapes = emptyList() // Only rectangle shape
    )
    
    // Image picker launcher
    val imagePickerLauncher = rememberFilePickerLauncher(
        type = FileKitType.Image
    ) { file ->
        file?.let {
            scope.launch {
                try {
                    // Convert PlatformFile to ImageSrc for Krop
                    val imageSrc = it.toImageSrc()
                    
                    // Launch the cropping dialog
                    when (val cropResult = imageCropper.crop(imageSrc)) {
                        is CropResult.Cancelled -> {
                            // User cancelled cropping - just dismiss
                            onDismiss()
                        }
                        is CropResult.Success -> {
                            // Convert cropped ImageBitmap to ByteArray
                            val croppedBytes = cropResult.bitmap.encodeToByteArray(
                                format = ImageFormat.JPEG,
                                quality = 90
                            )
                            
                            // Compress if too large
                            val finalBytes = if (croppedBytes.size > maxSizeKB * 1000) {
                                FileKit.compressImage(
                                    bytes = croppedBytes,
                                    quality = compressionQuality,
                                    maxWidth = maxDimension,
                                    maxHeight = maxDimension,
                                    imageFormat = ImageFormat.JPEG
                                )
                            } else {
                                croppedBytes
                            }
                            
                            onImageSelected(finalBytes)
                            onDismiss()
                        }
                        CropError.LoadingError -> {
                            println("Error loading image for cropping")
                            onDismiss()
                        }
                        CropError.SavingError -> {
                            println("Error saving cropped image")
                            onDismiss()
                        }
                    }
                } catch (e: Exception) {
                    println("Error processing image: ${e.message}")
                    onDismiss()
                }
            }
        } ?: onDismiss() // File picker was cancelled
    }
    
    // Image source selection dialog
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.select_image_source)) },
        text = {
            Column {
                TextButton(
                    onClick = {
                        imagePickerLauncher.launch()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.choose_from_gallery))
                }
                if (showRemoveOption && currentImage != null) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    TextButton(
                        onClick = {
                            onImageSelected(null)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(Res.string.remove_photo),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
    
    // Display cropper dialog when active
    imageCropper.cropState?.let { state ->
        ImageCropperDialog(
            state = state,
            style = cropperStyle,
            topBar = { CustomTopBar(state) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(state: CropState) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = { state.done(accept = false) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
        },
        actions = {
            IconButton(onClick = { state.reset() }) {
                Icon(Icons.Default.Restore,"Resets crop state")
            }
            IconButton(onClick = { state.done(accept = true) }, enabled = !state.accepted) {
                Icon(Icons.Default.Done,
                    "Confirm crop"
                    )
            }
        }
    )
}