package com.example.mapsapplication.Objects

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentManager
import com.example.mapsapplication.Fragments.PlaceBottomSheet
import com.example.mapsapplication.Objects.Network.LocationAPI.LocationAPI
import com.example.mapsapplication.Objects.Network.LocationAPI.LocationObject
import com.example.mapsapplication.Objects.Network.RouteAPI.RouteAPI
import com.example.mapsapplication.Objects.Network.RouteAPI.RouteObject
import com.example.mapsapplication.R
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CustomOverlay(
    private val map: MapView,
    private val locationOverlay: MyLocationNewOverlay,
    private val compassOverlay: CompassOverlay,
    private val supportFragmentManager: FragmentManager,
    ) : Overlay() {

    private val pressedMarker = Marker(map)
    private val routePolyline = Polyline(map)
    private val mapController = map.controller
    private val routeAPI = RouteAPI().init()
    private val locationAPI = LocationAPI().init()
    var inFollowRouteMode = false

    private lateinit var lastKnownLocation: Location

    init {
        pressedMarker.icon =
            AppCompatResources.getDrawable(map.context, R.drawable.ic_baseline_place_24)
        routePolyline.infoWindow = null
    }

    private var customOverlay: Overlay = object : Overlay() {
        override fun onLongPress(e: MotionEvent?, mapView: MapView?): Boolean {
            map.overlays.remove(pressedMarker)
            val proj = mapView!!.projection
            val markerLocation = proj.fromPixels(e!!.x.toInt(), e.y.toInt()) as GeoPoint
            pressedMarker.position = markerLocation
            map.overlays.add(pressedMarker)
            mapController.animateTo(markerLocation)
            lastKnownLocation = locationOverlay.mMyLocationProvider.lastKnownLocation
            createBottomSheet(markerLocation)
            return super.onLongPress(e, mapView)
        }

        override fun onFling(
            pEvent1: MotionEvent?,
            pEvent2: MotionEvent?,
            pVelocityX: Float,
            pVelocityY: Float,
            pMapView: MapView?
        ): Boolean {
            if (inFollowRouteMode) {
                disableFollowRouteMode()
            }
            return super.onFling(pEvent1, pEvent2, pVelocityX, pVelocityY, pMapView)
        }
    }

    fun enableInFollowRouteMode() {
        inFollowRouteMode = true
        zoomToUser()
        followUserAngle()
        locationOverlay.enableFollowLocation()
    }

    fun disableFollowRouteMode() {
        inFollowRouteMode = false
        locationOverlay.disableFollowLocation()
    }

    private fun zoomToUser() {
        lastKnownLocation = locationOverlay.mMyLocationProvider.lastKnownLocation
        val geoPoint = GeoPoint(lastKnownLocation.latitude, lastKnownLocation.longitude)
        mapController.animateTo(geoPoint, 18.0, 100)
    }

    private fun followUserAngle() {
        compassOverlay.mOrientationProvider.startOrientationProvider { orientation, source ->
            if (inFollowRouteMode) {
                map.mapOrientation = orientation
            }
        }
    }

    private fun createBottomSheet(markerLocation: GeoPoint) {
        Log.d("tag", "Creating location info")
        locationAPI
            .searchLocationByLatLon(markerLocation.latitude, markerLocation.longitude)
            .enqueue(object : Callback<LocationObject> {
                override fun onResponse(
                    call: Call<LocationObject>,
                    response: Response<LocationObject>
                ) {
                    if (response.isSuccessful) {
                        val locationObject = response.body() as LocationObject
                        PlaceBottomSheet(locationObject, this@CustomOverlay).show(
                            supportFragmentManager,
                            PlaceBottomSheet.TAG
                        )
                    } else {
                        Log.d("tag", "${response}")
                        Log.d("tag", "${response.body()}")
                        Log.d("tag", "${response.code()}")
                        Log.d("tag", "${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<LocationObject>, t: Throwable) {
                    Log.d("tag", Gson().toJson(call))
                    Log.d("tag", Gson().toJson(t))
                }

            })
    }

    fun findRouteFromCurrentLocation(markerLocation: GeoPoint) {
        val depCoordinates = "${lastKnownLocation.longitude},${lastKnownLocation.latitude}"
        val arrCoordinates = "${markerLocation.longitude},${markerLocation.latitude}"
        requestRoute(depCoordinates, arrCoordinates)
    }

    private fun requestRoute(depCoordinates: String, arrCoordinates: String) {
        routeAPI.getRoute("driving", depCoordinates, arrCoordinates)
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val listGeoPoint: MutableList<GeoPoint> = mutableListOf()
                        val routeObject: RouteObject =
                            Gson().fromJson(response.body(), RouteObject::class.java)
                        routeObject.routes.first().geometry.coordinates.forEach { coordinate ->
                            val geoPoint = GeoPoint(coordinate[1], coordinate[0])
                            listGeoPoint.add(geoPoint)
                        }
                        createRoute(listGeoPoint)
                    } else {
                        Log.d("tag", Gson().toJson(response.code()))
                        Log.d("tag", Gson().toJson(response.errorBody()))
                        Log.d("tag", Gson().toJson(response.body()))
                        Log.d("tag", Gson().toJson(call.request()))
                        Log.d("tag", Gson().toJson(call))
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("tag", Gson().toJson(t))
                    Log.d("tag", Gson().toJson(call))
                }

            })
    }

    fun createRoute(listPoints: List<GeoPoint>) {
        if (map.overlays.contains(routePolyline)) {
            map.overlays.remove(routePolyline)
        }

        routePolyline.setPoints(listPoints)
        map.overlays.add(routePolyline)

        enableInFollowRouteMode()
    }

    fun connectOverlay() {
        if (map.overlays.contains(customOverlay)) {
            map.overlays.remove(customOverlay)
        }

        map.overlays.add(customOverlay)
    }
}