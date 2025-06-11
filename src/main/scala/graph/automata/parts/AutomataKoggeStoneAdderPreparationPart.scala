package com.anax.graphtool
package graph.automata.parts

import math.{Coord, Vector}

import com.anax.graphtool.graph.automata.AutomataCell
import com.anax.graphtool.graph.automata.parts.AutomataKoggeStoneAdderPreparationPart.{INPUT_A, INPUT_B, OUTPUT_G, OUTPUT_P}

class AutomataKoggeStoneAdderPreparationPart(offset: Vector) extends AutomataPart(offset) {
	private val du = u*2
	
	val x = AutomataCell(new Coord(0, 0).add(offset), AutomataCell.x)
	val g = AutomataCell(new Coord(du, 0).add(offset), AutomataCell.g)
	val c1 = AutomataCell(new Coord(0, du).add(offset), AutomataCell.c)
	val c2 = AutomataCell(new Coord(du, du).add(offset), AutomataCell.c)
	c1.link(x)
	c2.link(g)
	registerNodes(Array(x, g, c1, c2))
	registerCOutput(c1, OUTPUT_P)
	registerCOutput(c2, OUTPUT_G)
	
	override def setInput(in: Int, input: AutomataPartOutput): Unit = {
		in match{
			case INPUT_A | INPUT_B => {
				input.cell.link(x)
				input.cell.link(g)
			}
			case _ => {}
		}
	}
}

object AutomataKoggeStoneAdderPreparationPart {
	val INPUT_A = 0
	val INPUT_B = 1
	val OUTPUT_P = 0
	val OUTPUT_G = 1
}
