package com.anax.graphtool
package graph.minesweeper

import game.GameCursor
import graph.{Graph, GraphNode, PhysicalGraphNode}
import math.{Coord, Vector}
import render.Renderable

import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.awt.{Color, Graphics2D}
import java.util.concurrent.ConcurrentHashMap
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.boundary.break
import scala.util.{Random, boundary}

class MinesweeperGameGraph extends Graph with Renderable {
	
	def checkForWin(): Boolean = {
		boundary {
			for (node <- nodes) {
				node match {
					case cell: MinesweeperCell => {
						if (!cell.isUncovered && !cell.isMine) {
							break(false)
						}
					}
				}
			}
			triggerWin()
			return true
		}
	}
	
	override def processKeyPress(map: ConcurrentHashMap[Int, Boolean]): Unit = {
		if (map.getOrDefault(KeyEvent.VK_F, false)){
			map.put(KeyEvent.VK_F, false)
			flagTrivial()
			uncoverTrivial()
			cullRedundant()
			checkForWin()
		}
	}
	
	override def onLeftClick(): Unit = {
		cullRedundant()
		checkForWin()
	}
	
	def isRedundant(node: GraphNode): Boolean = {
		
		node match{
			case cell: MinesweeperCell => {
				if(!cell.isUncovered || cell.isMine){return false}
				if (cell.getAdjacent().map {
					case adjacentCell: MinesweeperCell => adjacentCell.isUncovered
					case _ => false
				}.forall(identity)) {
					return true
				}
			}
		}
		false
	}
	
	def uncoverRandomZero(): Unit = {
		getRandomZero().foreach(z => z.uncover())
	}
	
	def highlightRandomZero(): Unit = {
		getRandomZero().foreach(z => z.setOutlineColor(Color.RED))
	}
	
	private def getRandomZero(): Option[MinesweeperCell] = {
		val zeroes = new ArrayBuffer[MinesweeperCell]()
		for (node <- nodes) {
			node match {
				case cell: MinesweeperCell => {
					if (cell.adjacentMineCount == 0 && !cell.isMine) {
						zeroes.append(cell)
					}
				}
			}
		}
		if (zeroes.isEmpty) {
			return None
		}
		Some(zeroes(random.nextInt(zeroes.length)))
		
	}
	
	def cullRedundant(): Unit = {
		val redundant = new ArrayBuffer[GraphNode]()
		val uncoveredAdjacentSets = new mutable.HashSet[Set[GraphNode]]()
		
		for(node <- nodes){
			node match{case cell: MinesweeperCell => {
				cell.cullRedundantLinks()}
				if(cell.isUncovered && !cell.isMine){
					val adjacent = cell.getAdjacent().toSet
					if(uncoveredAdjacentSets.contains(adjacent)){
						redundant.append(cell)
					}else{
						uncoveredAdjacentSets.add(adjacent)
					}
				}
			}
			
			if(isRedundant(node)){
				redundant.append(node)
			}
		}
		
		for(node <- redundant){
			removeNode(node)
		}
		
		if(redundant.nonEmpty){cullRedundant()}
	}
	
	def triggerWin(): Unit = {
		println("YOU WON!")
	}
	
	def triggerLoss(): Unit = {
		println("YOU LOST!")
	}
	
	def addCollatzConjectureGraph(bound: Int, spacing: Int, mineDensity: Double): Unit = {
		val nodes: mutable.Map[Int, PhysicalGraphNode] = new mutable.HashMap()
		val next = (x: Int) => if (x%2==0) x/2 else 3*x+1
		for(i <- 1 until bound+1){
			var prev = nodes.getOrElse(i, new MinesweeperCell(new Coord(0, 0)).withMineState(random.nextDouble() <= mineDensity).withName(i.toString))
			nodes(i) = prev
			
			var n = i
			while (n != 1){
				n = next(n)
				val current = nodes.getOrElse(n, new MinesweeperCell(new Coord(0, 0)).withMineState(random.nextDouble() <= mineDensity).withName(n.toString))
				nodes(n) = current
				prev.link(current)
				prev = current
			}
			
		}
		for(key <- nodes.keys){
			val node = nodes(key)
			node.setPosition(new Coord(key*spacing, node.getAdjacent().size*spacing))
		}
		addAll(nodes.values)
	}
	
