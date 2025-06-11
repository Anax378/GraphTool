package com.anax.graphtool
package graph.automata.parts

import math.{Coord, Vector}

import com.anax.graphtool.graph.GraphCell
import com.anax.graphtool.graph.automata.AutomataCell

import scala.collection.mutable
class AutomataAndForkPart(offset: Vector, n: Int, mask: Array[Boolean], cycle: Int) extends AutomataPart(offset):
	val size: Int = Math.ceil(Math.log(n)/Math.log(2)).toInt
	
	val width = GraphCell.DEFAULT_RADIUS*2
	
	val clock = new AutomataClockPart(offset.add(new Vector(0, 0)), cycle)
	clock.rotate(-Math.PI/2)
	
	val masks: Array[AutomataCell] = Array.fill(1<<size)(null)
	for(i <- 0 until 1<<size ){
		if(!mask.lift(i).getOrElse(false)){
			masks(i) = new AutomataCell(new Coord(i*width, width).add(offset), AutomataCell.x)
			val a = new AutomataCell(new Coord(i*width+(width/4), width-(width/2)).add(offset), AutomataCell.x)
			val b = new AutomataCell(new Coord(i*width-(width/4), width-(width/2)).add(offset), AutomataCell.x)
			masks(i).link(a)
			masks(i).link(b)
			registerNode(a)
			registerNode(b)
			clock.getOutputs().get(0).foreach(o => o.cell.link(masks(i)))
		}else{
			masks(i) = new AutomataCell(new Coord(i*width, width).add(offset), AutomataCell.l)
		}
	}
	
	registerNodes(clock.getNodes())
	registerNodes(masks)
	
	var layer = masks
	for(l <- 1 until (size + 1)){
		val nextLayer: Array[AutomataCell] = Array.fill(1 << (size - l))(null)
		for(i <- nextLayer.indices){
			nextLayer(i) = new AutomataCell(new Coord(i*width, l*width+width).add(offset), AutomataCell.g)
			nextLayer(i).link(layer( i*2 ))
			nextLayer(i).link(layer( i*2 + 1))
		}
		layer = nextLayer
		registerNodes(layer)
	}
	
	for (i <- layer.indices){
		val c = new AutomataCell(layer(i).position.add(new Vector(GraphCell.DEFAULT_RADIUS*2, 0)), AutomataCell.c)
		c.link(layer(i))
		this.registerCOutput(c, i, new Vector(2, 0))
	}
	
	override def setInput(in: Int, input: AutomataPartOutput): Unit = {
		masks.lift(in).foreach(m => m.link(input.cell))
	}
	
