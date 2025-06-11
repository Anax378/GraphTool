package com.anax.graphtool
package graph.automata.parts

import math.{Coord, Vector}

import com.anax.graphtool.graph.automata.AutomataCell

class AutomataChainPart(offset: Vector, length: Int, state: Int, outputOffset: Vector = new Vector(2, 0)) extends AutomataPart(offset):
	val cells = for i <- 0 until length
		yield new AutomataCell(new Coord(0, i * 20).add(offset), state)
		
	for(i <- 1 until cells.length) {
		cells(i).link(cells(i-1))
	}
	val c = new AutomataCell(new Coord(0, length*20).add(offset), AutomataCell.c)
	c.link(cells.last)
	
	registerCOutput(c, 0, outputOffset)
	registerNodes(cells)
	
	override def setInput(in: Int, input: AutomataPartOutput): Unit = {
		if(in == 0){
			input.cell.link(cells.head)
		}
	}
