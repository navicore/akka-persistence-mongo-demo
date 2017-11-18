package onextent.akka.mongo.demo.actors

import akka.actor._
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.mongo.demo.actors.AssessmentService._
import onextent.akka.mongo.demo.actors.fuctions.ActorServiceSupport
import onextent.akka.mongo.demo.models.assessments._

object AssessmentService {
  def props(implicit timeout: Timeout) = Props(new AssessmentService)
  def name = "AssessmentService"

  final case class GetByName(name: String)
  final case class DeleteByName(name: String)
}

class AssessmentService(implicit timeout: Timeout)
    extends ActorServiceSupport
    with PersistentActor
    with LazyLogging {

  val conf: Config = ConfigFactory.load()
  val snapShotInterval: Int = conf.getInt("main.snapShotInterval")
  override def persistenceId: String =
    conf.getString("main.assessmentPersistenceId") + "_service"
  var state: List[String] = List[String]()

  override def receiveRecover: Receive = {

    case assessment: Assessment =>
      state = assessment.name :: state
      forward(assessment, assessment.name)

    case deleteCmd: DeleteByName =>
      state = state.filter(n => n != deleteCmd.name)
      stopActor(deleteCmd.name)

    case SnapshotOffer(_, snapshot: List[String @unchecked]) => {
      snapshot.foreach(createAssessmentActor)
      state = snapshot
    }

    case _: RecoveryCompleted =>
      logger.info("assessment service recovery completed")

  }

  override def receiveCommand: Receive = {

    case assessment: Assessment =>
      state = assessment.name :: state
      persistAsync(assessment) { _ =>
        {
          forward(assessment, assessment.name)
          if (lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0)
            saveSnapshot(state)
        }
      }

    case deleteCmd: DeleteByName =>
      if (state.contains(deleteCmd.name)) {
        state = state.filter(n => n != deleteCmd.name)
        persistAsync(deleteCmd) { _ =>
          {
            stopActor(deleteCmd.name)
            sender() ! Some(deleteCmd)
            if (lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0)
              saveSnapshot(state)
          }
        }
      } else {
        sender() ! None
      }

    case GetByName(name) => forwardIfExists(GetByName(name), name)

  }

}
