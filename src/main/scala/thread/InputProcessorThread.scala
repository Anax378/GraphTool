package com.anax.graphtool
package thread

import graph.{Graph, PhysicalGraphNode}
import main.GameState
import math.{Coord, Vector}
import render.Renderable
import window.Window
import com.anax.graphtool.graph.minesweeper.{MinesweeperCell, MinesweeperGameGraph}

import java.awt.Point
import java.awt.event.KeyEvent

class InputProcessorThread(val state: GameState, val window: Window, val graph: Graph) extends Thread {
	
	window.mouseDraggedListener = processMouseDragged
	window.mouseScrolledListener = processMouseScroll
	window.keyPressedListener = processKeyPressed
	window.mousePressedListener = processMousePressed
	window.mouseReleasedListener = processMouseReleased
	
	var carriedNode: PhysicalGraphNode = null
	
	var isPaused: Boolean = true
	
	def processMouseDragged(): Unit = {
		state.offset = state.offset.add(new Vector(window.unprocessedDragDX, window.unprocessedDragDY))
		window.unprocessedDragDX = 0
		window.unprocessedDragDY = 0
	}
	
	def processMouseScroll(): Unit = {
		val mousePosition: Point = window.label.getMousePosition()
		if (mousePosition == null) {
			return
		}
		
		val mouse: Coord = new Coord(mousePosition.getX, mousePosition.getY)
		val mouseWOld: Coord = Renderable.fromScreenPosition(mouse, state.scale, state.offset)
		
		state.zoom += window.unprocessedWheelScroll * state.scrollSensitivity
		window.unprocessedWheelScroll = 0
		state.scale = Math.pow(Math.E, state.zoom)
		
		val mouseWNew: Coord = Renderable.fromScreenPosition(mouse, state.scale, state.offset)
		val correction = mouseWOld.vectorTo(mouseWNew).scale(state.scale)
		state.offset = state.offset.add(correction)
	}
	
	def processMousePressed(): Unit = {
		if (window.isUnprocessedMouseLeftPress) {
			window.isUnprocessedMouseLeftPress = false
			
			if (window.keysDown.getOrDefault(KeyEvent.VK_SHIFT, false)) {
				if (graph.highlightSource != null) {
					carriedNode = graph.highlightSource
				}
			} else {
				if (graph.highlightSource != null) {
					graph.highlightSource.onLeftClick()
					graph.onLeftClick()
				}
			}
		}
		
		if (window.isUnprocessedMouseRightPress) {
			window.isUnprocessedMouseRightPress = false
			
			if (graph.highlightSource != null) {
				graph.highlightSource match {
					case cell: MinesweeperCell => {
						cell.onRightClick()
					}
					case _ => {}
				}
			}
			
		}
		
		if(window.isUnprocessedMouseMiddlePress) {
			window.isUnprocessedMouseMiddlePress = false
			val mousePosition = window.label.getMousePosition
			if(mousePosition != null){
				val position = Renderable.fromScreenPosition(
					new Coord(mousePosition.getX, mousePosition.getY),
					state.scale,
					state.offset
				)
				graph.cursor.position = position
				
			}
			
		}
	}
	
	def processMouseReleased(): Unit = {
		if (!window.isMouseLeftDown) {
			carriedNode = null
		}
	}
	
	def processKeyPressed(): Unit = {
		if (window.unprocessedKeyPresses.getOrDefault(KeyEvent.VK_SPACE, false)) {
			window.unprocessedKeyPresses.put(KeyEvent.VK_SPACE, false)
			isPaused = !isPaused
		}
		if (window.unprocessedKeyPresses.getOrDefault(KeyEvent.VK_C, false)){
			window.unprocessedKeyPresses.put(KeyEvent.VK_C, false)
			
			if (window.keysDown.getOrDefault(KeyEvent.VK_CONTROL, false)){
				for (cell <- graph.nodes.collect({ case p: PhysicalGraphNode => p })) {
					cell.surroundByAdjacent(50)
				}
				graph.randomlyDisplace(1)
			}else{
				if (graph.highlightSource != null) {
					graph.highlightSource.surroundByAdjacent(50)
				}
			}
		}
		
		if(window.unprocessedKeyPresses.getOrDefault(KeyEvent.VK_G, false)){
			window.unprocessedKeyPresses.put(KeyEvent.VK_G, false)
			if(graph.highlightSource != null){
				graph.highlightSource.spreadBFSSubTree(20)
				graph.randomlyDisplace(1)
			}
		}
		
		graph.processKeyPress(window.unprocessedKeyPresses)
		
		state.simulationStepsPerSecond = if (isPaused) 0 else state.defaultSimulationStepsPerSecond
	}
	
	override def run(): Unit = {
		while (true) {
			val mouse: Point = window.label.getMousePosition()
			
			if (mouse != null) {
				val mousePos: Coord = Renderable.fromScreenPosition(new Coord(mouse.getX, mouse.getY), state.scale, state.offset)
				
				if (carriedNode != null) {
					carriedNode.setPosition(mousePos)
				}
				
				var found = false
				for (node <- graph.nodes) {
					node match {
						case cell: PhysicalGraphNode => {
							if (cell.isInside(mousePos)) {
								graph.highlightSource = cell
								found = true
							}
						}
					}
				}
				if (!found) {
					graph.highlightSource = null
				}
				
			} else {
				graph.highlightSource = null
			}
			
			if(window.keysDown.getOrDefault(KeyEvent.VK_S, false)){
				if (window.keysDown.getOrDefault(KeyEvent.VK_CONTROL, false)){
					state.simulationStep = state.defaultSimulationStep * 10
				}else{
					state.simulationStep = state.defaultSimulationStep * 3
				}
			}
			else{
				state.simulationStep = state.defaultSimulationStep
			}
		}
	}
}