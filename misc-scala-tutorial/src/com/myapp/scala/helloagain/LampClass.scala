package com.myapp.scala.helloagain

class LampClass(an: Boolean) {
  var leuchtet = an;
  println("Lampe angelegt " + leuchtet);
  
}

object MyMain {
  def main(args: Array[String]) {
    new LampClass(true);
    new LampClass(false);
  }
}