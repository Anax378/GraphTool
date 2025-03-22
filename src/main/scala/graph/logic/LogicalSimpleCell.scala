package com.anax.graphtool
package graph.logic

import math.Coord
import render.Renderable

import java.awt.Graphics2D
import java.awt.image.BufferedImage

class LogicalSimpleCell(position: Coord, inputChannel: Int, outputChannel: Int) extends LogicalCell(position) {
	
	override def processUpdate(): Unit = {
		var state = false
		for(node <- getAdjacent().collect({case l: LogicalCell => l})){
			if(node.channels(inputChannel)){
				state = true
			}
		}
		channels(outputChannel) = state
	}
	
	override def renderBodyOnImage(image: BufferedImage, scale: Double, offset: math.Vector, graphics: Graphics2D): BufferedImage = {
		super.renderBodyOnImage(image, scale, offset, graphics)
		val renderPosition: Coord = Renderable.toScreenPosition(getPosition(), scale, offset)
		val renderX: Int = renderPosition.roundX()
		val renderY: Int = renderPosition.roundY()
		val renderRadius: Int = Math.round((radius * scale * 0.3 * 0.5).toFloat)
		
		graphics.setPaint(LogicalCell.getChannelColor(inputChannel))
		graphics.fillOval(renderX-renderRadius, renderY-renderRadius*2, renderRadius*2, renderRadius*2)
		
		graphics.setPaint(LogicalCell.getChannelColor(outputChannel))
		graphics.fillOval(renderX-renderRadius, renderY, renderRadius*2, renderRadius*2)
		image
	}
	
}
