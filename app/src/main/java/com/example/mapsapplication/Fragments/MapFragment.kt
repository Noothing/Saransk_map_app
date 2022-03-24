package com.example.mapsapplication.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mapsapplication.R
import com.example.mapsapplication.databinding.FragmentMapBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import pub.devrel.easypermissions.EasyPermissions
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.example.mapsapplication.Objects.Attraction
import com.example.mapsapplication.Objects.CustomInformationWindow
import com.example.mapsapplication.Objects.CustomOverlay
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import org.osmdroid.config.Configuration
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import pub.devrel.easypermissions.AppSettingsDialog

class MapFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: FragmentMapBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Configuration.getInstance().load(
            requireActivity(),
            requireContext().getSharedPreferences("map_shared_pref", Context.MODE_PRIVATE)
        )

        val madMapTiles = XYTileSource(
            "MADskillsMaps",
            0,
            18,
            256,
            ".png",
            arrayOf("https://map.madskill.ru/osm/")
        )

        binding.map.setTileSource(madMapTiles)
        val mapController = binding.map.controller
        val saranskGeoPoint = GeoPoint(54.1844, 45.1817)
        mapController.setCenter(saranskGeoPoint)
        mapController.setZoom(16.0)

        val rotationOverlay = RotationGestureOverlay(binding.map)
        rotationOverlay.isEnabled = true
        binding.map.setMultiTouchControls(true)
        binding.map.overlays.add(rotationOverlay)

        connectLocationOverlay()
        createAttractionsMarkers()
    }

    private val attractions = listOf(
        Attraction(
            0,
            "https://sun9-64.userapi.com/impg/RvAi-RUUvKkZLq2O9JHWf2UjtFbirmhy4i049A/odUPyYZhXUk.jpg?size=800x800&quality=96&sign=b517746c5a7aae5385eea4879ecfaeb2&type=album",
            "Test name",
            "Test description",
            GeoPoint(54.195571380111105, 45.190685758495384)
        ),
        Attraction(
            1,
            "https://sun9-64.userapi.com/impg/RvAi-RUUvKkZLq2O9JHWf2UjtFbirmhy4i049A/odUPyYZhXUk.jpg?size=800x800&quality=96&sign=b517746c5a7aae5385eea4879ecfaeb2&type=album",
            "Test name",
            "Test description",
            GeoPoint(54.18479708346839, 45.18696920286629)
        ),
        Attraction(
            2,
            "https://sun9-64.userapi.com/impg/RvAi-RUUvKkZLq2O9JHWf2UjtFbirmhy4i049A/odUPyYZhXUk.jpg?size=800x800&quality=96&sign=b517746c5a7aae5385eea4879ecfaeb2&type=album",
            "Test name",
            "Test description",
            GeoPoint(54.18174593239031, 45.18152343751088)
        ),
        Attraction(
            3,
            "https://sun9-64.userapi.com/impg/RvAi-RUUvKkZLq2O9JHWf2UjtFbirmhy4i049A/odUPyYZhXUk.jpg?size=800x800&quality=96&sign=b517746c5a7aae5385eea4879ecfaeb2&type=album",
            "Test name",
            "Test description",
            GeoPoint(54.17682824405263, 45.18325950185684)
        ),
        Attraction(
            4,
            "https://sun9-64.userapi.com/impg/RvAi-RUUvKkZLq2O9JHWf2UjtFbirmhy4i049A/odUPyYZhXUk.jpg?size=800x800&quality=96&sign=b517746c5a7aae5385eea4879ecfaeb2&type=album",
            "Test name",
            "Test description",
            GeoPoint(54.18194245846317, 45.20367909471841)
        ),
        Attraction(
            5,
            "https://sun9-64.userapi.com/impg/RvAi-RUUvKkZLq2O9JHWf2UjtFbirmhy4i049A/odUPyYZhXUk.jpg?size=800x800&quality=96&sign=b517746c5a7aae5385eea4879ecfaeb2&type=album",
            "Test name",
            "Test description",
            GeoPoint(54.175752614097, 45.128758031213216)
        )
    )

    private fun createAttractionsMarkers() {
        attractions.forEach { attraction ->
            val marker = Marker(binding.map)
            val infoWindow = CustomInformationWindow(binding.map, attraction)
            marker.infoWindow = infoWindow
            marker.icon = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_attractions_24
            )

            marker.position = attraction.coordination

            binding.map.overlays.add(marker)
        }
    }

    private fun connectLocationOverlay() {
        if (locationAccess()) {
            val userIcon = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_person_pin_circle_24
            )
            val arrowIcon = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_navigation_24
            )
            val locationOverlay = MyLocationNewOverlay(binding.map)
            locationOverlay.enableMyLocation()
            locationOverlay.enableFollowLocation()
            locationOverlay.setDirectionArrow(userIcon!!.toBitmap(), arrowIcon!!.toBitmap())
            binding.map.overlays.add(locationOverlay)

            val compassOverlay = CompassOverlay(requireContext(), binding.map)
            binding.map.overlays.add(compassOverlay)

            val customOverlay =
                CustomOverlay(binding.map, locationOverlay, compassOverlay, childFragmentManager)
            customOverlay.connectOverlay()
        }
    }

    private fun locationAccess(): Boolean {
        if (locationActive()) {
            if (EasyPermissions.hasPermissions(
                    requireContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                if (EasyPermissions.hasPermissions(
                        requireContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    return true
                } else {
                    EasyPermissions.requestPermissions(
                        requireActivity(),
                        "We need this",
                        0,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    return false
                }
            } else {
                EasyPermissions.requestPermissions(
                    requireActivity(),
                    "We need this",
                    1,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
                return false
            }
        } else {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Отключена геолокация")
                .setMessage("Для корректной работы приложения необходим доступ к Вашей геолокации.")
                .setPositiveButton(
                    "Включить"
                ) { dialog, _ ->
                    val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                    startActivity(intent)
                    dialog.dismiss()
                }.show()
            return false
        }
    }

    private fun locationActive(): Boolean {
        val lm = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        ))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        connectLocationOverlay()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when (requestCode) {
            0 -> connectLocationOverlay()
            1 -> connectLocationOverlay()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog
                .Builder(requireActivity())
                .setTitle("Недостаточно прав")
                .setRationale("Приложению было отказано в доступе к геолокации. К сожалению, без доступа приложение не будет работать корректно")
                .build()
                .show()
        }
    }
}