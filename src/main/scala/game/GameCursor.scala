package com.anax.graphtool
package game

import render.Renderable

import com.anax.graphtool.math.Coord

import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage

class GameCursor(var position: Coord = new Coord(0, 0)) extends Renderable {
	val radius: Int = 5
	
	val color: Color = Color.GREEN
	val dotColor: Color = Color.WHITE
	
	override def renderOnImage(image: BufferedImage, scale: Double, offset: math.Vector, layer: Int, g2d: Graphics2D): BufferedImage = {
		val renderPosition = Renderable.toScreenPosition(position, scale, offset)
		val x = renderPosition.roundX()
		val y = renderPosition.roundY()
		g2d.setPaint(color)
		g2d.drawOval(x-radius,y-radius, radius*2, radius*2)
		if(x >= 0 && y >= 0 && x < image.getWidth && y < image.getHeight){
			image.setRGB(renderPosition.roundX(), renderPosition.roundY(), dotColor.getRGB)
		}
		image
	}
}
