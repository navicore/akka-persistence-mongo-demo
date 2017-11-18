package onextent.akka.mongo.demo.models.assessments

import onextent.akka.mongo.demo.models.JsonSupport
import spray.json._

trait AssessmentJsonSupport extends JsonSupport {

  implicit val assessmentFormat: RootJsonFormat[Assessment] = jsonFormat4(
    Assessment)
}
