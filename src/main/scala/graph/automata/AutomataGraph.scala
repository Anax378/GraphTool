package com.anax.graphtool
package graph.automata

import graph.{Graph, GraphNode}

import com.anax.graphtool.math.Coord

import java.awt.event.KeyEvent
import java.util.concurrent.ConcurrentHashMap
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class AutomataGraph extends Graph{
	var paused: Boolean = true
	var step: Boolean = false
	
	def update(): Unit = {
		if(paused){
			if(step){
				step = !step
			}else{
				return
			}
		}
		val cells = nodes.collect({case a: AutomataCell => a})
		for(cell <- cells){
			cell.prepareNextState()
		}
		for(cell <- cells){
			cell.updateState()
		}
	}
	
	def addRandomGraph(cellCount: Int, connectionMultiplier: Double, spacing: Int): Unit = {
		val graphNodes = getRandomTree(cellCount, spacing)
		for (i <- 0 until Math.round(connectionMultiplier * cellCount - cellCount).toInt) {
			graphNodes(random.nextInt(graphNodes.length)).link(graphNodes(random.nextInt(graphNodes.length)))
		}
		addAll(graphNodes)
	}
	
	def addRandomTree(cellCount: Int, spacing: Int): Unit = {
		addAll(getRandomTree(cellCount, spacing))
	}
	
	private def getRandomTree(cellCount: Int, spacing: Int): ArrayBuffer[GraphNode] = {
		val treeNodes: ArrayBuffer[GraphNode] = new ArrayBuffer[GraphNode]()
		val states = (AutomataCell.all() ++ Array.fill(500)(AutomataCell.l))
		for (i: Int <- 0 until cellCount) {
			val state = states(random.nextInt(states.length))
			val cell: AutomataCell = new AutomataCell(new Coord(random.nextInt(cellCount * spacing), random.nextInt(cellCount * spacing)), state)
			if (i > 0) {
				cell.link(treeNodes(random.nextInt(treeNodes.length)))
			}
			treeNodes.addOne(cell)
		}
		treeNodes
	}
	
	
	def fromString(string: String, map: Map[String, Int], scale: Double): Unit = {
		val cells: mutable.HashMap[String, AutomataCell] = new mutable.HashMap()
		for(line <- string.linesIterator){
			val parts = line.trim.split(" ")
			if(parts(0).startsWith("!")){
				val name = parts(0).tail
				var cs = name.split(":")
				var x = cs(0).toInt
				var y = cs(1).toInt
				cells.put(name, new AutomataCell(new Coord(x*scale, y*scale), map(parts(1))))
			}else{
				cells(parts(0)).link(cells(parts(1)))
			}
		}
		addAll(cells.values)
	}
	
	override def processKeyPress(unprocessed: ConcurrentHashMap[Int, Boolean]): Unit = {
		if(unprocessed.getOrDefault(KeyEvent.VK_P, false)){
			unprocessed.put(KeyEvent.VK_P, false)
			paused = !paused
		}
		if(unprocessed.getOrDefault(KeyEvent.VK_RIGHT, false)){
			unprocessed.put(KeyEvent.VK_RIGHT, false)
			step = true
		}
	}
	
}
