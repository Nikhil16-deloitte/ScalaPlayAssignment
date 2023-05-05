package models


import play.api.libs.json.{Json, OFormat}

case class TeamWins(id:Option[Int],
                    team:String,
                    wins:Int)

object TeamWins
{
  implicit val formatter: OFormat[TeamWins] = Json.format[TeamWins]
}
