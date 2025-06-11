package com.anax.graphtool
package graph

import math.{Coord, Vector}

import com.anax.graphtool.graph.GraphCell.{DEFAULT_CONNECTION_ATTRACTION_CONSTANT, DEFAULT_RADIUS, DEFAULT_REPULSION_CONSTANT}
import com.anax.graphtool.graph.minesweeper.MinesweeperCell
import com.anax.graphtool.render.Renderable

import java.awt.{Color, Font, Graphics2D}
import java.awt.image.BufferedImage
import scala.collection.mutable
import scala.util.Random


object GraphCell {
	var DEFAULT_RADIUS: Double = 10
	var DEFAULT_REPULSION_CONSTANT = 3000
	var DEFAULT_CONNECTION_ATTRACTION_CONSTANT = 0.001 //+ 0.44
}

class GraphCell(var position: Coord) extends PhysicalGraphNode with Renderable {
	
	val adjacent: mutable.HashSet[GraphNode] = new mutable.HashSet[GraphNode]()
	val connectionAttractionConstant = DEFAULT_CONNECTION_ATTRACTION_CONSTANT
	val random: Random = new Random()
	val repulsionConstant = DEFAULT_REPULSION_CONSTANT
	
	var name: String = ""
	var backgroundColor: () => Color = () => Color.BLACK
	var numberColor: () => Color = () => Color.WHITE
	var highlightColor: () => Color = () => Color.CYAN
	var linkColor: (PhysicalGraphNode) => Color = (_) => Color.DARK_GRAY
	var outlineColor: () => Color = () => Color.WHITE
	
	var velocity: Vector = new Vector(0, 0);
	var radius: Double = DEFAULT_RADIUS
	
	override def getPosition(): Coord = position
	override def getVelocity(): Vector = velocity
	override def isInside(position: Coord): Boolean = position.vectorTo(this.position).magnitude() <= radius
	override def setPosition(position: Coord): Unit = {this.position = position}
	override def setVelocity(velocity: Vector): Unit = {this.velocity = velocity}
	override def getAdjacent(): mutable.Set[GraphNode] = adjacent;
	
	override def updateVelocity(nodes: Iterable[GraphNode], deltaTime: Double): Unit = {
		var acceleration = new Vector(0, 0)
		for(node: GraphNode <- nodes){
			var constant: Double = repulsionConstant
			node match {
				case cell: PhysicalGraphNode => {
					cell match {case minesweeperCell: MinesweeperCell => {constant = minesweeperCell.repulsionConstant} case _=>{}}
					if(node != this){
						val direction: Vector = cell.getPosition().vectorTo(position)
						val distance: Double = direction.magnitude()
						acceleration = acceleration.add(direction.scaleTo(1.0 / (distance * distance)*repulsionConstant))
					}
				}
				case _=>{}
			}
		}
		for (node: GraphNode <- adjacent) {
			node match {
				case cell: PhysicalGraphNode => {
					if (node != this) {
						val vec = position.vectorTo(cell.getPosition())
						acceleration = acceleration.add(vec.scale(connectionAttractionConstant))
					}
				}
				case _=>{}
			}
		}
		
		acceleration = acceleration.scale(deltaTime)
		//this.velocity = this.velocity.scale(0.5).add(acceleration)
		this.velocity = acceleration
		this.velocity = this.velocity.cap(deltaTime*1000)
		this.velocity = this.velocity.restore()
	}

	override def link(other: GraphNode): Unit = {
		if(other == this){return}
		adjacent.add(other)
		if (!other.getAdjacent().contains(this)){
			other.link(this);
		}
	}
	
	override def unlink(other: GraphNode): Unit = {
		adjacent.remove(other)
		if (other != null && other.getAdjacent().contains(this)){
			other.unlink(this)
		}
	}
	
	override def renderOnImage(image: BufferedImage, scale: Double, offset: math.Vector, layer: Int, graphics: Graphics2D = null): BufferedImage = {
		val g2d: Graphics2D = if (graphics != null) graphics else image.createGraphics()
		if (layer == 0) {
			return renderConnectionsOnImage(image, scale, offset, g2d)
		}
		if (layer == 1) {
			return renderBodyOnImage(image, scale, offset, g2d);
		}
		
		image
	}
	
	def renderBodyOnImage(image: BufferedImage, scale: Double, offset: math.Vector, graphics: Graphics2D): BufferedImage = {
		val g2d: Graphics2D = if (graphics != null) graphics else image.createGraphics()
		val renderPosition: Coord = Renderable.toScreenPosition(getPosition(), scale, offset)
		val renderX: Int = renderPosition.roundX()
		val renderY: Int = renderPosition.roundY()
		val renderRadius: Int = Math.round((radius * scale).toFloat)
		
		g2d.setPaint(backgroundColor())
		g2d.fillOval(renderX - renderRadius, renderY - renderRadius, renderRadius * 2, renderRadius * 2)
		
		
		val source = getSource()
		
		if (source == this || (source != null && source.getAdjacent().contains(this))) {
			g2d.setPaint(highlightColor())
		} else {
			g2d.setPaint(outlineColor())
		}
		
		g2d.drawOval(renderX - renderRadius, renderY - renderRadius, renderRadius * 2, renderRadius * 2)
		
		val halfRadius: Int = renderRadius / 2

		val font: Font = new Font("Arial", Font.PLAIN, Math.max((renderRadius * 2), 0))
		g2d.setFont(font)
		g2d.setPaint(Color.WHITE)
		val width: Int = g2d.getFontMetrics().getMaxAdvance
		val textCoord: Coord = renderPosition.add(new math.Vector(-renderRadius / 2, font.getSize / 2))
		g2d.drawString(name, textCoord.roundX(), textCoord.roundY())
		
		image
	}
	
	def renderConnectionsOnImage(image: BufferedImage, scale: Double, offset: math.Vector, graphics: Graphics2D): BufferedImage = {
		val g2d: Graphics2D = if (graphics != null) graphics else image.createGraphics()
		val renderPosition: Coord = Renderable.toScreenPosition(getPosition(), scale, offset)
		for (node: GraphNode <- adjacent) {
			node match {
				case physicalNode: PhysicalGraphNode => {
					if (physicalNode.hashCode() < this.hashCode()) {
						
						if (getSource() == this || getSource() == physicalNode) {
							g2d.setPaint(highlightColor())
						} else {
							g2d.setPaint(linkColor(physicalNode))
						}
						
						val nodeRenderPosition: Coord = Renderable.toScreenPosition(physicalNode.getPosition(), scale, offset)
						g2d.drawLine(renderPosition.roundX(), renderPosition.roundY(), nodeRenderPosition.roundX(), nodeRenderPosition.roundY())
					}
				}
				case _=>{}
			}
		}
		image
	}
	
}
