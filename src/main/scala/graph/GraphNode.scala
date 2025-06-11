package com.anax.graphtool
package graph

import scala.collection.mutable

trait GraphNode {
	def getAdjacent(): mutable.Set[GraphNode]
	def link(other: GraphNode): Unit
	def unlink(other: GraphNode): Unit
	def unlinkAll(): Unit = {
		getAdjacent().foreach(a => unlink(a))
	}
	
	def getDFSSubTree(visited: mutable.Set[GraphNode] = new mutable.HashSet[GraphNode]()): ContainerGraphNode = {
		val root: ContainerGraphNode = new ContainerGraphNode(this);
		for(node <- getAdjacent()){
			if(!visited.contains(node)){
				visited.add(node)
				root.link(node.getDFSSubTree(visited))
			}
		}
		return root
	}
	
	def getBFSSubTree(): ContainerGraphNode = {
		val root = new ContainerGraphNode(this);
		val visited = new mutable.HashSet[GraphNode]()
		visited.add(this)
		var layer = new mutable.HashSet[ContainerGraphNode]()
		var nextLayer = new mutable.HashSet[ContainerGraphNode]()
		for(node <- root.inner.getAdjacent()){
			visited.add(node)
			val c = new ContainerGraphNode(node)
			layer.add(c)
			root.link(c)
		}
		while (layer.nonEmpty) {
			for (l <- layer){
				for(child <- l.inner.getAdjacent()){
					if(!visited.contains(child)){
						visited.add(child)
						val c = new ContainerGraphNode(child)
						l.link(c)
						nextLayer.add(c)
					}
				}
			}
			layer = nextLayer;
			nextLayer = new mutable.HashSet[ContainerGraphNode]()
		}
		return root
	}
	
	def getConnected(visited: mutable.Set[GraphNode] = new mutable.HashSet[GraphNode]()): mutable.Set[GraphNode] = {
		for(node <- getAdjacent()){
			if(!visited.contains(node)){
				visited.add(node)
				node.getConnected(visited)
			}
		}
		return visited
	}
	
	def getFurthest: GraphNode = {
		if (getAdjacent().isEmpty) {
			return this
		}
		var distance = 0
		var furthest = this
		var layer = getAdjacent()
		val visited = new mutable.HashSet[GraphNode]();
		
		while(layer.nonEmpty){
			visited.addAll(layer)
			furthest = layer.iterator.next()
			layer = layer.flatMap(node => node.getAdjacent()) -- visited
			distance += 1
		}
		println(s"distance: $distance")
		return furthest
	}
	
	
}
