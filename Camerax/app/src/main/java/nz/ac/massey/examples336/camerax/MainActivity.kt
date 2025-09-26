package nz.ac.massey.examples336.camerax

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import nz.ac.massey.examples336.camerax.ui.theme.CameraxTheme
import java.text.SimpleDateFormat

fun openGallery(context:Context) {
    val imageColumns = arrayOf( MediaStore.Images.Media._ID )
    val imageOrderBy = MediaStore.Images.Media.DATE_ADDED + " DESC"
    val imageCursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        imageColumns,
        null,
        null,
        imageOrderBy
    )
    if(imageCursor == null) return
    imageCursor.moveToFirst()
    val photoId = imageCursor.getLong(0)
    imageCursor.close()

    Log.i(TAG, "id=$photoId")
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(
        Uri.withAppendedPath(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "" + photoId
        ), "image/jpeg"
    )
    context.startActivity(intent)

}

const val TAG = "CameraX"
@SuppressLint("SimpleDateFormat")
fun takePhoto(context:Context, imageCapture: ImageCapture) {
    // Create time stamped name and MediaStore entry.
   val name = "IMG_"+SimpleDateFormat("yyyyMMdd_HHmmss")
              .format(System.currentTimeMillis())
   val contentValues = ContentValues().apply {
       put(MediaStore.MediaColumns.DISPLAY_NAME, name)
       put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
       if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
           put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
       }
   }

   // Create output options object which contains file + metadata
   val outputOptions = ImageCapture.OutputFileOptions
           .Builder(context.contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues)
           .build()

   // Set up image capture listener, which is triggered after photo has
   // been taken
   imageCapture.takePicture(
       outputOptions,
       ContextCompat.getMainExecutor(context),
       object : ImageCapture.OnImageSavedCallback {
           override fun onError(exc: ImageCaptureException) {
               Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
           }

           override fun
               onImageSaved(output: ImageCapture.OutputFileResults){
               val msg = "Photo capture succeeded: ${output.savedUri}"
               Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
               Log.d(TAG, msg)
           }
       }
   )
}

enum class ButtonState { Pressed, Idle }
fun Modifier.bounceClick() = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(if (buttonState == ButtonState.Pressed) 0.70f else 1f)

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {  }
        )
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}

@Composable
fun CameraApp(modifier: Modifier = Modifier, viewModel: CameraViewModel=viewModel ()) {

    val localContext = LocalContext.current

    Box(modifier=modifier.fillMaxSize()) {
        CameraPreview(
            Modifier,viewModel
        )
        Box(modifier=Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(48.dp).size(64.dp)
                    .bounceClick()
                    .clickable(enabled = true, onClick = {
                        takePhoto(localContext, viewModel.imageCapture)
                    }),
                painter = painterResource(id = R.drawable.ic_radio_button_checked_black_24dp),
                contentDescription = ""
            )
            Image(
                modifier = Modifier.align(Alignment.BottomStart)
                    .padding(bottom = 48.dp, start = 32.dp).size(64.dp)
                    .clickable(enabled = true, onClick = { openGallery(localContext) }),
                painter = painterResource(id = R.drawable.ic_photo_library_black_24dp),
                contentDescription = ""
            )
            Image(
                modifier = Modifier.align(Alignment.BottomEnd)
                    .padding(bottom = 48.dp, end = 32.dp).size(64.dp)
                    .clickable(enabled = true, onClick = { viewModel.cameraSelection.intValue = 1 - viewModel.cameraSelection.intValue }),
                painter = painterResource(id = R.drawable.flip_camera_android_24px),
                contentDescription = ""
            )
        }
    }
}


@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current


    LaunchedEffect(lifecycleOwner) {
        viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
    }


    LaunchedEffect(viewModel.cameraSelection.intValue) {
        viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
    }

    val surfaceRequest=viewModel.surfaceRequest.collectAsState()

    surfaceRequest.value?.let {
        CameraXViewfinder(
            surfaceRequest = it,
            modifier = modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = false)
@Composable
fun CameraScreenPreview() {
        CameraApp()
}

class MainActivity : ComponentActivity() {
    private val permissions =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val activityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            )
            { permissions ->
                // Handle Permission granted/rejected
                var permissionGranted = true
                permissions.entries.forEach {
                    if (it.key in this@MainActivity.permissions && !it.value)
                        permissionGranted = false
                }
                if (!permissionGranted) {
                    Toast.makeText(
                        baseContext,
                        "Permission request denied",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    setContent {
                        CameraxTheme {
                            CameraApp(modifier=Modifier)
                        }
                    }
                }
            }
        activityResultLauncher.launch(permissions)
    }
}
