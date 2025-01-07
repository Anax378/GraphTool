package com.anax.graphtool
package main

import window.Window

import com.anax.graphtool.graph.MinesweeperGameGraph
import com.anax.graphtool.thread.{RenderThread, SimulationThread}

import java.awt.event.KeyEvent
import scala.util.Random

object Main {
	def main(args: Array[String]): Unit = {
		
		val window: Window = new Window(1000, 1000)
		val graph: MinesweeperGameGraph = new MinesweeperGameGraph()
		val state: GameState = new GameState()
		val inputProcessor: InputProcessor = new InputProcessor(state, window, graph)
		val random: Random = new Random()
		
		//graph.addMinesweeperGrid(50, 50, 25, 0.2)
		graph.addRandomTree(1000, 5, 0.05)
		//graph.addRandomGraph(1000, 2, 10, 0.2)
		
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
		
		
	}
}
