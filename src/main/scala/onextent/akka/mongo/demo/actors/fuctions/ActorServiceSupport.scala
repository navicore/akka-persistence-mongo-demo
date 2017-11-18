package onextent.akka.mongo.demo.actors.fuctions

import akka.actor._
import onextent.akka.mongo.demo.actors.AssessmentActor

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

trait ActorServiceSupport extends Actor {

  def createAssessmentActor(actorId: String): Unit = {
    def notFound(): Unit =
      context.actorOf(AssessmentActor.props(actorId), actorId)
    context
      .child(actorId)
      .fold(notFound())(_ => {})
  }

  def forwardIfExists[T: TypeTag](query: T, actorId: String)(
      implicit tag: ClassTag[T]): Unit = {
    def notFound(): Unit = sender() ! None
    context
      .child(actorId)
      .fold(notFound())(_ forward query)
  }

  // create if does not exist
  def forward[T: TypeTag](query: T, actorId: String)(
      implicit tag: ClassTag[T]): Unit = {
    def notFound(): Unit =
      context.actorOf(AssessmentActor.props(actorId), actorId) forward query
    context
      .child(actorId)
      .fold(notFound())(_ forward query)
  }

  def stopActor(actorId: String): Unit = {
    def notFound(): Unit = Unit
    context
      .child(actorId)
      .fold(notFound())(context.stop)
  }

}
