package com.anax.graphtool
package Graph

import math.Coord
import math.Vector

trait PhysicalGraphNode extends GraphNode {
	def getPosition(): Coord
	def getVelocity(): Vector
	def isInside(position: Coord): Boolean
	def setPosition(position: Coord): Unit
	def setVelocity(velocity: Vector): Unit
	def applyVelocity(): Unit = {
		setPosition(getPosition().add(getVelocity()))
	}
}
