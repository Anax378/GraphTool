package com.anax.graphtool
package Graph

import math.Coord

import com.anax.graphtool.render.Renderable

import java.awt.image.BufferedImage

class MinesweeperCell(position: Coord) extends GraphCell(position) with Renderable {
	override def renderOnImage(image: BufferedImage, scale: Int, offset: math.Vector, layer: Int): BufferedImage = ???
}
