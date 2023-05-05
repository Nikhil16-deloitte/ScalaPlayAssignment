package modules.actor

import Dao.{IPLMatchDao, TeamsDao, VenueDao}
import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.routing.RoundRobinPool
import modules.actor.ChildActor.ChildActor

import javax.inject.Inject
import scala.concurrent.ExecutionContext




  class ReaderActor @Inject()(val iPLMatchDao: IPLMatchDao,val teamsDao: TeamsDao,val venueDao: VenueDao)extends Actor {

    val venues = collection.mutable.Set[String]()
    val teams = collection.mutable.Set[String]()

    implicit val ec: ExecutionContext = context.system.dispatcher

    val workerRouter: ActorRef = context.actorOf(RoundRobinPool(10).props(Props(new ChildActor(iPLMatchDao,venueDao,teamsDao,venues,teams))), "workerRouter")

    override def receive: Receive = {
      case record: Array[String] => {
        workerRouter ! record
      }

      case Terminated(child) =>
        println(s"Child actor ${child.path.name} terminated")

    }
}

