package com.anax.graphtool
package main

import window.Window

import com.anax.graphtool.graph.automata.parts.{AutomataAndForkPart, AutomataChainPart, AutomataClockPart, AutomataDataFlipFlopPart, AutomataDecoderPart, AutomataGridPart, AutomataKoggeStoneAdderPart, AutomataKoggeStoneAdderProcessPart, AutomataRAMPart, AutomataRegister, AutomataToggleClockPart}
import com.anax.graphtool.graph.automata.{AutomataCell, AutomataGraph, AutomataToggleCell}
import com.anax.graphtool.graph.logic.{LogicCircuitGraph, LogicalButtonCell, LogicalNorCell, LogicalSimpleCell, LogicalToggleCell}
import com.anax.graphtool.graph.minesweeper.{MinesweeperCell, MinesweeperGameGraph}
import com.anax.graphtool.graph.mover.MoverCell
import com.anax.graphtool.graph.{Graph, PhysicalGraphNode}
import com.anax.graphtool.math.Coord
import com.anax.graphtool.math.Vector
import com.anax.graphtool.thread.{ConsoleThread, InputProcessorThread, RenderThread, SimulationThread}

import java.awt.event.KeyEvent
import java.util
import scala.collection.immutable.HashMap
import scala.collection.{immutable, mutable}
import scala.io.Source
import scala.util.Random

