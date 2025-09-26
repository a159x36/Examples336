package nz.ac.massey.examples336.camerax

import android.R.attr.screenSize
import android.content.Context
import android.util.Size
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.video.FallbackStrategy
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CameraViewModel: ViewModel() {

    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest
    val resolutionSelector = ResolutionSelector.Builder().setResolutionStrategy(
        ResolutionStrategy(Size(Int.MAX_VALUE, Int.MAX_VALUE),
            ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER))
        .setAspectRatioStrategy(AspectRatioStrategy(AspectRatio.RATIO_16_9,
            AspectRatioStrategy.FALLBACK_RULE_AUTO)
        ).build()

    private var previewUseCase = Preview.Builder().setResolutionSelector(resolutionSelector).build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.value = newSurfaceRequest
        }
    }

    var imageCapture = ImageCapture.Builder().setResolutionSelector(resolutionSelector).build()

    val cameraSelection = mutableIntStateOf(CameraSelector.LENS_FACING_BACK)

    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)



        val useCaseGroup = UseCaseGroup.Builder()
            .addUseCase(previewUseCase)         // Add Preview UseCase
            .addUseCase(imageCapture) // Add Image Capture UseCase
            .build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(cameraSelection.intValue)
            .build()
        processCameraProvider.bindToLifecycle(
            lifecycleOwner = lifecycleOwner,
            cameraSelector = cameraSelector,
            useCaseGroup = useCaseGroup)

        // Cancellation signals we're done with the camera
        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
        }
    }
}