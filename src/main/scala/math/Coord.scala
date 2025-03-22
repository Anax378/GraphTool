package com.anax.graphtool
package math

class Coord (val x: Double, val y: Double) {
  
	def add(vec: Vector): Coord = new Coord(x + vec.x, y + vec.y)
	def rotate(origin: Coord, angle: Double): Coord = {
		
		val vec = origin.vectorTo(this)
		origin.add(vec.transform(Matrix2D.rotationMatrix(angle)))
		
	}
	def vectorTo(other: Coord): Vector = new Vector(other.x-x, other.y-y)
	def scale(factor: Double): Coord = new Coord(x*factor, y*factor)
	def transform(matrix:  Matrix2D): Coord = new Coord(x*matrix.a + y*matrix.b, x*matrix.c + y*matrix.d)
	def roundX(): Int = Math.round(x.toFloat)
	def roundY(): Int = Math.round(y.toFloat)
	def restore(): Coord = new Coord(if x.isNaN then 0 else x, if y.isNaN then 0 else y)
	
 
}


