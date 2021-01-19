package com.bcorp.demo.elkg

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class ElkgSimpleApplication

fun main(args: Array<String>){
	runApplication<ElkgSimpleApplication>(*args)
}

@RestController
class HelloController{
	val log : Logger = LoggerFactory.getLogger(javaClass)
	val greetType = "Hello"
	@GetMapping("/greet")
	fun sayHello() : String {
		log.debug("Sending a greet : '{}'", greetType)
		return greetType
	}
}