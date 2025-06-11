package com.anax.graphtool
package graph.automata.parts

import graph.automata.AutomataCell

import com.anax.graphtool.graph.GraphCell
import com.anax.graphtool.math.{Coord, Vector}

import java.awt.Color
import scala.annotation.tailrec
import scala.collection.mutable

trait AutomataPart(val offset: Vector) {
	val u = GraphCell.DEFAULT_RADIUS
	val outputs = new mutable.HashMap[Int, AutomataPartOutput]
	val nodes = new mutable.HashSet[AutomataCell]()
	
	def registerCOutput(cell: AutomataCell, output: Int, offset: Vector = new Vector(0, 2)): Unit = {
		assert(!outputs.isDefinedAt(output), "multiple registrations not allowed")
		val out = new AutomataPartOutput(cell, offset)
		outputs(output) = out
	}
	
	def forwardOutput(out: AutomataPartOutput, output: Int): Unit = {
		assert(!outputs.isDefinedAt(output), "multiple registrations not allowed")
		outputs(output) = out
	}
	
	def mergeOutputInto(output: Int, out: AutomataPartOutput): Unit = {
		getOutputs().get(output).foreach(o => {
			o.mergeInto(out)
			nodes.remove(o.c)
			outputs(output) = out
		})
	}
	
	def registerNode(node: AutomataCell) = nodes.add(node)
	def registerNodes(ns: IterableOnce[AutomataCell]) = nodes.addAll(ns)
	
	def getNodes(): Set[AutomataCell] = nodes.toSet ++ outputs.values.flatMap(_.getNodes())
	def getOutputs(): Map[Int, AutomataPartOutput] = outputs.toMap
	
	def setInput(in: Int, input: AutomataPartOutput): Unit
	
	def connectInto(other: AutomataPart, output: Int, input: Int): Unit = {
		getOutputs().get(output).foreach(o => other.setInput(input, o))
	}
	def spread(factor: Double): Unit = {
		for (node <- this.getNodes()){
			node.setPosition(node.getPosition().scale(factor))
		}
	}
	
	def rotate(angle: Double): Unit = {
		for(node <- getNodes()){
			node.setPosition(node.getPosition().rotate(new Coord(0, 0).add(offset), angle))
		}
	}
	
	def update(ticks: Int): Unit = {
		val nodes = getNodes()
		for(i <- 0 until ticks){
			for (node <- nodes) {
				node.prepareNextState()
			}
			for (node <- nodes) {
				node.updateState()
			}
		}
		
	}
	
}
