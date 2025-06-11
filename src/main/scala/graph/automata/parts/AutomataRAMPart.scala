package com.anax.graphtool
package graph.automata.parts

import math.{Coord, Vector}

import com.anax.graphtool.graph.GraphCell
import com.anax.graphtool.graph.automata.AutomataCell


/*
Input:
var address
var input_data
val set

Output:
var data

*/
class AutomataRAMPart(offset: Vector, size: Int, limit: Int, cycle: Int, regSize: Int) extends AutomataPart(offset) {
	val SET_INPUT: Int = 0
	val ADDRESS_INPUT_OFFSET: Int = 1
	val DATA_INPUT_OFFSET: Int = ADDRESS_INPUT_OFFSET + size
	
	val decoderSize = (Math.ceil(Math.log(size)/Math.log(2)).toInt + 2) * u *2
	val decoder = new AutomataDecoderPart(size, limit, offset.add(new Vector(2*u, 0)), cycle)
	
	val ands = (0 until Math.min(1<<size, limit)).map(i =>
		new AutomataAndGatePart(new Vector(size*u*2+u*4, decoderSize*i+6*u).add(offset))
	)
	
	val regs = (0 until Math.min(1<<size, limit)).map(i => {
		val reg = new AutomataRegister(new Vector(size*u*2 + 8*u, decoderSize * i).add(offset), regSize)
		val or = new AutomataOrGatePart(new Vector(size*u*2 + 2*u, decoderSize * i + u+6*u).add(offset))
		decoder.connectInto(or, i, 0)
		decoder.connectInto(ands(i), i, 0)
		
		or.connectInto(reg, 0, AutomataRegister.OUT_INPUT)
		registerNodes(or.getNodes())
		ands(i).connectInto(reg, 0, AutomataRegister.SET_INPUT)
		
		reg
	})
	
	val setChain = new AutomataChainPart(new Vector(-2*u, 0).add(offset), decoder.delay - 2, AutomataCell.l)
	ands.foreach(a => setChain.connectInto(a, 0, 0))
	
	val dataChains: Array[AutomataChainPart] = (0 until regSize).map(i => {
		val chain = new AutomataChainPart(new Vector(size*u*2+8*u+4*u*i, -(decoder.delay-1+3)*2*u), decoder.delay-2+3, AutomataCell.l)
		regs.foreach(r => chain.connectInto(r, 0, AutomataRegister.DATA_INPUT_OFFSET+i))
		chain
	}).toArray
	
	for(i <- 0 until regSize){
		for(j <- 1 until Math.min(1<<size, limit)){
			regs(0).getOutputs().get(i).foreach(o => regs(j).mergeOutputInto(i, o))
		}
		regs(0).getOutputs().get(i).foreach(o => forwardOutput(o, i))
	}
	
	registerNodes(decoder.getNodes())
	ands.foreach(a => registerNodes(a.getNodes()))
	regs.foreach(r => registerNodes(r.getNodes()))
	dataChains.foreach(d => registerNodes(d.getNodes()))
	registerNodes(setChain.getNodes())
	
	override def setInput(in: Int, input: AutomataPartOutput): Unit = {
		if(in == SET_INPUT){
			setChain.setInput(0, input)
			return
		}
		if(in < DATA_INPUT_OFFSET){
			decoder.setInput(in - ADDRESS_INPUT_OFFSET, input)
			return
		}
		dataChains.lift(in-DATA_INPUT_OFFSET).foreach(d => d.setInput(0, input))
	}
}
