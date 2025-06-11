package com.anax.graphtool
package graph.automata.parts

import graph.automata.AutomataCell
import math.Vector

import com.anax.graphtool.graph.GraphCell

class AutomataPartOutput (val c: AutomataCell, offset: Vector = new Vector(0, 2)) {
	
	var d1: AutomataCell = null
	var d2: AutomataCell = null
	
	def getNodes(): Array[AutomataCell]  = {
		Array(d1, d2, c).flatMap(o => Option(o))
	}
	
	def cell: AutomataCell = {
		if(d1 == null){
			d1 = new AutomataCell(c.position.add(offset.scale(GraphCell.DEFAULT_RADIUS)), AutomataCell.d)
			d1.link(c)
		}
		d1
	}
	def complementaryCell: AutomataCell = {
		if (d2 == null){
			d2 = new AutomataCell(c.position.add(offset.add(new Vector(1, 0)).scaleTo(GraphCell.DEFAULT_RADIUS)), AutomataCell.d)
			d2.link(c)
		}
		d2
	}
	
	def mergeInto(other: AutomataPartOutput): Unit = {
		c.unlink(d1)
		c.unlink(d2)
		c.getAdjacent().foreach(a => other.c.link(a))
		c.unlinkAll()
	}
	
}
