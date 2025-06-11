package com.anax.graphtool
package graph.automata.parts

import math.{Coord, Vector}

import com.anax.graphtool.graph.GraphCell
import com.anax.graphtool.graph.automata.AutomataCell
import com.anax.graphtool.graph.automata.parts.AutomataDataFlipFlopPart.{DATA_INPUT, OUT_INPUT, SET_INPUT}

import scala.util.Random

class AutomataDataFlipFlopPart(offset: Vector, var initialState: Boolean = false) extends AutomataPart(offset) {
	initialState = new Random().nextBoolean()
	
	val du = 2*u
	
	val g1 = new AutomataCell(new Coord(0, -du).add(offset), AutomataCell.g)
	val g2 = new AutomataCell(new Coord(0, -2*du).add(offset), if initialState then AutomataCell.gp else AutomataCell.g)
	val g3 = new AutomataCell(new Coord(du, 0).add(offset), AutomataCell.g)
	val g4 = new AutomataCell(new Coord(0, -3*du).add(offset), AutomataCell.g)
	
	val c = new AutomataCell(new Coord(0, -4*du).add(offset), AutomataCell.c)
	
	val x1 = new AutomataCell(new Coord(du, -du).add(offset), AutomataCell.x)
	val x2 = new AutomataCell(new Coord(du, -2*du).add(offset), AutomataCell.x)
	
	val l1 = new AutomataCell(new Coord(du, -4*du).add(offset), AutomataCell.l)
	val l2 = new AutomataCell(new Coord(du, -3*du).add(offset), AutomataCell.l)
	
	
	g1.link(g2)
	g2.link(x1)
	g2.link(x2)
	g2.link(g3)
	g2.link(g4)
	g4.link(l2)
	l2.link(l1)
	g4.link(c)
	
	registerNodes(Array(g1, g2, g3, g4, l2, x1, x2, l1))
	registerCOutput(c, 0, new Vector(0, 2))
	rotate(Math.PI)
	
	override def setInput(in: Int, input: AutomataPartOutput): Unit = {
		in match {
			case DATA_INPUT => {
				input.cell.link(g1)
				input.complementaryCell.link(g1)
			}
			case SET_INPUT => {
				x1.link(input.cell)
				x1.link(input.complementaryCell)
				x2.link(input.cell)
				x2.link(input.complementaryCell)
				g3.link(input.cell)
				g3.link(input.complementaryCell)
			}
			case OUT_INPUT => {
				l1.link(input.cell)
			}
			case _ => {}
		}
	}
}

object AutomataDataFlipFlopPart {
	val DATA_INPUT = 0
	val SET_INPUT = 1
	val OUT_INPUT = 2
	
}

