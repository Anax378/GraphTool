package com.anax.graphtool
package graph

import math.Coord
import math.Vector

trait PhysicalGraphNode extends GraphNode {
	def getPosition(): Coord
	def getVelocity(): Vector
	def updateVelocity(nodes: Iterable[GraphNode], deltaTime: Double): Unit
	def isInside(position: Coord): Boolean
	def setPosition(position: Coord): Unit
	def setVelocity(velocity: Vector): Unit
	def applyVelocity(): Unit = {
		setPosition(getPosition().add(getVelocity()))
	}
}
