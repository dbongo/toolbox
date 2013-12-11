package controllers.api

import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.libs.ws.WS
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object WSTool extends Controller {
  
  case class Call(url: String, method: String, body:String)
  case class Response(body: String)

//  def call = TODO

  def call = Action.async(parse.json) { implicit req =>
    Json.reads[Call].reads(req.body).fold(
      invalid => Future(BadRequest),
      call => {
        val req = WS.url(call.url)
        val response = call.method match {
          case "GET" => req.get()
          case "POST" => req.post(call.body)
          case _ => throw new RuntimeException("Bad method: " + call.method)
        }

        response.map{ r =>
          Ok(Json.writes[Response].writes(Response(r.body)))
        }
      }
    )
  }
}