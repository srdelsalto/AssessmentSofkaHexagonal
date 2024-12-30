package co.com.sofka.bankingaccount.bankingaccount.infrastructure.entryPoints.web.routers;

import co.com.sofka.bankingaccount.bankingaccount.application.dto.request.TransactionRequestDTO;
import co.com.sofka.bankingaccount.bankingaccount.infrastructure.entryPoints.web.handlers.TransactionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class TransactionRouter {
    @RouterOperations({
            @RouterOperation(
                    path = "/transaction",
                    beanClass = TransactionHandler.class,
                    beanMethod = "executeTransaction",
                    operation = @Operation(
                            summary = "Execute a transaction",
                            description = "Processes a transaction for a specified account.",
                            requestBody = @RequestBody(
                                    description = "Transaction details",
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = TransactionRequestDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Transaction processed successfully"),
                                    @ApiResponse(responseCode = "400", description = "Invalid transaction details")
                            }
                    )
            )
    })
    @Bean
    public RouterFunction<ServerResponse> transactionRoutes(TransactionHandler handler) {
        return route()
                .POST("/transaction", handler::executeTransaction)  // Crear una transacciones por cuenta
                .build();
    }
}
