package com.anax.graphtool
package graph.automata.parts
import math.{Coord, Vector}

import com.anax.graphtool.graph.automata.AutomataCell
import com.anax.graphtool.graph.automata.parts.AutomataKoggeStoneAdderProcessPart.{GP_INPUT, G_INPUT, G_OUTPUT, PP_INPUT, P_INPUT, P_OUTPUT}

class AutomataKoggeStoneAdderProcessPart(offset: Vector) extends AutomataPart(offset) {
	private val du = 2*u
	
	val g1 = new AutomataCell(new Coord(0, 0).add(offset), AutomataCell.g)
	val g2 = new AutomataCell(new Coord(2*du, 0).add(offset), AutomataCell.g)
	val l1 = new AutomataCell(new Coord(du, 0).add(offset), AutomataCell.l)
	val c2 = new AutomataCell(new Coord(0, du).add(offset), AutomataCell.c)
	val c3 = new AutomataCell(new Coord(du, du).add(offset), AutomataCell.c)
	registerNodes(Array(g1, g2, l1, c2, c3))
	
	g1.link(c2)
	l1.link(c3)
	g2.link(c3)
	
	registerCOutput(c2, P_OUTPUT)
	registerCOutput(c3, G_OUTPUT)
	
	override def setInput(in: Int, input: AutomataPartOutput): Unit = {
		in match {
			case P_INPUT => {
				input.cell.link(g1)
				input.cell.link(g2)
			}
			case G_INPUT => {input.cell.link(l1)}
			case GP_INPUT => {input.cell.link(g2)}
			case PP_INPUT => {input.cell.link(g1)}
			case _ => {}
		}
	}
}

object AutomataKoggeStoneAdderProcessPart {
	val P_INPUT = 0
	val G_INPUT = 1
	val PP_INPUT = 2
	val GP_INPUT = 3
	val P_OUTPUT = 0
	val G_OUTPUT = 1
}
