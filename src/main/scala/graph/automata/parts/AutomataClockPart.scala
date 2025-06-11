package com.anax.graphtool
package graph.automata.parts

import math.Vector

import com.anax.graphtool.graph.automata.AutomataCell

import java.util

class AutomataClockPart(offset: Vector, length: Int) extends AutomataPart(offset) {
	val chain = new AutomataChainPart(offset, length, AutomataCell.l)
	chain.cells.last.link(chain.cells.head)
	
	chain.cells.last.state = AutomataCell.lp
	chain.cells.head.state = AutomataCell.lz
	chain.getOutputs().get(0).foreach(o => forwardOutput(o, 0))
	
	registerNodes(chain.getNodes())
	update(2)
	
	override def setInput(in: Int, input: AutomataPartOutput): Unit = {}
}
