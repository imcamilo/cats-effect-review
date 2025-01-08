package com.imcamilo.review

import cats.effect.IO
import scala.io.StdIn

// embodies some kind of computation that performs side effects
// IO description of an actual computation

object IOIntro extends App {

  val firstIO: IO[Int] = IO.pure(30) // pure not perform side effect
  val aDelayIO: IO[Int] = IO.delay({ // if u r not sure, use delay
    println("Im producing an integer")
    30
  })
  val shouldntDoThis: IO[Int] = IO.pure({
    println("Im producing an integer")
    30
  })
  val aDelayIO2: IO[Int] = IO { // apply == delay
    println("Im producing an integer")
    30
  }

  // unsafeRunSync needs an IO runtime, its like a thread pool
  import cats.effect.unsafe.implicits.global // platform - io runtime
  println(aDelayIO.unsafeRunSync())

  // map, flatMap
  val checkinIO = firstIO.map(_ * 2)
  val printingIO = firstIO.flatMap(a => IO.delay(println(a)))

  def smallProgram(): IO[Unit] = for {
    line1 <- IO(StdIn.readLine())
    line2 <- IO(StdIn.readLine())
    _ <- IO.delay(println(line1 + line2))
  } yield ()

  println(smallProgram().unsafeRunSync())

  // type classes
  // mapN - combine IO effects as tuples
  import cats.syntax.apply._
  val combined: IO[Int] = (firstIO, checkinIO).mapN(_ + _)

  def smallProgram2(): IO[Unit] =
    (IO(StdIn.readLine()), IO(StdIn.readLine())).mapN(_ + _).map(println)
  
  println(smallProgram2().unsafeRunSync())

  //exercices
  def sequenceTakeLast1[A, B](ioa: IO[A], iob: IO[B]): IO[B] =
    for {
      _ <- ioa
      response <- iob
    } yield response
  
  def sequenceTakeLast2[A, B](ioa: IO[A], iob: IO[B]): IO[B] = ioa.flatMap(_ => iob)

  def sequenceTakeLast3[A, B](ioa: IO[A], iob: IO[B]): IO[B] = ioa *> iob // andThen
  
  def sequenceTakeLast4[A, B](ioa: IO[A], iob: IO[B]): IO[B] = ioa >> iob // andThen with by-name call

  def sequenceFirst[A, B](ioa: IO[A], iob: IO[B]): IO[A] =
    for {
      response <- ioa
      _ <- iob
    } yield response
  
  def sequenceFirst2[A, B](ioa: IO[A], iob: IO[B]): IO[A] = ioa.flatMap(a => iob.map(_ => a))

  def sequenceFirst3[A, B](ioa: IO[A], iob: IO[B]): IO[A] = ioa <* iob

  def forever[A](io: IO[A]): IO[Unit] = for {
    response <- io
    _ <- forever(io)
  } yield ()

  def forever2[A](io: IO[A]): IO[Unit] = io.flatMap(_ => forever2(io))

  def forever3[A](io: IO[A]): IO[Unit] = io >> forever3(io)
  
  def forever4[A](io: IO[A]): IO[Unit] = io *> forever4(io)
  
  def forever5[A](io: IO[A]): IO[Unit] = io.foreverM  // with tail rec

  def convert[A, B](ioa: IO[A], value: B): IO[B] = ioa.map(_ => value)

  def convert2[A, B](ioa: IO[A], value: B): IO[B] = ioa.as(value) //same

  def asUnit[A](ioa: IO[A]): IO[Unit] = for {
      _ <- ioa
  } yield ()

  def asUnit2[A](ioa: IO[A]): IO[Unit] = ioa.map(_ => ())
  
  def asUnit3[A](ioa: IO[A]): IO[Unit] = ioa.as(()) //compiles, but its unreadable

  def asUnit4[A](ioa: IO[A]): IO[Unit] = ioa.void // same

  def sum(n: Int): Int =
    if (n <= 0) 0
    else n + sum(n - 1)

  // for comprehension again
  def sumIO(n: Int): IO[Int] = 
    if(n == 0) IO.pure(0)
    else for {
      lastN <- IO(n)
      prev <- sumIO(n - 1)
    } yield prev + lastN

  //fib io
  def fib(n: Int): IO[BigInt] = 
    if(n<=2) IO.pure(1)
    else for {
      last <- IO(fib(n -1)).flatMap(x => x) // flatten
      prev <- IO(fib(n -2)).flatMap(x => x) // flatten
    } yield last + prev
  
  def fib2(n: Int): IO[BigInt] = 
    if(n<=2) IO(1)
    else for {
      last <- IO.defer(fib(n -1)) // same as IO.dalay(...).flatten
      prev <- IO.defer(fib(n -2)) // same as IO.dalay(...).flatten
    } yield last + prev

}
