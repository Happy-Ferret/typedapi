package typedapi.internal.test.util

import org.http4s.{Headers, Header, Request, Uri}
import org.http4s.dsl.io._
import org.http4s.client.dsl.io._
import org.http4s.client.Client
import cats.effect.IO

final case class TestResult[A](actual: A, expected: A)

object TestClient {

  import UserCoding._

  def testPath(client: Client[IO], port: Int): TestResult[User] =
    TestResult(client.expect[User](s"http://localhost:$port/path").unsafeRunSync(), User("joe", 27))

  def testSegment(client: Client[IO], port: Int): TestResult[User] =
    TestResult(client.expect[User](s"http://localhost:$port/segment/jim").unsafeRunSync(), User("jim", 27))

  def testQueries(client: Client[IO], port: Int): TestResult[User] = 
    TestResult(client.expect[User](s"http://localhost:$port/query?age=42").unsafeRunSync(), User("joe", 42))

  def testHeaders(client: Client[IO], port: Int): List[TestResult[User]] = List(
    TestResult(client.expect[User](Request[IO](method = GET, uri = Uri.fromString(s"http://localhost:$port/header").right.get, headers = Headers(Header("age", "42")))).unsafeRunSync(), User("joe", 42)),
    TestResult(client.expect[User](Request[IO](method = GET, uri = Uri.fromString(s"http://localhost:$port/header/raw").right.get, headers = Headers(Header("age", "42"), Header("name", "jim")))).unsafeRunSync(), User("jim", 42))
  )

  def testMethods(client: Client[IO], port: Int): List[TestResult[User]] = List(
    TestResult(client.expect[User](s"http://localhost:$port/").unsafeRunSync(), User("joe", 27)),
    TestResult(client.expect[User](PUT(Uri.fromString(s"http://localhost:$port/").right.get)).unsafeRunSync(), User("joe", 27)),
    TestResult(client.expect[User](PUT(Uri.fromString(s"http://localhost:$port/body").right.get, User("joe", 27))).unsafeRunSync(), User("joe", 27)),
    TestResult(client.expect[User](POST(Uri.fromString(s"http://localhost:$port/").right.get)).unsafeRunSync(), User("joe", 27)),
    TestResult(client.expect[User](POST(Uri.fromString(s"http://localhost:$port/body").right.get, User("joe", 27))).unsafeRunSync(), User("joe", 27)),
    TestResult(client.expect[User](DELETE(Uri.fromString(s"http://localhost:$port/?reasons=because").right.get)).unsafeRunSync(), User("joe", 27))
  )
}
