package com.anax.graphtool
package main

import window.Window

import com.anax.graphtool.graph.automata.parts.{AutomataChainPart, AutomataKoggeStoneAdderPart, AutomataRAMPart, AutomataToggleClockPart}
import com.anax.graphtool.graph.automata.{AutomataCell, AutomataGraph}
import com.anax.graphtool.graph.logic.{LogicalNorCell, LogicalSimpleCell, LogicalToggleCell}
import com.anax.graphtool.math.Coord
import com.anax.graphtool.math.Vector
import com.anax.graphtool.thread.{InputProcessorThread, RenderThread, SimulationThread}

import scala.collection.mutable
import scala.util.Random

object MainAutomata {
	def main(args: Array[String]): Unit = {
		val options = Util.parseInputArguments(args)
		val window: Window = new Window(1000, 1000)
		val graph = new AutomataGraph()
		val state: GameState = new GameState()
		val inputProcessor: InputProcessorThread = new InputProcessorThread(state, window, graph)
		val random: Random = new Random()
		
		options.getOrElse("preset", "ram") match {
			case "ram" => {
				val limit = 10
				val size = 16
				val width = 16
				val ram = new AutomataRAMPart(new Vector(0, 0), size, limit, 8, width)
				val set = new AutomataToggleClockPart(new Vector(-40, -600), 8)
				set.connectInto(ram, 0, ram.SET_INPUT)
				graph.addAll(set.getNodes())
				
				for(i <- 0 until size){
					val t = new AutomataToggleClockPart(new Vector(i*20, -600), 8)
					t.connectInto(ram, 0, ram.ADDRESS_INPUT_OFFSET+i)
					graph.addAll(t.getNodes())
				}
				for(i <- 0 until width){
					val t = new AutomataToggleClockPart(new Vector(i*20+size*20+40, -600), 8)
					t.connectInto(ram, 0, ram.DATA_INPUT_OFFSET+i)
					graph.addAll(t.getNodes())
				}
				
				graph.addAll(ram.getNodes())
				graph.spread(2)
			}
			case "adder" => {
				val size = 16
				val KSA = new AutomataKoggeStoneAdderPart(new Vector(0, 0), size)
				val aInputs = (0 until size).map(i =>
					new AutomataToggleClockPart(new Vector(KSA.u * 2 * i, -20 * KSA.u * 2), 8).withRandomState()
				)
				aInputs.indices.foreach(i =>
					aInputs(i).connectInto(KSA, 0, KSA.A_INPUT_OFFSET + i)
				)
				val bInputs = (0 until size).map(i =>
					new AutomataToggleClockPart(new Vector(KSA.u * 2 * (i + size + 1), -20 * KSA.u * 2), 8).withRandomState()
				)
				bInputs.indices.foreach(i =>
					bInputs(i).connectInto(KSA, 0, KSA.B_INPUT_OFFSET + i)
				)
				val cin = new AutomataToggleClockPart(new Vector(KSA.u * 2 * (size * 2 + 2), -20 * KSA.u * 2), 8).withRandomState()
				cin.connectInto(KSA, 0, KSA.CIN_INPUT)
				
				val outputs = (0 until size).map(i =>
					new AutomataChainPart(new Vector(KSA.u * 2 * (i + 2), 20 * KSA.u * 2), 8, AutomataCell.l)
				)
				outputs.indices.foreach(i =>
					KSA.connectInto(outputs(i), i, 0)
				)
				val cout = new AutomataChainPart(new Vector(0, 20 * KSA.u * 2), 8, AutomataCell.l)
				KSA.connectInto(cout, KSA.COUT_OUTPUT, 0)
				
				aInputs.foreach(p => graph.addAll(p.getNodes()))
				bInputs.foreach(p => graph.addAll(p.getNodes()))
				outputs.foreach(p => graph.addAll(p.getNodes()))
				graph.addAll(cin.getNodes())
				graph.addAll(cout.getNodes())
				
				graph.addAll(KSA.getNodes())
				//graph.randomlyDisplace(2)
				graph.spread(2)
				println(graph.nodes.size)
				
			}
		}
	

		val renderThread: RenderThread = new RenderThread(
			graph,
			window.setImage,
			() => {
				state.scale
			},
			() => {
				state.offset
			},
			() => window.frame.getWidth(),
			() => window.frame.getHeight()
		)
		
		val simulationThread: SimulationThread = new SimulationThread(state, graph)
		
		renderThread.start()
		simulationThread.start()
		inputProcessor.start()
		
		while (true) {
			graph.update()
			block(50)
		}
	}
	
	def block(millis: Int): Unit = {
		val end = System.currentTimeMillis() + millis
		while (System.currentTimeMillis() < end) {}
	}
}



