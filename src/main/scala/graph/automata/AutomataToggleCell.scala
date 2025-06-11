package com.anax.graphtool
package graph.automata

import math.Coord

import java.awt.Color

class AutomataToggleCell(position: Coord) extends AutomataCell(position, AutomataCell.lz){
	var toggled = false
	outlineColor = () => Color.RED
	
	override def onLeftClick(): Unit = {
		toggled = !toggled
	}
	
	override def prepareNextState(): Unit = {
		this.nextState = if toggled then AutomataCell.lp else AutomataCell.lz
	}
	
}
