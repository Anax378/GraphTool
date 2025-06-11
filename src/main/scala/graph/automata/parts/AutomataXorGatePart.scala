package com.anax.graphtool
package graph.automata.parts

import math.{Coord, Vector}

import com.anax.graphtool.graph.automata.AutomataCell

class AutomataXorGatePart(offset: Vector) extends AutomataPart(offset) {
	val x = new AutomataCell(new Coord(0, 0).add(offset), AutomataCell.x)
	val c = new AutomataCell(new Coord(0, 2*u).add(offset), AutomataCell.c)
	x.link(c)
	registerNode(x)
	registerNode(c)
	registerCOutput(c, 0)
	
	override def setInput(in: Int, input: AutomataPartOutput): Unit = {
		input.cell.link(x)
	}
}
