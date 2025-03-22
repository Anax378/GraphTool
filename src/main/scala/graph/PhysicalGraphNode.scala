package com.anax.graphtool
package graph

import math.Coord
import math.Vector

import scala.collection.mutable

trait PhysicalGraphNode extends GraphNode {
	var getSource: () => PhysicalGraphNode = () => null
	
	def getPosition(): Coord
	def getVelocity(): Vector
	def updateVelocity(nodes: Iterable[GraphNode], deltaTime: Double): Unit
	def isInside(position: Coord): Boolean
	def setPosition(position: Coord): Unit
	def setVelocity(velocity: Vector): Unit
	def applyVelocity(): Unit = {
		setPosition(getPosition().add(getVelocity()).restore())
	}
	
	def spreadSubTree(initialVector: Vector, totalAngle: Double, scale: Double, visited: mutable.HashSet[GraphNode] = new mutable.HashSet[GraphNode]()): Unit = {
		visited.add(this)
		val physicalAdjacent = getAdjacent().collect({case p: PhysicalGraphNode => p})
		val incrementAngle = totalAngle / (physicalAdjacent.size+1)
		var vec = initialVector.rotate(-totalAngle/2)
		for(cell <- physicalAdjacent){
			if(!visited.contains(cell)){
				visited.add(cell)
				cell.setPosition(new Coord(0, 0).add(vec))
				vec = vec.rotate(incrementAngle)
				cell.spreadSubTree(vec.add(vec.scaleTo(scale)), incrementAngle, scale, visited)
			}
		}
	}
	
	def spreadBFSSubTree(scale: Double): Unit = {
		val root = getBFSSubTree()
		root.spreadSubTree(new Vector(0, scale), Math.PI * 2, scale)
		setPosition(new Coord(0, 0))
		for (node <- root.getConnected().collect({ case c: ContainerGraphNode => c })) {
			node.inner match {
				case p: PhysicalGraphNode => {
					p.setPosition(node.position)
				}
				case _ => {}
			}
		}
	}
	
	def surroundByAdjacent(distance: Double): Unit = {
		val angle = (2 * Math.PI) / getAdjacent().count(node => node.isInstanceOf[PhysicalGraphNode])
		var pos = getPosition().add(new Vector(0, distance))
		
		for (node <- getAdjacent()) {
			node match {
				case cell: PhysicalGraphNode => {
					cell.setPosition(pos)
					pos = pos.rotate(getPosition(), angle)
				}
			}
		}
	}
	
	def onRightClick(): Unit = {}
	def onLeftClick(): Unit = {}
}
