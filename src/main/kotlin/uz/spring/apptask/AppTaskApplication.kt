package uz.spring.apptask

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl::class)
@OpenAPIDefinition(
    info = Info(
        title = "Task",
        version = "1.0",
        description = "",
        contact = Contact(name = "Jaloliddin", email = "jaloliddindeveloper@gmail.com")
    )
)
class AppTaskApplication

fun main(args: Array<String>) {
    runApplication<AppTaskApplication>(*args)
}
