package com.anax.graphtool
package graph

import math.Coord

import com.anax.graphtool.render.Renderable

import java.awt.{Color, Font, Graphics2D}
import java.awt.image.BufferedImage
import scala.collection.mutable
import scala.util.Random

class MinesweeperCell(position: Coord) extends GraphCell(position) with Renderable {
	
	def backgroundColor: Color = Color.BLACK
	
	private val colorChoices = for {
		r <- List(0, 255)
		g <- List(0, 255)
		b <- List(0, 255)
		if !(r == 0 && g == 0 && b == 0)
	} yield new Color(r, g, b)
	
	val hubColor = colorChoices(random.nextInt(colorChoices.size))
	
	private var hub_ : MinesweeperCell = null
	def hub: MinesweeperCell = {
		if (hub_ != null){hub_}
		var isHub = true
		var mostLinks = adjacent.size
		var bestHub = this
		
		for(node <- adjacent){
			node match{case cell: MinesweeperCell => {
				if(cell.adjacent.size > mostLinks){
					bestHub = cell
					mostLinks = cell.adjacent.size
				}
			}}
		}
		hub_ = bestHub
		bestHub
	}
	
	def outlineColor: Color = {
		if (hub == this){return hubColor}
		hub.outlineColor.darker()
	}
	val linkColor: Color = Color.DARK_GRAY
	val mineColor: Color = Color.RED
	val highlightColor: Color = Color.CYAN
	
	var isUncovered: Boolean = false
	var isMine: Boolean = false
	var adjacentMineCount: Int = 0
	var isFlagged: Boolean = false
	
	def updateAdjacentMineCount(): Unit = {
		var mines = 0
		for(node <- adjacent){
			node match{
				case cell: MinesweeperCell => {
					if (cell.isMine){
						mines += 1
					}
				}
			}
		}
		adjacentMineCount = mines
	}
	
	//uncovers cell. returns false if cell was a mine, true otherwise
	def uncover(visited: mutable.Set[GraphNode] = new mutable.HashSet[GraphNode]()): Boolean = {
		
		
		if(this.isFlagged){return true}
		
		this.isUncovered = true
		visited.add(this)
		updateAdjacentMineCount()
		if (isMine) {return false}
		if (adjacentMineCount == 0){
			for (node: GraphNode <- adjacent){
				node match {case cell: MinesweeperCell => {
					if (!visited.contains(cell)){
						cell.uncover(visited)
					}
				}}
			}
		}
		
		return true
	}
	
	override def renderOnImage(image: BufferedImage, scale: Double, offset: math.Vector, layer: Int, g2d : Graphics2D = null): BufferedImage = {
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
		val renderRadius: Int = Math.round( (radius*scale).toFloat )
		
		g2d.setPaint(backgroundColor)
		g2d.fillOval(renderX-renderRadius, renderY-renderRadius, renderRadius*2, renderRadius*2)
		
		if(MinesweeperCell.highlightSource == this){
			g2d.setPaint(highlightColor)
		}else{
			g2d.setPaint(outlineColor)
		}
		
		g2d.drawOval(renderX-renderRadius, renderY-renderRadius, renderRadius*2, renderRadius*2)
		
		val halfRadius: Int = renderRadius/2
		if (isUncovered) {
			if (isMine){
				g2d.setPaint(mineColor)
				g2d.drawOval(renderX - halfRadius, renderY - halfRadius, halfRadius * 2, halfRadius * 2)
			}else{
				val font: Font = new Font("Arial", Font.PLAIN, Math.max((renderRadius*2), 0))
				g2d.setFont(font)
				g2d.setPaint(outlineColor)
				val width : Int = g2d.getFontMetrics().getMaxAdvance
				val textCoord: Coord = renderPosition.add(new math.Vector(-renderRadius/2, font.getSize/2))
				g2d.drawString(adjacentMineCount.toString, textCoord.roundX(), textCoord.roundY())
				
			}
		}else if (isFlagged) {
			g2d.setPaint(mineColor)
			g2d.fillRect(renderX-halfRadius, renderY-halfRadius, renderRadius, renderRadius)
		}
		image
	}
	
	def renderConnectionsOnImage(image: BufferedImage, scale: Double, offset: math.Vector, graphics: Graphics2D): BufferedImage = {
		val g2d: Graphics2D = if (graphics != null) graphics else image.createGraphics()
		val renderPosition: Coord = Renderable.toScreenPosition(getPosition(), scale, offset)
		for (node: GraphNode <- adjacent){
			node match{
				case physicalNode: PhysicalGraphNode => {
					if (physicalNode.hashCode() < this.hashCode()){
						
						if(MinesweeperCell.highlightSource == this || MinesweeperCell.highlightSource == physicalNode){
							g2d.setPaint(highlightColor)
						}else{
							g2d.setPaint(linkColor)
						}
						
						val nodeRenderPosition: Coord = Renderable.toScreenPosition(physicalNode.getPosition(), scale, offset)
						g2d.drawLine(renderPosition.roundX(), renderPosition.roundY(), nodeRenderPosition.roundX(), nodeRenderPosition.roundY())
					}
				}
			}
		}
		image
	}
}

object MinesweeperCell {
	var highlightSource: PhysicalGraphNode = null
}









