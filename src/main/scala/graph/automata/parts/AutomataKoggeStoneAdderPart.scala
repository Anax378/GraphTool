package com.anax.graphtool
package graph.automata.parts

import math.{Coord, Vector}

import com.anax.graphtool.graph.automata.AutomataCell

class AutomataKoggeStoneAdderPart(offset: Vector, size: Int) extends AutomataPart(offset) {
	private val logSize: Int = Math.ceil(Math.log(size)/Math.log(2)).toInt
	private val trueSize: Int = 1<<logSize
	
	val A_INPUT_OFFSET: Int = 0
	val B_INPUT_OFFSET: Int = 1<<logSize
	val CIN_INPUT: Int = B_INPUT_OFFSET*2
	val COUT_OUTPUT: Int = 1<<logSize
 
	private val du = u*2
	
	val prepLayer: Array[AutomataKoggeStoneAdderPreparationPart] = (0 until size).map(i =>
		new AutomataKoggeStoneAdderPreparationPart(new Vector(i*4*du, 0).add(offset))
	).toArray
	
	
	val cinChain = new AutomataChainPart(new Vector(trueSize*4*du, 0).add(offset), 1, AutomataCell.l)
	
	var passLayer: Array[AutomataChainPart] = Array(new AutomataChainPart(new Vector(4*du*trueSize, 3*du), 1, AutomataCell.l))
	var processLayer: Array[AutomataKoggeStoneAdderProcessPart] = (0 until trueSize).map(i =>
		new AutomataKoggeStoneAdderProcessPart(new Vector(i*4*du, 3*du).add(offset))
	).toArray
	
	prepLayer.indices.foreach(i =>
		prepLayer(i).connectInto(processLayer(i),
			AutomataKoggeStoneAdderPreparationPart.OUTPUT_P,
			AutomataKoggeStoneAdderProcessPart.P_INPUT)
		
		prepLayer(i).connectInto(processLayer(i),
			AutomataKoggeStoneAdderPreparationPart.OUTPUT_G,
			AutomataKoggeStoneAdderProcessPart.G_INPUT)
		
		if(i != 0){
			prepLayer(i).connectInto(processLayer(i-1),
				AutomataKoggeStoneAdderPreparationPart.OUTPUT_P,
				AutomataKoggeStoneAdderProcessPart.PP_INPUT)
			
			prepLayer(i).connectInto(processLayer(i-1),
				AutomataKoggeStoneAdderPreparationPart.OUTPUT_G,
				AutomataKoggeStoneAdderProcessPart.GP_INPUT)
		}
	)
	cinChain.connectInto(processLayer.last, 0, AutomataKoggeStoneAdderProcessPart.GP_INPUT)
	cinChain.connectInto(passLayer(0), 0, 0)
	
	registerNodes(cinChain.getNodes())
	passLayer.foreach(p => registerNodes(p.getNodes()))
	processLayer.foreach(p => registerNodes(p.getNodes()))
	
