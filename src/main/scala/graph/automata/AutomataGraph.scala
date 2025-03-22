package com.anax.graphtool
package graph.automata

import graph.Graph

import com.anax.graphtool.math.Coord

import java.awt.event.KeyEvent
import java.util.concurrent.ConcurrentHashMap
import scala.collection.mutable

class AutomataGraph extends Graph{
	var paused: Boolean = true
	
	def update(): Unit = {
		if(paused){return}
		val cells = nodes.collect({case a: AutomataCell => a})
		for(cell <- cells){
			cell.prepareNextState()
		}
		for(cell <- cells){
			cell.updateState()
		}
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
	}
	
}
