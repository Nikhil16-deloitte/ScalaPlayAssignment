package models
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate


case class MatchDetails(id:Int,
                        city:String,
                        date:LocalDate,
                        ManOfMatch:String,
                        venue:String,
                        team1:String,
                        team2:String,
                        toss_Winner:String,
                        toss_decision:String,
                        winner:String,
                        result:String,
                        umpire1:String,
                        umpire2:String)

object MatchDetails {
  implicit val formatter: OFormat[MatchDetails] = Json.format[MatchDetails]
}

