package onextent.akka.mongo.demo

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.mongo.demo.actors.AssessmentService
import onextent.akka.mongo.demo.http.{AssessmentRoute, ErrorSupport}

import scala.concurrent.ExecutionContextExecutor

object Main extends App with LazyLogging with ErrorSupport {

  implicit val actorSystem: ActorSystem = ActorSystem("rest-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContextExecutor =
    actorSystem.dispatcher

  val assessmentService: ActorRef = actorSystem.actorOf(
    AssessmentService.props(timeout),
    AssessmentService.name)

  val route =
    HealthCheck ~
      AssessmentRoute(assessmentService)

  Http().bindAndHandle(route, "0.0.0.0", port)

}
