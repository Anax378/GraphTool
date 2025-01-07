package com.anax.graphtool
package thread

import render.Renderable

import java.awt.Graphics2D
import java.awt.image.BufferedImage

class RenderThread(val provider: Renderable,
                   val consumer: (BufferedImage) => Unit,
                   val getScale: () => Double,
                   val getOffset: () => math.Vector,
                   val getWidth: () => Int,
                   val getHeight: () => Int ) extends Thread {
	
	override def run(): Unit = {
		while (true) {
			val image: BufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB)
			val g2d: Graphics2D = image.createGraphics()
			consumer(provider.renderOnImage(image, getScale(), getOffset(), 0, g2d))
		}
	}
}
