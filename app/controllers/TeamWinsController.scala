package controllers

import Dao.TeamWinsDao
import akka.stream.Materializer
import akka.util.Timeout
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class TeamWinsController @Inject()(val teamWinsDao: TeamWinsDao,
                                   val controllerComponents: ControllerComponents)
                                  (implicit mat: Materializer, ec: ExecutionContext)
  extends BaseController {

  implicit val timeout: Timeout = Timeout(5 seconds)

  def getWin(team: String): Action[AnyContent] = Action.async { _ =>
    teamWinsDao.getMatch(team).map(x => Ok(Json.toJson(x)))
  }

  def getAllWins: Action[AnyContent] = Action.async { _ =>
    teamWinsDao.fetchAll().map(x => Ok(Json.toJson(x)))
  }


}

