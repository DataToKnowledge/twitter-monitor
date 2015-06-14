package it.dtk.twitter

import scala.concurrent.duration.FiniteDuration

/**
 * Created by fabiofumarola on 11/06/15.
 */
object MessageProtocol {

  case class Track(
    users: Set[Long] = Set(),
    terms: Set[String] = Set(),
    language: Set[String] = Set())

  case class AddWorkers(number: Int)
  case class DelWorkers(number: Int)
  case class Workers(number: Int)

  sealed trait Operation
  case object Start extends Operation
  case object Stop extends Operation
  case object Restart extends Operation
  case object Status extends Operation
  case class ChangeWorkersRate(value: FiniteDuration)


  sealed trait OperationReply
  case class OperationAck(operation: String, msg: String) extends OperationReply
  case class OperationFailed(operation: String, msg: String) extends OperationReply

}
