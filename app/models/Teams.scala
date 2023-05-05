package models

import play.api.libs.json.{Json, OFormat}

case class Teams(id: Option[Int], name: String)

object Teams {
  implicit val formatter: OFormat[Teams] = Json.format[Teams]
}
