package com.example.mapsapplication.Objects

import android.widget.ImageView
import android.widget.TextView
import com.example.mapsapplication.R
import com.squareup.picasso.Picasso
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow

class CustomInformationWindow(private val map: MapView, private val attraction: Attraction) : InfoWindow(R.layout.info_window, map)  {

    override fun onOpen(item: Any?) {
        closeAllInfoWindowsOn(map)

        val title = mView.findViewById<TextView>(R.id.infoTitle)
        val body = mView.findViewById<TextView>(R.id.infoBody)
        val image = mView.findViewById<ImageView>(R.id.infoImage)

        title.text = attraction.name
        body.text = attraction.description

        Picasso
            .with(map.context)
            .load(attraction.image)
            .into(image)
    }

    override fun onClose() {

    }

}