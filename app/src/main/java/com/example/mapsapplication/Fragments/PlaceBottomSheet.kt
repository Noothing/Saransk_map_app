package com.example.mapsapplication.Fragments

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.mapsapplication.Objects.CustomOverlay
import com.example.mapsapplication.Objects.Network.LocationAPI.LocationAPI
import com.example.mapsapplication.Objects.Network.LocationAPI.LocationObject
import com.example.mapsapplication.Objects.Network.RouteAPI.RouteAPI
import com.example.mapsapplication.Objects.Network.RouteAPI.RouteObject
import com.example.mapsapplication.R
import com.example.mapsapplication.databinding.FragmentMapBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.osmdroid.util.GeoPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaceBottomSheet(private val locationObject: LocationObject, private val customOverlay: CustomOverlay) : BottomSheetDialogFragment() {

    private lateinit var title: TextView
    private lateinit var description: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.place_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = view.findViewById(R.id.headerPlace)
        title.text = locationObject.display_name
        description = view.findViewById(R.id.descriptionPlace)
        description.text = descriptionBuilder()
        val button = view.findViewById<MaterialButton>(R.id.getRoute)

        button.setOnClickListener {
            val geoPoint = GeoPoint(locationObject.lat, locationObject.lon)
            this.dismiss()
            customOverlay.findRouteFromCurrentLocation(geoPoint)
        }
    }

    private fun descriptionBuilder() : String {
        var text = ""
        val token = object : TypeToken<Map<String, String>>(){}.type
        val locationJson = Gson().toJson(locationObject.address)
        val locationMap: Map<String, String> = Gson().fromJson(locationJson, token)
        locationMap.forEach { t, u ->
            if (u != null ){
                text += " ${u} ,"
            }
        }

        return text
    }

    companion object{
        const val TAG = "Bottom sheet place"
    }
}