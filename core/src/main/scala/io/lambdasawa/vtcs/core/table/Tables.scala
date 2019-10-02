package io.lambdasawa.vtcs.core.table
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Channels.schema ++ ChatMessages.schema ++ ChatPages.schema ++ FlywaySchemaHistory.schema ++ Videos.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Channels
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param channelId Database column channel_id SqlType(VARCHAR), Length(64,true)
   *  @param title Database column title SqlType(VARCHAR), Length(255,true)
   *  @param createdAt Database column created_at SqlType(DATETIME)
   *  @param updatedAt Database column updated_at SqlType(DATETIME) */
  case class ChannelsRow(id: Long, channelId: String, title: String, createdAt: java.sql.Timestamp, updatedAt: java.sql.Timestamp)
  /** GetResult implicit for fetching ChannelsRow objects using plain SQL queries */
  implicit def GetResultChannelsRow(implicit e0: GR[Long], e1: GR[String], e2: GR[java.sql.Timestamp]): GR[ChannelsRow] = GR{
    prs => import prs._
    ChannelsRow.tupled((<<[Long], <<[String], <<[String], <<[java.sql.Timestamp], <<[java.sql.Timestamp]))
  }
  /** Table description of table channels. Objects of this class serve as prototypes for rows in queries. */
  class Channels(_tableTag: Tag) extends profile.api.Table[ChannelsRow](_tableTag, Some("vtcs"), "channels") {
    def * = (id, channelId, title, createdAt, updatedAt) <> (ChannelsRow.tupled, ChannelsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(channelId), Rep.Some(title), Rep.Some(createdAt), Rep.Some(updatedAt)).shaped.<>({r=>import r._; _1.map(_=> ChannelsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column channel_id SqlType(VARCHAR), Length(64,true) */
    val channelId: Rep[String] = column[String]("channel_id", O.Length(64,varying=true))
    /** Database column title SqlType(VARCHAR), Length(255,true) */
    val title: Rep[String] = column[String]("title", O.Length(255,varying=true))
    /** Database column created_at SqlType(DATETIME) */
    val createdAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created_at")
    /** Database column updated_at SqlType(DATETIME) */
    val updatedAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("updated_at")
  }
  /** Collection-like TableQuery object for table Channels */
  lazy val Channels = new TableQuery(tag => new Channels(tag))

  /** Entity class storing rows of table ChatMessages
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param messageId Database column message_id SqlType(VARCHAR), Length(512,true)
   *  @param chatId Database column chat_id SqlType(VARCHAR), Length(64,true)
   *  @param pageToken Database column page_token SqlType(VARCHAR), Length(64,true), Default(None)
   *  @param message Database column message SqlType(VARCHAR), Length(512,true)
   *  @param amount Database column amount SqlType(BIGINT), Default(None)
   *  @param currency Database column currency SqlType(VARCHAR), Length(8,true), Default(None)
   *  @param publishedAt Database column published_at SqlType(DATETIME)
   *  @param createdAt Database column created_at SqlType(DATETIME) */
  case class ChatMessagesRow(id: Long, messageId: String, chatId: String, pageToken: Option[String] = None, message: String, amount: Option[Long] = None, currency: Option[String] = None, publishedAt: java.sql.Timestamp, createdAt: java.sql.Timestamp)
  /** GetResult implicit for fetching ChatMessagesRow objects using plain SQL queries */
  implicit def GetResultChatMessagesRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Option[String]], e3: GR[Option[Long]], e4: GR[java.sql.Timestamp]): GR[ChatMessagesRow] = GR{
    prs => import prs._
    ChatMessagesRow.tupled((<<[Long], <<[String], <<[String], <<?[String], <<[String], <<?[Long], <<?[String], <<[java.sql.Timestamp], <<[java.sql.Timestamp]))
  }
  /** Table description of table chat_messages. Objects of this class serve as prototypes for rows in queries. */
  class ChatMessages(_tableTag: Tag) extends profile.api.Table[ChatMessagesRow](_tableTag, Some("vtcs"), "chat_messages") {
    def * = (id, messageId, chatId, pageToken, message, amount, currency, publishedAt, createdAt) <> (ChatMessagesRow.tupled, ChatMessagesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(messageId), Rep.Some(chatId), pageToken, Rep.Some(message), amount, currency, Rep.Some(publishedAt), Rep.Some(createdAt)).shaped.<>({r=>import r._; _1.map(_=> ChatMessagesRow.tupled((_1.get, _2.get, _3.get, _4, _5.get, _6, _7, _8.get, _9.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column message_id SqlType(VARCHAR), Length(512,true) */
    val messageId: Rep[String] = column[String]("message_id", O.Length(512,varying=true))
    /** Database column chat_id SqlType(VARCHAR), Length(64,true) */
    val chatId: Rep[String] = column[String]("chat_id", O.Length(64,varying=true))
    /** Database column page_token SqlType(VARCHAR), Length(64,true), Default(None) */
    val pageToken: Rep[Option[String]] = column[Option[String]]("page_token", O.Length(64,varying=true), O.Default(None))
    /** Database column message SqlType(VARCHAR), Length(512,true) */
    val message: Rep[String] = column[String]("message", O.Length(512,varying=true))
    /** Database column amount SqlType(BIGINT), Default(None) */
    val amount: Rep[Option[Long]] = column[Option[Long]]("amount", O.Default(None))
    /** Database column currency SqlType(VARCHAR), Length(8,true), Default(None) */
    val currency: Rep[Option[String]] = column[Option[String]]("currency", O.Length(8,varying=true), O.Default(None))
    /** Database column published_at SqlType(DATETIME) */
    val publishedAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("published_at")
    /** Database column created_at SqlType(DATETIME) */
    val createdAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created_at")
  }
  /** Collection-like TableQuery object for table ChatMessages */
  lazy val ChatMessages = new TableQuery(tag => new ChatMessages(tag))

  /** Entity class storing rows of table ChatPages
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param chatId Database column chat_id SqlType(VARCHAR), Length(64,true)
   *  @param pageToken Database column page_token SqlType(VARCHAR), Length(64,true), Default(None)
   *  @param nextPageToken Database column next_page_token SqlType(VARCHAR), Length(64,true), Default(None)
   *  @param pollingInterval Database column polling_interval SqlType(BIGINT)
   *  @param offlineAt Database column offline_at SqlType(DATETIME), Default(None)
   *  @param createdAt Database column created_at SqlType(DATETIME) */
  case class ChatPagesRow(id: Long, chatId: String, pageToken: Option[String] = None, nextPageToken: Option[String] = None, pollingInterval: Long, offlineAt: Option[java.sql.Timestamp] = None, createdAt: java.sql.Timestamp)
  /** GetResult implicit for fetching ChatPagesRow objects using plain SQL queries */
  implicit def GetResultChatPagesRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Option[String]], e3: GR[Option[java.sql.Timestamp]], e4: GR[java.sql.Timestamp]): GR[ChatPagesRow] = GR{
    prs => import prs._
    ChatPagesRow.tupled((<<[Long], <<[String], <<?[String], <<?[String], <<[Long], <<?[java.sql.Timestamp], <<[java.sql.Timestamp]))
  }
  /** Table description of table chat_pages. Objects of this class serve as prototypes for rows in queries. */
  class ChatPages(_tableTag: Tag) extends profile.api.Table[ChatPagesRow](_tableTag, Some("vtcs"), "chat_pages") {
    def * = (id, chatId, pageToken, nextPageToken, pollingInterval, offlineAt, createdAt) <> (ChatPagesRow.tupled, ChatPagesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(chatId), pageToken, nextPageToken, Rep.Some(pollingInterval), offlineAt, Rep.Some(createdAt)).shaped.<>({r=>import r._; _1.map(_=> ChatPagesRow.tupled((_1.get, _2.get, _3, _4, _5.get, _6, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column chat_id SqlType(VARCHAR), Length(64,true) */
    val chatId: Rep[String] = column[String]("chat_id", O.Length(64,varying=true))
    /** Database column page_token SqlType(VARCHAR), Length(64,true), Default(None) */
    val pageToken: Rep[Option[String]] = column[Option[String]]("page_token", O.Length(64,varying=true), O.Default(None))
    /** Database column next_page_token SqlType(VARCHAR), Length(64,true), Default(None) */
    val nextPageToken: Rep[Option[String]] = column[Option[String]]("next_page_token", O.Length(64,varying=true), O.Default(None))
    /** Database column polling_interval SqlType(BIGINT) */
    val pollingInterval: Rep[Long] = column[Long]("polling_interval")
    /** Database column offline_at SqlType(DATETIME), Default(None) */
    val offlineAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("offline_at", O.Default(None))
    /** Database column created_at SqlType(DATETIME) */
    val createdAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created_at")
  }
  /** Collection-like TableQuery object for table ChatPages */
  lazy val ChatPages = new TableQuery(tag => new ChatPages(tag))

  /** Entity class storing rows of table FlywaySchemaHistory
   *  @param installedRank Database column installed_rank SqlType(INT), PrimaryKey
   *  @param version Database column version SqlType(VARCHAR), Length(50,true), Default(None)
   *  @param description Database column description SqlType(VARCHAR), Length(200,true)
   *  @param `type` Database column type SqlType(VARCHAR), Length(20,true)
   *  @param script Database column script SqlType(VARCHAR), Length(1000,true)
   *  @param checksum Database column checksum SqlType(INT), Default(None)
   *  @param installedBy Database column installed_by SqlType(VARCHAR), Length(100,true)
   *  @param installedOn Database column installed_on SqlType(TIMESTAMP)
   *  @param executionTime Database column execution_time SqlType(INT)
   *  @param success Database column success SqlType(BIT) */
  case class FlywaySchemaHistoryRow(installedRank: Int, version: Option[String] = None, description: String, `type`: String, script: String, checksum: Option[Int] = None, installedBy: String, installedOn: java.sql.Timestamp, executionTime: Int, success: Boolean)
  /** GetResult implicit for fetching FlywaySchemaHistoryRow objects using plain SQL queries */
  implicit def GetResultFlywaySchemaHistoryRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[String], e3: GR[Option[Int]], e4: GR[java.sql.Timestamp], e5: GR[Boolean]): GR[FlywaySchemaHistoryRow] = GR{
    prs => import prs._
    FlywaySchemaHistoryRow.tupled((<<[Int], <<?[String], <<[String], <<[String], <<[String], <<?[Int], <<[String], <<[java.sql.Timestamp], <<[Int], <<[Boolean]))
  }
  /** Table description of table flyway_schema_history. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class FlywaySchemaHistory(_tableTag: Tag) extends profile.api.Table[FlywaySchemaHistoryRow](_tableTag, Some("vtcs"), "flyway_schema_history") {
    def * = (installedRank, version, description, `type`, script, checksum, installedBy, installedOn, executionTime, success) <> (FlywaySchemaHistoryRow.tupled, FlywaySchemaHistoryRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(installedRank), version, Rep.Some(description), Rep.Some(`type`), Rep.Some(script), checksum, Rep.Some(installedBy), Rep.Some(installedOn), Rep.Some(executionTime), Rep.Some(success)).shaped.<>({r=>import r._; _1.map(_=> FlywaySchemaHistoryRow.tupled((_1.get, _2, _3.get, _4.get, _5.get, _6, _7.get, _8.get, _9.get, _10.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column installed_rank SqlType(INT), PrimaryKey */
    val installedRank: Rep[Int] = column[Int]("installed_rank", O.PrimaryKey)
    /** Database column version SqlType(VARCHAR), Length(50,true), Default(None) */
    val version: Rep[Option[String]] = column[Option[String]]("version", O.Length(50,varying=true), O.Default(None))
    /** Database column description SqlType(VARCHAR), Length(200,true) */
    val description: Rep[String] = column[String]("description", O.Length(200,varying=true))
    /** Database column type SqlType(VARCHAR), Length(20,true)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Rep[String] = column[String]("type", O.Length(20,varying=true))
    /** Database column script SqlType(VARCHAR), Length(1000,true) */
    val script: Rep[String] = column[String]("script", O.Length(1000,varying=true))
    /** Database column checksum SqlType(INT), Default(None) */
    val checksum: Rep[Option[Int]] = column[Option[Int]]("checksum", O.Default(None))
    /** Database column installed_by SqlType(VARCHAR), Length(100,true) */
    val installedBy: Rep[String] = column[String]("installed_by", O.Length(100,varying=true))
    /** Database column installed_on SqlType(TIMESTAMP) */
    val installedOn: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("installed_on")
    /** Database column execution_time SqlType(INT) */
    val executionTime: Rep[Int] = column[Int]("execution_time")
    /** Database column success SqlType(BIT) */
    val success: Rep[Boolean] = column[Boolean]("success")

    /** Index over (success) (database name flyway_schema_history_s_idx) */
    val index1 = index("flyway_schema_history_s_idx", success)
  }
  /** Collection-like TableQuery object for table FlywaySchemaHistory */
  lazy val FlywaySchemaHistory = new TableQuery(tag => new FlywaySchemaHistory(tag))

  /** Entity class storing rows of table Videos
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param videoId Database column video_id SqlType(VARCHAR), Length(64,true)
   *  @param channelId Database column channel_id SqlType(VARCHAR), Length(64,true)
   *  @param title Database column title SqlType(VARCHAR), Length(255,true)
   *  @param publishedAt Database column published_at SqlType(DATETIME)
   *  @param chatId Database column chat_id SqlType(VARCHAR), Length(64,true), Default(None)
   *  @param startTime Database column start_time SqlType(DATETIME), Default(None)
   *  @param endTime Database column end_time SqlType(DATETIME), Default(None)
   *  @param createdAt Database column created_at SqlType(DATETIME) */
  case class VideosRow(id: Long, videoId: String, channelId: String, title: String, publishedAt: java.sql.Timestamp, chatId: Option[String] = None, startTime: Option[java.sql.Timestamp] = None, endTime: Option[java.sql.Timestamp] = None, createdAt: java.sql.Timestamp)
  /** GetResult implicit for fetching VideosRow objects using plain SQL queries */
  implicit def GetResultVideosRow(implicit e0: GR[Long], e1: GR[String], e2: GR[java.sql.Timestamp], e3: GR[Option[String]], e4: GR[Option[java.sql.Timestamp]]): GR[VideosRow] = GR{
    prs => import prs._
    VideosRow.tupled((<<[Long], <<[String], <<[String], <<[String], <<[java.sql.Timestamp], <<?[String], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<[java.sql.Timestamp]))
  }
  /** Table description of table videos. Objects of this class serve as prototypes for rows in queries. */
  class Videos(_tableTag: Tag) extends profile.api.Table[VideosRow](_tableTag, Some("vtcs"), "videos") {
    def * = (id, videoId, channelId, title, publishedAt, chatId, startTime, endTime, createdAt) <> (VideosRow.tupled, VideosRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(videoId), Rep.Some(channelId), Rep.Some(title), Rep.Some(publishedAt), chatId, startTime, endTime, Rep.Some(createdAt)).shaped.<>({r=>import r._; _1.map(_=> VideosRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6, _7, _8, _9.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column video_id SqlType(VARCHAR), Length(64,true) */
    val videoId: Rep[String] = column[String]("video_id", O.Length(64,varying=true))
    /** Database column channel_id SqlType(VARCHAR), Length(64,true) */
    val channelId: Rep[String] = column[String]("channel_id", O.Length(64,varying=true))
    /** Database column title SqlType(VARCHAR), Length(255,true) */
    val title: Rep[String] = column[String]("title", O.Length(255,varying=true))
    /** Database column published_at SqlType(DATETIME) */
    val publishedAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("published_at")
    /** Database column chat_id SqlType(VARCHAR), Length(64,true), Default(None) */
    val chatId: Rep[Option[String]] = column[Option[String]]("chat_id", O.Length(64,varying=true), O.Default(None))
    /** Database column start_time SqlType(DATETIME), Default(None) */
    val startTime: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("start_time", O.Default(None))
    /** Database column end_time SqlType(DATETIME), Default(None) */
    val endTime: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("end_time", O.Default(None))
    /** Database column created_at SqlType(DATETIME) */
    val createdAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created_at")
  }
  /** Collection-like TableQuery object for table Videos */
  lazy val Videos = new TableQuery(tag => new Videos(tag))
}
