package com.anax.graphtool
package graph.automata.parts

import math.{Coord, Vector}

import com.anax.graphtool.graph.automata.AutomataCell

class AutomataAndGatePart(offset: Vector) extends AutomataPart(offset) {
	val g = new AutomataCell(new Coord(0, 0).add(offset), AutomataCell.g)
	val c = new AutomataCell(new Coord(0, u).add(offset), AutomataCell.c)
	g.link(c)
	registerNode(g)
	registerCOutput(c, 0)
	
	override def setInput(in: Int, input: AutomataPartOutput): Unit = {
		input.cell.link(g)
	}
}
