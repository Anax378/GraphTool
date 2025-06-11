package com.anax.graphtool
package graph.automata.parts

import math.{Coord, Vector}

import com.anax.graphtool.graph.automata.AutomataCell

class AutomataOrGatePart(offset: Vector) extends AutomataPart(offset) {
	val l = new AutomataCell(new Coord(0, 0).add(offset), AutomataCell.l)
	val c = new AutomataCell(new Coord(0, u).add(offset), AutomataCell.c)
	l.link(c)
	registerNode(l)
	registerCOutput(c, 0)
	
	override def setInput(in: Int, input: AutomataPartOutput): Unit = {
		input.cell.link(l)
	}
}
