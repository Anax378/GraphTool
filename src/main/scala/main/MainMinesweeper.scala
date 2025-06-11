package com.anax.graphtool
package main

import graph.automata.AutomataGraph
import thread.{InputProcessorThread, RenderThread, SimulationThread}
import window.Window

import com.anax.graphtool.graph.GraphCell
import com.anax.graphtool.graph.minesweeper.MinesweeperGameGraph

import scala.util.Random

object MainMinesweeper {
	def main(args: Array[String]): Unit = {
		val options = Util.parseInputArguments(args)
		
		val window: Window = new Window(1000, 1000)
		val graph = new MinesweeperGameGraph()
		val state: GameState = new GameState()
		val inputProcessor: InputProcessorThread = new InputProcessorThread(state, window, graph)
		val random: Random = new Random()
		
		val mineDensity = options.get("mine-density").flatMap(_.toDoubleOption).getOrElse(0.15)
		val gridWidth = options.get("grid-width").flatMap(_.toIntOption).getOrElse(50)
		val gridHeight = options.get("grid-height").flatMap(_.toIntOption).getOrElse(50)
		val cellCount = options.get("cell-count").flatMap(_.toIntOption).getOrElse(200)
		val connectionMutliplier = options.get("connection-multiplier").flatMap(_.toDoubleOption).getOrElse(3.0)
		val spacing = options.get("spacing").flatMap(_.toDoubleOption).getOrElse(23.0)
		
		val connectionPull = options.get("connection-pull").flatMap(s => {
			if(s == "firm"){
				Some(0.44)
			}else{
				s.toDoubleOption
			}
		}).getOrElse(0.001) //default=loose
		GraphCell.DEFAULT_CONNECTION_ATTRACTION_CONSTANT = connectionPull
		
		options.getOrElse("layout", "grid") match {
			case "grid" => {
				graph.addMinesweeperGrid(gridWidth, gridHeight, spacing, mineDensity)
			}
			case "random" => {
				graph.addRandomGraph(cellCount, connectionMutliplier, spacing, mineDensity)
			}
			case "tree" => {
				graph.addRandomGraph(cellCount, connectionMutliplier, spacing, mineDensity)
			}
		}
		
		//graph.addCompleteBipartiteGraph(20, 50, 40, 0.2)
		//graph.addCompleteGraph(40, 40, 0.2)
		//graph.addCompleteNPartiteGraph(Array.fill(10)(2), 40, 0.2)
		//graph.addCollatzConjectureGraph(100, 40, 0.2)
		//graph.addPrimeDistanceGrid(100, 40, 0.15)
		//graph.surroundAroundPosition(new Coord(0, 0), graph.nodes.collect({case t: PhysicalGraphNode => t}), 100000)
		//graph.addRandomTree(300, 5, 0.15)
		//graph.highlightRandomZero()	}
		
		
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
		
		
	}
}