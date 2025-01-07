package com.anax.graphtool
package graph

import scala.collection.mutable

trait GraphNode {
	def getAdjacent(): mutable.Set[GraphNode]
	def link(other: GraphNode): Unit
	def unlink(other: GraphNode): Unit
	
	
}
