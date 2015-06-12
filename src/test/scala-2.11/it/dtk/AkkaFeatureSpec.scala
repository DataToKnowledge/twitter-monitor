package it.dtk

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalactic.ConversionCheckedTripleEquals
import org.scalatest._

/**
  * Created by fabiofumarola on 09/06/15.
  */
abstract class AkkaFeatureSpec(_system: ActorSystem) extends TestKit(_system)
     with FeatureSpecLike
     with GivenWhenThen
     with Matchers
     with BeforeAndAfterAll
     with ConversionCheckedTripleEquals
     with ImplicitSender {

   def this(name: String) = this(ActorSystem(name))


   override def afterAll: Unit = system.shutdown()
 }
