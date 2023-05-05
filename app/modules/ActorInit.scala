package modules

import Dao.{IPLMatchDao, TeamWinsDao, TeamsDao, VenueDao}
import akka.actor.{Actor, ActorSystem, PoisonPill, Props, Terminated}
import models.{MatchDetails, TeamWins}
import modules.actor.ReaderActor

import javax.inject.{Inject, Singleton}
import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.Source

@Singleton
class ActorInit @Inject()(iPLMatchDao: IPLMatchDao,teamsDao: TeamsDao,venueDao: VenueDao,TeamWinsDao: TeamWinsDao) {

  def populateTables(iPLMatchDao: IPLMatchDao,TeamWinsDao: TeamWinsDao,teamsDao: TeamsDao): Unit ={

    val MatchListFuture = iPLMatchDao.fetchAll()
    val teams = collection.mutable.Set[String]()
    val winners=collection.mutable.ListBuffer[String]()
    val matchList: Seq[MatchDetails] = Await.result(MatchListFuture, Duration.Inf)
    for(m<-matchList)
      {
        teams += m.team1
        winners += m.winner
      }

      for(t<-teams) {
        var count=0;
        {
          for(w<-winners)
            {
              if(w.equalsIgnoreCase(t)) {
                count+=1
              }
            }
            TeamWinsDao.insert(new TeamWins(None,t,count))
        }
      }
  }
  val source = Source.fromFile("IPL.csv")
  val lines = source.getLines()
  val linewithoutheader=lines.drop(1)

  val system = ActorSystem("FileReaderSystem")
  val fileReaderActor = system.actorOf(Props(new ReaderActor(iPLMatchDao,teamsDao, venueDao)), "fileReaderActor")

  system.actorOf(Props(new Actor {
    context.watch(fileReaderActor)

    def receive = {
      case Terminated(`fileReaderActor`) =>

        populateTables(iPLMatchDao,TeamWinsDao,teamsDao)
    }
  }))

  var bool=false
  while (linewithoutheader.hasNext){
    val line =lines.next()
    val record = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?<![ ])(?![ ])")
    fileReaderActor ! record
  }

if(!lines.hasNext)
  {
    fileReaderActor!PoisonPill
  }
}
