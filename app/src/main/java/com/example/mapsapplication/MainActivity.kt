package com.example.mapsapplication

import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mapsapplication.Fragments.MapFragment
import com.example.mapsapplication.databinding.ActivityMainBinding
import com.google.android.gms.ads.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = MapFragment()
        setCurrentFragment(mapFragment)

        val id = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)

        MobileAds.initialize(this){}
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf("ca-app-pub-3940256099942544/6300978111")).build()
        MobileAds.setRequestConfiguration(configuration)

        val adRequest = AdRequest.Builder().build()
        adRequest.isTestDevice(this)
        binding.adView.loadAd(adRequest)
        binding.adView.adListener = object : AdListener(){
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Toast.makeText(
                    applicationContext,
                    p0.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()
    }

    override fun onPause() {
        binding.adView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.adView.destroy()
        super.onDestroy()
    }

    fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}