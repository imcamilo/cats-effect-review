package com.imcamilo.review

import scala.concurrent.Future

object Effects extends App {

	def combine(a: Int, b: Int) = a + b

	/**
	  * Effect Types:
			1. A type signature must describe the kind of calculation that will be performed
			2. A type signature must describe the VALUE that will be calculated
			3. When side effects are needed -effect construction is separated from effect execution-
	  */

	// Option
	// 1. ok
	// 2. ok
	// 3. ok. Side effects are not needed
	val anOption: Option[Int] = Option(30)

	// Future
	// 1. ok
	// 2. ok
	// 3. Side effect is required. Execution is not separated from construction
	import scala.concurrent.ExecutionContext.Implicits.global
	val aFuture: Future[Int] = Future(30)
	
	// custom
	case class MyIO[A](unsafeRun: () => A) {
		def map[B](f: A => B): MyIO[B] = MyIO(() => f(unsafeRun()))
		def flatMap[B](f: A => MyIO[B]): MyIO[B] = MyIO(() => f(unsafeRun()).unsafeRun())
	}

	// MyIO - IS AN EFFECT TYPE
	// 1. describes any computation that might produce a side effect
	// 2. calculates a value of type A, if its successfull
	// 3. side effect are required for evaluation of () => A.
		// yes. the creation of IO does not produce the side effect on construction
	val anIO: MyIO[Int] = MyIO( () => {
		println("producing 30...")
		30
	})

	anIO.unsafeRun()

}
