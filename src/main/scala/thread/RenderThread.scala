package com.anax.graphtool
package thread

import render.Renderable

import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage

class RenderThread(val provider: Renderable,
                   val consumer: (BufferedImage) => Unit,
                   val getScale: () => Double,
                   val getOffset: () => math.Vector,
                   val getWidth: () => Int,
                   val getHeight: () => Int ) extends Thread {
	
	override def run(): Unit = {
		var width = getWidth()
		var height = getHeight()
		var image: BufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
		var buffer: BufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
		while (true) {
			if(getWidth() != width || getHeight() != height){
				width = getWidth()
				height = getHeight()
				image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB)
				buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB)
			}
			
			val temp = image
			image = buffer
			buffer = temp
			
			val g2d: Graphics2D = image.createGraphics()
			g2d.setPaint(Color.BLACK)
			g2d.fillRect(0, 0, width, height)
			consumer(provider.renderOnImage(image, getScale(), getOffset(), 0, g2d))
		}
	}
}
