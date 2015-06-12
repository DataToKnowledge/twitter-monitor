package it.dtk.twitter

import scala.concurrent.duration.FiniteDuration

/**
 * Created by fabiofumarola on 11/06/15.
 */
object MessageProtocol {

  sealed trait ToMonitor
  case class TrackUsers(set: Set[Long]) extends ToMonitor
  case class TrackTerms(terms: Set[String]) extends ToMonitor
  case class TrackLanguages(set: Set[String]) extends ToMonitor

  sealed trait MonitoringReply
  case class MonitoringAck(op: ToMonitor, msg: String) extends MonitoringReply

  sealed trait Operation
  case object Start extends Operation
  case object Stop extends Operation
  case object Restart extends Operation
  case object Status extends Operation
  case object AddWorker extends Operation
  case class ChangeWorkersRate(value: FiniteDuration)

  sealed trait OperationReply
  case class OperationAck(op: Operation, msg: String)
  case class OperationFailed(op: Operation, msg: String)

}
