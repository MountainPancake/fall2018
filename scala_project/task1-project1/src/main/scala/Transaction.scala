import exceptions._
import scala.collection.mutable.Queue

object TransactionStatus extends Enumeration {
  val  status,SUCCESS, PENDING, FAILED = Value
}

class TransactionQueue {

    private val q: Queue[Transaction] = new Queue[Transaction]()

    // Remove and return the first element from the queue
    def pop: Transaction = {
        q.dequeue()
    }

    // Return whether the queue is empty
    def isEmpty: Boolean = {
        q.isEmpty
    }

    // Add new element to the back of the queue
    def push(t: Transaction): Unit = {
        q.enqueue(t)
    }

    // Return the first element from the queue without removing it
    def peek: Transaction = {
        q.head
    }

    // Return an iterator to allow you to iterate over the queue
    def iterator: Iterator[Transaction] = {
        q.iterator
    }
}

class Transaction(val transactionsQueue: TransactionQueue,
                  val processedTransactions: TransactionQueue,
                  val from: Account,
                  val to: Account,
                  val amount: Double,
                  val allowedAttemps: Int) extends Runnable {

      var status: TransactionStatus.Value = TransactionStatus.PENDING

      override def run: Unit = {

          def doTransaction() = {
            try {
                from.withdraw(amount)
                to.deposit(amount)
              //  status = TransactionStatus.SUCCESS
            } catch {
              case e: Exception  =>
            //  status = TransactionStatus.FAILED
            }


          }

          if (from.uid < to.uid) from synchronized {
              to synchronized {
                doTransaction()
              }
          } else to synchronized {
              from synchronized {
                doTransaction()
              }
          }

          // Extend this method to satisfy requirements.
        }
}
