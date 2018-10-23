import scala.concurrent.forkjoin.ForkJoinPool

class Bank(val allowedAttempts: Integer = 3) {

    private val uid = 14
    private val transactionsQueue: TransactionQueue = new TransactionQueue()
    private val processedTransactions: TransactionQueue = new TransactionQueue()
    private val executorContext = new ForkJoinPool(4)

    def addTransactionToQueue(from: Account, to: Account, amount: Double): Unit = {
      transactionsQueue push new Transaction(
        transactionsQueue, processedTransactions, from, to, amount, allowedAttempts)
        processTransactions
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
        if (!transactionsQueue.isEmpty) {
            val transaction = transactionsQueue.pop
            executorContext submit transaction
            Thread.sleep(10)
        }
    }

    def addAccount(initialBalance: Double): Account = {
        new Account(this, initialBalance)
    }

    def getProcessedTransactionsAsList: List[Transaction] = {
        processedTransactions.iterator.toList
    }
}
