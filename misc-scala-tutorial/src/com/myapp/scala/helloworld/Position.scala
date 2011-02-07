package com.myapp.scala.helloworld

trait Position {
    var x = 0
    var y = 0
    var z = 0
    
    def verschieben(xd: Int, yd: Int, zd: Int) {
      x += xd;
      y += yd;
      z += zd;
    }
}
