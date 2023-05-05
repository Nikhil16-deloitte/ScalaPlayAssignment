package Dao

import models.Venue
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VenueDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents)(
  implicit ec: ExecutionContext) extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._
  class VenueTable(tag: Tag) extends Table[Venue](tag, "Venues") {
    def id: Rep[Option[Int]] = column[Int]("id", O.PrimaryKey,O.AutoInc)
    def VenueName: Rep[String] = column[String]("Venue")

    override def * : ProvenShape[Venue] = (id,VenueName) <> ((Venue.apply _).tupled, Venue.unapply)
  }

  private val venueTableQuery =TableQuery[VenueTable]
  def insert(venue: Venue): Future[Unit] =
    db.run(DBIO.seq(venueTableQuery.schema.createIfNotExists, venueTableQuery += venue))

  def fetchAll(): Future[Seq[Venue]] = db.run(venueTableQuery.result)

}
