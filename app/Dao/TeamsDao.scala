package Dao

import models.Teams
import models.{Teams, Venue}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TeamsDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents)(
  implicit ec: ExecutionContext) extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._
  class TeamsTable(tag: Tag) extends Table[Teams](tag, "teams") {

    def id: Rep[Option[Int]] = column[Int]("id", O.PrimaryKey,O.AutoInc)
    def teamName: Rep[String] = column[String]("TeamName")

    override def * : ProvenShape[Teams] = (id,teamName) <> ((Teams.apply _).tupled, Teams.unapply)

  }
  private val teamsTableQuery =TableQuery[TeamsTable]


  def insert(teams: Teams): Future[Unit] =
    db.run(DBIO.seq(teamsTableQuery.schema.createIfNotExists, teamsTableQuery += teams))

  def fetchAll(): Future[Seq[Teams]] = db.run(teamsTableQuery.result)
}
