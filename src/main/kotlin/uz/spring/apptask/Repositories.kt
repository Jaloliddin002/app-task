package uz.spring.apptask

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.repository.query.Param

@NoRepositoryBean
interface BaseRepository<T : BaseEntity> : JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    fun findByIdAndDeletedFalse(id: Long): T?
    fun trash(id: Long): T?
    fun trashList(ids : List<Long>): List<T?>
    fun findAllNotDeleted(): List<T>
    fun findAllNotDeleted(pageable: Pageable): Page<T>
}

class BaseRepositoryImpl<T : BaseEntity>(
    entityInformation: JpaEntityInformation<T, Long>, entityManager: EntityManager,
) : SimpleJpaRepository<T, Long>(entityInformation, entityManager), BaseRepository<T> {

    val isNotDeletedSpecification = Specification<T> { root, _, cb -> cb.equal(root.get<Boolean>("deleted"), false) }

    override fun findByIdAndDeletedFalse(id: Long) = findByIdOrNull(id)?.run { if (deleted) null else this }

    @Transactional
    override fun trash(id: Long): T? = findByIdOrNull(id)?.run {
        deleted = true
        save(this)
    }

    override fun findAllNotDeleted(): List<T> = findAll(isNotDeletedSpecification)
    override fun findAllNotDeleted(pageable: Pageable): Page<T> = findAll(isNotDeletedSpecification, pageable)
    override fun trashList(ids: List<Long>): List<T?> = ids.map { trash(it) }
}

interface UserRepository :  BaseRepository<User> {
    fun existsByUsername(username: String): Boolean
    fun existsByIdAndDeletedFalse(id: Long): Boolean
}

interface TransactionRepository : BaseRepository<Transaction> {
    fun existsByIdAndDeletedFalse(id: Long): Boolean

}

interface CategoryRepository : BaseRepository<Category> {
    fun existsByIdAndDeletedFalse(id: Long): Boolean
}

interface ProductRepository : BaseRepository<Product> {
    fun existsByIdAndDeletedFalse(id: Long) : Boolean
}

interface TransactionItemRepository : BaseRepository<TransactionItem> {
    @Query("select p.id, p.name, ti.count, ti.price, ti.total_amount, transaction_id from transaction t\n" +
            "    join transaction_item ti on ti.transaction_id = t.id\n" +
            "    join product p on p.id = ti.product_id where t.user_id = :userId", nativeQuery = true)
    fun getProductsByUserId(@Param("userId") userId: Long): List<ProductDto>


    @Query("select p.id, p.name, ti.count, ti.price, ti.total_amount, transaction_id\n" +
            "from transaction_item ti \n" +
            "    join product p on ti.product_id = p.id\n" +
            "where transaction_id = ?\n", nativeQuery = true)
    fun getProductsByTransactionId(transactionId: Long): List<ProductDto>
}

interface UserPaymentTransactionRepository : BaseRepository<UserPaymentTransaction> {
    fun findAllByUserId(userId: Long): List<UserPaymentTransaction>
}