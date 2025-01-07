package com.anax.graphtool
package render

import java.awt.image.BufferedImage
import math.{Coord, Vector}

import java.awt.Graphics2D

trait Renderable {
  def renderOnImage(image: BufferedImage, scale: Double, offset: Vector, layer: Int, g2d: Graphics2D = null): BufferedImage
}

object Renderable {
  def toScreenPosition(position: Coord, scale: Double, offset: Vector): Coord = position.scale(scale).add(offset)
  def fromScreenPosition(position: Coord, scale: Double, offset: Vector): Coord = position.add(offset.scale(-1)).scale(1/scale)
}
