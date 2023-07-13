package uz.spring.apptask

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import java.util.*

sealed class DemoException(message: String? = null): RuntimeException(message) {
    abstract fun errorType(): ErrorCode

    fun getErrorMessage(errorMessageSource: ResourceBundleMessageSource, vararg array: Any?): BaseMessage{
        return BaseMessage(
            errorType().code,
            errorMessageSource.getMessage(
                errorType().toString(),
                array,
                Locale(LocaleContextHolder.getLocale().language)
            )
        )
    }
}


class UserNameExistException(val username: String): DemoException() {
    override fun errorType() = ErrorCode.USER_NAME_EXIST
}

class UserNotFoundException(val id: Long): DemoException() {
    override fun errorType() = ErrorCode.USER_NOT_FOUND
}

class TransactionNotFoundException(val id: Long): DemoException() {
    override fun errorType() = ErrorCode.TRANSACTION_NOT_FOUND
}

class CategoryNotFoundException(val id: Long): DemoException() {
    override fun errorType() = ErrorCode.CATEGORY_NOT_FOUND
}

class ProductNotFoundException(val id: Long): DemoException() {
    override fun errorType() = ErrorCode.PRODUCT_NOT_FOUND
}

class TransactionItemNotFoundException(val id: Long): DemoException() {
    override fun errorType() = ErrorCode.TRANSACTION_ITEM_NOT_FOUND
}

class UserPaymentTransactionNotFoundException(val id: Long): DemoException() {
    override fun errorType() = ErrorCode.USER_PAYMENT_TRANSACTION_NOT_FOUND
}