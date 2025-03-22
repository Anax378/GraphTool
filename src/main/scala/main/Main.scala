package com.anax.graphtool
package main

import window.Window

import com.anax.graphtool.graph.automata.{AutomataCell, AutomataGraph}
import com.anax.graphtool.graph.logic.{LogicCircuitGraph, LogicalButtonCell, LogicalNorCell, LogicalSimpleCell, LogicalToggleCell}
import com.anax.graphtool.graph.minesweeper.{MinesweeperCell, MinesweeperGameGraph}
import com.anax.graphtool.graph.{Graph, PhysicalGraphNode}
import com.anax.graphtool.math.Coord
import com.anax.graphtool.thread.{ConsoleThread, InputProcessorThread, RenderThread, SimulationThread}

import java.awt.event.KeyEvent
import java.util
import scala.collection.immutable.HashMap
import scala.collection.mutable
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
		
		graph.fromString(Source.fromFile("input.txt").mkString, map.toMap, 20);
		
		//graph.addAll(Array(a1, a2, s, r, b1, b2, o1, o2, n1, n2))
		
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
		
		while(true){
			graph.update()
			block(20)
		}
		
	}
	
	def block(millis: Int): Unit = {
		val end = System.currentTimeMillis() + millis
		while(System.currentTimeMillis() < end){}
	}
	
}
