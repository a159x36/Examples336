package nz.ac.massey.gpxtrack

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.ViewModel
import nz.ac.massey.gpxtrack.MapPoints.MapPoint
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.StampStyle
import com.google.android.gms.maps.model.StrokeStyle
import com.google.android.gms.maps.model.StyleSpan
import com.google.android.gms.maps.model.TextureStyle
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.log
import kotlin.math.round
import androidx.core.net.toUri
import kotlin.math.roundToInt

class MapViewModel():ViewModel() {
    data class MapDrawingState(
        val points: MutableList<LatLng> = mutableListOf(),
        val spans: MutableList<StyleSpan> = mutableListOf()
    )
    data class MapUiState(
        val mapproperties: MapProperties = MapProperties(mapType = MapType.SATELLITE),
        val mapuisettings: MapUiSettings = MapUiSettings(zoomControlsEnabled = false),
        val camerapositionstate: CameraPositionState = CameraPositionState(),
        val width: Float = 4f,
        val colours: Boolean = true,
        val showmap: Boolean = false,
        val showicons: Boolean = true,
        val showlegend: Boolean = true,
        val showdetails: Boolean = true,
        val startmarkerstate: MarkerState = MarkerState(),
        val maxspeedmarkerstate: MarkerState = MarkerState(),
        val showmaxspeed: Boolean = true,
        val sliderval:Float = 0f,
        val msg:String = ""
    )
    var uri= "".toUri()
    val mappoints: MapPoints = MapPoints()

    private val _drawingstate = MutableStateFlow(MapDrawingState())

    val drawingstate=_drawingstate.asStateFlow()
    private val _mapuistate = MutableStateFlow(MapUiState())
    val mapuistate=_mapuistate.asStateFlow()
    var startindex=0
    var sliderstartindex=0
    var sliderendindex=0
    fun setposition(llb: LatLngBounds) {
        val width = llb.northeast.longitude - llb.southwest.longitude
        val height = llb.northeast.latitude - llb.southwest.latitude
        val scale = log(512.0 / width.coerceAtLeast(height), 2.0)
        val centre = llb.center
        _mapuistate.update { it.copy(camerapositionstate =
            CameraPositionState(position = CameraPosition.fromLatLngZoom(centre, scale.toFloat())))}
    }

    fun updatemarkers() {
        if(mappoints.mapPoints.size==0 || mappoints.mMaxpoint>=mappoints.mapPoints.size-1)
            return
        startindex=round((sliderendindex-sliderstartindex)*_mapuistate.value.sliderval).toInt()+sliderstartindex
        val ptStart=mappoints.mapPoints[startindex]
        _mapuistate.update { it.copy(
            startmarkerstate=MarkerState(position=
                LatLng(ptStart.latitude, ptStart.longitude)),
            maxspeedmarkerstate = MarkerState(position=LatLng(
                mappoints.mapPoints[mappoints.mMaxpoint].latitude,
                mappoints.mapPoints[mappoints.mMaxpoint].longitude)))
        }
    }

    fun showmap(show:Boolean)  {
        _mapuistate.update { it.copy(showmap=show)}
    }
    fun showicons(show:Boolean)  {
        _mapuistate.update { it.copy(showicons=show)}
    }

    fun sliderchange(newval: Float) {
        _mapuistate.update { it.copy(sliderval = newval)}
        updatemarkers()
    }

    fun sliderchangedone() {
        CoroutineScope(Dispatchers.Main).launch {
            drawMapPoints()
        }
    }

    fun setMessage(context: Context, stringid: Int) {
        _mapuistate.update { it.copy(msg = context.getString(stringid)  )}
    }

    fun loadanddrawmap(context: Context) :Boolean{
        mappoints.mapPoints.clear()
        if (!mappoints.loadMapPoints(context, uri)) {
            return false
        }
        setMessage(context,R.string.loading)
        setposition(mappoints.mBounds.build())
        setMessage(context,R.string.processing)
        mappoints.processMapPoints()
        setMessage(context,R.string.drawing)
        drawMapPoints()
        sliderstartindex=0
        sliderendindex=mappoints.mapPoints.size-1
        return true
    }


    fun drawMapPoints() {

        CoroutineScope(Dispatchers.Default).launch {
            val points = mappoints.mapPoints
            _drawingstate.value.points.clear()
            _drawingstate.value.spans.clear()
            val maptype= when (Settings.mapType) {
                "Satellite" -> MapType.SATELLITE
                "Hybrid" -> MapType.HYBRID
                "Normal" -> MapType.NORMAL
                "Terrain" -> MapType.TERRAIN
                else -> MapType.NONE
            }
            _mapuistate.update {it.copy(width=Settings.lineWidth.toFloat(),
                colours=Settings.showColours,
                showlegend=Settings.showLegend,
                showdetails=Settings.showDetails,
                showmaxspeed=Settings.showMaxSpeed,
                mapproperties=MapProperties(mapType = maptype))}

            updatemarkers()
            val stampStyle: StampStyle =
                TextureStyle.newBuilder(BitmapDescriptorFactory.fromResource(R.drawable.arrowstamp))
                    .build()
            val ll = mutableListOf<LatLng>()
            val spans = mutableListOf<StyleSpan>()
            var lastspeed = 0.0//points[1].avSpeed / mappoints.mMaxAverageSpeed
            var segments = 0
            val showArrows = Settings.showArrows
            val showColours = Settings.showColours
            for (i in points.indices) {
                val p = points[i]
                ll.add(LatLng(p.latitude, p.longitude))
                if (i > 0) {
                    val lastpoint: MapPoint = points[i-1]
                    var speed = p.avSpeed
                    if(mappoints.mMaxAverageSpeed>0)  speed = (speed) / mappoints.mMaxAverageSpeed
                    if (speed > 1) speed = 1.0
                    if (speed < 0) speed = 0.0
                    speed = (speed * 32.0).roundToInt() / 32.0
                    segments++
                    if ( speed != lastspeed || p.distance == 0.0) {
                        var col =
                            Color.HSVToColor(floatArrayOf(250 - lastspeed.toFloat() * 250, 1f, 1f))

                        if (/*p.distance == 0.0 || */p.timestamp-lastpoint.timestamp> secsBetweenBreaks*1000) {
                            col = 0
                        }
                        if(p.distance == 0.0) {
                            Log.i(TAG,"${p.timestamp} ${lastpoint.timestamp}, ${p.latitude}, ${p.longitude}, ${lastpoint.latitude}, ${lastpoint.longitude}")
                        }

                        if (showColours) {
                            if (showArrows) spans.add(
                                StyleSpan(
                                    StrokeStyle.colorBuilder(col).stamp(stampStyle).build(),
                                    segments.toDouble()
                                )
                            )
                            else spans.add(StyleSpan(col, segments.toDouble()))
                        }
                        segments = 0
                        lastspeed = speed
                    }
                }
            }
            _mapuistate.update { it.copy(showmap = true)}

            val difftime = mappoints.gettraveltimeinsecs()
            if (!mappoints.mapPoints.isEmpty()) {
                _mapuistate.update {
                    it.copy(
                        msg = Settings.getDesciptionString(
                            mappoints.mapPoints[0].timestamp,
                            (mappoints.mTotaldist / 1000) / (difftime.toDouble()),
                            mappoints.mTotaldist,
                            mappoints.gettraveltimeinsecs(),
                            mappoints.mMax1SecSpeed,
                            mappoints.mMax2SecSpeed,
                            mappoints.mFoilTime
                        )
                    )
                }
            }
            _drawingstate.update { state -> MapDrawingState(spans = spans, points = ll) }

        }
    }
}