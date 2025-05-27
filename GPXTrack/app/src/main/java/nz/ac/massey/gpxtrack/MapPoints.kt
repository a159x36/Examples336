package nz.ac.massey.gpxtrack

import android.content.Context
import android.location.Location
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.ticofab.androidgpxparser.parser.GPXParser
import io.ticofab.androidgpxparser.parser.domain.Gpx
import io.ticofab.androidgpxparser.parser.domain.TrackPoint

const val secsBetweenBreaks = 60

class MapPoints internal constructor() {
    class MapPoint (
        var latitude: Double,
        var longitude: Double,
        var timestamp: Long,
        var avSpeed: Double,
        var distance: Double,
        var iSpeed: Double
    )

    val mapPoints: MutableList<MapPoint> = ArrayList<MapPoint>(1000)

    var mBounds: LatLngBounds.Builder = LatLngBounds.builder()
    var mMaxAverageSpeed: Double = 0.1
    var mMax1SecSpeed: Double = 0.0
    var mMax2SecSpeed: Double = 0.0
    var mTotaldist: Double = 0.0
    var mFoilTime: Double = 0.0
    var mMaxpoint: Int = 0
    var mHasBreaks: Boolean = false

    fun getlengthinseconds(): Long {
        val npoints = mapPoints.size
        if (npoints == 0) return 0
        val starttime = mapPoints[0].timestamp
        val endtime = mapPoints[npoints - 1].timestamp
        return ((endtime - starttime) / 1000.0f).toLong()
    }

    fun gettraveltimeinsecs(): Long {
        if (mapPoints.size <= 1) return 0
        if (!mHasBreaks && mapPoints[0].timestamp != 0L) {
            return getlengthinseconds()
        }
        var total: Long = 0
        var t = mapPoints[0].timestamp
        for (i in 1..mapPoints.size-1) {
            val t1 = mapPoints[i].timestamp
            val diff = t1 - t
            if (diff < secsBetweenBreaks * 1000 && diff > 0) total += diff
            t = t1
        }
        if (total < 0) return 0
        return total / 1000
    }

    fun getdistance(p1lat: Double, p1lon: Double, p2lat: Double, p2lon: Double): Double {
        val dist = FloatArray(1)
        Location.distanceBetween(
            p1lat,
            p1lon,
            p2lat,
            p2lon,
            dist
        )
        return dist[0].toDouble()
    }

    fun loadMapPoints(c: Context, uri: Uri): Boolean {
        val gpxParser = GPXParser()

        mFoilTime=0.0
        mMax1SecSpeed=0.0
        mMax2SecSpeed=0.0
        mHasBreaks=false
        mTotaldist=0.0
        mMaxAverageSpeed=0.0
        var parsedGpx: Gpx
        try {
            val istr = c.contentResolver.openInputStream(uri)
            parsedGpx = gpxParser.parse(istr)
            istr?.close()
        } catch (_: Exception) {
            return false
        }

        val tracks = parsedGpx.tracks

        var lastpoint: TrackPoint? = null
        val maxacc: Double = Settings.maxAcc
        val minspeed: Double = Settings.minSpeed / 3600
        var ispeed = 0.0
        for (track in tracks) {
            for (segment in track.trackSegments) {
                for (point in segment.trackPoints) {
                    val dd: Double
                    val acc: Double
                    val time: Long
                    val sp: Double
                    if (lastpoint != null) {
                        time = if (point.time != null && lastpoint.time != null)
                            point.time.millis - lastpoint.time.millis else 1000

                        if (time / 1000 > secsBetweenBreaks) {
                            mHasBreaks = true
                        }
                        dd = getdistance(point.latitude, point.longitude, lastpoint.latitude, lastpoint.longitude)

                        if (time != 0L) {
                            sp = dd / time
                            acc = sp - ispeed
                            val lastispeed = ispeed
                            if (acc < maxacc / 3600) ispeed = sp
                            else ispeed += maxacc / (3600 * 2)
                            if (ispeed > mMax1SecSpeed) mMax1SecSpeed = ispeed
                            val speed2sec = (ispeed + lastispeed) / 2
                            if (speed2sec > mMax2SecSpeed) mMax2SecSpeed = speed2sec
                            if (ispeed > minspeed) mFoilTime += time / 1000.0
                        }
                        mTotaldist += dd
                    } else dd = 0.0
                    val timestamp = if (point.time != null) point.time.millis else 0

                    mapPoints.add(
                        MapPoint(
                            point.latitude,
                            point.longitude,
                            timestamp,
                            0.0,
                            dd,
                            ispeed
                        )
                    )
                    lastpoint = point
                    val ll = LatLng(point.latitude, point.longitude)
                    mBounds.include(ll)
                }
            }
        }
        if (mapPoints.isEmpty()) mBounds.include(LatLng(0.0, 0.0))
        return true
    }

