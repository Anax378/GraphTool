package com.anax.graphtool
package graph

import com.anax.graphtool.math.Coord
import com.anax.graphtool.render.Renderable

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class MinesweeperGameGraph extends Renderable {
	val nodes: mutable.HashSet[GraphNode] = new mutable.HashSet[GraphNode]()
	
	def addNode(node: GraphNode): Unit = {
		nodes.add(node)
		updateAdjacentMineCounts()
	}
	def removeNode(node: GraphNode): Unit = {
		nodes.remove(node)
		for(adjacent <- node.getAdjacent()){
			node.unlink(adjacent)
		}
		updateAdjacentMineCounts()
	}
	def addAll(nodeCollection: IterableOnce[GraphNode]): Unit = {
		nodes.addAll(nodeCollection)
		updateAdjacentMineCounts()
	}
	def offsetAll(offset: math.Vector): Unit = {
		for(node <- nodes){
			node match{case cell: PhysicalGraphNode => {
				cell.setPosition(cell.getPosition().add(offset))
			}}
		}
	}
	
	def triggerWin(): Unit = {
		println("YOU WON!")
	}
	
	def triggerLoss(): Unit = {
		println("YOU LOST!")
	}
	
	def addRandomGraph(cellCount: Int, connectionMultiplier: Double, spacing: Int, mineDensity: Double): Unit = {
		val graphNodes: ArrayBuffer[GraphNode] = new ArrayBuffer[GraphNode]()
		val random: Random = new Random()
		for(i <- 0 until cellCount){
			val cell: MinesweeperCell = new MinesweeperCell(new Coord(random.nextInt(cellCount*spacing), random.nextInt(cellCount*spacing)))
			cell.isMine = random.nextDouble() <= mineDensity
			graphNodes.addOne(cell)
		}
		
		for(i <- 0 until Math.round(connectionMultiplier*cellCount).toInt){
			graphNodes(random.nextInt(graphNodes.length)).link(graphNodes(random.nextInt(graphNodes.length)))
		}
		
		addAll(graphNodes)
	}
	
	def addRandomTree(cellCount: Int, spacing: Int, mineDensity: Double): Unit = {
		val treeNodes: ArrayBuffer[GraphNode] = new ArrayBuffer[GraphNode]()
		val random: Random = new Random()
		for (i: Int <- 0 until cellCount){
			val cell: MinesweeperCell = new MinesweeperCell(new Coord(random.nextInt(cellCount*spacing), random.nextInt(cellCount*spacing)))
			cell.isMine = random.nextDouble() <= mineDensity
			if(i > 0){
				cell.link(treeNodes(random.nextInt(treeNodes.length)))
			}
			treeNodes.addOne(cell)
		}
		addAll(treeNodes)
	}
	
	def addMinesweeperGrid(width: Int, height: Int, spacing: Double, mineDensity: Double): Unit = {
		val random: Random = new Random()
		val generated: Array[GraphNode] = Array.fill(width*height)(null)
		def toIndex(x: Int, y: Int) = y*width+x
		
		for (y: Int <- 0 until height){
			for(x: Int <- 0 until width){
				val cell: MinesweeperCell = new MinesweeperCell(new Coord(x*spacing, y*spacing));
				cell.isMine = random.nextDouble() <= mineDensity
				generated(y*width+x) = cell
				if(x != 0){generated(toIndex(x-1, y)).link(cell)}
				if(x != 0 && y != 0){generated(toIndex(x-1, y-1)).link(cell)}
				if(y != 0){generated(toIndex(x, y-1)).link(cell)}
				if(x < width-1 && y != 0){generated(toIndex(x+1, y-1)).link(cell)}
			}
		}
		addAll(generated)
	}
	
	def updateAdjacentMineCounts(): Unit = {
		for(node <- nodes){
			node match{
				case cell: MinesweeperCell => cell.updateAdjacentMineCount()
			}
		}
	}
	
	def advanceTime(deltaTime: Double): Unit = {
		for(node: GraphNode <- nodes){
			node match{
				case cell: PhysicalGraphNode => {
					cell.updateVelocity(nodes, deltaTime)
				}
			}
		}
		for (node: GraphNode <- nodes) {
			node match {
				case cell: PhysicalGraphNode => {
					cell.applyVelocity()
				}
			}
		}
	}
	
	override def renderOnImage(image: BufferedImage, scale: Double, offset: math.Vector, layer: Int, g2d : Graphics2D): BufferedImage = {
		for (node <- nodes){
			node match {
				case renderable: Renderable => {
					renderable.renderOnImage(image, scale, offset, 0)
				}
			}
		}
		for (node <- nodes) {
			node match {
				case renderable: Renderable => {
					renderable.renderOnImage(image, scale, offset, 1)
				}
			}
		}
		image
	}
}

