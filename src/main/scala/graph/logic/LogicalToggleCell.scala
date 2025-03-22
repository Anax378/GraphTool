package com.anax.graphtool
package graph.logic

import math.Coord
import render.Renderable

import java.awt.Graphics2D
import java.awt.image.BufferedImage

class LogicalToggleCell(position: Coord, val channel: Int) extends LogicalCell(position) {
	var toggled = false
	this.adjacent.add(this)
	
	override def processUpdate(): Unit = {
		channels(channel) = toggled
	}
	
	override def renderBodyOnImage(image: BufferedImage, scale: Double, offset: math.Vector, graphics: Graphics2D): BufferedImage = {
		super.renderBodyOnImage(image, scale, offset, graphics)
		val renderPosition: Coord = Renderable.toScreenPosition(getPosition(), scale, offset)
		val renderX: Int = renderPosition.roundX()
		val renderY: Int = renderPosition.roundY()
		val renderRadius: Int = Math.round((radius * scale * 0.3).toFloat)
		
		graphics.setPaint(LogicalCell.getChannelColor(channel))
		graphics.fillOval(renderX - renderRadius, renderY - renderRadius, renderRadius * 2, renderRadius * 2)
		
		image
	}
	
	override def update(): Boolean = {
		processUpdate()
		true
	}
	
	override def onLeftClick(): Unit = {
		toggled = !toggled
	}
}
