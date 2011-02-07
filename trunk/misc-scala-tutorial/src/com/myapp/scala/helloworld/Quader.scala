package com.myapp.scala.helloworld

trait Quader {
    var breite = 1
    var hoehe = 1
    var tiefe = 1
    
    def skalieren(faktor: Int) {
      breite *= faktor
      hoehe *= faktor
      tiefe *= faktor
    }
}
