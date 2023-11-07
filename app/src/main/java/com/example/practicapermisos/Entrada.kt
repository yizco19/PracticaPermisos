package com.example.practicapermisos

import android.graphics.Bitmap
import android.location.Location

class Entrada {

    var imagen: Bitmap? = null
    var location: Location? = null

    constructor(imagen: Bitmap?, location: Location?) {
        this.imagen = imagen
        this.location = location
    }
}