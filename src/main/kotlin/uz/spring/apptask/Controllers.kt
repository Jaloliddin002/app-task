package uz.spring.apptask

import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@ControllerAdvice
class ExceptionHandlers(
    private val errorMessageSource: ResourceBundleMessageSource
) {

    @ExceptionHandler(DemoException::class)
    fun handleException(exception: DemoException): ResponseEntity<*> {
        return when (exception) {
            is UserNotFoundException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.id))
            is UserNameExistException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.username))
            is TransactionNotFoundException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.id))
            is CategoryNotFoundException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.id))
            is ProductNotFoundException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.id))
            is TransactionItemNotFoundException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.id))
            is UserPaymentTransactionNotFoundException -> ResponseEntity.badRequest()
                .body(exception.getErrorMessage(errorMessageSource, exception.id))
        }
    }
}

@RestController
@RequestMapping("api/v1/users")
class UserController (private val userService: UserService) {

    @PostMapping
    fun create(@RequestBody dto: UserCreateDto) = userService.create(dto)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: UserUpdateDto) = userService.update(id, dto)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long) : GetOneUserDto = userService.getOne(id)

    @GetMapping
    fun getAll(pageable: Pageable) : Page<GetOneUserDto> = userService.getAll(pageable)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = userService.delete(id)

    @GetMapping("products/{userId}")
    fun getUserProducts(@PathVariable userId: Long) = userService.getProducts(userId)

}

@RestController
@RequestMapping("api/v1/admin")
class AdminController (private val transactionService: TransactionService) {

    @GetMapping()
    fun getAllTransactions(pageable: Pageable) : Page<GetOneTransactionDto> = transactionService.getAll(pageable)
}

@RestController
@RequestMapping("api/v1/transactions")
class TransactionController (private val transactionService: TransactionService) {
    @PostMapping
    fun create(@RequestBody dto: TransactionCreateDto) = transactionService.create(dto)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long) : GetOneTransactionDto = transactionService.getOne(id)

    @GetMapping
    fun getAll(pageable: Pageable) : Page<GetOneTransactionDto> = transactionService.getAll(pageable)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: TransactionCreateDto) = transactionService.update(id, dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = transactionService.delete(id)
}


@RestController
@RequestMapping("api/v1/category")
class CategoryController (private val categoryService: CategoryService) {
    @PostMapping
    fun create(@RequestBody dto: CategoryCreateDto) = categoryService.create(dto)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long) : GetOneCategoryDto = categoryService.getOne(id)

    @GetMapping
    fun getAll(pageable: Pageable) : Page<GetOneCategoryDto> = categoryService.getAll(pageable)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: CategoryUpdateDto) = categoryService.update(id, dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = categoryService.delete(id)
}

@RestController
@RequestMapping("api/v1/product")
class ProductController (private val productService: ProductService) {
    @PostMapping
    fun create(@RequestBody dto: ProductCreateDto) = productService.create(dto)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long) : GetOneProductDto = productService.getOne(id)

    @GetMapping
    fun getAll(pageable: Pageable) : Page<GetOneProductDto> = productService.getAll(pageable)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: ProductUpdateDto) = productService.update(id, dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = productService.delete(id)
}


@RestController
@RequestMapping("api/v1/transaction-item")
class TransactionItemController (private val transactionItemService: TransactionItemService) {
    @PostMapping
    fun create(@RequestBody dto: TransactionItemCreateDto) = transactionItemService.create(dto)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: TransactionItemUpdateDto) = transactionItemService.update(id, dto)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long) : GetOneTransactionItemDto = transactionItemService.getOne(id)

    @GetMapping
    fun getAll(pageable: Pageable) : Page<GetOneTransactionItemDto> = transactionItemService.getAll(pageable)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = transactionItemService.delete(id)
}


@RestController
@RequestMapping("api/v1/user-payment-transaction")
class UserPaymentTransactionController (private val userPaymentTransactionService: UserPaymentTransactionService) {
    @PostMapping
    fun fillBalance(@RequestBody dto: UserPaymentTransactionCreateDto) = userPaymentTransactionService.create(dto)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: UserPaymentTransactionCreateDto) = userPaymentTransactionService.update(id, dto)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long) : GetOneUserPaymentTransactionDto = userPaymentTransactionService.getOne(id)

    @GetMapping
    fun getAll(pageable: Pageable) : Page<GetOneUserPaymentTransactionDto> = userPaymentTransactionService.getAll(pageable)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = userPaymentTransactionService.delete(id)

    @GetMapping("payment-history/{id}")
    fun getPaymentHistory(@PathVariable id: Long) : List<GetOneUserPaymentTransactionDto> = userPaymentTransactionService.getPaymentHistory(id)
}
