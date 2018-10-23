import scala.concurrent.forkjoin.ForkJoinPool

class Bank(val allowedAttempts: Integer = 3) {

    private val uid = 14
    private val transactionsQueue: TransactionQueue = new TransactionQueue()
    private val processedTransactions: TransactionQueue = new TransactionQueue()
    private val executorContext = new ForkJoinPool(4)

    def addTransactionToQueue(from: Account, to: Account, amount: Double): Unit = {
      transactionsQueue push new Transaction(
        transactionsQueue, processedTransactions, from, to, amount, allowedAttempts)
    }

    // Hint: use a counter
    private object Counter {
        var prevId = 0
    }
    def generateAccountId: Int = {
        Counter.synchronized{
            Counter.prevId += 1
            Counter.prevId
        }
    }

    private def processTransactions: Unit = {
        var tries: Int = 0
        var transaction: Transaction = transactionsQueue.peek
        while (tries < transaction.allowedAttemps && transaction.status == TransactionStatus.PENDING){
          tries += 1
        try {
          transaction.run()
          processedTransactions.push(transactionsQueue.pop)

        } catch {

          case e: Exception =>

        }
      }


    }

    def addAccount(initialBalance: Double): Account = {
        new Account(this, initialBalance)
    }

    def getProcessedTransactionsAsList: List[Transaction] = {
        processedTransactions.iterator.toList
    }

}
