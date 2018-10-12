import exceptions._

class Account(val bank: Bank, initialBalance: Double) {

    class Balance(var amount: Double) {}

    val balance = new Balance(initialBalance)
    val uid = bank.generateAccountId

    def withdraw(amount: Double): Unit = {
        if(amount < 0) throw new exceptions.IllegalAmountException()
        if(balance.amount < amount) throw new exceptions.NoSufficientFundsException()
        balance.synchronized {
            balance.amount -= amount
        }
    }
    def deposit(amount: Double): Unit = {
        if(amount < 0) throw new exceptions.IllegalAmountException()
        balance.synchronized {
            balance.amount += amount
        }
    }
    def getBalanceAmount: Double = {
        balance.amount
    }

    def transferTo(account: Account, amount: Double) = {
        if(amount < 0) throw new exceptions.IllegalAmountException()
        bank addTransactionToQueue (this, account, amount)
    }


}
