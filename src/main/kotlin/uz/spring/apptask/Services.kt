package uz.spring.apptask

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

interface UserService {
    fun create(dto: UserCreateDto)
    fun update(id: Long, dto: UserUpdateDto)
    fun getOne(id: Long): GetOneUserDto
    fun getAll(pageable: Pageable) : Page<GetOneUserDto>
    fun delete(id: Long)
    fun getProducts(id: Long) : List<ProductDto>
}

interface TransactionService {
    fun create(dto: TransactionCreateDto)
    fun update(id: Long, dto: TransactionCreateDto)
    fun getOne(id: Long) : GetOneTransactionDto
    fun getAll(pageable: Pageable) : Page<GetOneTransactionDto>
    fun delete(id: Long)
}

interface CategoryService {
    fun create(dto: CategoryCreateDto)
    fun update(id: Long, dto: CategoryUpdateDto)
    fun getOne(id: Long) : GetOneCategoryDto
    fun getAll(pageable: Pageable) : Page<GetOneCategoryDto>
    fun delete(id: Long)
}

interface ProductService {
    fun create(dto: ProductCreateDto)
    fun update(id: Long, dto: ProductUpdateDto)
    fun getOne(id: Long) : GetOneProductDto
    fun getAll(pageable: Pageable) : Page<GetOneProductDto>
    fun delete(id: Long)
}

interface TransactionItemService{
    fun create(dto: TransactionItemCreateDto)
    fun update(id: Long, dto: TransactionItemUpdateDto)
    fun getOne(id: Long) : GetOneTransactionItemDto
    fun getAll(pageable: Pageable) : Page<GetOneTransactionItemDto>
    fun delete(id: Long)
    fun getProductsByTransactionId(id: Long) : List<ProductDto>
}

interface UserPaymentTransactionService{
    fun create(dto: UserPaymentTransactionCreateDto)
    fun update(id: Long, dto: UserPaymentTransactionCreateDto)
    fun getOne(id: Long) : GetOneUserPaymentTransactionDto
    fun getAll(pageable: Pageable) : Page<GetOneUserPaymentTransactionDto>
    fun delete(id: Long)
    fun getPaymentHistory(id: Long) : List<GetOneUserPaymentTransactionDto>
}


@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val transactionItemRepository: TransactionItemRepository,
) : UserService {

    @Transactional
    override fun create(dto: UserCreateDto) {
        dto.run{
            if (userRepository.existsByUsername(username)) throw UserNameExistException(username)
            userRepository.save(toEntity())
        }
    }

    override fun update(id: Long, dto: UserUpdateDto) {
        val user = userRepository.findByIdAndDeletedFalse(id)
            ?: throw UserNotFoundException(id)
        dto.run {
            fullName?.let { user.fullName = it }
            username?.let { user.username = it }
        }
        userRepository.save(user)
    }

    override fun getOne(id: Long) = userRepository.findByIdAndDeletedFalse(id)?.let { GetOneUserDto.toDto(it) }
        ?: throw UserNotFoundException(id)

    override fun getAll(pageable: Pageable) = userRepository.findAllNotDeleted(pageable).map { GetOneUserDto.toDto(it) }

    override fun getProducts(id: Long): List<ProductDto>  = transactionItemRepository.getProductsByUserId(id)

    override fun delete(id: Long) {
        userRepository.trash(id) ?: throw UserNotFoundException(id)
    }
}

@Service
class TransactionServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val entityManager: EntityManager,
) : TransactionService {

    @Transactional
    override fun create(dto: TransactionCreateDto) {
        dto.run {
            val user = userId.let {
                userRepository.existsByIdAndDeletedFalse(it).runIfFalse { throw UserNotFoundException(it) }
                entityManager.getReference(User::class.java, it)
            }
            transactionRepository.save(toEntity(user))
        }
    }

    override fun update(id: Long, dto: TransactionCreateDto) {
        val transaction = (transactionRepository.findByIdAndDeletedFalse(id)
            ?: throw TransactionNotFoundException(id))
        dto.run {
            totalAmount.let { transaction.totalAmount = it}
        }
        transactionRepository.save(transaction)
    }

    override fun getOne(id: Long) = transactionRepository.findByIdAndDeletedFalse(id)?.let { GetOneTransactionDto.toDto(it) }
        ?: throw TransactionNotFoundException(id)

    override fun getAll(pageable: Pageable) = transactionRepository.findAllNotDeleted(pageable).map { GetOneTransactionDto.toDto(it) }

    override fun delete(id: Long) {
        transactionRepository.trash(id) ?: throw TransactionNotFoundException(id)
    }
}

@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository
) : CategoryService {

    @Transactional
    override fun create(dto: CategoryCreateDto) {
        dto.run {
            categoryRepository.save(toEntity())
        }
    }

    override fun update(id: Long, dto: CategoryUpdateDto) {
        val category = (categoryRepository.findByIdAndDeletedFalse(id)
            ?: throw CategoryNotFoundException(id))
        dto.run {
            name?.let { category.name = it }
            description?.let { category.description = it }
            orderNumber?.let { category.orderNumber = it }
        }
        categoryRepository.save(category)
    }

    override fun getOne(id: Long) : GetOneCategoryDto = categoryRepository.findByIdAndDeletedFalse(id)?.let { GetOneCategoryDto.toDto(it) }
        ?: throw CategoryNotFoundException(id)

    override fun getAll(pageable: Pageable): Page<GetOneCategoryDto> =  categoryRepository.findAllNotDeleted(pageable).map { GetOneCategoryDto.toDto(it) }

    override fun delete(id: Long) {
        categoryRepository.trash(id) ?: throw CategoryNotFoundException(id)
    }
}


