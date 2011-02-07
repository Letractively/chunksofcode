package com.myapp.scala.helloworld;

object HelloWorld {
   
   def oncePerSecond(callback: () => Unit) {
       while (true) {
         callback()
         Thread.sleep(1000)
       }
   }
   
   def main(args: Array[String]) {
     
//     val k = new Koerper
//     k.verschieben(1,1,1)
//     k.skalieren(3)
//     
//     println(k)
     
     oncePerSecond( () => println("hello world"))
   }
}