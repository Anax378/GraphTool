package com.anax.graphtool
package graph.mover

import graph.{GraphCell, GraphNode, PhysicalGraphNode}

import com.anax.graphtool.graph.minesweeper.MinesweeperCell
import com.anax.graphtool.math.{Coord, Vector}

import java.awt.Color
import scala.util.Random

class MoverCell(position: Coord, val state: Int) extends GraphCell(position) {
	val colorRandom = new Random(state)
	val color = new Color(colorRandom.nextInt(256), colorRandom.nextInt(256), colorRandom.nextInt(256))
	this.backgroundColor = () => color
	this.outlineColor = backgroundColor
	
	override def updateVelocity(nodes: Iterable[GraphNode], deltaTime: Double): Unit = {
		var acceleration = new Vector(0, 0)
		for (node: GraphNode <- nodes) {
			var constant: Double = repulsionConstant
			node match {
				case cell: PhysicalGraphNode => {
					cell match {
						case minesweeperCell: MinesweeperCell => {
							constant = minesweeperCell.repulsionConstant
						}
						case _ => {}
					}
					if (node != this) {
						
						
						val direction: Vector = cell.getPosition().vectorTo(this.getPosition())
						val distance: Double = direction.magnitude()
						var f = direction.scaleTo(1.0 / (distance * distance) * repulsionConstant)
						cell match {
							case m: MoverCell => {
								if(m.state != state){
									f = f.scale(-1)
								}
							}
							case _ => {}
						}
						acceleration = acceleration.add(f)
					}
				}
				case _ => {}
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
				case _ => {}
			}
		}
		
		acceleration = acceleration.scale(deltaTime)
		//this.velocity = this.velocity.scale(1).add(acceleration)
		this.velocity = acceleration
		//this.velocity = this.velocity.cap(deltaTime * 100000)
		this.velocity = this.velocity.restore()
	}
	
	
}
