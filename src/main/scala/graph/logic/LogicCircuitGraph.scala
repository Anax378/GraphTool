package com.anax.graphtool
package graph.logic

import graph.{Graph, GraphNode}

import com.anax.graphtool.graph.minesweeper.MinesweeperCell
import com.anax.graphtool.math.Coord

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class LogicCircuitGraph extends Graph {
	var toUpdate: mutable.HashSet[LogicalCell] = new mutable.HashSet[LogicalCell]()
	var updateAlways: mutable.HashSet[LogicalCell] = new mutable.HashSet[LogicalCell]()
	
	override def addNode(node: GraphNode, updateMineCounts: Boolean): Unit = {
		super.addNode(node, updateMineCounts)
		node match {
			case t: LogicalToggleCell => {updateAlways.add(t)}
			case b: LogicalButtonCell => {updateAlways.add(b)}
			case _ => {}
		}
	}
	
	def update(): Unit = {
		val toUpdateNext = new mutable.HashSet[LogicalCell]()
		
		for (node <- toUpdate){
			updateNode(node, toUpdateNext, new mutable.HashSet[LogicalCell]())
		}
		
		toUpdate = toUpdateNext.addAll(updateAlways)
	}
	
	def updateNode(node: LogicalCell, toUpdateNext: mutable.HashSet[LogicalCell], visited: mutable.HashSet[LogicalCell] = new mutable.HashSet[LogicalCell]()): Unit = {
		if(node.update()){
			for(adjacent <- node.getAdjacent().collect({case l: LogicalCell => l})){
				if(visited.contains(adjacent)){
					toUpdateNext.add(adjacent)
				}else{
					visited.add(adjacent)
					updateNode(adjacent, toUpdateNext, visited)
				}
			}
		}
	}
	
	override def onGraphChanged(): Unit = {
		toUpdate.addAll(nodes.collect({case l: LogicalCell => l}))
	}
	
}
