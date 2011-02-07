package com.myapp.scala.helloagain

trait Position {

  var x = 0;
  var y = 0;
  var z = 0;
  
  def verschieben(xLen: Int, yLen: Int, zLen: Int) {
    z += zLen;
    y += yLen;
    x += xLen;
  }
}
