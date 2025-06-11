package com.anax.graphtool
package main

import scala.collection.mutable

object Util {
	def parseInputArguments(args: Array[String]): Map[String, String]= {
		val options: mutable.HashMap[String, String] = new mutable.HashMap()
		for(i <- args.indices){
			if(args(i).startsWith("--")){
				options.put(args(i).substring(2), args.lift(i+1).getOrElse(""))
			}
		}
		options.toMap
	}
}
