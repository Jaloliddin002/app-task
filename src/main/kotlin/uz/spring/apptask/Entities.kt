package uz.spring.apptask

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.math.BigDecimal
import java.util.Date

@MappedSuperclass
class BaseEntity (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @CreatedDate @Temporal(TemporalType.TIMESTAMP) var createdDate: Date? = null,
    @LastModifiedDate @Temporal(TemporalType.TIMESTAMP) var modifiedDate: Date? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false,
)

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false, unique = true) var username: String,
    @Column(length = 128) var fullName: String,
    @ColumnDefault(value = "0") var balance: BigDecimal,
) : BaseEntity()

@Entity
class Transaction(
    @ManyToOne(fetch = FetchType.LAZY) var user: User,
    @ColumnDefault(value = "0") var totalAmount: BigDecimal,
    @Temporal(TemporalType.TIMESTAMP) var date: Date,
) : BaseEntity()

@Entity
class Category(
    @Column(nullable = false) var name: String,
    var orderNumber: Long,
    var description: String? = null
) : BaseEntity()


@Entity
class Product(
    @Column(nullable = false) var name: String,
    var count: Long,
    @ManyToOne(fetch = FetchType.LAZY) var category: Category,
) : BaseEntity()

@Entity
class TransactionItem(
    var count: Long,
    var price: BigDecimal,
    var totalAmount: BigDecimal,
    @ManyToOne(fetch = FetchType.LAZY) var product: Product,
    @ManyToOne(fetch = FetchType.LAZY) var transaction: Transaction,
) : BaseEntity()

@Entity
class UserPaymentTransaction(
    var amount: BigDecimal? = null,
    @ManyToOne(fetch = FetchType.LAZY) var user: User,
    @Temporal(TemporalType.TIMESTAMP) var date: Date,
) : BaseEntity()