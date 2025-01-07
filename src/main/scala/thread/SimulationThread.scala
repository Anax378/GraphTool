package com.anax.graphtool
package thread

import main.GameState

import com.anax.graphtool.graph.MinesweeperGameGraph

class SimulationThread(val state: GameState, val graph: MinesweeperGameGraph) extends Thread{
	
	override def run(): Unit = {
		var start = System.currentTimeMillis()
		while(true){
			while((System.currentTimeMillis()-start) < (1000/state.simulationStepsPerSecond)) {}
			start = System.currentTimeMillis()
			graph.advanceTime(state.simulationStep)
		}
	}
	
}
