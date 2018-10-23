import java.util.NoSuchElementException

import akka.actor._
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration._
import akka.util.Timeout

case class GetAccountRequest(accountId: String)

case class CreateAccountRequest(initialBalance: Double)

case class IdentifyActor()

class Bank(val bankId: String) extends Actor {

    val accountCounter = new AtomicInteger(1001)


    def createAccount(initialBalance: Double): ActorRef = {
        // Should create a new Account Actor and return its actor reference. Accounts should be assigned with unique ids (increment with 1).
        BankManager.createAccount(accountCounter.getAndIncrement.toString, bankId, initialBalance)

    }

    def findAccount(accountId: String): Option[ActorRef] = {
        // Use BankManager to look up an account with ID accountId
      try {
        if (accountId.length <= 4){
          Option(BankManager.findAccount(bankId, accountId))
        }else{
          Option(BankManager.findAccount(accountId.substring(0, 4),accountId.substring(4)))
        }
      } catch {
        case e: Exception => None
      }

    }

    def findOtherBank(bankId: String): Option[ActorRef] = {
        // Use BankManager to look up a different bank with ID bankId
        try {
          Option(BankManager.findBank(bankId))
        } catch {
          case e: Exception => None
        }



    }

    override def receive = {
        case CreateAccountRequest(initialBalance) => sender ! createAccount(initialBalance) // Create a new account
        case GetAccountRequest(id) => sender ! findAccount(id) // Return account
        case IdentifyActor => sender ! this
        case t: Transaction => processTransaction(t)

        case t: TransactionRequestReceipt => {
          if (t.toAccountNumber.length <= 4 || t.toAccountNumber.substring(0, 4) == bankId){
            findAccount(t.toAccountNumber).get ! t
          }else{
            findOtherBank(t.toAccountNumber.substring(0, 4)).get ! t
          }
        }
        case msg => ???
    }

    def processTransaction(t: Transaction): Unit = {
        implicit val timeout = new Timeout(5 seconds)
        val isInternal = (t.to.length <= 4 || t.to.substring(0, 4) == bankId)
        val toBankId = if (isInternal) bankId else t.to.substring(0, 4)
        val toAccountId = if (isInternal) t.to else t.to.substring(4)
        val transactionStatus = t.status

        if (!isInternal){
          findOtherBank(toBankId).get ! t
        }else{
          findAccount(toAccountId).get ! t
        }


        // This method should forward Transaction t to an account or another bank, depending on the "to"-address.
        // HINT: Make use of the variables that have been defined above.

    }
}
