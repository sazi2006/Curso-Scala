
import models.Person
import org.scalatest.Assertion
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.Matchers._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.{PersonRepository, PersonRepositoryImp}
import services.PersonService

import scala.concurrent.{ExecutionContext, Future}


class PersonServiceSpec extends PlaySpec with GuiceOneAppPerSuite with MockitoSugar with ScalaFutures {

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .overrides(bind[PersonRepository].to[personRepoMock])
    .build()

  val personService = app.injector.instanceOf(classOf[PersonService])
  // implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

  "PersonService#list" should {
    "return no persons" in {

      val futureResult: Future[Seq[Person]]  = personService.listPersons()

      val persons: Seq[Person] = futureResult.futureValue
      persons should have length 0

      /*futureResult.map(persons =>
        assert(persons.toList.isEmpty)
      )*/
    }
  }

  "PersonService#create" should {
    "save persons successfully" in {

      val person = mock[Person]
      val futureResult: Future[Int]  = personService.savePerson(person)

      val resultCode = futureResult.futureValue
      assert( resultCode == 1)

      /*futureResult.map(resultCode =>
        assert(resultCode == 2)
      )*/
    }
  }
}

class personRepoMock extends PersonRepository {
  override def create(person: Person): Future[Int] = Future.successful(1)
  override def list(): Future[Seq[Person]] = Future.successful(List())
  override def del(id: Long): Future[Int] = Future.successful(1)
  override def update(person: Person): Future[Int] = Future.successful(1)
}