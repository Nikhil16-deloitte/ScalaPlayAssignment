package Dao

import models.TeamWins
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TeamWinsDao  @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents)(
  implicit ec: ExecutionContext) extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  class TeamWinsTable(tag: Tag) extends Table[TeamWins](tag, "team_wins") {
    def id: Rep[Option[Int]] = column[Int]("id", O.PrimaryKey,O.AutoInc)
    def teamName: Rep[String] = column[String]("Team")
    def wins:Rep[Int]=column[Int]("Wins")

    override def * : ProvenShape[TeamWins] = (id,teamName,wins) <> ((TeamWins.apply _).tupled, TeamWins.unapply)
  }


  private val teamsTableQuery =TableQuery[TeamWinsTable]


  def insert(teamWins: TeamWins): Future[Unit] =
    db.run(DBIO.seq(teamsTableQuery.schema.createIfNotExists, teamsTableQuery += teamWins))

  def getMatch(team: String): Future[Seq[TeamWins]] =
    db.run(teamsTableQuery.filter(_.teamName === team).result)

  def fetchAll(): Future[Seq[TeamWins]] = db.run(teamsTableQuery.result)


}