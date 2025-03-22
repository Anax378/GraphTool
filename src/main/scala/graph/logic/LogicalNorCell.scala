package com.anax.graphtool
package graph.logic

import math.Coord

class LogicalNorCell(position: Coord, inputChannel: Int, outputChannel: Int) extends LogicalCell(position) {
	
	override def processUpdate(): Unit = {
		var state = true
		for(node <- getAdjacent().collect({case l: LogicalCell => l})){
			if (node.channels(inputChannel)){
				state = false
			}
		}
		channels(outputChannel) = state
	}

}
