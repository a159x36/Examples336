package nz.ac.massey.gpxtrack

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.util.Log.i
import android.view.PixelCopy
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.MapsInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nz.ac.massey.gpxtrack.ui.MapUi
import java.util.UUID

const val TAG="GPXTrack"
class MapActivity : ComponentActivity() {

    var interactive=false

    override fun onResume() {
        super.onResume()
        if(!interactive) loadAndDrawOnMap()
    }

    fun share() {
        val v: View = window.decorView
        val viewBitmap =
            createBitmap(v.width, v.height)
        val loc = IntArray(2)
        v.getLocationInWindow(loc)
        viewmodel.showicons(false)

        CoroutineScope(Dispatchers.Main).launch {
            delay(100)
            PixelCopy.request(
                window,
                Rect(
                    v.left + loc[0],
                    v.top + loc[1],
                    v.right + loc[0],
                    v.bottom + loc[1]
                ),
                viewBitmap,
                { i: Int ->
                    viewmodel.showicons(true)
                    val path = MediaStore.Images.Media.insertImage(
                        contentResolver,
                        viewBitmap,
                        UUID.randomUUID().toString(),
                        null
                    )
                    val uri = path.toUri()
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "image/png"
                    share.putExtra(Intent.EXTRA_STREAM, uri)
                    startActivity(Intent.createChooser(share, getString(R.string.share_image)))
                },
                v.handler
            )
        }
    }

    val viewmodel: MapViewModel by viewModels()

    private val REQUIRED_PERMISSIONS = arrayOf (
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Settings.init(application)
        MapsInitializer.initialize(applicationContext)
        this.enableEdgeToEdge()
        viewmodel.showmap(false)
        setContent {
            MapUi(viewModel = viewmodel, shareFn = { share() })
        }

        val intent = getIntent()
        if (intent != null) {
            Log.i(TAG,"Intent: $intent ${intent.action} ${intent.type}")
            if (intent.action == Intent.ACTION_SEND) {
                val uri = if(Build.VERSION.SDK_INT<33) intent.getParcelableExtra(Intent.EXTRA_STREAM)
                else intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                if (uri != null) {
                    viewmodel.uri=uri
                }
            }
            if (intent.action == Intent.ACTION_VIEW) {
                val uri = intent.data
                if (uri != null) {
                    viewmodel.uri=uri
                }
            }
            if (intent.action == Intent.ACTION_MAIN) {
                interactive=true
                val activityResultLauncher =
                registerForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions -> // Handle Permission granted/rejected
                    var permissionGranted = true
                    permissions.entries.forEach {
                        if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                            permissionGranted = false
                    }
                    if (permissionGranted) {
                        /*
                        val locationManager=getSystemService(LOCATION_SERVICE) as LocationManager
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,0f,
                            {
                                Log.i(TAG,"Location: $it")
                                viewmodel.mappoints.addpoint(it.latitude,it.longitude, it.time)
                                viewmodel.drawMapPoints()
                                if(viewmodel.mappoints.mapPoints.size>1)
                                    viewmodel.setposition(viewmodel.mappoints.mBounds.build())
                            }
                        )

                         */
                        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                        val locationRequest = LocationRequest.Builder(1000).setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()
                        fusedLocationClient.requestLocationUpdates(locationRequest,{
                            Log.i(TAG,"Location: $it")
                            viewmodel.mappoints.addpoint(it.latitude,it.longitude, it.time)
                            viewmodel.drawMapPoints()
                            if(viewmodel.mappoints.mapPoints.size>1)
                                    viewmodel.setposition(viewmodel.mappoints.mBounds.build())
                        }, Looper.getMainLooper())

                        viewmodel.showmap(true)
                    }

                }
                activityResultLauncher.launch(REQUIRED_PERMISSIONS)
            }
            intent.action = ""
        }

    }

    fun loadAndDrawOnMap() {
        val context = this
        CoroutineScope(Dispatchers.Main).launch {
            if (!viewmodel.loadanddrawmap(context))
                finishActivity(0)
            viewmodel.showmap(true)
        }
    }
}
