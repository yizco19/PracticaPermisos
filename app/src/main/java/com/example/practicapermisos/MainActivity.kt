package com.example.practicapermisos

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class MainActivity : AppCompatActivity() {
    private lateinit var requestPermissionLauncher:
            ActivityResultLauncher<Array<String>>
    private lateinit var requestCamera:
            ActivityResultLauncher<Void?>

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var adaptador: AdaptadorEntrada
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    private var lastlocation: Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //configuración del servidor de localizaciones
        this.configLocation()
        //configuración de las actividades de permisos
        this.configRequests()
        adaptador = AdaptadorEntrada(this)
        findViewById<ListView>(R.id.list_view).adapter = adaptador
        findViewById<Button>(R.id.b_peticion).setOnClickListener() {
            val hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            val hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            if(!hasCameraPermission){
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
            }
            if(!hasLocationPermission){
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            }
            if (hasCameraPermission && hasLocationPermission) {
                fusedLocationClient.requestLocationUpdates(// Se solicita la actualización
                    locationRequest!!,
                    locationCallback as LocationCallback,
                    null
                )
                //se abre la camara
                requestCamera.launch(null) // Se solicita la foto



            }
        }




    }
    private fun configLocation() {
        //el servidor
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)
        //la configuración de la actualización, se asocia a
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(1000)
                .build();
        //que se ejecuta cuando se actualiza
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                //se obtiene la última coordenada
                lastlocation = p0.lastLocation
            }
        }
        //se asocia el servicio al tratamiento de la actualización

        fusedLocationClient.removeLocationUpdates(this.locationCallback as LocationCallback)

    }
    private fun configRequests() {
        //los dos launcher para permisos y fotografía
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(
                )
            ) {
            }

        requestCamera =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview
                ()
            )

            //se trata la devolucion desde una función anónima
            {
                var image = it
                adaptador.add(image?.let { it1 -> Entrada(it1, lastlocation) })


            }
    }
}
