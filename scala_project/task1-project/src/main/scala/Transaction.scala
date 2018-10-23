import exceptions._
import scala.collection.mutable.Queue

object TransactionStatus extends Enumeration {
  val SUCCESS, PENDING, FAILED = Value
}

class TransactionQueue {

    private val q: Queue[Transaction] = new Queue[Transaction]()

    // Remove and return the first element from the queue
    def pop: Transaction = q synchronized {
        q.dequeue
    }

    // Return whether the queue is empty
    def isEmpty: Boolean = q synchronized {
        q.isEmpty
    }

    // Add new element to the back of the queue
    def push(t: Transaction): Unit = q synchronized {
        q.enqueue(t)
    }

    // Return the first element from the queue without removing it
    def peek: Transaction = q synchronized {
        q.head
    }

    // Return an iterator to allow you to iterate over the queue
    def iterator: Iterator[Transaction] = q synchronized {
        q.iterator
    }
}

class Transaction (val transactionsQueue: TransactionQueue,
                  val processedTransactions: TransactionQueue,
                  val from: Account,
                  val to: Account,
                  val amount: Double,
                  val allowedAttempts: Int) extends Runnable {

      var status: TransactionStatus.Value = TransactionStatus.PENDING

      override def run: Unit = {

          var attempts: Int = allowedAttempts

          def doTransaction() = {
              from withdraw amount
              to deposit amount
          }

          def tryTransaction: Unit = {
            if (from.uid < to.uid) from synchronized {
                to synchronized {
                  doTransaction

                }
            } else to synchronized {
                from synchronized {
                  doTransaction
                }
            }
          }

          while (attempts > 0) {
            try {
              tryTransaction
              status = TransactionStatus.SUCCESS
              processedTransactions push this
              return
            } catch {
              case e: NoSufficientFundsException => status = TransactionStatus.FAILED
                  attempts -= 1
              case f: IllegalAmountException => status = TransactionStatus.FAILED
                  attempts -= 1
            }
            Thread.sleep(15)
          }

          processedTransactions push this
          // Extend this method to satisfy requirements.
        }
}
