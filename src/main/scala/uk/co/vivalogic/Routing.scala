package uk.co.vivalogic

import spray.http.{MediaTypes, StatusCodes}
import spray.routing.PathMatchers.{HexIntNumber, IntNumber, Segment}
import uk.co.vivalogic.ScreenServer._
import uk.co.vivalogic.encoders.{MPicoSysEPDEncoder, ElectricBarometerLcdEncoder}
import MediaTypes._

/**
 * Created by gavin on 29/05/15.
 */
object Routing {
  val encoders = Map( 0 -> ElectricBarometerLcdEncoder,
                      1 -> MPicoSysEPDEncoder)

  val authenticationRoute = pathPrefix(HexIntNumber) { authToken =>
    get {
      clientIP { ip =>
        if (!tokenIsValid(authToken))
          complete(StatusCodes.Unauthorized, "Your authentication token is not valid. If you believe this to be incorrect, please contact info@vivalogic.co.uk")
        else if (!tokenIsTaciturn(authToken))
          complete(StatusCodes.EnhanceYourCalm, "You have exceeded your usage credits. Please try again in 5 minutes, or upgrade to the next pricing tier.")
        else
          pathPrefix(IntNumber) { scheme =>
            if (!encoders.contains(scheme))
              respondWithMediaType(`text/html`) {
                complete(StatusCodes.NotImplemented, "Encoding scheme " + scheme + " is not implemented. The valid schemes are: <br>" + schemeTable)
              }
            else
              pathPrefix(IntNumber) { compression =>
                if (compression != 0)
                  complete(StatusCodes.NotImplemented, "No compression schemes are currently implemented; specify scheme 0")
                else
                  pathPrefix(IntNumber) { width =>
                    pathPrefix(IntNumber) { height =>
                      if (width > 640 || height > 640)
                        complete(StatusCodes.RequestEntityTooLarge, "Maximum screen size is currently 640 x 640; for larger screens, please contact info@vivalogic.co.uk")
                      else
                        pathPrefix(Segment) { rotation =>
                          if (!"NESW".contains(rotation))
                            complete(StatusCodes.BadRequest, rotation + " is not a valid rotation")
                          else if ("N" != rotation)
                            complete(StatusCodes.NotImplemented, "Rotation not yet supported")
                          else
                            path(Segment) { url =>
                              println(scheme + " " + width + " " + height + " " + url + " " + ip)
                              val screen = encoders(scheme).convertImage(ScreenServer.render(url, width, height), width, height)
                              //TODO: Handle error: URL not reachable
                              //TODO: Handle error: encoding error
                              complete(screen)
                            } ~ complete("Please provide the source URL")
                        } ~ complete("Specify rotation (N, S, E, W)")
                    } ~ complete("What is your screen height?")
                  } ~ complete("What is your screen width?")
              } ~ complete("Specify compression")
          } ~ complete("Auth token validated. Please specify a scheme")
      } ~ complete("Unable to determine client IP; aborting")
    } ~ complete("Was not a GET request")
  } ~ complete(StatusCodes.Unauthorized, "To access this service, you will need a valid authentication token. Obtain one by emailing info@vivalogic.co.uk")
  // TODO: Sign-up page
  // TODO: Billing
  // TODO: Possible crash on non-int encoding scheme

  def schemeTable: String = {
    var t = new StringBuilder
    t ++= "<table border=1>"
    t ++= "<th>ID</th><th>Name</th>"
    for (s <- encoders.keys) {
      t ++= "<tr><td>"
      t ++= s.toString
      t ++= "</td><td>"
      t ++= encoders(s).name
      t ++= "</td></tr>"
    }
    t ++= "</table>"
    t.toString()
  }

  def tokenIsValid(token: Int): Boolean = {
    // TODO: Back this with a database
    token == 0x80081e5
  }

  def tokenIsTaciturn(token: Int): Boolean = {
    // TODO: Back this with a database, as well
    true
  }
}

/**
  Pricing tiers:
  $0        - Free:   100 requests/day
  $5/month  - Basic:  1,000 requests/day
  $20/month - Pro:    10,000 requests/day
  POA       - Custom: whatever
*/