package com.anax.graphtool
package graph.automata.parts
import math.Vector

import com.anax.graphtool.graph.GraphCell
import com.anax.graphtool.graph.automata.parts.AutomataRegister.{DATA_INPUT_OFFSET, OUT_INPUT, SET_INPUT}

object AutomataRegister {
	val SET_INPUT = 0
	val OUT_INPUT = 1
	val DATA_INPUT_OFFSET = 2
	
}

class AutomataRegister(offset: Vector, size: Int) extends AutomataPart(offset) {
	val dffs: Array[AutomataDataFlipFlopPart] = (0 until size).map(i => {
		val dff = new AutomataDataFlipFlopPart(new Vector(GraphCell.DEFAULT_RADIUS * 4 * i, 0).add(offset))
		dff.getOutputs().get(0).foreach(o => forwardOutput(o, i))
		registerNodes(dff.getNodes())
		dff
	}
	).toArray
	
	override def setInput(in: Int, input: AutomataPartOutput): Unit = {
		in match {
			case SET_INPUT => {
				dffs.foreach(dff => dff.setInput(AutomataDataFlipFlopPart.SET_INPUT, input))
			}
			case OUT_INPUT => {
				dffs.foreach(dff => dff.setInput(AutomataDataFlipFlopPart.OUT_INPUT , input))
			}
			case _ => {
				dffs.lift(in - DATA_INPUT_OFFSET).foreach(dff => dff.setInput(AutomataDataFlipFlopPart.DATA_INPUT, input))
			}
		}
	}
}
