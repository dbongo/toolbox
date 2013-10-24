package models

import slick.driver.PostgresDriver.simple._
import Database.threadLocalSession
import play.api.Logger
import play.api.db.DB
import play.api.Play.current
import scala.slick.lifted.ColumnOption.DBType

case class User(firstName: String, lastName: String, id: Option[Long] = None)

object UserTable extends Table[User]("test_user"){
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def firstName = column[String]("first_name", DBType("varchar(50)"))
  def lastName = column[String]("last_name", DBType("varchar(50)"))
  def * = firstName ~ lastName ~ id.? <> (User, User.unapply _)
  def forInsert = firstName ~ lastName <> (
    {t => User(t._1, t._2)}, {(u: User) => Some(u.firstName, u.lastName)}
  )

  def findAll : Seq[User] = Database.forDataSource(DB.getDataSource()) withTransaction {
    Query(UserTable).sortBy(_.lastName).list
  }

  def findById(id: Long): Option[User] = Database.forDataSource(DB.getDataSource()) withTransaction {
    Query(UserTable).where(_.id === id).firstOption
  }

//  def findByEmail(email: String): Option[User] = Database.forDataSource(DB.getDataSource()) withTransaction {
//    Query(UserTable).where(_.email === email).firstOption
//  }
//
//  def insert(user: User, password: String) : (Long, String) = Database.forDataSource(DB.getDataSource("entity")) withTransaction {
//    val salt = AuthUtil.salt()
//    val vid = UserTable.newVid
//    val id = UserTable.forInsert returning UserTable.id insert user.copy(password =  AuthUtil.encrypt(password, salt), uid = salt, vid = vid)
//    (id, vid)
//  }
//
//  def update(user: User) = Database.forDataSource(DB.getDataSource("entity")) withTransaction {
//    Query(UserTable).where(_.id === user.id.get).map(u => u.firstName ~ u.lastName ~ u.email ~ u.updateDate).update((user.firstName, user.lastName, user.email, new DateTime))
//  }
//
//  def delete(id: Long) = Database.forDataSource(DB.getDataSource("entity")) withTransaction {
//    Query(UserAppTable).where(_.userId === id).delete
//    Query(UserTable).where(_.id === id).delete
//  }
}