	def addRandomGraph(cellCount: Int, connectionMultiplier: Double, spacing: Double, mineDensity: Double): Unit = {
		val graphNodes = getRandomTree(cellCount, spacing, mineDensity)
		
		for(i <- 0 until Math.round(connectionMultiplier * cellCount - cellCount).toInt){
			graphNodes(random.nextInt(graphNodes.length)).link(graphNodes(random.nextInt(graphNodes.length)))
		}
		
		addAll(graphNodes)
	}
	
	def addRandomTree(cellCount: Int, spacing: Int, mineDensity: Double): Unit = {
		addAll(getRandomTree(cellCount, spacing, mineDensity))
	}
	
	private def getRandomTree(cellCount: Int, spacing: Double, mineDensity: Double): ArrayBuffer[GraphNode] = {
		val treeNodes: ArrayBuffer[GraphNode] = new ArrayBuffer[GraphNode]()
		val random: Random = new Random()
		for (i: Int <- 0 until cellCount){
			val cell: MinesweeperCell = new MinesweeperCell(new Coord(random.nextInt(cellCount*spacing.toInt), random.nextInt(cellCount*spacing.toInt)))
			cell.isMine = random.nextDouble() <= mineDensity
			if(i > 0){
				cell.link(treeNodes(random.nextInt(treeNodes.length)))
			}
			treeNodes.addOne(cell)
		}
		treeNodes
	}
	
	def addCompleteNPartiteGraph(n: Array[Int], spacing: Double, mineDensity: Double): Unit = {
		val sets: Array[Array[GraphNode]] = Array.tabulate(n.length)(i => Array.tabulate(n(i))(
			j => new MinesweeperCell(
				new Coord((j - (n(i)/2))*spacing, i*spacing)).withMineState(random.nextDouble() <= mineDensity)))
		for(a <- sets){
			for(b <- sets){
				if(a != b){
					for(c <- a){
						for(d <- b){
							c.link(d)
						}
					}
				}
			}
		}
		for (set <- sets){
			addAll(set)
		}
	}
	
	def addCompleteBipartiteGraph(n: Int, m: Int, spacing: Double, mineDensity: Double):Unit = {
		val setA: Array[GraphNode] = Array.tabulate(n)(i =>
			new MinesweeperCell(new Coord(i*spacing, 0)).withMineState(random.nextDouble() <= mineDensity))
		
		val setB: Array[GraphNode] = Array.tabulate(m)(i =>
			new MinesweeperCell(new Coord((i+((n-m)/2))*spacing, spacing)).withMineState(random.nextDouble() <= mineDensity))
		
		for(a <- setA){
			for(b <- setB){
				a.link(b);
			}
		}
		
		addAll(setA)
		addAll(setB)
	}
	
	def addCompleteGraph(n: Int, spacing: Double, mineDensity: Double): Unit = {
		val nodes: Array[PhysicalGraphNode] = Array.fill(n)(new MinesweeperCell(new Coord(0, 0)).withMineState(random.nextDouble() <= mineDensity))
		for(a <- nodes){
			for(b <- nodes){
				a.link(b)
			}
		}
		surroundAroundPosition(new Coord(0, 0), nodes, spacing/(2*Math.sin(Math.PI/n)))
		addAll(nodes)
	}

	def addHexagonalGrid(width: Int, height: Int, spacing: Double, mineDensity: Double): Unit = {
		val grid: Array[GraphNode] = Array.fill(width*height)(null)
		for (j <- 0 until height){
			for (i <- 0 until width){
				val cell = new MinesweeperCell(new Coord((spacing*i*3)/2, spacing*Math.sqrt(3)*(j+i*0.5) ))
				cell.isMine = random.nextDouble() <= mineDensity
				grid(j*height + i) = cell
				if(j != 0 && (i+1) < width){grid((j-1)*height+i+1).link(cell)}
				if(j != 0){grid((j-1)*height+i).link(cell)}
				if(i != 0){grid(j*height+i-1).link(cell)}
			}
		}
		addAll(grid)
	}
	
	def isPrime(p: Int): Boolean = {
		if (p < 2){
			return false
		}
		for(d <- 2 until Math.sqrt(p).toInt+1){
			if (p%d == 0){
				return false
			}
		}
		return true
	}
	
