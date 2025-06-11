package com.anax.graphtool
package graph.automata.parts
import math.{Coord, Vector}

import com.anax.graphtool.graph.automata.AutomataCell

import scala.collection.mutable

class AutomataDecoderPart(size: Int, limit: Int, offset: Vector, cycle: Int) extends AutomataPart(offset){
	
	val logSize: Int = Math.ceil(Math.log(size)/Math.log(2)).toInt
	val delay = logSize + 3
	
	val bools = for i <- 0 until Math.min(1<<size, limit)
		yield (size -1 to 0 by -1).map(p => ((i >> p) & 1) == 1).toArray
	
	val forks: Array[AutomataAndForkPart] = Array.fill(bools.length)(null);
	
	for (b <- bools.indices){
		val fork = new AutomataAndForkPart(offset.add(new Vector(0, b*2*u*(logSize+2) + 0*u*2*2)), size, bools(b), cycle)
		fork.getOutputs().get(0).foreach(o => forwardOutput(o, b))
		
		forks(b) = fork
		registerNodes(fork.getNodes())
	}
	
	override def setInput(in: Int, input: AutomataPartOutput): Unit = {
		for(fork <- forks){
			fork.setInput(in, input)
		}
	}
}
