package com.anax.graphtool
package math

class Vector(val x: Double, val y: Double) {
  
  def add(other: Vector): Vector = new Vector(x + other.x, y + other.y)
  def scale(factor: Double): Vector = new Vector(x * factor, y * factor)
  def subtract(other: Vector): Vector = add(other.scale(-1))
  def magnitude(): Double = Math.sqrt(x * x + y * y)
  def normalize(): Vector = scale(1/magnitude())
  def scaleTo(magnitude: Double): Vector = normalize().scale(magnitude)
  def transform(matrix: Matrix2D): Vector = new Vector(x*matrix.a + y*matrix.b, x*matrix.c + y*matrix.d)
  def rotate(angle: Double): Vector = transform(Matrix2D.rotationMatrix(angle))
  def cap(max: Double): Vector = if (magnitude() < max) this else this.scaleTo(max)
  def restore(): Vector =  new Vector(if x.isNaN then 0 else x, if y.isNaN then 0 else y)
  
  override def toString: String = s"{x: $x, y: $y}"
  
}