@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val entityManager: EntityManager
) : ProductService {

    @Transactional
    override fun create(dto: ProductCreateDto) {
        dto.run {
            val category = categoryId.let {
                categoryRepository.existsByIdAndDeletedFalse(it).runIfFalse { throw CategoryNotFoundException(it) }
                entityManager.getReference(Category::class.java, it)
            }
            productRepository.save(toEntity(category))
        }
    }

    override fun update(id: Long, dto: ProductUpdateDto) {
        val product = (productRepository.findByIdAndDeletedFalse(id)
                ?: throw ProductNotFoundException(id))
        dto.run {
            name?.let { product.name = it }
            count?.let { product.count = it }
        }
        productRepository.save(product)
    }

    override fun getOne(id: Long): GetOneProductDto  = productRepository.findByIdAndDeletedFalse(id)?.let { GetOneProductDto.toDto(it) }
            ?: throw ProductNotFoundException(id)

    override fun getAll(pageable: Pageable): Page<GetOneProductDto> =  productRepository.findAllNotDeleted(pageable).map { GetOneProductDto.toDto(it) }

    override fun delete(id: Long) {
        productRepository.trash(id) ?: throw ProductNotFoundException(id)
    }
}

@Service
class TransactionItemServiceImpl(
    private val transactionItemRepository: TransactionItemRepository,
    private val productRepository: ProductRepository,
    private val entityManager: EntityManager,
    private val transactionRepository: TransactionRepository
) : TransactionItemService {

    @Transactional
    override fun create(dto: TransactionItemCreateDto) {
        dto.run {
            val product = productId.let {
                productRepository.existsByIdAndDeletedFalse(it).runIfFalse { throw ProductNotFoundException(it) }
                entityManager.getReference(Product::class.java, it)
            }
            val transaction = transactionId.let {
                transactionRepository.existsByIdAndDeletedFalse(it).runIfFalse { throw TransactionNotFoundException(it) }
                entityManager.getReference(Transaction::class.java, it)
            }
            transactionItemRepository.save(toEntity(transaction, product))
        }
    }

    override fun update(id: Long, dto: TransactionItemUpdateDto) {
        val transactionItem = (transactionItemRepository.findByIdAndDeletedFalse(id)
                ?: throw TransactionItemNotFoundException(id))
        dto.run {
            count?.let { transactionItem.count = it }
            price?.let { transactionItem.price = it }
            totalAmount?.let { transactionItem.totalAmount = it }
        }
        transactionItemRepository.save(transactionItem)
    }

    override fun getOne(id: Long): GetOneTransactionItemDto =  transactionItemRepository.findByIdAndDeletedFalse(id)?.let { GetOneTransactionItemDto.toDto(it) }
        ?: throw TransactionItemNotFoundException(id)

    override fun getAll(pageable: Pageable): Page<GetOneTransactionItemDto> =  transactionItemRepository.findAllNotDeleted(pageable).map { GetOneTransactionItemDto.toDto(it) }

    override fun delete(id: Long) {
        transactionItemRepository.trash(id) ?: throw TransactionItemNotFoundException(id)
    }

    override fun getProductsByTransactionId(id: Long): List<ProductDto> = transactionItemRepository.getProductsByTransactionId(id)
}


@Service
class UserPaymentTransactionServiceImpl(
    private val userPaymentTransactionRepository: UserPaymentTransactionRepository,
    private val userRepository: UserRepository,
) : UserPaymentTransactionService {

    @Transactional
    override fun create(dto: UserPaymentTransactionCreateDto) {
        dto.run {
            val user = userRepository.findByIdAndDeletedFalse(userId) ?: throw UserNotFoundException(userId)
            user.balance = user.balance.add(amount)
            userRepository.save(user)
            userPaymentTransactionRepository.save(toEntity(user))
        }
    }

    override fun update(id: Long, dto: UserPaymentTransactionCreateDto) {
        val userPaymentTransaction = (userPaymentTransactionRepository.findByIdAndDeletedFalse(id)
                ?: throw UserPaymentTransactionNotFoundException(id))
        dto.run {
            amount?.let { userPaymentTransaction.amount = it }
        }
        userPaymentTransactionRepository.save(userPaymentTransaction)
    }

    override fun getOne(id: Long): GetOneUserPaymentTransactionDto =   userPaymentTransactionRepository.findByIdAndDeletedFalse(id)?.let { GetOneUserPaymentTransactionDto.toDto(it) }
        ?: throw UserPaymentTransactionNotFoundException(id)

    override fun getAll(pageable: Pageable): Page<GetOneUserPaymentTransactionDto> =   userPaymentTransactionRepository.findAllNotDeleted(pageable).map { GetOneUserPaymentTransactionDto.toDto(it) }


    override fun delete(id: Long) {
        userPaymentTransactionRepository.trash(id) ?: throw UserPaymentTransactionNotFoundException(id)
    }

    override fun getPaymentHistory(id: Long): List<GetOneUserPaymentTransactionDto> =  userPaymentTransactionRepository.findAllByUserId(id).map { GetOneUserPaymentTransactionDto.toDto(it) }
}