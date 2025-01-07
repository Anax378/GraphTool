package com.anax.graphtool
package Graph

import math.{Coord, Vector}

import com.anax.graphtool.render.Renderable

import java.awt.Color
import java.awt.image.BufferedImage
import scala.collection.mutable

class GraphCell(var position: Coord) extends PhysicalGraphNode{
	
	val adjacent: mutable.HashSet[GraphNode] = new mutable.HashSet[GraphNode]()
	var velocity: Vector = new Vector(0, 0);
	var radius: Double = 10
	
	override def getPosition(): Coord = position
	override def getVelocity(): Vector = velocity
	override def isInside(position: Coord): Boolean = position.vectorTo(this.position).magnitude() <= radius
	override def setPosition(position: Coord): Unit = {this.position = position}
	override def setVelocity(velocity: Vector): Unit = {this.velocity = velocity}
	override def getAdjacent(): mutable.Set[GraphNode] = adjacent;
	override def link(other: GraphNode): Unit = {
		adjacent.add(other)
		if (!other.getAdjacent().contains(this)){
			other.link(this);
		}
	}
	
	override def unlink(other: GraphNode): Unit = {
		adjacent.remove(other)
		if (other.getAdjacent().contains(this)){
			other.unlink(this)
		}
	}
	
}