object Main {
	def main(args: Array[String]): Unit = {
		
		val window: Window = new Window(1000, 1000)
		val graph = new AutomataGraph()
		val state: GameState = new GameState()
		val inputProcessor: InputProcessorThread = new InputProcessorThread(state, window, graph)
		val random: Random = new Random()
		
		//graph.addMinesweeperGrid(50, 50, 25, 0.15)
		//graph.addRandomGraph(500, 3, 20, 0.1)
		//graph.addCompleteBipartiteGraph(20, 50, 40, 0.2)
		//graph.addCompleteGraph(40, 40, 0.2)
		//graph.addCompleteNPartiteGraph(Array.fill(10)(2), 40, 0.2)
		//graph.addCollatzConjectureGraph(100, 40, 0.2)
		//graph.addPrimeDistanceGrid(100, 40, 0.15)
		//graph.surroundAroundPosition(new Coord(0, 0), graph.nodes.collect({case t: PhysicalGraphNode => t}), 100000)
		//graph.addRandomTree(300, 5, 0.15)
		//graph.highlightRandomZero()
		
		val s = new LogicalToggleCell(new Coord(0,0), 0)
		val r = new LogicalToggleCell(new Coord(0,0), 0)
		
		val n1 = new LogicalNorCell(new Coord(0, 0), 0, 1)
		val n2 = new LogicalNorCell(new Coord(0, 0), 0, 1)
		
		val o1 = new LogicalSimpleCell(new Coord(0, 0), 1, 1)
		val o2 = new LogicalSimpleCell(new Coord(0, 0), 1, 1)
		
		val a1 = new LogicalSimpleCell(new Coord(0, 0), 1, 0)
		val a2 = new LogicalSimpleCell(new Coord(0, 0), 1, 0)
		
		val b1 = new LogicalSimpleCell(new Coord(0, 0), 0, 0)
		val b2 = new LogicalSimpleCell(new Coord(0, 0), 0, 0)
		
		n1.link(s)
		n1.link(b1)
		n1.link(o1)
		
		n2.link(r)
		n2.link(b2)
		n2.link(o2)
		
		a1.link(b1)
		a2.link(b2)
		
		a1.link(o2)
		a2.link(o1)
		
		val map: mutable.HashMap[String, Int] = new mutable.HashMap()
		map.put("l", AutomataCell.l)
		map.put("l+", AutomataCell.lp)
		map.put("l0", AutomataCell.lz)
		map.put("c", AutomataCell.c)
		map.put("c+", AutomataCell.cp)
		map.put("d", AutomataCell.d)
		map.put("d+", AutomataCell.dp)
		map.put("g", AutomataCell.g)
		map.put("g+", AutomataCell.gp)
		map.put("x", AutomataCell.x)
		map.put("x+", AutomataCell.xp)
		
		//graph.fromString(Source.fromFile("input.txt").mkString, map.toMap, 20);
		//graph.surroundAroundPosition(new Coord(0, 0), graph.nodes.collect({case p: PhysicalGraphNode => p}), 1000000)
		
		//graph.addAll(Array(a1, a2, s, r, b1, b2, o1, o2, n1, n2))
		
		/*
		val n = 16
		val limit = 100
		val mux = new AutomataDecoderPart(n, limit, new Vector(0, 0), 8)
		
		for(i <- 0 until n){
			val toggleClock = new AutomataToggleClockPart(new Vector(20*i, -300), 8)
			toggleClock.getOutputs().get(0).foreach(o => mux.setInput(i, o))
			graph.addAll(toggleClock.getNodes())
		}
		
		for(i <- 0 until limit){
			val chain = new AutomataChainPart(new Vector(16*20, 20*8*i+20*8), 50, AutomataCell.l)
			chain.rotate(-Math.PI/2)
			mux.getOutputs().get(i).foreach(o => chain.setInput(0, o))
			graph.addAll(chain.getNodes())
		}
		
		graph.addAll(mux.getNodes())
		 */
		
		/*
		val size = 16
		val register = new AutomataRegister(new Vector(0, 0), size)
		for(i <- 0 until size){
			val clock = new AutomataToggleClockPart(new Vector(i*40, 0), 8)
			clock.rotate(Math.PI)
			clock.connectInto(register, 0, AutomataRegister.DATA_INPUT_OFFSET + i)
			graph.addAll(clock.getNodes())
		}
		
		val c1 = new AutomataToggleClockPart(new Vector(-100, 0), 8)
		val c2 = new AutomataToggleClockPart(new Vector(-100, 100), 8)
		c1.rotate(Math.PI/2)
		c2.rotate(Math.PI/2)
		
		c1.connectInto(register, 0, AutomataRegister.SET_INPUT)
		c2.connectInto(register, 0, AutomataRegister.OUT_INPUT)
		
		graph.addAll(c1.getNodes())
		graph.addAll(c2.getNodes())
		graph.addAll(register.getNodes())
		
		
		 */
		/*
		val limit = 10
		val size = 16
		val width = 16
		var ram = new AutomataRAMPart(new Vector(0, 0), size, limit, 8, width)
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
		ram = null
		
		 */
		
		val size = 16
		val KSA = new AutomataKoggeStoneAdderPart(new Vector(0, 0), size)
		val aInputs = (0 until size).map(i =>
			new AutomataToggleClockPart(new Vector(KSA.u*2*i, -20*KSA.u*2), 8).withRandomState()
		)
		aInputs.indices.foreach(i =>
			aInputs(i).connectInto(KSA, 0, KSA.A_INPUT_OFFSET+i)
		)
		val bInputs = (0 until size).map(i =>
			new AutomataToggleClockPart(new Vector(KSA.u*2*(i+size+1), -20*KSA.u*2), 8).withRandomState()
		)
		bInputs.indices.foreach(i =>
			bInputs(i).connectInto(KSA, 0, KSA.B_INPUT_OFFSET+i)
		)
		val cin = new AutomataToggleClockPart(new Vector(KSA.u*2*(size*2+2), -20*KSA.u*2), 8).withRandomState()
		cin.connectInto(KSA, 0, KSA.CIN_INPUT)
		
		val outputs = (0 until size).map(i =>
			new AutomataChainPart(new Vector(KSA.u*2*(i+2), 20*KSA.u*2), 8, AutomataCell.l)
		)
		outputs.indices.foreach(i =>
			KSA.connectInto(outputs(i), i, 0)
		)
		val cout = new AutomataChainPart(new Vector(0, 20*KSA.u*2), 8, AutomataCell.l)
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
		
		val renderThread: RenderThread = new RenderThread(
			graph,
			window.setImage,
			() => {state.scale},
			() => {state.offset},
			() => window.frame.getWidth(),
			() => window.frame.getHeight()
		)
		
		val simulationThread: SimulationThread = new SimulationThread(state, graph)
		
		renderThread.start()
		simulationThread.start()
		inputProcessor.start()
		
		while (true){
			graph.update()
			block(50)
		}
		
	}
	
	def block(millis: Int): Unit = {
		val end = System.currentTimeMillis() + millis
		while(System.currentTimeMillis() < end){}
	}
	
}
