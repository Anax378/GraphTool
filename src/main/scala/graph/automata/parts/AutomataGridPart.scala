package com.anax.graphtool
package graph.automata.parts

import graph.automata.AutomataCell
import math.{Coord, Vector}

class AutomataGridPart(offset: Vector, width: Int, height: Int, state: Int) extends AutomataPart(offset) {
	
	val cells: IndexedSeq[AutomataCell] = for
		j <- 0 until height
		i <- 0 until width
	yield new AutomataCell(new Coord(i*50, j*50).add(offset), state)
	
	val map: Map[Int, AutomataCell] = cells.indices.map(i => i -> cells(i)).toMap
	
	override def setInput(in: Int, input: AutomataPartOutput): Unit = {
		cells.lift(in).foreach(i => i.link(input.cell))
	}
	
	for (i <- 0 until width) {
		for (j <- 0 until height) {
			if(i > 0){
				map.get((j + 0) * (width) + i + -1).foreach(o => o.link(cells(j * width + i)))
				map.get((j + 1) * (width) + i + -1).foreach(o => o.link(cells(j * width + i)))
				map.get((j + -1) * (width) + i + -1).foreach(o => o.link(cells(j * width + i)))
			}
			if(i < width-1){
				map.get((j + 0) * (width) + i + 1).foreach(o => o.link(cells(j * width + i)))
				map.get((j + -1) * (width) + i + 1).foreach(o => o.link(cells(j * width + i)))
				map.get((j + 1) * (width) + i + 1).foreach(o => o.link(cells(j * width + i)))
			}
			
			map.get((j + -1) * (width) + i + 0).foreach(o => o.link(cells(j * width + i)))
			map.get((j + 1) * (width) + i + 0).foreach(o => o.link(cells(j * width + i)))
		}
	}
}
