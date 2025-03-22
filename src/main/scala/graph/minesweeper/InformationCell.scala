package com.anax.graphtool
package graph.minesweeper

import graph.GraphNode

trait InformationCell {
	def set(): Set[GraphNode]
	def lower(): Int
	def upper(): Int
	
}
