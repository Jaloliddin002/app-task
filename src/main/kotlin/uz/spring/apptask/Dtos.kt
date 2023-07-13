package uz.spring.apptask

import java.math.BigDecimal
import java.util.*

data class BaseMessage(val code: Int, val message: String?)

data class UserCreateDto(
    val fullName: String,
    val username: String
) {
    fun toEntity() = User(username, fullName, BigDecimal(0))
}

data class UserUpdateDto(
    val username: String?,
    val fullName: String?,
)

data class GetOneUserDto(
    val fullName: String,
    val username: String,
    val balance: BigDecimal,
    val id: Long?,
) {
    companion object {
        fun toDto(user: User) : GetOneUserDto {
            return user.run {
                GetOneUserDto(fullName, username, balance, id)
            }
        }
    }
}

data class TransactionCreateDto(
    val userId: Long,
    val totalAmount: BigDecimal,
) {
    fun toEntity(user: User) = Transaction(user, totalAmount, Date())
}

data class GetOneTransactionDto(
    val id: Long?,
    val userId: Long?,
    val totalAmount: BigDecimal,
    val date: Date,
) {
    companion object{
        fun toDto(transaction: Transaction) : GetOneTransactionDto {
            return transaction.run {
                GetOneTransactionDto(id, user.id, totalAmount, date)
            }
        }
    }
}

data class CategoryCreateDto(
    val name: String,
    val description: String?,
    val orderNumber: Long
) {
    fun toEntity() = Category(name, orderNumber, description)
}

data class CategoryUpdateDto(
    val name: String?,
    val description: String?,
    val orderNumber: Long?
)

data class GetOneCategoryDto(
    val id:  Long?,
    val name: String,
    val description: String?,
    val orderNumber: Long
) {
    companion object {
        fun toDto(category: Category) : GetOneCategoryDto {
            return category.run {
                GetOneCategoryDto(id, name, description, orderNumber)
            }
        }
    }
}

data class ProductCreateDto(
    val name: String,
    val count: Long,
    val categoryId: Long,
) {
    fun toEntity(category: Category) = Product(name, count, category)
}

data class ProductUpdateDto(
    val name: String?,
    val count: Long?,
)

data class GetOneProductDto(
    val id: Long?,
    val name: String,
    val count: Long,
    val categoryId: Long?,
) {
   companion object {
       fun toDto(product: Product) : GetOneProductDto {
           return product.run {
               GetOneProductDto(id, name, count, category.id)
           }
       }
   }
}


data class TransactionItemCreateDto(
    val productId: Long,
    val count: Long,
    val price: BigDecimal,
    val totalAmount: BigDecimal,
    val transactionId: Long
) {
    fun toEntity(transaction: Transaction, product: Product) = TransactionItem(count, price, totalAmount, product, transaction)
}

data class TransactionItemUpdateDto(
    val count: Long?,
    val price: BigDecimal?,
    val totalAmount: BigDecimal?,
)

data class GetOneTransactionItemDto(
    val id: Long?,
    val productId: Long?,
    val count: Long,
    val price: BigDecimal,
    val totalAmount: BigDecimal,
    val transactionId: Long?
) {
    companion object {
        fun toDto(transactionItem: TransactionItem) : GetOneTransactionItemDto {
            return transactionItem.run {
                GetOneTransactionItemDto(id, product.id, count, price, totalAmount, transaction.id)
            }
        }
    }
}

data class UserPaymentTransactionCreateDto(
    val amount: BigDecimal?,
    val userId: Long,
) {
    fun toEntity(user: User) = UserPaymentTransaction(amount, user, Date())
}


data class GetOneUserPaymentTransactionDto(
    val id: Long?,
    val amount: BigDecimal?,
    val date: Date,
    val userId: Long?,
) {
    companion object {
        fun toDto(userPaymentTransaction: UserPaymentTransaction) : GetOneUserPaymentTransactionDto {
            return userPaymentTransaction.run {
                GetOneUserPaymentTransactionDto(id, amount, date, user.id)
            }
        }
    }
}

data class FillBalanceDto(
    val amount: BigDecimal,
)

data class ProductDto(
    val id: Long,
    val name: String,
    val count: Long,
    val price: BigDecimal,
    val totalAmount: BigDecimal,
    val transactionId: Long,
)