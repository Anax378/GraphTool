package com.anax.graphtool
package graph

import math.{Coord, Vector}

import com.anax.graphtool.render.Renderable

import java.awt.Color
import java.awt.image.BufferedImage
import scala.collection.mutable
import scala.util.Random

class GraphCell(var position: Coord) extends PhysicalGraphNode{
	
	val adjacent: mutable.HashSet[GraphNode] = new mutable.HashSet[GraphNode]()
	val connectionAttractionConstant = 0.45
	val random: Random = new Random()
	val repulsionConstant = 3000 // + random.nextInt(10000)
	
	var velocity: Vector = new Vector(0, 0);
	var radius: Double = 10
	
	override def getPosition(): Coord = position
	override def getVelocity(): Vector = velocity
	override def isInside(position: Coord): Boolean = position.vectorTo(this.position).magnitude() <= radius
	override def setPosition(position: Coord): Unit = {this.position = position}
	override def setVelocity(velocity: Vector): Unit = {this.velocity = velocity}
	override def getAdjacent(): mutable.Set[GraphNode] = adjacent;
	
	override def updateVelocity(nodes: Iterable[GraphNode], deltaTime: Double): Unit = {
		velocity = new Vector(0, 0)
		for(node: GraphNode <- adjacent){
			node match {
				case cell: PhysicalGraphNode => {
					if (node != this){
						velocity = velocity.add(position.vectorTo(cell.getPosition()).scale(connectionAttractionConstant))
					}
				}
			}
		}
		
		
		for(node: GraphNode <- nodes){
			var constant: Double = repulsionConstant
			node match {
				case cell: PhysicalGraphNode => {
					cell match {case minesweeperCell: MinesweeperCell => {constant = minesweeperCell.repulsionConstant}}
					if(node != this){
						val direction: Vector = cell.getPosition().vectorTo(position)
						val distance: Double = direction.magnitude()
						velocity = velocity.add(direction.scaleTo((1.0 / (distance * distance)) * repulsionConstant))
					}
				}
			}
		}
		velocity = velocity.scale(deltaTime)
		velocity = velocity.cap(deltaTime*100)
	}
	
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