    fun addpoint(latitude: Double, longitude: Double, timestamp: Long) {
        val lastpoint=mapPoints.lastOrNull()
        if(lastpoint!=null) {
            val dd = getdistance(lastpoint.latitude, lastpoint.longitude, latitude, longitude)
            val time=timestamp-lastpoint.timestamp
            val ispeed = dd / (timestamp - lastpoint.timestamp)
            mapPoints.add(
                MapPoint(
                    latitude,
                    longitude,
                    timestamp,
                    0.0,
                    dd,
                    ispeed
                )
            )
            processMapPoints()
            val ll = LatLng(latitude, longitude)
            mBounds.include(ll)
            mTotaldist += dd
        } else {
            mapPoints.add(
                MapPoint(
                    latitude,
                    longitude,
                    timestamp,
                    0.0,
                    0.0,
                    0.0
                )
            )

        }
    }

    fun clamp(value: Int, min: Int, max: Int): Int {
        if (value < min) return min
        if (value > max) return max
        return value
    }

    fun processMapPoints() {
        var sumspeed = 0.0
        mTotaldist = 0.0
        mMax1SecSpeed = 0.0
        mMax2SecSpeed = 0.0
        val window: Int = Settings.window
        val npoints = mapPoints.size
        if (npoints == 0) return
        for (i in -window / 2..  npoints-1) {
            // eg i=10, window=5
            // ni=12, pi=7
            // ++++++++++++
            //   |  ^  |
            //   p    n
            // eg i=10, window=4
            // ni=13, pi=8
            // ++++++++++++
            //   |  ^  |
            var ni = i + (window - 1) / 2
            var pi = i - window / 2 - 1

            val niInrange = ni < npoints && ni >= 0
            val piInrange = pi < npoints && pi >= 0
            if (niInrange) sumspeed += mapPoints[ni].iSpeed
            if (piInrange) sumspeed -= mapPoints[pi].iSpeed

            ni = clamp(ni, 0, npoints - 1)
            pi = clamp(pi, 0, npoints - 1)

            if (i > 0) {
                val p = mapPoints[i]
                val lastp = mapPoints[i - 1]
                if ((p.timestamp - lastp.timestamp) / 1000 < secsBetweenBreaks) { // max time between timestamps
                    mTotaldist += p.distance
                    val sumtime = if (niInrange && piInrange) (mapPoints[ni].timestamp - mapPoints[pi].timestamp).toDouble()
                    else ((ni - pi) * 1000).toDouble()

                    val speed = (sumspeed * 1000) / sumtime
                    val speed1sec = p.iSpeed
                    val speed2sec = (speed1sec + lastp.iSpeed) / 2
                    val acc = (speed1sec - lastp.iSpeed) * 3600

                    if (speed > mMaxAverageSpeed && acc > 0.0) {
                        mMaxAverageSpeed = speed
                    }
                    if (speed1sec > mMax1SecSpeed && acc > 0.0) {
                        mMax1SecSpeed = speed1sec
                        mMaxpoint = i
                    }
                    if (speed2sec > mMax2SecSpeed && acc > 0.0) {
                        mMax2SecSpeed = speed2sec
                    }
                    p.avSpeed = speed
                } else {
                    p.distance = 0.0
                    mHasBreaks = true
                }
            }
        }
    }



}
