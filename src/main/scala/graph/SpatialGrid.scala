package com.anax.graphtool
package graph

import com.anax.graphtool.math.Coord
import com.anax.graphtool.math.Vector

import scala.collection.mutable

class SpatialGrid(val size: Double) {
	
	val gridCells: mutable.Map[(Int, Int), mutable.Set[PhysicalGraphNode]] = new mutable.HashMap()
	val cellGrid: mutable.Map[PhysicalGraphNode, mutable.Set[PhysicalGraphNode]] = new mutable.HashMap()
	val cells: mutable.Set[PhysicalGraphNode] = new mutable.HashSet[PhysicalGraphNode]()
	
	def getGridCoords(pos: Coord): (Int, Int) = {
		(Math.floor(pos.x/size).toInt, Math.floor(pos.y/size).toInt)
	}
	
	def getGrid(gridCoords: (Int, Int)): mutable.Set[PhysicalGraphNode] = {
		if(!gridCells.contains(gridCoords)){
			gridCells(gridCoords) = new mutable.HashSet[PhysicalGraphNode]()
		}
		gridCells(gridCoords)
	}
	
	def addCell(cell: PhysicalGraphNode): Unit = {
		val gridCoords = getGridCoords(cell.getPosition())
		val set = getGrid(gridCoords)
		set.add(cell)
		cellGrid(cell) = set
		cells.add(cell)
	}
	
	def removeCell(cell: PhysicalGraphNode): Unit = {
		if(cellGrid.contains(cell)){
			cellGrid(cell).remove(cell)
			cellGrid.remove(cell)
		}
		cells.remove(cell)
	}
	
	def update(): Unit = {
		for(cell <- cells){
			val gridCoords = getGridCoords(cell.getPosition())
			if(cellGrid(cell) != gridCells.getOrElse(gridCoords, null)){
				cellGrid(cell).remove(cell)
				val grid = getGrid(gridCoords)
				grid.add(cell)
				cellGrid(cell) = grid
			}
		}
	}
	
	def collectInRadius(pos: Coord): Set[PhysicalGraphNode] = {
		val gridCoords = getGridCoords(pos)
		
		val neighborCoords = for {
			i <- -1 until 2
			j <- -1 until 2
		} yield {
			(gridCoords._1+i, gridCoords._2+j)
		}
		
		val nodes: mutable.Set[PhysicalGraphNode] = new mutable.HashSet()
		
		for(coord <- neighborCoords){
			if (gridCells.contains(coord)){
				nodes.addAll(gridCells(coord))
			}
		}
		nodes.filter(node => node.getPosition().vectorTo(pos).magnitude() <= size).toSet
	}
	
}
