package com.anax.graphtool
package graph.logic

import graph.GraphCell
import graph.logic.LogicalCell.getChannelColor
import math.Coord
import render.Renderable

import java.awt.image.BufferedImage
import java.awt.{Color, Graphics2D}

class LogicalCell(position: Coord) extends GraphCell(position) {
	var channels: Array[Boolean] = Array(false, false, false)
	var b: Boolean = false
	
	def processUpdate(): Unit = {}
	def update(): Boolean = {
		val channels = this.channels.clone()
		processUpdate()
		!channels.sameElements(this.channels)
	}
	
	override def renderBodyOnImage(image: BufferedImage, scale: Double, offset: math.Vector, graphics: Graphics2D): BufferedImage = {
		super.renderBodyOnImage(image, scale, offset, graphics)
		val renderPosition: Coord = Renderable.toScreenPosition(getPosition(), scale, offset)
		val renderX: Int = renderPosition.roundX()
		val renderY: Int = renderPosition.roundY()
		
		if(channels(0)){
			val renderRadius: Int = Math.round((radius * scale * 0.8).toFloat)
			graphics.setPaint(getChannelColor(0))
			graphics.drawOval(renderX - renderRadius, renderY - renderRadius, renderRadius * 2, renderRadius * 2)
		}
		if (channels(1)) {
			val renderRadius: Int = Math.round((radius * scale * 0.7).toFloat)
			graphics.setPaint(getChannelColor(1))
			graphics.drawOval(renderX - renderRadius, renderY - renderRadius, renderRadius * 2, renderRadius * 2)
		}
		if (channels(2)) {
			val renderRadius: Int = Math.round((radius * scale * 0.6).toFloat)
			graphics.setPaint(getChannelColor(2))
			graphics.drawOval(renderX - renderRadius, renderY - renderRadius, renderRadius * 2, renderRadius * 2)
		}
		image
	}
	
}

object LogicalCell {
	val R: Int = 0
	val G: Int = 1
	val B: Int = 2
	
	def getChannelColor(channel: Int): Color = {
		if(channel == R){
			return Color.RED
		}
		if(channel == G){
			return Color.GREEN
		}
		if(channel == B){
			return new Color(100, 0, 255)
		}
		
		return Color.WHITE
	}
}


