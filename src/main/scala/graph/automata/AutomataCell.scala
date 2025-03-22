package com.anax.graphtool
package graph.automata

import graph.GraphCell

import com.anax.graphtool.math.Coord
import com.anax.graphtool.render.Renderable

import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage
import scala.collection.mutable
import scala.util.boundary.break

class AutomataCell(position: Coord, var state: Int) extends GraphCell(position) {
	var nextState: Int = state
	var forceExcite = false;
	
	override def onLeftClick(): Unit = {
		forceExcite = true
	}
	
	linkColor = {
		case a: AutomataCell => {
			if AutomataCell.excited.contains(state) || AutomataCell.excited.contains(a.state)
			then Color.LIGHT_GRAY
			else Color.DARK_GRAY}
		case _ => Color.DARK_GRAY
	}
	
	def prepareNextState(): Unit = {
		val cells = getAdjacent().collect({case a: AutomataCell => a})
		state match {
			case AutomataCell.l => {
				if(forceExcite){
					forceExcite = false
					this.nextState = AutomataCell.lp
					return
				}
				for(cell <- cells){
					if(AutomataCell.excited.contains(cell.state)){
						this.nextState = AutomataCell.lp
						return
					}
				}
			}
			case AutomataCell.lp => {this.nextState = AutomataCell.lz}
			case AutomataCell.lz => {this.nextState = AutomataCell.l}
			case AutomataCell.c => {for(cell <- cells){
				if(cell.state != AutomataCell.dp && AutomataCell.excited.contains(cell.state)){
					nextState = AutomataCell.cp
					return
				}
			}}
			case AutomataCell.cp => {nextState = AutomataCell.c}
			case AutomataCell.d => {for(cell <- cells) {
				if(cell.state == AutomataCell.cp){
					nextState = AutomataCell.dp
					return
				}
			}}
			case AutomataCell.dp => {nextState = AutomataCell.d}
			case AutomataCell.g => {
				var atLeastOne = false
				for(cell <- cells){
					if(AutomataCell.excited.contains(cell.state))
						if(atLeastOne){
							nextState = AutomataCell.gp
							return
						}
						atLeastOne = true
				}
				
			}
			case AutomataCell.gp => {nextState = AutomataCell.g}
			case AutomataCell.x => {
				var num = cells.count(p => {AutomataCell.excited.contains(p.state)})
				if(num == 1){nextState = AutomataCell.xp}
			}
			case AutomataCell.xp => {nextState = AutomataCell.x}
			case _ => {}
		}
	}
	
	outlineColor = () => backgroundColor()
	
	backgroundColor = () => {
		state match {
			case AutomataCell.l => {
				new Color(55, 55, 55)
			}
			case AutomataCell.lp => {
				new Color(255, 255, 255)
			}
			case AutomataCell.lz => {
				new Color(30, 30, 30)
			}
			case AutomataCell.c => {
				new Color(0, 0, 55)
			}
			case AutomataCell.cp => {
				new Color(0, 0, 255)
			}
			case AutomataCell.d => {
				new Color(0, 55, 55)
			}
			case AutomataCell.dp => {
				new Color(0, 255, 255)
			}
			case AutomataCell.g => {
				new Color(56, 22, 0)
			}
			case AutomataCell.gp => {
				new Color(255, 100, 0)
			}
			case AutomataCell.x => {
				new Color(55, 0, 55)
			}
			case AutomataCell.xp => {
				new Color(255, 0, 255)
			}
			case _ => {
				new Color(0, 0, 0)
			}
		}
	}

	def updateState(): Unit = {
		this.state = nextState
	}
	
}

object AutomataCell {
	val l: Int = 0
	val lp: Int = 1
	val lz: Int = 2
	val c: Int = 3
	val cp: Int = 4
	val d: Int = 5
	val dp: Int = 6
	val g: Int = 7
	val gp: Int = 8
	val x: Int = 9
	val xp: Int = 10
	val excited: Set[Int] = Set(lp, dp, gp, xp)
}
