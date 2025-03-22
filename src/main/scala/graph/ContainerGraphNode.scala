package com.anax.graphtool
package graph

import com.anax.graphtool.math.Coord

import scala.collection.mutable

class ContainerGraphNode (var inner: GraphNode, position: Coord = new Coord(0, 0)) extends GraphCell(position){

}
