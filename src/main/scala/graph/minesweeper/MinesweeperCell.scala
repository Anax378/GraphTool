package com.anax.graphtool
package graph.minesweeper

import graph.{GraphCell, GraphNode}
import math.Coord
import render.Renderable

import java.awt.image.BufferedImage
import java.awt.{Color, Font, Graphics2D}
import scala.collection.{immutable, mutable}
import scala.util.Random

class MinesweeperCell(position: Coord) extends GraphCell(position) with Renderable with InformationCell {
	
	private val colorBlacklist: Array[Color] = Array(Color.BLACK, Color.CYAN);
	private val colorChoices = (for {
		r <- List(0, 255)
		g <- List(0, 255)
		b <- List(0, 255)
	} yield new Color(r, g, b)).filter(color => !colorBlacklist.contains(color))
	
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
	
	def withMineState(state: Boolean): MinesweeperCell = {
		this.isMine = state
		this
	}
	
	override def onRightClick(): Unit = {
		isFlagged = !isFlagged
	}
	
	override def onLeftClick(): Unit = {
		uncover()
	}
	
	def setOutlineColor(color: Color): Unit = {
		outlineColor = () => color
	}
	
	numberColor = () => {
		if(adjacentMineCount < colorChoices.size){
			colorChoices(adjacentMineCount)
		}else {
			val random = new Random(adjacentMineCount + 42)
			new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))
		}
	}
	
	val mineColor: Color = Color.RED
	
	var isUncovered: Boolean = false
	var isMine: Boolean = false
	var adjacentMineCount: Int = 0
	var isFlagged: Boolean = false
	
	def withName(name: String): MinesweeperCell = {
		this.name = name
		this
	}
	
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
	
	def cullRedundantLinks(): Unit = {
		if(!isUncovered || isMine){return}
		for(node <- adjacent) {
			node match {
				case cell: MinesweeperCell => {
					if(cell.isUncovered && !cell.isMine){unlink(cell)}
				}
			}
		}
	}
	
	override def renderBodyOnImage(image: BufferedImage, scale: Double, offset: math.Vector, graphics: Graphics2D): BufferedImage = {
		val g2d: Graphics2D = if (graphics != null) graphics else image.createGraphics()
		val renderPosition: Coord = Renderable.toScreenPosition(getPosition(), scale, offset)
		val renderX: Int = renderPosition.roundX()
		val renderY: Int = renderPosition.roundY()
		val renderRadius: Int = Math.round( (radius*scale).toFloat )
		
		g2d.setPaint(backgroundColor())
		g2d.fillOval(renderX-renderRadius, renderY-renderRadius, renderRadius*2, renderRadius*2)
		
		
		val source = getSource()
		
		if(source == this || (source != null && source.getAdjacent().contains(this)) ){
			g2d.setPaint(highlightColor())
		}else{
			g2d.setPaint(outlineColor())
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
				g2d.setPaint(numberColor())
				val width : Int = g2d.getFontMetrics().getMaxAdvance
				val textCoord: Coord = renderPosition.add(new math.Vector(-renderRadius/2, font.getSize/2))
				g2d.drawString(adjacentMineCount.toString, textCoord.roundX(), textCoord.roundY())
				
			}
		}else if (isFlagged) {
			g2d.setPaint(mineColor)
			g2d.fillRect(renderX-halfRadius, renderY-halfRadius, renderRadius, renderRadius)
		}else{
			val font: Font = new Font("Arial", Font.PLAIN, Math.max((renderRadius * 2), 0))
			g2d.setFont(font)
			g2d.setPaint(Color.WHITE)
			val width: Int = g2d.getFontMetrics().getMaxAdvance
			val textCoord: Coord = renderPosition.add(new math.Vector(-renderRadius / 2, font.getSize / 2))
			g2d.drawString(name, textCoord.roundX(), textCoord.roundY())
		}
		image
	}
	

	override def set(): Set[GraphNode] = {
		this.getAdjacent().toSet
	}
	
	override def lower(): Int = {
		if (isUncovered) {
			return adjacentMineCount
		}
		return 0
	}
	
	override def upper(): Int = {
		if(isUncovered){
			return adjacentMineCount
		}
		return getAdjacent().size
	}
}









