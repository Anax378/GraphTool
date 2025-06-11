package com.anax.graphtool
package graph.automata.parts

import math.{Coord, Vector}

import com.anax.graphtool.graph.automata.{AutomataCell, AutomataToggleCell}

class AutomataToggleClockPart(offset : Vector, length: Int) extends AutomataPart(offset){
	val cells: Seq[AutomataCell] = for i <- 0 until length
		yield new AutomataCell(new Coord(0, i*20).add(offset), AutomataCell.l)
	
	for (i <- 1 until cells.length){
		cells(i).link(cells(i-1))
	}
	
	
	cells.head.link(cells.last)
	cells.last.state = AutomataCell.lp
	cells.head.state = AutomataCell.lz
	
	val t = new AutomataToggleCell(new Coord(0, 20*(length)).add(offset))
	
	val g = new AutomataCell(new Coord(0, 20*(length+1)).add(offset), AutomataCell.g)
	val c = new AutomataCell(new Coord(0, 20*(length+2)).add(offset), AutomataCell.c)
	
	cells.last.link(g)
	g.link(c)
	
	registerCOutput(c, 0)
	g.link(t)
	
	registerNode(g)
	registerNode(t)
	registerNodes(cells)
	
	update(3)
	
	override def setInput(in: Int, input: AutomataPartOutput): Unit = {}
	def withRandomState(): AutomataToggleClockPart = {
		t.toggled = t.random.nextBoolean()
		this
	}
}
