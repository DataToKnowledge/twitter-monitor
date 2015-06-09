import akka.actor.{ Actor, Props, ActorRef, ActorSystem }
import akka.testkit.{ TestProbe, ImplicitSender, TestKit }
import org.scalatest.{FlatSpecLike, BeforeAndAfterAll, FlatSpec, Matchers}
import scala.concurrent.duration._
import org.scalactic.ConversionCheckedTripleEquals

/**
 * Created by fabiofumarola on 09/06/15.
 */
abstract class AkkaUnitSpec(_system: ActorSystem) extends TestKit(_system)
    with FlatSpecLike
    with Matchers
    with BeforeAndAfterAll
    with ConversionCheckedTripleEquals
    with ImplicitSender {

  def this(name: String) = this(ActorSystem(name))


  override def afterAll: Unit = system.shutdown()
}
