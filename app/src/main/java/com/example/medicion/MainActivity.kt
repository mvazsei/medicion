package com.example.medicion

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.xr.runtime.math.toRadians
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min


class MainActivity : AppCompatActivity(){

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var resultTextView: TextView

    private var gpsPoints : MutableList<Pair<Double,Double>> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        resultTextView = findViewById(R.id.resultTextView)

        val markPointButton : Button = findViewById(R.id.markPointButton)
        val calculateAreaButton : Button = findViewById(R.id.calculateAreaButton)
        val newMeasurementButton : Button = findViewById(R.id.newMeasurementButton)
        checkLocationPermissions()



        //boton para marcar puntos gps
        markPointButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@setOnClickListener
            }
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {location ->
                if(location!=null){
                    if(gpsPoints.size<3){
                        gpsPoints.add(Pair(location.latitude,location.longitude))
                        Toast.makeText(this, "Punto ${gpsPoints.size} marcado: (${location.latitude}, ${location.longitude})",
                            Toast.LENGTH_SHORT).show()

                    }else{
                        Toast.makeText(this, "Ya has marcado 3 puntos",
                            Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "No se puede obtener ubicación",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
        //Botón para calcular el area
        calculateAreaButton.setOnClickListener {
            if(gpsPoints.size==3){
                val(x1,y1)= gpsPoints[0]
                val(x2,y2)= gpsPoints[1]
                val(x3,y3)= gpsPoints[2]

                val latMin = min(min(y1, y2), y3)
                val latMax = max(max(y1, y2), y3)
                val lonMin = min(min(x1, x2), x3)
                val lonMax = max(max(x1, x2), x3)

                // Conversiones de grados a distancia en kilómetros
                val base = (lonMax - lonMin) * 111 * Math.cos(Math.toRadians((latMin + latMax) / 2))
                val altura = (latMax - latMin) * 111

                // Calcular el área
                val area = base * altura // Área en kilómetros cuadrados
                // Mostrar el resultado
                resultTextView.text = """
                    Base: %.2f metros
                    Altura: %.2f metros
                    Área: %.2f m²
                """.trimIndent().format(base, altura, area)

            }else{
                Toast.makeText(this, "Necesitas marcar 3 puntos primero.", Toast.LENGTH_SHORT).show()
            }
        }
        //Boton para realizar nueva medicion
        newMeasurementButton.setOnClickListener {
            gpsPoints.clear()
            resultTextView.text ="Inicia nueva medición marcando los puntos"
            Toast.makeText(this, "Preparado para nueva medición",Toast.LENGTH_SHORT).show()
        }

    }

    private fun checkLocationPermissions() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso de ubicación concedido", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

}