	for(l <- 1 until logSize){
		val shiftNumber = 1<<l
		val oldPassLayer = passLayer
		val oldProcessLayer = processLayer
		
		processLayer = (0 until trueSize+1-shiftNumber).map(i =>
			new AutomataKoggeStoneAdderProcessPart(new Vector(i*4*du, (l+1)*3*du).add(offset))
		).toArray
		
		passLayer = (0 until shiftNumber).map(i =>
			new AutomataChainPart(new Vector((i+trueSize-shiftNumber+1)*4*du, (l+1)*3*du).add(offset), 1, AutomataCell.l)
		).toArray
		
		(shiftNumber until oldProcessLayer.length).foreach(i => {
			oldProcessLayer(i).connectInto(processLayer(i - shiftNumber),
				AutomataKoggeStoneAdderProcessPart.P_OUTPUT,
				AutomataKoggeStoneAdderProcessPart.PP_INPUT
			)
			oldProcessLayer(i).connectInto(processLayer(i - shiftNumber),
				AutomataKoggeStoneAdderProcessPart.G_OUTPUT,
				AutomataKoggeStoneAdderProcessPart.GP_INPUT
			)
		})
		oldProcessLayer.indices.foreach(i => {
			if(i < processLayer.length){
				oldProcessLayer(i).connectInto(processLayer(i),
					AutomataKoggeStoneAdderProcessPart.P_OUTPUT,
					AutomataKoggeStoneAdderProcessPart.P_INPUT
				)
				oldProcessLayer(i).connectInto(processLayer(i),
					AutomataKoggeStoneAdderProcessPart.G_OUTPUT,
					AutomataKoggeStoneAdderProcessPart.G_INPUT
				)
			}else{
				oldProcessLayer(i).connectInto(passLayer(i-processLayer.length),
					AutomataKoggeStoneAdderProcessPart.G_OUTPUT, 0)
			}
		})
		oldPassLayer.indices.foreach(i => {
			oldPassLayer(i).connectInto(passLayer(i + (passLayer.length-oldPassLayer.length)), 0, 0)
			oldPassLayer(i).connectInto(processLayer(i + processLayer.length-oldPassLayer.length), 0,
				AutomataKoggeStoneAdderProcessPart.GP_INPUT
			)
		})
		passLayer.foreach(p => registerNodes(p.getNodes()))
		processLayer.foreach(p => registerNodes(p.getNodes()))
		oldPassLayer.foreach(p => registerNodes(p.getNodes()))
		oldProcessLayer.foreach(p => registerNodes(p.getNodes()))
	}
	
	val propagateChains: Array[AutomataChainPart] = (0 until trueSize).map(i => {
		new AutomataChainPart(new Vector((i)*4*du+3*du, 3*du).add(offset), logSize*3-2, AutomataCell.l, new Vector(0, 2))
	}).toArray
	
	prepLayer.indices.foreach(i =>
		prepLayer(i).connectInto(propagateChains(i),
			AutomataKoggeStoneAdderPreparationPart.OUTPUT_P, 0)
	)
	
	val xorLayer: Array[AutomataXorGatePart] = (0 until trueSize).map(i => {
		new AutomataXorGatePart(new Vector(du*3*i+3*du, (logSize+1)*3*du).add(offset))
	}).toArray
	
	propagateChains.indices.foreach(i =>
		propagateChains(i).connectInto(xorLayer(i), 0, 0)
	)
	
	(1 until processLayer.length).foreach(i =>
		processLayer(i).connectInto(xorLayer(i-1), AutomataKoggeStoneAdderProcessPart.G_OUTPUT, 1)
	)
	passLayer.indices.foreach(i => {
		passLayer(i).connectInto(xorLayer(processLayer.length+i-1), 0, 1)
	})
	
	val coutChain = new AutomataChainPart(new Vector(0 , (logSize+1)*3*du).add(offset), 1, AutomataCell.l)
	processLayer(0).connectInto(coutChain, AutomataKoggeStoneAdderProcessPart.G_OUTPUT, 0)
	coutChain.getOutputs().get(0).foreach(o => forwardOutput(o, COUT_OUTPUT))
	
	xorLayer.indices.foreach(i =>
		xorLayer(i).getOutputs().get(0).foreach( o => forwardOutput(o, i))
	)
	
	propagateChains.foreach(p => registerNodes(p.getNodes()))
	registerNodes(coutChain.getNodes())
	passLayer.foreach(p => registerNodes(p.getNodes()))
	processLayer.foreach(p => registerNodes(p.getNodes()))
	xorLayer.foreach(x => registerNodes(x.getNodes()))
	prepLayer.foreach(p => registerNodes(p.getNodes()))
	
	override def setInput(in: Int, input: AutomataPartOutput): Unit = {
		if(in >= 0 && in < B_INPUT_OFFSET){
			prepLayer(in).setInput(AutomataKoggeStoneAdderPreparationPart.INPUT_A, input)
		}
		else if(in < CIN_INPUT){
			prepLayer(in-B_INPUT_OFFSET).setInput(AutomataKoggeStoneAdderPreparationPart.INPUT_B, input)
		}
		else if (in == CIN_INPUT){
			cinChain.setInput(0, input)
		}
	}
}
