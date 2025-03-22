package com.anax.graphtool
package thread

import graph.PhysicalGraphNode
import main.GameState
import math.{Coord, Vector}
import window.Window
import com.anax.graphtool.graph.minesweeper.{MinesweeperCell, MinesweeperGameGraph}

import scala.collection.mutable
import scala.io.StdIn
import scala.util.Try

class ConsoleThread(val graph: MinesweeperGameGraph, val window: Window, val state: GameState) extends Thread{
	
	override def run(): Unit = {
		while(true){
			print("#")
			runCommand(StdIn.readLine())
		}
	}
	
	def runCommand(input: String): Unit = {
		if(input.isEmpty){return}
		val parts = input.split(" ")
		val command = parts(0)
		command match{
			case "sur" => {
			
				if (parts.size != 2 && parts.size != 3){
					println("size: " + parts.size)
					println("missing arguments for sur. usage: sur <selector> ?<distance>")
					return
				}
				var distance: Double = 50
				if(parts.size == 3){
					val number = Try(parts(2).toDouble).toOption
					number match {
						case Some(dist) => distance = dist
						case None => {
							println("not a number: " + parts(2))
						}
					}
				}
				
				val selector = parts(1)
				try{
					val selected = matchForSelector(selector)
					surroundAroundPosition(graph.cursor.position, selected, distance)
				} catch{
					case e: InvalidInputException => {
						println(e.message)
					}
				}
			}
			
			case _ => {
				println("unknown command: " + command)
			}
		}
	}
	
	def getFilterForSelectorConstraint(constraint: String): (PhysicalGraphNode => Boolean) = {
		if (constraint.isEmpty) {
			throw InvalidInputException("empty selector constraint")
		}
		
		constraint(0) match {
			case 'a' => (node => extractComparatorFromConstraint(constraint.drop(1))(node.getAdjacent().size))
			case 'm' => {
				case cell: MinesweeperCell => (cell.isUncovered && extractComparatorFromConstraint(constraint.drop(1))(cell.adjacentMineCount))
				case _ => false
			}
			case 'f' => ({
				case cell: MinesweeperCell => if (constraint.length > 1 && constraint(1) == '!')
				then !cell.isFlagged
				else cell.isFlagged
				case _ => false
			})
			case 'c' => ({
				case cell: MinesweeperCell=> if (constraint.length > 2 && constraint(1) == '!')
				then cell.isUncovered
				else !cell.isUncovered
				case _ => false
			})
			case _ => {throw new InvalidInputException("unknown constraint: " + constraint(0))}
		}
	}
	
	def extractComparatorFromConstraint(comparator: String): (Int => Boolean) = {
		var negate = comparator.startsWith("!")
		var expression = if negate then comparator.tail else comparator
		
		if(expression.contains("-")){
			if(expression.startsWith("-")){
				val number = Try(comparator.drop(1).toInt).toOption
				number match {
					case Some(num) => {return (x => (x <= num) != negate)}
					case None => {throw new InvalidInputException("not a number: " + comparator.drop(1))}
				}
			}
			if(expression.endsWith("-")){
				val number = Try(comparator.init.toInt).toOption
				number match {
					case Some(num) => {return (x => (x >= num) != negate)}
					case None => {throw new InvalidInputException("not a number: " + comparator.init)}
				}
			}
			
			val nums = expression.split("-")
			if(nums.size != 2){throw new InvalidInputException("invalid constraint syntax: " + comparator)}
			val num1 = Try(nums(0).toInt).toOption
			val num2 = Try(nums(1).toInt).toOption
			num1 match {
				case Some(n1) => {
					num2 match {
						case Some(n2) => {(x => ((x >= n1) && (x <= n2)) != negate ) }
						case None => {throw new InvalidInputException("not a number: " + nums(1))}
					}
				}
				case None => {throw new InvalidInputException("not a number: " + nums(0))}
			}
		}else{
			val number = Try(expression.toInt).toOption
			number match{
				case Some(num) => {return (x => (x == num) != negate) }
				case None => {throw new InvalidInputException("not a number: " + expression)}
			}
		}
	}
	
	def matchForSelector(selector: String): Set[PhysicalGraphNode] = {
		var selected = new mutable.HashSet[PhysicalGraphNode]()
		for (node <- graph.nodes) {
			node match {
				case cell: PhysicalGraphNode => {
					selected.add(cell)
				}
			}
		}
		val constraintSplit = "(?=[a-zA-Z])".r
		val constraints = constraintSplit.split(selector)
		println(constraints.mkString("Array(", ", ", ")"))
		for(constraint <- constraints){
			selected = selected.filter(getFilterForSelectorConstraint(constraint))
		}
		selected.toSet
	}
	
	def surroundAroundPosition(origin: Coord, cells: Iterable[PhysicalGraphNode], distance: Double): Unit = {
		val angle = (2*Math.PI)/cells.size
		var position = origin.add(new Vector(0, distance))
		for(cell <- cells){
			cell.setPosition(position)
			position = position.rotate(origin, angle)
		}
	}
	
}

class InvalidInputException(val message: String) extends Exception