	def primesUpTo(n: Int): Seq[Int] = {
		if (n < 2) return Seq.empty
		
		val isPrime = Array.fill(n + 1)(true)
		isPrime(0) = false
		isPrime(1) = false
		
		for (i <- 2 to Math.sqrt(n).toInt+1 if isPrime(i)) {
			for (j <- i * i to n by i) {
				isPrime(j) = false
			}
		}
		isPrime.zipWithIndex.collect { case (true, index) => index }
	}
	
	def getDistanceOneNumbers(n: Int): Set[Int] = {
		val s = n.toString
		val length = s.length
		
		(for {
			i <- 0 until length
			d <- '0' to '9' if d != s(i)
			//if !(i == 0 && d == '0')
		} yield {
			val newNumber = s.updated(i, d)
			newNumber.toInt
		}).toSet
	}
	
	def addPrimeDistanceGrid(bound: Int, spacing: Double, mineDensity: Double): Unit = {
		val primes = primesUpTo(bound)
		val primeSet: Set[Int] = primes.toSet
		val cells: mutable.Map[Int, MinesweeperCell] = new mutable.HashMap()
		for(i <- primes.indices){
			cells(primes(i)) = new MinesweeperCell(new Coord(i*spacing, 0))
				.withName(primes(i).toString)
				.withMineState(random.nextDouble <= mineDensity)
		}
		for(prime <- primes){
			val adjacentPrimes = getDistanceOneNumbers(prime).intersect(primeSet)
			for(a <- adjacentPrimes){
				cells(prime).link(cells(a))
			}
		}
		addAll(cells.values)
	}
	
	def addMinesweeperGrid(width: Int, height: Int, spacing: Double, mineDensity: Double): Unit = {
		val generated: Array[GraphNode] = Array.fill(width*height)(null)
		def toIndex(x: Int, y: Int) = y*width+x
		
		for (y: Int <- 0 until height){
			for(x: Int <- 0 until width){
				val cell: MinesweeperCell = new MinesweeperCell(new Coord(x*spacing, y*spacing));
				cell.isMine = random.nextDouble() <= mineDensity
				generated(y*width+x) = cell
				if(x != 0){generated(toIndex(x-1, y)).link(cell)}
				if(x != 0 && y != 0){generated(toIndex(x-1, y-1)).link(cell)}
				if(y != 0){generated(toIndex(x, y-1)).link(cell)}
				if(x < width-1 && y != 0){generated(toIndex(x+1, y-1)).link(cell)}
			}
		}
		addAll(generated)
	}
	
	def updateAdjacentMineCounts(): Unit = {
		for(node <- nodes){
			node match{
				case cell: MinesweeperCell => cell.updateAdjacentMineCount()
			}
		}
	}

	def cullFlagLinks(): Unit = {
		for (node <- nodes) {
			node match {
				case cell: MinesweeperCell => {
					if (cell.isFlagged) {
						for (adjacent <- cell.adjacent) {
							cell.unlink(adjacent)
						}
					}
				}
			}
		}
		updateAdjacentMineCounts()
	}
	
	def uncoverTrivial(): Boolean = {
		var ret = false
		for(node <- nodes){
			node match{case cell: MinesweeperCell => {
				if(cell.isUncovered){
					if(cell.adjacent.count({
						case adjacentCell: MinesweeperCell => adjacentCell.isFlagged
						case _ => false
					}) == cell.adjacentMineCount){
						for(adjacent <- cell.adjacent){
							adjacent match {case adjacentCell: MinesweeperCell => {
								if(!adjacentCell.isFlagged){
									if(!adjacentCell.isUncovered){ret = true}
									adjacentCell.uncover()
								}
							}}
						}
					}
				}
			}}
		}
		ret
	}

	def flagTrivial(): Boolean = {
		var ret = false;
		for(node <- nodes){
			node match{ case cell: MinesweeperCell => {
				if(cell.isUncovered && cell.adjacent.count({
					case adjacentCell: MinesweeperCell => !adjacentCell.isUncovered || (adjacentCell.isUncovered && adjacentCell.isMine)
					case _ => false
				}) == cell.adjacentMineCount){
					for(adjacent <- cell.adjacent){
						adjacent match {case adjacentCell: MinesweeperCell => {
							if(!adjacentCell.isFlagged){ret = true}
							adjacentCell.isFlagged = true
						}}
					}
				}
			}}
		}
		ret
	}
}
