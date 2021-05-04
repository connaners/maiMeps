package com.plete.maimeps

import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var currentLocation: LatLng
    private lateinit var currentAddress: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        
        //menyimpan LatLng ke dalam mutableMapOf
        var locationList = mutableMapOf<String, Double>("latLoc1" to 0.0, "lngLoc1" to 0.0, "latLoc2" to 0.0, "lngLoc2" to 0.0)
        
        //menyimpan alamat ke dalam muableMapOf
        var addressList = mutableMapOf<String, String>("address1" to "", "address2" to "")

        // Add a marker in JKT and move the camera
        val jkt = LatLng(-6.2088, 106.8456)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jkt, 16f))

        //fungsi yg membuat marker dapat berpindah pindah ketika bergeser
        mMap.setOnCameraIdleListener {
            currentLocation = mMap.cameraPosition.target
            val geocoder = Geocoder(this)
            mMap.clear() //menghapus jejak marker ketika bergeser
            var geoCoderResult = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)
            currentAddress = geoCoderResult[0].getAddressLine(0) //mendapatkan value alamat sesuai marker
            mMap.addMarker(MarkerOptions().position(currentLocation).title("Position"))
        }

        btnSet.setOnClickListener {
            /**
             * parsing nilai tiap tiap parameter ke mutableMapOf
             */
            if (locationList.get("latLoc1") == 0.0){
                locationList.put("latLoc1", currentLocation.latitude)
                locationList.put("lngLoc1", currentLocation.longitude)

                addressList.put("address1", currentAddress)
            } else{
                locationList.put("latLoc2", currentLocation.latitude)
                locationList.put("lngLoc2", currentLocation.longitude)

                addressList.put("address2", currentAddress)
            }

            /**
             * menampilkan/mengambil nilai dalam mutableMapOf
             * untuk ditampilkan di TextView
             */
            tvLatLng1.text = "${addressList.get("address1")}"
            tvLatLng2.text = "${addressList.get("address2")}"
        }

        btnHitungJarak.setOnClickListener {
            val loc1 = LatLng(locationList.get("latLoc1")!!, locationList.get("lngLoc1")!!) // mengambil nilai LatLng dan memastikannya tidak berniali 0.0
            val location1 = Location("")
            location1.latitude = locationList.get("latLoc1")!!
            location1.longitude = locationList.get("lngLoc2")!!

            val loc2 = LatLng(locationList.get("latLoc2")!!, locationList.get("lngLoc2")!!) // mengambil nilai LatLng dan memastikannya tidak berniali 0.0
            val location2 = Location("")
            location2.latitude = locationList.get("latLoc2")!!
            location2.longitude = locationList.get("lngLoc2")!!

            /**
             * membuat marker sesuai dengan nilai yg telah diset
             * dan memberikannya garis hubung
             * kemudian menampilkan hasil jarak antara keduanya
             * pada TextView
             */
            mMap.addMarker(MarkerOptions().position(loc1).title("position"))
            mMap.addMarker(MarkerOptions().position(loc2).title("position"))

            mMap.addPolyline(PolylineOptions()
                .clickable(true)
                .add(loc1, loc2)
                .color(R.color.white)
                .width(16f))
            val distance = location1.distanceTo(location2)
            tvJarakHitungLokasi.text = "${distance/1000} KM"
        }

        /**
         * reset semua nilai yg ada pada mutableMapOf
         * dan TextView
         * serta menghilangkan marker sebelumnya
         */
        btnReset.setOnClickListener {
            locationList.putAll(setOf("latLoc1" to 0.0, "lngLoc1" to 0.0, "latLoc2" to 0.0, "lngLoc2"  to 0.0))
            addressList.putAll(setOf("address1" to "", "address2" to ""))
            tvLatLng1.text = ""
            tvLatLng2.text = ""
            tvJarakHitungLokasi.text = ""
            mMap.clear()
        }
    }
}
