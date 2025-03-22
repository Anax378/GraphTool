package com.anax.graphtool
package main
import math.Vector

import com.anax.graphtool.game.GameCursor

class GameState {
	var offset: Vector = new Vector(0, 0)
	@volatile var scale: Double = 1.0
	@volatile var zoom: Double = 0.0
	@volatile var scrollSensitivity: Double = -0.1
	
	val defaultSimulationStepsPerSecond: Double = 20_0000000
	@volatile var simulationStepsPerSecond: Double = 0
	
	val defaultSimulationStep: Double = 0.1
	@volatile var simulationStep: Double = defaultSimulationStep
	
}
