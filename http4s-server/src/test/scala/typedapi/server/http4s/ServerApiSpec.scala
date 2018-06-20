package typedapi.server.http4s

import typedapi.internal.test.util.{TestClient, TestResult, User}
import org.http4s.client.blaze._
import org.http4s.server.blaze.BlazeBuilder
import cats.effect.IO
import org.specs2.mutable.Specification

final class ServerApiSpec extends Specification {

  sequential

  import TestServer._
  import TestClient._

  val client = Http1Client[IO]().unsafeRunSync

  def tests(port: Int) = {
    def executeTest(result: TestResult[User]) = result.expected === result.actual
    def executeTests(results: List[TestResult[User]]) = results.map(executeTest)

    "paths and segments" >> {
      executeTest(testPath(client, port))
      executeTest(testSegment(client, port))
    }
  
    "queries" >> {
      executeTest(testQueries(client, port))
    }
 
    "headers" >> {
      executeTests(testHeaders(client, port))
    }

    "methods" >> {
      executeTests(testMethods(client, port))
    }
  }

  val serverDsl = startDsl(BlazeBuilder[IO]).unsafeRunSync()
  val serverDef = startDef(BlazeBuilder[IO]).unsafeRunSync()

  s"http4s implements TypedApi's server interface" >> {
    "api dsl" >> {
      tests(9000)
    }

    "api definition" >> {
      tests(10000)
    }

    step {
      serverDsl.shutdown.unsafeRunSync()
      serverDef.shutdown.unsafeRunSync()
    }
  }
}
