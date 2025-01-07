package com.anax.graphtool
package main

import window.Window
import math.{Coord, Vector}

import com.anax.graphtool.graph.{MinesweeperCell, MinesweeperGameGraph, PhysicalGraphNode}
import com.anax.graphtool.render.Renderable

import java.awt.Point
import java.awt.event.KeyEvent

class InputProcessor(val state: GameState,val window: Window, val graph: MinesweeperGameGraph) extends Thread{
	
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
		if(mousePosition == null){return}
		
		val mouse: Coord = new Coord(mousePosition.getX, mousePosition.getY)
		val mouseWOld: Coord = Renderable.fromScreenPosition(mouse, state.scale, state.offset)
		
		state.zoom += window.unprocessedWheelScroll*state.scrollSensitivity
		window.unprocessedWheelScroll = 0
		state.scale = Math.pow(Math.E, state.zoom)
		
		val mouseWNew: Coord = Renderable.fromScreenPosition(mouse, state.scale, state.offset)
		val correction = mouseWOld.vectorTo(mouseWNew).scale(state.scale)
		state.offset = state.offset.add(correction)
	}
	
	def processMousePressed(): Unit = {
		if(window.isUnprocessedMouseLeftPress){
			window.isUnprocessedMouseLeftPress = false
			
			if(window.keysDown.getOrDefault(KeyEvent.VK_SHIFT, false)){
				if (MinesweeperCell.highlightSource != null) {
					carriedNode = MinesweeperCell.highlightSource
				}
			} else {
				if( MinesweeperCell.highlightSource != null){
					MinesweeperCell.highlightSource match {case cell: MinesweeperCell => {
						if(!cell.uncover()){graph.triggerLoss()}
					}}
				}
			}
		}
		
		if(window.isUnprocessedMouseRightPress){
			window.isUnprocessedMouseRightPress = false
			
			if (MinesweeperCell.highlightSource != null){
				MinesweeperCell.highlightSource match {case cell: MinesweeperCell => {
					cell.isFlagged = !cell.isFlagged
				}}
			}
			
		}
	}
	
	def processMouseReleased(): Unit = {
		if(!window.isMouseLeftDown){
			carriedNode = null
		}
	}
	
	def processKeyPressed(): Unit = {
		if(window.unprocessedKeyPresses.getOrDefault(KeyEvent.VK_SPACE, false)){
			window.unprocessedKeyPresses.put(KeyEvent.VK_SPACE, false)
			isPaused = !isPaused
		}
		state.simulationStepsPerSecond = if (isPaused) 0 else state.defaultSimulationStepsPerSecond
		
	}
	
	override def run(): Unit = {
		while (true){
			val mouse: Point = window.label.getMousePosition()
			
			if(mouse != null){
				val mousePos: Coord = Renderable.fromScreenPosition(new Coord(mouse.getX, mouse.getY), state.scale, state.offset)
				
				if(carriedNode != null){
					carriedNode.setPosition(mousePos)
				}
				
				var found = false
				for(node <- graph.nodes){
					node match {case cell: PhysicalGraphNode => {
						if(cell.isInside(mousePos)){
							MinesweeperCell.highlightSource = cell
							found = true
						}
					}}
				}
				if(!found){MinesweeperCell.highlightSource = null}
				
			}else{
				MinesweeperCell.highlightSource = null
			}
		}
	}
	
}
