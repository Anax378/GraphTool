package com.anax.graphtool
package graph

import render.Renderable

import com.anax.graphtool.game.GameCursor
import com.anax.graphtool.graph.minesweeper.MinesweeperCell
import com.anax.graphtool.math.Coord
import com.anax.graphtool.math.Vector

import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage
import java.util.concurrent.ConcurrentHashMap
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.boundary.break
import scala.util.{Random, boundary}

class Graph extends Renderable{
	val nodes: mutable.HashSet[GraphNode] = new mutable.HashSet[GraphNode]()
	val grid: SpatialGrid = new SpatialGrid(100)
	val cursor: GameCursor = new GameCursor()
	val random: Random = new Random()
	var highlightSource: PhysicalGraphNode = null
	
	def getSource: PhysicalGraphNode = {
		highlightSource
	}
	
	def onLeftClick(): Unit = {}
	
	def addNode(node: GraphNode, updateMineCounts: Boolean = true): Unit = {
		nodes.add(node)
		node match {
			case cell: PhysicalGraphNode =>
				cell.getSource = () => {getSource}
				grid.addCell(cell)
		}
		if (updateMineCounts) {
			onGraphChanged()
		}
	}
	
	def graphString: String = {
		val builder = new StringBuilder()
		for (node <- nodes) {
			node match {
				case cell: MinesweeperCell => {
					for (adjacent <- cell.getAdjacent()) {
						adjacent match {
							case adjacentCell: MinesweeperCell => {
								builder.append(s"${cell.name} -> ${adjacentCell.name}").append("\n")
							}
						}
					}
				}
			}
		}
		return builder.toString()
	}
	
	def removeNode(node: GraphNode): Unit = {
		nodes.remove(node)
		for (adjacent <- node.getAdjacent()) {
			node.unlink(adjacent)
		}
		node match {
			case cell: PhysicalGraphNode =>
				grid.removeCell(cell)
		}
		onGraphChanged()
	}
	
	def processKeyPress(unprocessed: ConcurrentHashMap[Int, Boolean]): Unit = {
	}
	
	def addAll(nodeCollection: IterableOnce[GraphNode]): Unit = {
		var added = 0
		for (node <- nodeCollection) {
			added += 1
			addNode(node, false)
		}
		onGraphChanged()
	}
	
	def onGraphChanged(): Unit = {}
	
	def offsetAll(offset: math.Vector): Unit = {
		for (node <- nodes) {
			node match {
				case cell: PhysicalGraphNode => {
					cell.setPosition(cell.getPosition().add(offset))
				}
			}
		}
	}
	
	def surroundAroundPosition(origin: Coord, cells: Iterable[PhysicalGraphNode], distance: Double): Unit = {
		val angle = (2 * Math.PI) / cells.size
		var position = origin.add(new Vector(0, distance))
		for (cell <- cells) {
			cell.setPosition(position)
			position = position.rotate(origin, angle)
		}
	}
	
	def advanceTime(deltaTime: Double): Unit = {
		grid.update()
		for (node: GraphNode <- nodes) {
			node match {
				case cell: PhysicalGraphNode => {
					val close = grid.collectInRadius(cell.getPosition())
					cell.updateVelocity(close, deltaTime)
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
	
	def randomlyDisplace(scale: Double): Unit = {
		for(node <- nodes.collect({case p: PhysicalGraphNode => p})){
			node.setPosition(node.getPosition().add(new Vector(random.nextDouble(), random.nextDouble()).scale(scale)))
		}
	}
	
	override def renderOnImage(image: BufferedImage, scale: Double, offset: math.Vector, layer: Int, g2d: Graphics2D): BufferedImage = {
		for (node <- nodes) {
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
		cursor.renderOnImage(image, scale, offset, 0, g2d)
	}
	
}
