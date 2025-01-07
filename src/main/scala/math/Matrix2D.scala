package com.anax.graphtool
package math

class Matrix2D(val a: Double,val b:Double,
               val c: Double, val d: Double) {
	
	def multiply(other: Matrix2D): Matrix2D = {
		new Matrix2D(other.a*a + other.c*b, a*other.b + b*other.d, c*other.a + d*other.c, c*other.b + d*other.d)
	}
}

object Matrix2D {
	def rotationMatrix(angle: Double): Matrix2D = {
		val cos: Double = Math.cos(angle)
		val sin: Double = Math.sin(angle)
		new Matrix2D(cos, -sin, sin, cos)
	}
	def scaleMatrix(factor: Double): Matrix2D = new Matrix2D(factor, 0, 0, factor)
}

