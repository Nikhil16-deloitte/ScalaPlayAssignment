package modules.actor

import Dao.{IPLMatchDao, TeamsDao, VenueDao}
import akka.actor.Actor
import akka.stream.ActorMaterializer
import models.{MatchDetails, Teams, Venue}

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.util.Failure


object ChildActor {

  def parseLocalDate(dateStr: String, dateFormat: String): LocalDate = {
    dateStr.replace('-','/')
    val formatter = DateTimeFormatter.ofPattern(dateFormat)
    val date = LocalDate.parse(dateStr, formatter)
    return date
  }

  class ChildActor(iPLMatchDao: IPLMatchDao,venueDao: VenueDao,teamsDao: TeamsDao,teams:mutable.Set[String],venues:mutable.Set[String]) (implicit ec: ExecutionContext) extends Actor {
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    override def receive: Receive = {
      case record: Array[String] => {
        val md= MatchDetails(
          id = record(0).toInt,
          city = record(1), date = parseLocalDate(record(2),"yyyy-MM-dd"), ManOfMatch = record(3), venue = record(4), team1 = record(6), team2 = record(7), toss_Winner = record(8), toss_decision = record(9), winner = record(10), result = record(11) + " " + record(12), umpire1 = record(15), umpire2 = record(16)
        )
        //        println(md.toString)
        iPLMatchDao.insert(md)

        if(!teams.contains(record(6).trim)){
          teams += record(6).trim
          teamsDao.insert(new Teams(None,record(6).trim))
        }
        if(!venues.contains(record(4).trim)) {
          venues += record(4).trim
          venueDao.insert(new Venue(None,record(4).trim))
        }

      }
      case Failure(ex) =>
        context.actorSelection("/user/masterActor/errorHandler") ! ex
    }
  }


}