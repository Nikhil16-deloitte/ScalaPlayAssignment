package controllers
import Dao.IPLMatchDao
import akka.stream.Materializer
import akka.util.Timeout
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps


class IPLMatchDataController @Inject()(val iPLMatchDao: IPLMatchDao,
                                       val controllerComponents: ControllerComponents)
                                      (implicit mat: Materializer, ec: ExecutionContext)
  extends BaseController {

  implicit val timeout: Timeout = Timeout(5 seconds)

  def getMatch(id: Int): Action[AnyContent] = Action.async { _ =>
    iPLMatchDao.getMatch(id).map(x => Ok(Json.toJson(x)))
  }

  def getAllMatches: Action[AnyContent] = Action.async { _ =>
    iPLMatchDao.fetchAll().map(x => Ok(Json.toJson(x)))
  }

  def getMatchesByTeam(team:String): Action[AnyContent] = Action.async { _ =>
    iPLMatchDao.getMatchesByTeam(team).map(x => Ok(Json.toJson(x)))
  }

}