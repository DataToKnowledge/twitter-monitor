package it.dtk

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalactic.ConversionCheckedTripleEquals
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

/**
 * Created by fabiofumarola on 09/06/15.
 */
abstract class AkkaWordSpec(_system: ActorSystem) extends TestKit(_system)
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll
    with ConversionCheckedTripleEquals
    with ImplicitSender {

  def this(name: String) = this(ActorSystem(name))


  override def afterAll: Unit = system.shutdown()
}
