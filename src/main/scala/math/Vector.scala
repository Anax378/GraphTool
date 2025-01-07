package com.anax.graphtool
package math

class Vector(val x: Double, val y: Double) {
  
  def add(other: Vector): Vector = new Vector(x + other.x, y + other.y)
  def scale(factor: Double): Vector = new Vector(x * factor, y * factor)
  def subtract(other: Vector): Vector = add(other.scale(-1))
  def magnitude(): Double = Math.sqrt(x * x + y * y)
  def normalize(): Vector = scale(1/magnitude())
  def scaleTo(magnitude: Double) = normalize().scale(magnitude)
  def transoform(matrix: Matrix2D) = new Vector(x*matrix.a + y*matrix.b, x*matrix.c + y*matrix.d)
  def cap(max: Double): Vector = if (magnitude() < max) this else this.scaleTo(max)
  
  override def toString: String = s"{x: $x, y: $y}"
  
}
